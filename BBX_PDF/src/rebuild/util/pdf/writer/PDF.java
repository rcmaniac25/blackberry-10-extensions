//#preprocessor

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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import rebuild.util.text.StringUtilities;

import net.rim.device.api.crypto.MD5Digest;
//#ifdef NO_FORMATTERS
import net.rim.device.api.util.Arrays;
//#endif
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;

/**
 * Represents a raw PDF document, defined only by PDFObjects.
 */
public final class PDF
{
    private Vector objects;
    
    public PDF()
    {
        this.objects = new SimpleSortingVector();
        ((SimpleSortingVector)this.objects).setSort(true);
        ((SimpleSortingVector)this.objects).setSortComparator(new ObjectComparer());
    }
    
    public void addObject(PDFObject obj)
    {
    	objects.addElement(obj);
    }
    
    public boolean removeObject(PDFObject obj)
    {
    	return objects.removeElement(obj);
    }
    
    public int objectCount()
    {
    	return objects.size();
    }
    
    public void reset()
    {
    	objects.removeAllElements();
    }
    
    public PDFObject getObject(int index)
    {
    	return (PDFObject)objects.elementAt(index);
    }
    
    private Vector getUnusedObjects()
    {
    	Vector unused = new Vector();
    	
    	//Copy list
    	int len = objects.size();
    	for(int i = 0; i < len; i++)
    	{
    		unused.addElement(objects.elementAt(i));
    	}
    	
    	//Find unused objects
    	for(int i = 0; i < len; i++)
        {
            findUsedObjects((PDFObject)objects.elementAt(i), unused);
        }
        
        return unused;
    }
    
    private static void findUsedObjects(PDFObject objectToCheck, Hashtable table, Vector unused)
    {
        //Look through table
        for (Enumeration en = table.keys(); en.hasMoreElements(); )
        {
        	Object key = en.nextElement();
        	Object value = table.get(key);
            if (objectToCheck != null)
            {
                if (key.equals("Type"))
                {
                    if (value instanceof PDFName)
                    {
                        if (((PDFName)value).value.equals("Catalog"))
                        {
                            //PDF Catalog, need this
                            unused.removeElement(objectToCheck);
                        }
                    }
                }
            }
            
            if (value instanceof PDFObject)
            {
                //Don't want to check again
                if (unused.contains(value))
                {
                    unused.removeElement(value);
                    findUsedObjects((PDFObject)value, unused);
                }
            }
            else if (value instanceof Hashtable)
            {
            	findUsedObjects(objectToCheck, (Hashtable)value, unused);
            }
            else if (value instanceof Object[])
            {
            	findUsedObjects(objectToCheck, (Object[])value, unused);
            }
        }
    }
    
    private static void findUsedObjects(PDFObject objectToCheck, Object[] arr, Vector unused)
    {
        //Look through array
    	int len = arr.length;
    	for(int i = 0; i < len; i++)
        {
    		Object obj = arr[i];
            if (obj instanceof PDFObject)
            {
                if (unused.contains(obj))
                {
                    unused.removeElement(obj);
                    findUsedObjects((PDFObject)obj, unused);
                }
            }
            else if (obj instanceof Hashtable)
            {
            	findUsedObjects(objectToCheck, (Hashtable)obj, unused);
            }
            else if (obj instanceof Object[])
            {
            	findUsedObjects(objectToCheck, (Object[])obj, unused);
            }
        }
    }
    
    private static void findUsedObjects(PDFObject objectToCheck, Vector unused)
    {
        //Ignore null objects
        if (objectToCheck.value != null)
        {
            if (objectToCheck.value instanceof PDFObject)
            {
                if (unused.contains(objectToCheck.value))
                {
                    //Indirect object
                    unused.removeElement(objectToCheck.value);
                    findUsedObjects((PDFObject)objectToCheck.value, unused);
                }
            }
            else if (objectToCheck.value instanceof Hashtable)
            {
            	findUsedObjects(objectToCheck, (Hashtable)objectToCheck.value, unused);
            }
            else if (objectToCheck.value instanceof Object[])
            {
            	findUsedObjects(objectToCheck, (Object[])objectToCheck.value, unused);
            }
        }
    }
    
    private static class ObjectComparer implements Comparator
    {
		public int compare(Object o1, Object o2)
		{
			PDFObject p1 = (PDFObject)o1;
			PDFObject p2 = (PDFObject)o2;
			return p1.number - p2.number;
		}
    }
    
    public long write(OutputStream s) throws IOException
    {
    	ByteArrayOutputStream ms = new ByteArrayOutputStream();
    	
        //--Write header--
        ms.write(PDFWriters.encodeString("%PDF-1.4\n")); //1.4 gets us a lot of features (with the exception of object streams)
        ms.write(new byte[]{ (byte)'%', (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)'\n'}); //Since we will contain binary data, we want to make sure our PDF indicates it has binary data
        ms.flush();
        
        long objectOffset = ms.size();
        
        //Find the catalog element
        PDFObject cat = null;
        int objCount = this.objects.size();
        for(int i = 0; i < objCount; i++)
        {
        	PDFObject obj = (PDFObject)this.objects.elementAt(i);
            if (obj.value instanceof Hashtable)
            {
            	Hashtable table = (Hashtable)obj.value;
                for (Enumeration e = table.keys(); e.hasMoreElements(); )
                {
                	Object key = e.nextElement();
                    if (key.equals("Type"))
                    {
                    	Object value = table.get(key);
                        if (value instanceof PDFName)
                        {
                            if (((PDFName)value).value.equals("Catalog"))
                            {
                                //PDF Catalog, need this
                                cat = obj;
                                break;
                            }
                        }
                    }
                }
                if (cat != null)
                {
                    //Found catalog, we can stio
                    break;
                }
            }
        }
        if (cat == null)
        {
            //No catalog, we can't continue
            return -1;
        }
        
        //Find all unused (free) objects
        Vector freeObjects = getUnusedObjects();
        
        ByteArrayOutputStream xrefStream = new ByteArrayOutputStream();
        
        //--Write xref--
        //First the header
        xrefStream.write(PDFWriters.encodeString("xref\n"));
        
        //Then the xref table info
//#ifdef NO_FORMATTERS
        xrefStream.write(PDFWriters.encodeString("0 " + (this.objects.size() + 1) + "\n"));
//#else
        xrefStream.write(PDFWriters.encodeString(StringUtilities.format_printf("0 %d\n", new Integer(this.objects.size() + 1))));
//#endif
        
        if (freeObjects.size() > 0)
        {
            //Need to find the first free object
            for(int i = 0; i < objCount; i++)
            {
            	PDFObject obj = (PDFObject)this.objects.elementAt(i);
                if (freeObjects.contains(obj))
                {
                	//Get "next free object" number
//#ifdef NO_FORMATTERS
                    StringBuffer sb = new StringBuffer();
                    
                    String value = Integer.toString(obj.number);
                    if (value.length() < 10)
                    {
                    	char[] zeros = new char[10 - value.length()];
                    	Arrays.fill(zeros, '0');
                        sb.append(zeros);
                    }
                    else if (value.length() > 10)
                    {
                        return -1;
                    }
                    sb.append(value);

                    //Finish first xref
                    sb.append(" 65536 f \n");
                    
                    xrefStream.write(PDFWriters.encodeString(sb.toString(), "US-ASCII"));
//#else
                    xrefStream.write(PDFWriters.encodeString(StringUtilities.format_printf("%010d 65536 f \n", new Integer(obj.number)), "US-ASCII"));
//#endif
                    break;
                }
            }
        }
        else
        {
            //No free objects, we can finish the "linked-list" here
            xrefStream.write(PDFWriters.encodeString("0000000000 65536 f \n", "US-ASCII"));
        }
        
        //--Write objects--
        for(int k = 0; k < objCount; k++)
        {
        	PDFObject obj = (PDFObject)this.objects.elementAt(k);
        	
            //Write for the body
            obj.write(ms);
            
            //Write the xref
            StringBuffer sb = new StringBuffer();
            
            boolean free = freeObjects.contains(obj);
            freeObjects.removeElement(obj);
//#ifdef NO_FORMATTERS
            String value;
//#endif
            
            if (free)
            {
                //For free objects, we need to get the next free object and write it's value. If there are no more free objects, we write zero
                if (freeObjects.size() > 0)
                {
                    for (int i = this.objects.indexOf(obj) + 1; i < this.objects.size(); i++)
                    {
                        if (freeObjects.contains(this.objects.elementAt(i)))
                        {
                            //Get "next free object" number
//#ifdef NO_FORMATTERS
                        	value = Integer.toString(((PDFObject)this.objects.elementAt(i)).number);
                            if (value.length() < 10)
                            {
                            	char[] zeros = new char[10 - value.length()];
                            	Arrays.fill(zeros, '0');
                                sb.append(zeros);
                            }
                            else if (value.length() > 10)
                            {
                                return -1;
                            }
                            sb.append(value);
//#else
                            sb.append(StringUtilities.format_printf("%010d", new Integer(((PDFObject)this.objects.elementAt(i)).number)));
//#endif
                            break;
                        }
                    }
                }
                else
                {
                    sb.append("0000000000");
                }
            }
            else
            {
                //First the byte offset
//#ifdef NO_FORMATTERS
            	value = Long.toString(objectOffset);
            	if (value.length() < 10)
                {
            		//Need to append zeros
                	char[] zeros = new char[10 - value.length()];
                	Arrays.fill(zeros, '0');
                    sb.append(zeros);
                }
                else if (value.length() > 10)
                {
                    return -1;
                }
                sb.append(value);
//#else
                sb.append(StringUtilities.format_printf("%010d", new Long(objectOffset)));
//#endif
            }
            
//#ifdef NO_FORMATTERS
            //Space
            sb.append(' ');
            
            //Next the generation number
            value = Integer.toString(obj.revision);
            if (value.length() < 5)
            {
            	//Need to append zeros
            	char[] zeros = new char[5 - value.length()];
            	Arrays.fill(zeros, '0');
                sb.append(zeros);
            }
            else if (value.length() > 5)
            {
                return -1;
            }
            sb.append(value);
            
            //Space
            sb.append(' ');
            
            //If this is a free object or not
            sb.append(free ? 'f' : 'n');
            
            //End of line
            sb.append(" \n");
//#else
            sb.append(StringUtilities.format_printf(" %05d %c \n", new Integer(obj.revision), new Character(free ? 'f' : 'n')));
//#endif
            
            //Write the value
            xrefStream.write(PDFWriters.encodeString(sb.toString(), "US-ASCII")); //Needs to be 20 bytes, so don't use a specific encoding
            
            //Reset objectOffset
            objectOffset = ms.size();
        }
        
        //Now write the xref table to the output
        ms.write(xrefStream.toByteArray());
        xrefStream.close();
        xrefStream = null;
        
        //--Write trailer--
        ms.write(PDFWriters.encodeString("trailer"));
        
        //Need a dictionary for trailer
        Hashtable trailerDict = new Hashtable();
        trailerDict.put("Size", new Integer(this.objects.size() + 1));
        trailerDict.put("Root", cat); //Need the catalog object
        PDFHexString ID = new PDFHexString(MD5Hash(PDFWriters.encodeString(Calendar.getInstance().toString())));
        trailerDict.put("ID", new Object[] { ID, ID }); //An ID is recommended
        PDFWriters.WriteTable(ms, trailerDict);
        
        //Now write the byte offset to the xref table
        ms.write(PDFWriters.encodeString("startxref"));
        ms.write(PDFWriters.encodeString("\n"));
        ms.write(PDFWriters.encodeString(Long.toString(objectOffset))); //After writing the last object, the offset will equal the stream position, which will be at the start of the xref table
        
        //Finally, write the end of file marker
        ms.write(PDFWriters.encodeString("%%EOF"));
        
        s.write(ms.toByteArray());
        s.flush();
        return ms.size();
    }
    
    private static String MD5Hash(byte[] value)
    {
    	byte[] hash = null;
//#ifdef NO_SIGNING
    	hash = value;
//#else
    	MD5Digest md5 = new MD5Digest();
    	md5.update(value);
    	hash = md5.getDigest();
//#endif
        try
        {
			return new String(hash, "ISO-8859-1");
		}
        catch (UnsupportedEncodingException e)
		{
		}
        return null;
    }
}
