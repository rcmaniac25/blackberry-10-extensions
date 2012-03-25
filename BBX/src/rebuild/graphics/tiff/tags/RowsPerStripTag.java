//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2011-2012 Vincent Simonetti
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
 * The number of rows per strip.
 */
public final class RowsPerStripTag extends Tag
{
	private static final int DEFAULT = 0xFFFFFFFF; //(2^32)-1
	
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 278;
	}
	
	/**
	 * Set the number of rows-per-strip. A "row" is the width of an image. A "strip" is a buffering method so that the
	 * TIFF reader can buffer the image in a easier manner instead of reading in the entire image at once.
	 * The default is <code>(2<sup>32</sup>) - 1</code>, which is effectively infinity.
	 */
	public RowsPerStripTag()
	{
		this(DEFAULT);
	}
	
	/**
	 * Set the number of rows-per-strip. A "row" is the width of an image. A "strip" is a buffering method so that the
	 * TIFF reader can buffer the image in a easier manner instead of reading in the entire image at once.
	 * @param rows The number of rows-per-strip.
	 */
	public RowsPerStripTag(short rows)
	{
		this(rows, false);
	}
	
	/**
	 * Set the number of rows-per-strip. A "row" is the width of an image. A "strip" is a buffering method so that the
	 * TIFF reader can buffer the image in a easier manner instead of reading in the entire image at once.
	 * @param rows The number of rows-per-strip.
	 */
	public RowsPerStripTag(int rows)
	{
		this(rows, rows > Short.MAX_VALUE);
	}
	
	private RowsPerStripTag(int rows, boolean imageGreaterThen64k)
	{
		super(getTagTypeValue(), (imageGreaterThen64k ? Writer.LONG : Writer.SHORT), 1, 
				imageGreaterThen64k ? rows : Writer.shiftValue((short)rows, 0), null);
		//TODO: Make sure that if the high-order bit is set that it still takes it like a unsigned int.
		if(rows < 1)
		{
			throw new java.lang.IllegalArgumentException("rows < 1");
		}
	}
	
	/**
	 * Set the left-justified rows-per-strip that the tag contains.
	 * @param rows The rows-per-strip that the tag should contains.
	 * @see getValue
	 */
	public void setValue(int rows)
	{
		//TODO: Make sure that if the high-order bit is set that it still takes it like a unsigned int.
		if(rows < 1)
		{
			throw new java.lang.IllegalArgumentException("rows < 1");
		}
		boolean big = rows > Short.MAX_VALUE;
		this.value = (big ? rows : Writer.shiftValue((short)rows, 0));
		this.dataType = (big ? Writer.LONG : Writer.SHORT);
	}
	
	/**
	 * Get the rows-per-strip defined in this tag.
	 * @return The rows-per-strip.
	 */
	public int getRowsPerStrip()
	{
		return this.dataType == Writer.LONG ? (int)(this.value & 0x00000000FFFFFFFFL) : Writer.unshiftShort(this.value, 0);
	}
	
	/**
	 * Get the number of strips-per-image.
	 * @param imageLength The image length.
	 * @return The number of strips-per-image.
	 * @see ImageLengthTag
	 */
	public int getStripsPerImage(int imageLength)
	{
		int irows = getRowsPerStrip();
		//TODO: Figure out a better way to do this
		double rows = (double)Long.parseLong(Integer.toHexString(irows), 16);
		return (int)Math.floor((imageLength + rows - 1) / rows);
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
		return true;
	}
	
	/**
	 * Get if this {@link Tag}'s current value is the default value. This is ignored if {@link Tag#hasDefault()} returns <code>false</code>.
	 * @return <code>true</code> if the {@link Tag}'s value is the default value, <code>false</code> if otherwise.
	 */
	public boolean isDefault()
	{
		return getRowsPerStrip() == DEFAULT;
	}
}
