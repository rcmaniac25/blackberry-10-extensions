//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2008-2012 Vincent Simonetti
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
// to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
//---------------------------------------------------------------------------------
//
package rebuild.util.pdf.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.device.api.util.Arrays;

/**
 * Individual type writers.
 */
final class PDFWriters
{
	//Helper functions to get Strings in a specific encoding without a try/catch statement statement
	public static byte[] encodeString(String str)
  	{
  		return encodeString(str, "ISO-8859-1");
  	}
  	
  	public static byte[] encodeString(String str, String enc)
  	{
  		try
  		{
  			return str.getBytes(enc);
  		}
  		catch(UnsupportedEncodingException uee)
  		{
  		}
  		return null;
  	}
	
  	//Writers
	public static long WriteValue(OutputStream s, Object obj) throws IOException
    {
        long len = -1;
        if (obj == null)
        {
            len = WriteNull(s);
        }
        else if (obj instanceof PDFName)
        {
            len = WriteName(s, ((PDFName)obj).value);
        }
        else if (obj instanceof PDFObject)
        {
            len = PDFObject.writeIndirect(s, (PDFObject)obj);
        }
        else if (obj instanceof PDFHexString)
        {
            len = WriteString(s, ((PDFHexString)obj).value, false);
        }
        else if (obj instanceof String)
        {
            len = WriteString(s, (String)obj, true);
        }
        else if (obj instanceof Boolean)
        {
            len = WriteBool(s, ((Boolean)obj).booleanValue());
        }
        else if (obj instanceof Integer || obj instanceof Long || obj instanceof Float || obj instanceof Double)
        {
            len = WriteNumber(s, obj);
        }
        else if (obj instanceof Object[])
        {
            len = WriteArray(s, (Object[])obj);
        }
        else if (obj instanceof Hashtable)
        {
            len = WriteTable(s, (Hashtable)obj);
        }
        return len;
    }
	
	public static long WriteTable(OutputStream s, Hashtable value) throws IOException
    {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        
        //Head
        mem.write(encodeString("<<"));
        
        //Get gap
        byte[] space = encodeString(" ");
        byte[] newline = encodeString("\n");
        
        boolean first = true;
        
        for (Enumeration e = value.keys(); e.hasMoreElements(); )
        {
        	String key = (String)e.nextElement();
            
            if (first)
            {
                //We don't want to write a new line for the first element
                first = false;
            }
            else
            {
                mem.write(newline);
            }
            
            //Write key
            if (WriteName(mem, key) == -1)
            {
                return -1;
            }
            
            //Write gap
            mem.write(space);
            
            //Write value
            if (WriteValue(mem, value.get(key)) == -1)
            {
                return -1;
            }
        }
        
        //Tail
        mem.write(encodeString(">>"));
        
        //Write out
        s.write(mem.toByteArray());
        
        return mem.size();
    }
	
	public static long WriteArray(OutputStream s, Object[] value) throws IOException
    {
		ByteArrayOutputStream mem = new ByteArrayOutputStream();
		
        //Head
        mem.write(encodeString("["));
        
        //Get gap
        byte[] space = encodeString(" ");
        
        for (int i = 0; i < value.length; i++)
        {
            if (WriteValue(mem, value[i]) == -1)
            {
                return -1;
            }
            
            if (i < value.length - 1)
            {
                //Write space
                mem.write(space);
            }
        }
        
        //Tail
        mem.write(encodeString("]"));
        
        //Write out
        s.write(mem.toByteArray());
        
        return mem.size();
    }
	
	public static long WriteName(OutputStream s, String value) throws IOException
    {
        StringBuffer sb = new StringBuffer();
        sb.append('/');
        
        int len = value.length();
        for(int i = 0; i < len; i++)
        {
        	char c = value.charAt(i);
            if (c == 0)
            {
                //Can't have a null char
                return -1;
            }
            else if (c < 0x21 || c > 0x7E)
            {
                //PDF 1.2 and higher can have any char but \0 in name (so long as outside of the specified range is in a hex format)
                String format = Integer.toHexString(c);
                sb.append('#');
                if (format.length() < 2)
                {
                    sb.append('0');
                }
                sb.append(format);
            }
            else
            {
                sb.append(c);
            }
        }
        
        byte[] dat = encodeString(sb.toString());
        s.write(dat);
        return dat.length;
    }
	
	public static long WriteString(OutputStream s, String value, boolean literal) throws IOException
    {
		StringBuffer sb = new StringBuffer();
		int len = value.length();
        if (literal)
        {
            //Process para
            int paraCount = 0;
            for(int i = 0; i < len; i++)
            {
            	char c = value.charAt(i);
                if (c == '(')
                {
                    paraCount++;
                }
                else if (c == ')')
                {
                    paraCount--;
                }
            }
            
            sb.append('(');
            
            //Format string
            for(int i = 0; i < len; i++)
            {
            	char c = value.charAt(i);
                switch (c)
                {
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '(':
                    case ')':
                        if (paraCount != 0)
                        {
                            //Unbalanced. To keep it simple, simply make them all control chars
                            sb.append('\\');
                        }
                        sb.append(c);
                        break;
                    default:
                        if (c < 0x20 || c > 0x7E)
                        {
                            //Outside ASCII range
                            String format = Integer.toOctalString(c);
                            sb.append('\\');
                            if (format.length() != 3)
                            {
                                if (format.length() < 3)
                                {
                                	char[] zeros = new char[3 - format.length()];
                                	Arrays.fill(zeros, '0');
                                    sb.append(zeros);
                                }
                                else
                                {
                                    //Question mark, what do we put here?
                                    sb.append("077");
                                }
                            }
                            else
                            {
                                sb.append(format);
                            }
                        }
                        else
                        {
                            //Simply add it
                            sb.append(c);
                        }
                        break;
                }
            }
            
            sb.append(')');
        }
        else
        {
            sb.append('<');
            
            //Remove all whitespace
            for(int i = 0; i < len; i++)
            {
            	char c = value.charAt(i);
                switch (c)
                {
                    case '\n':
                    case '\r':
                    case '\t':
                    case '\f':
                    case ' ':
                        break;
                    default:
                        String v = Integer.toHexString(c);
                        if (v.length() < 2)
                        {
                            sb.append('0');
                        }
                        sb.append(v);
                        break;
                }
            }
            
            sb.append('>');
        }
        byte[] dat = encodeString(sb.toString());
        s.write(dat);
        return dat.length;
    }
	
	public static long WriteBool(OutputStream s, boolean value) throws IOException
    {
        byte[] dat = encodeString(value ? "true" : "false");
        s.write(dat);
        return dat.length;
    }
	
	public static long WriteNumber(OutputStream s, Object value) throws IOException
    {
        byte[] dat = null;
        if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double)
        {
        	//Can't really format Float and Double, so just write it out and cross fingers
        	String str = value.toString();
        	if(str.indexOf('e') == -1 && str.indexOf('E') == -1 && str.indexOf('n') == -1 && str.indexOf('N') == -1)
        	{
        		//If there is no exponent, NaN, or Inf.
        		dat = encodeString(value.toString());
        		s.write(dat);
        	}
        }
        return dat != null ? dat.length : -1;
    }
	
	public static long WriteNull(OutputStream s) throws IOException
    {
        byte[] dat = encodeString("null");
        s.write(dat);
        return dat.length;
    }
	
	public static long WriteStream(OutputStream s, ByteArrayOutputStream value, Hashtable additionalTable) throws IOException
    {
        if (additionalTable != null)
        {
            //We can't have the additional dictionary replace required keys
            if (additionalTable.containsKey("Length") || additionalTable.containsKey("Filter") || additionalTable.containsKey("DL"))
            {
                return -1;
            }
        }
        
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        
        //Process stream
        StringBuffer sb = new StringBuffer();
        
        byte[] valueData = value.toByteArray();
        int valueLen = value.size();
        for(int i = 0; i < valueLen; i++)
        {
            String line = Integer.toHexString(valueData[i] & 0xFF);
            if(valueData[i] != 0)
            {
            	sb.append(' ');
            	sb.deleteCharAt(sb.length() - 1);
            }
            if (line.length() < 2)
            {
            	sb.append('0');
            }
            sb.append(line.toCharArray());
        }
        valueData = null;
        
        sb.append('>'); //EOD
        valueLen = sb.length();
        byte[] sMem = encodeString(sb.toString(), "US-ASCII");
        sb = null;
        
        Hashtable stTable = new Hashtable();
        stTable.put("Length", new Integer(sMem.length)); //Get the length of the encoded string
        stTable.put("Filter", new PDFName("ASCIIHexDecode")); //For easier management
        stTable.put("DL", new Integer(value.size())); //Not needed, but potentially useful
        if (additionalTable != null)
        {
            //Copy dictionary
        	for(Enumeration e = additionalTable.keys(); e.hasMoreElements();)
            {
        		Object key = e.nextElement();
        		stTable.put(key, additionalTable.get(key));
            }
        }
        
        byte[] newline = encodeString("\n");
        
        //Write dictionary
        WriteTable(mem, stTable);
        
        //Write start of stream
        mem.write(newline);
        mem.write(encodeString("stream"));
        mem.write(newline);
        
        //Write stream
        mem.write(sMem);
        sMem = null;
        
        //Write end of stream
        mem.write(newline);
        mem.write(encodeString("endstream"));
        mem.write(newline);
        
        s.write(mem.toByteArray());
        
        return mem.size();
    }
}
