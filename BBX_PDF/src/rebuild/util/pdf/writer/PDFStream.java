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
import java.util.Hashtable;

/**
 * A Stream data type within a PDF.
 */
public final class PDFStream
{
	ByteArrayOutputStream stream;
    Hashtable table;
    
    public PDFStream(ByteArrayOutputStream s)
    {
    	this(s, null);
    }
    
    public PDFStream(ByteArrayOutputStream s, Hashtable table)
    {
        this.stream = s;
        this.table = table;
    }
    
    public ByteArrayOutputStream getStream()
    {
		return stream;
	}
    
    public void setStream(ByteArrayOutputStream stream)
    {
		this.stream = stream;
	}
    
    public Hashtable getTable()
    {
		return table;
	}
    
    public void setTable(Hashtable table)
    {
		this.table = table;
	}
}
