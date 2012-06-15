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
import java.util.Hashtable;

//#ifndef NO_FORMATTERS
import rebuild.util.text.StringUtilities;
//#endif

/**
 * Represents a distinct PDF Object within a PDF.
 */
public final class PDFObject
{
	int number;
    short revision;
    Object value;
    
    public PDFObject(int num, int rev, Object value)
    {
        this.number = num;
        this.revision = (short)rev;
        this.value = value;
    }
    
    public int getNumber()
    {
		return number;
	}
    
    public void setNumber(int number)
    {
		this.number = number;
	}
    
    public short getRevision()
    {
		return revision;
	}
    
    public void setRevision(short revision)
    {
		this.revision = revision;
	}
    
    public Object getValue()
    {
		return value;
	}
    
    public void setValue(Object value)
    {
		this.value = value;
	}
    
    //Writes it like PDF_WriteValue
    public static long writeIndirect(OutputStream s, PDFObject pdfobj) throws IOException
    {
//#ifdef NO_FORMATTERS
        byte[] buffer = PDFWriters.encodeString("" + pdfobj.number + " " + pdfobj.revision + " R");
//#else
        byte[] buffer = PDFWriters.encodeString(StringUtilities.stripNullChar(StringUtilities.format_printf("%d %d R", new Integer(pdfobj.number), new Integer(pdfobj.revision))));
//#endif
        s.write(buffer);
        return buffer.length;
    }
    
    public long write(OutputStream s) throws IOException
    {
    	ByteArrayOutputStream mem = new ByteArrayOutputStream();
    	
        byte[] newline = PDFWriters.encodeString("\n");
        
        //Write first header
//#ifdef NO_FORMATTERS
        mem.write(PDFWriters.encodeString("" + this.number + " " + this.revision + " obj"));
//#else
        mem.write(PDFWriters.encodeString(StringUtilities.stripNullChar(StringUtilities.format_printf("%d %d obj", new Integer(this.number), new Integer(this.revision)))));
//#endif
        mem.write(newline);
        
        //Write data
        ByteArrayOutputStream vMem = new ByteArrayOutputStream();
        if (PDFWriters.WriteValue(vMem, this.value) == -1)
        {
            if (this.value instanceof ByteArrayOutputStream || this.value instanceof PDFStream)
            {
                //Special because streams must be objects
            	ByteArrayOutputStream ms;
                Hashtable at = null;
                
                if (this.value instanceof ByteArrayOutputStream)
                {
                    ms = (ByteArrayOutputStream)this.value;
                }
                else
                {
                    PDFStream pdfs = (PDFStream)this.value;
                    ms = pdfs.stream;
                    at = pdfs.table;
                }
                
                if (PDFWriters.WriteStream(vMem, ms, at) == -1)
                {
                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }
        mem.write(vMem.toByteArray());
        
        //Write footer
        mem.write(newline);
        mem.write(PDFWriters.encodeString("endobj"));
        mem.write(newline);
        
        s.write(mem.toByteArray());
        
        return mem.size();
    }
}
