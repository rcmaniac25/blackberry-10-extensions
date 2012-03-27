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
package rebuild.graphics.tiff.tags;

import java.io.IOException;

import rebuild.graphics.tiff.Tag;
import rebuild.graphics.tiff.Writer;

/**
 * The number of columns in the image, i.e., the number of pixels per scanline.
 */
public final class ImageWidthTag extends Tag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 256;
	}
	
	/**
	 * Create a new {@link ImageWidthTag}.
	 * @param width The width of the image.
	 */
	public ImageWidthTag(int width)
	{
		this(width, width > Short.MAX_VALUE);
	}
	
	private ImageWidthTag(int width, boolean imageGreaterThen64k)
	{
		super(getTagTypeValue(), (imageGreaterThen64k ? Writer.LONG : Writer.SHORT), 1, 
				imageGreaterThen64k ? width : Writer.shiftValue((short)width, 0), null);
		if(width < 0)
		{
			throw new IllegalArgumentException("width < 0");
		}
	}
	
	/**
	 * Set the left-justified width that the tag contains.
	 * @param value The width that the tag should contains.
	 * @see getValue
	 */
	public void setValue(int width)
	{
		boolean big = width > Short.MAX_VALUE;
		this.value = (big ? width : Writer.shiftValue((short)width, 0));
		this.dataType = ((short)(big ? Writer.LONG : Writer.SHORT));
	}
	
	/**
	 * Get the width defined in this tag.
	 * @return The width.
	 */
	public int getWidth()
	{
		return this.dataType == Writer.LONG ? (int)(this.value & 0x00000000FFFFFFFFL) : Writer.unshiftShort(this.value, 0);
	}
	
	/**
	 * Data type is determined by the value.
	 * @see setDataType
	 */
	public void setDataType(short dataType)
	{
	}
	
	/**
	 * There is always only 1 element.
	 * @see setCount
	 */
	public void setCount(int count)
	{
	}
	
	/**
	 * Unused.
	 */
	public void writeData(Writer wr) throws IOException
	{
	}
	
	/**
	 * No extra data, always returns false.
	 */
	protected boolean extraData(boolean big)
	{
		return false;
	}
	
	/**
	 * Setup the tag for writing in a BigTIFF.
	 */
	protected void setupForBigTIFF(boolean set)
	{
		if(set)
		{
			this.value <<= 32;
		}
		else
		{
			this.value >>= 32;
		}
	}
	
	/**
	 * Get if this {@link Tag} can have a default value, often defined by a parameter-less constructor.
	 * @return <code>true</code> if the {@link Tag} has a default value, <code>false</code> if otherwise. Default is <code>false</code>.
	 */
	public boolean hasDefault()
	{
		return false;
	}
	
	/**
	 * Unused.
	 */
	public boolean isDefault()
	{
		return false;
	}
}
