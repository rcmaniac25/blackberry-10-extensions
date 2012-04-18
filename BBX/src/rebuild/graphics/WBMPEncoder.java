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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.XYRect;

/**
 * A Wireless Application Protocol Bitmap (WBMP) encoder.
 * @since BBX 1.0.1
 */
public class WBMPEncoder extends ImageEncoder
{
	private static final int FixHeaderField = 0;
	private static final int TypeField = 0;
	
	private boolean isWhiteZero;
	
	/**
	 * Get the MIME type of the image encoder.
	 * @return The MIME type of the image encoder.
	 */
	public String getMime()
	{
		return "image/vnd.wap.wbmp";
	}
	
	/**
     * Class constructor
     */
    public WBMPEncoder()
    {
    	this(null);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     */
    public WBMPEncoder(Bitmap image)
    {
    	super(image, false);
    	isWhiteZero = false;
    }
    
    /**
     * Get if the image encoding is inverted, the default is false.
     * @return true if the image encoder is inverted, false if otherwise.
     */
    public final boolean getInverted()
    {
    	return isWhiteZero;
    }
    
    /**
     * Set if the image encoding is inverted, the default is false.
     * @param value true if the image encoder is inverted, false if otherwise.
     */
    public final void setInverted(boolean value)
    {
    	isWhiteZero = value;
    }
    
	/**
	 * Get the number of bits required to represent an int.
	 */
	private static int getNumBits(int intValue)
	{
		int numBits = 32;
		int mask = 0x80000000;
		while(mask != 0 && (intValue & mask) == 0)
		{
			numBits--;
			mask >>>= 1;
		}
		return numBits;
	}
	
	/**
	 * Convert an int value to WBMP multi-byte format.
	 */
	private static byte[] intToMultiByte(int intValue)
	{
		int numBitsLeft = getNumBits(intValue);
		byte[] multiBytes = new byte[(numBitsLeft + 6) / 7];
		
		int maxIndex = multiBytes.length - 1;
		for(int b = 0; b <= maxIndex; b++)
		{
			multiBytes[b] = (byte)((intValue >>> ((maxIndex - b) * 7)) & 0x7F);
			if(b != maxIndex)
			{
				multiBytes[b] |= (byte)0x80;
			}
		}
		
		return multiBytes;
	}
    
    /**
     * Creates an array of bytes that is the WBMP equivalent of the current image, specifying whether to encode alpha or not.
     * @param encodeAlpha Ignored, WBMP's don't have an alpha channel.
     * @return an array of bytes, or null if there was a problem
     */
    protected byte[] inEncode(boolean encodeAlpha) throws IOException
    {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	XYRect sourceRegion = new XYRect(0, 0, width, height);
    	sourceRegion.translate(xOffset, yOffset);
    	sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;
        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;
        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;
        XYRect destinationRegion = new XYRect(minX, minY, w, h);
        
        // Get the line stride, bytes per row, and data array.
        int lineStride = width;
        int bytesPerRow = (w + 7) / 8;
        
        //get the image data and pass it to the byte array
        byte[] bdata = super.getARGBByte(0, lineStride, destinationRegion);
        
        // Write WBMP header.
        stream.write(TypeField); // TypeField
        stream.write(FixHeaderField); // FixHeaderField
        stream.write(intToMultiByte(w)); // width
        stream.write(intToMultiByte(h)); // height
        
        // Write the data.
        if(!isWhiteZero && lineStride == bytesPerRow)
        {
            // Write the entire image.
            stream.write(bdata, 0, h * bytesPerRow);
        }
        else
        {
            // Write the image row-by-row.
            int offset = 0;
            if(!isWhiteZero)
            {
                // Black-is-zero
                for(int row = 0; row <  h; row++)
                {
                    stream.write(bdata, offset, bytesPerRow);
                    offset += lineStride;
                }
            }
            else
            {
                // White-is-zero: need to invert data.
                byte[] inverted = new byte[bytesPerRow];
                for(int row = 0; row < h; row++)
                {
                    for(int col = 0; col < bytesPerRow; col++)
                    {
                        inverted[col] = (byte)(~(bdata[col+offset]));
                    }
                    stream.write(inverted, 0, bytesPerRow);
                    offset += lineStride;
                }
            }
        }
        bdata = stream.toByteArray();
        stream.close();
        
    	return bdata;
    }
}
