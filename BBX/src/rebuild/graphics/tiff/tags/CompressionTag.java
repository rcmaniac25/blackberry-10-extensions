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

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.graphics.tiff.Tag;

/**
 * Compression scheme used on the image data.
 */
public final class CompressionTag extends ShortTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 259;
	}
	
	/**
	 * No compression, but pack data into bytes as tightly as possible, leaving no unused bits (except at the end of a row).
	 */
	public static final short NO_COMPRESSION = 1;
	/**
	 * CCITT Group 3 1-Dimensional Modified Huffman run length encoding.
	 */
	public static final short CCITT_RLE = 2;
	/**
	 * CCITT Group 3 fax encoding
	 */
	public static final short CCITT_FAX3 = 3;
	/**
	 * CCITT Group 4 fax encoding
	 */
	public static final short CCITT_FAX4 = 4;
	/**
	 * LZW compression.
	 */
	public static final short LZW = 5;
	/**
	 * The original, not that great, JPEG compression.
	 */
	public static final short JPEG_OLD = 6;
	/**
	 * The current JPEG compression.
	 */
	public static final short JPEG_NEW = 7;
	/**
	 * Adobe's Deflate algorithm.
	 */
	public static final short DEFLATE_ADOBE = 8;
	/**
	 * PackBits compression, aka Macintosh RLE.
	 */
	public static final short PACKBITS = (short)32773;
	
	/**
	 * Create a new Compression tag.
	 */
	public CompressionTag()
	{
		this(NO_COMPRESSION);
	}
	
	/**
	 * Create a new Compression tag.
	 * @param compression The compression value to use.
	 */
	public CompressionTag(short compression)
	{
		super(getTagTypeValue(), compression);
		if(compression < 0)
		{
			throw new IllegalArgumentException("compression < 0");
		}
		switch(compression)
		{
			case NO_COMPRESSION:
			case CCITT_RLE:
			case CCITT_FAX3:
			case CCITT_FAX4:
			case LZW:
			case JPEG_OLD:
			case JPEG_NEW:
			case DEFLATE_ADOBE:
			case PACKBITS:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_COMPRESSION));
		}
	}
	
	/**
	 * There is always only 1 element.
	 * @see setCount
	 */
	public void setCount(int count)
	{
	}
	
	/**
	 * Set the compression value to use.
	 * @param compression The compression to use.
	 */
	public void setValue(int compression)
	{
		if(compression < 0)
		{
			throw new IllegalArgumentException("compression < 0");
		}
		switch(compression)
		{
			case NO_COMPRESSION:
			case CCITT_RLE:
			case CCITT_FAX3:
			case CCITT_FAX4:
			case LZW:
			case JPEG_OLD:
			case JPEG_NEW:
			case DEFLATE_ADOBE:
			case PACKBITS:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_COMPRESSION));
		}
		super.setShort(0, (short)compression);
	}
	
	/**
	 * Get the current compression value.
	 * @return The compression that this tag is set to.
	 */
	public short getCompression()
	{
		return super.getShort(0);
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
		return getCompression() == NO_COMPRESSION;
	}
}
