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
import net.rim.device.api.ui.XYRect;

/**
 * The base abstract class that represents an image encoder.
 * @since BBX 1.0.1
 */
public abstract class ImageEncoder
{
	/** The encoded bytes. */
    protected byte[] dataBytes;
    
    /** The image. */
    protected Bitmap image;

    /** The width of the image. */
    protected int width;
    
    /** The height of the image. */
    protected int height;
    
    /** Encode alpha? */
    protected boolean encodeAlpha;
    
    /** The compression level. */
    protected int compressionLevel;
    
    /** The scale of the image in the horizontal orientation. Scale is defined as an int where 1 is the same width well 2 is two times the width. */
    protected int scaleX;
    
    /** The scale of the image in the vertical orientation. Scale is defined as an int where 1 is the same height well 2 is two times the height. */
    protected int scaleY;
    
    /** The offset of the x origin point of the image. */
    protected int xOffset;
    
    /** The offset of the y origin point of the image. */
    protected int yOffset;
    
    /**
	 * Get the MIME type of the image encoder.
	 * @return The MIME type of the image encoder.
	 */
	public abstract String getMime();
    
    /**
     * Class constructor
     */
    protected ImageEncoder(String mime)
    {
    	this(null, false, 0);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     */
    protected ImageEncoder(Bitmap image, String mime)
    {
    	this(image, false, 0);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, and whether to encode alpha.
     * @param image A Java Image object which uses the DirectColorModel
     * @param encodeAlpha Encode the alpha channel? false = no; true = yes
     */
    protected ImageEncoder(Bitmap image, boolean encodeAlpha)
    {
    	this(image, encodeAlpha, 0);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, and whether to encode alpha.
     * @param image A Java Image object which uses the DirectColorModel
     * @param encodeAlpha Encode the alpha channel? false = no; true = yes
     * @param compressionLevel The compression level of the image encoder.
     */
    protected ImageEncoder(Bitmap image, boolean encodeAlpha, int compressionLevel)
    {
    	this.image = image;
    	this.encodeAlpha = encodeAlpha;
    	this.compressionLevel = compressionLevel;
    	this.scaleX = 1;
    	this.scaleY = 1;
    }
	
    /**
	 * Encode an image in a specific format.
	 * @param encodeAlpha True if alpha should be encoded.
	 * @return The encoded data.
	 * @throws IOException If an error occurs.
	 */
	public final byte[] encode(boolean encodeAlpha) throws IOException
	{
		if (image == null)
        {
            return null;
        }
        width = image.getWidth();
        height = image.getHeight();
        return inEncode(encodeAlpha);
	}
    
	/**
	 * Encode an image in a specific format.
	 * @param encodeAlpha True if alpha should be encoded.
	 * @return The encoded data.
	 * @throws IOException If an error occurs.
	 */
	protected abstract byte[] inEncode(boolean encodeAlpha) throws IOException;
	
	/**
     * Set the image to be encoded.
     * @param image A Java Image object which uses the DirectColorModel.
     */
    public final void setImage(Bitmap image)
    {
        this.image = image;
        dataBytes = null;
    }
    
    /**
     * Set the alpha encoding on or off.
     * @param encodeAlpha  false = no, true = yes
     */
    public final void setEncodeAlpha(boolean encodeAlpha)
    {
        this.encodeAlpha = encodeAlpha;
    }
    
    /**
     * Retrieve alpha encoding status.
     * @return boolean false = no, true = yes
     */
    public final boolean getEncodeAlpha()
    {
        return encodeAlpha;
    }
	
	/**
	 * Encode an image in a specific format.
	 * @return The encoded data.
	 * @throws IOException If an error occurs.
	 */
	public final byte[] encode() throws IOException
	{
		return encode(encodeAlpha);
	}
	
	/**
     * Retrieve compression level.
     * @return int representing the compression level.
     */
    public final int getCompressionLevel()
    {
        return compressionLevel;
    }
    
    /**
     * Set the compression level to use.
     * @param The compression level to set.
     */
    public void setCompressionLevel(int level)
    {
    	this.compressionLevel = level;
    }
    
    /**
     * Get the ARGB data of the image in a int array.
     * @param offset The offset into the array to save the data.
     * @param stride The stride to save the image, should be at least the width of the image.
     * @param source The source size of the image.
     * @return The array of ARGB data.
     */
    protected int[] getARGBInt(int offset, int stride, XYRect source)
    {
    	if(image == null)
    	{
    		return null;
    	}
    	//Get the last index and add 1 to get the length since the index is zero based.
    	int[] data = new int[getIndex(source.x, source.y, source.width, source.height, offset) + 1];
    	image.getARGB(data, offset, stride, source.x, source.y, source.width, source.height);
    	return data;
    }
    
    /**
     * Get index into the int array in {@link ImageEncoder#getARGBInt(int, int, XYRect)}.
     * @param x The X location of the image.
     * @param y The Y location of the image.
     * @param offset The offset for the array.
     * @return The zero based index into the int array.
     * @see ImageEncoder#getARGBInt(int, int, XYRect)
     * @see ImageEncoder#getIndex(int, int, int, int, int)
     */
    protected int getIndex(int x, int y, int offset)
    {
    	return getIndex(x, y, width, height, offset);
    }
    
    /**
     * Get index into the int array in {@link ImageEncoder#getARGBInt(int, int, XYRect)}.
     * @param x The X location of the image.
     * @param y The Y location of the image.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param offset The offset for the array.
     * @return The zero based index into the int array.
     * @see ImageEncoder#getARGBInt(int, int, XYRect)
     * @see ImageEncoder#getIndex(int, int, int)
     */
    protected static int getIndex(int x, int y, int width, int height, int offset)
    {
    	return (offset + ((width - 1) - x) + ((height - 1) - y) * width);
    }
    
    /**
     * Get the ARGB data of the image in a byte array.
     * @param offset The offset into the array to save the data.
     * @param stride The stride to save the image, should be at least the width of the image.
     * @param source The source size of the image.
     * @return The array of ARGB data.
     */
    protected byte[] getARGBByte(int offset, int stride, XYRect source)
    {
    	int[] data = getARGBInt(offset, stride, source);
    	if(data == null)
    	{
    		return null;
    	}
    	byte[] bdata = new byte[data.length * 4];
    	int tLength = bdata.length;
        int ttL = 0;
        for(int i = 0; i < tLength; i++)
        {
        	int off = i % 4;
        	int mask = 0;
        	switch(off)
        	{
        		case 0:
        			mask = 0xFF000000;
        			off = 24;
        			break;
        		case 1:
        			mask = 0x00FF0000;
        			off = 16;
        			break;
        		case 2:
        			mask = 0x0000FF00;
        			off = 8;
        			break;
        		case 3:
        			mask = 0x000000FF;
        			off = 0;
        			break;
        	}
        	bdata[i] = (byte)((data[ttL] & mask) >> off);
        	if(off == 0)
        	{
        		ttL++;
        	}
        }
        return bdata;
    }
}
