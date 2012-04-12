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
// Created 2009
package rebuild.graphics;

import java.io.IOException;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.JPEGEncodedImage;

/**
 * A JPEG encoder.
 * @since BBX 1.0.1
 */
public class JPEGEncoder extends ImageEncoder
{
	/**
	 * Get the MIME type of the image encoder.
	 * @return The MIME type of the image encoder.
	 */
	public String getMime()
	{
		return "image/jpeg";
	}
	
	/**
     * Class constructor
     */
    public JPEGEncoder()
    {
    	super(null, false, 90);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     */
    public JPEGEncoder(Bitmap image)
    {
    	super(image, false, 90);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     * @param compression The compression level to use, the range is 0 - 100.
     */
    public JPEGEncoder(Bitmap image, int compression)
    {
    	super(image, false, compression);
    	if(compression < 0 || compression > 100)
    	{
    		super.compressionLevel = 90;
    	}
    }
    
    /**
     * Set the compression level to use
     * @param level 0 through 100
     */
    public void setCompressionLevel(int level)
    {
        if (level >= 0 && level <= 100)
        {
        	super.compressionLevel = level;
        }
    }
    
    /**
     * Creates an array of bytes that is the JPEG equivalent of the current image, specifying whether to encode alpha or not.
     * @param encodeAlpha Ignored, JPEG's don't have an alpha channel.
     * @return An array of bytes, or null if there was a problem.
     */
    protected byte[] inEncode(boolean encodeAlpha) throws IOException
    {
    	return JPEGEncodedImage.encode(super.image, super.compressionLevel).getData();
    }
}
