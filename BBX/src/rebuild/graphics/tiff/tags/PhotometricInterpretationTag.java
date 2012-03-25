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

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.graphics.tiff.Tag;

/**
 * The color space of the image data.
 */
public final class PhotometricInterpretationTag extends ShortTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 262;
	}
	
	/**
	 * For bilevel and grayscale images: 0 is imaged as white.
	 */
	public static final short WHITE_IS_ZERO = 0;
	/**
	 * For bilevel and grayscale images: 0 is imaged as black.
	 */
	public static final short BLACK_IS_ZERO = 1;
	/**
	 * RGB value of (0,0,0) represents black, and (255,255,255) represents white, assuming 8-bit components. The 
	 * components are stored in the indicated order: first Red, then Green, then Blue.
	 */
	public static final short RGB = 2;
	/**
	 * In this model, a color is described with a single component. The value of the component is used as an index 
	 * into the red, green and blue curves in the ColorMap field to retrieve an RGB triplet that defines the color. 
	 * When PhotometricInterpretation=PALETTE is used, ColorMap must be present and SamplesPerPixel must be 1.
	 */
	public static final short PALETTE = 3;
	/**
	 * This means that the image is used to define an irregularly shaped region of another image in the same TIFF 
	 * file. SamplesPerPixel and BitsPerSample must be 1. PackBits compression is recommended. The 1-bits define the 
	 * interior of the region; the 0-bits define the exterior of the region.
	 */
	public static final short MASK = 4;
	/**
	 * Separated, usually CMYK.
	 */
	public static final short SEPARATED = 5;
	/**
	 * YCbCr
	 */
	public static final short YCBCR = 6;
	/**
	 * CIE L*a*b* (see also specification supplements 1 and 2)
	 */
	public static final short CIELAB = 8;
	/**
	 * CIE L*a*b*, alternate encoding also known as ICC L*a*b* (see also specification supplements 1 and 2)
	 */
	public static final short ICCLAB = 9;
	/**
	 * CIE L*a*b*, alternate encoding also known as ITU L*a*b*, defined in ITU-T Rec. T.42, used in the TIFF-F and 
	 * TIFF-FX standard (RFC 2301). The Decode tag, if present, holds information about this particular CIE L*a*b* 
	 * encoding.
	 */
	public static final short ITULAB = 10;
	/**
	 * CFA (Color Filter Array)
	 */
	public static final short CFA = (short)32803;
	/**
	 * LinearRaw
	 */
	public static final short LINEAR_RAW = (short)34892;
	
	/**
	 * Create a new PhotometricInterpretation tag.
	 * @param type The type of image.
	 */
	public PhotometricInterpretationTag(short type)
	{
		super(getTagTypeValue(), type);
		if(type < 0 && (type != CFA || type != LINEAR_RAW))
		{
			throw new IllegalArgumentException("type < 0");
		}
		switch(type)
		{
			case WHITE_IS_ZERO:
			case BLACK_IS_ZERO:
			case RGB:
			case PALETTE:
			case MASK:
			case SEPARATED:
			case YCBCR:
			case CIELAB:
			case ICCLAB:
			case ITULAB:
			case CFA:
			case LINEAR_RAW:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_PHOTOMETRIC));
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
	 * Set the image type.
	 * @param type The image type.
	 */
	public void setValue(int type)
	{
		if(type < 0 && (type != CFA || type != LINEAR_RAW))
		{
			throw new IllegalArgumentException("type < 0");
		}
		switch(type)
		{
			case WHITE_IS_ZERO:
			case BLACK_IS_ZERO:
			case RGB:
			case PALETTE:
			case MASK:
			case SEPARATED:
			case YCBCR:
			case CIELAB:
			case ICCLAB:
			case ITULAB:
			case CFA:
			case LINEAR_RAW:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_PHOTOMETRIC));
		}
		super.setShort(0, (short)type);
	}
	
	/**
	 * Get the image type value.
	 * @return The image type that this tag is set to.
	 */
	public short getImageType()
	{
		return super.getShort(0);
	}
	
	/**
	 * Get a {@link SamplesPerPixelTag} for the current photometric.
	 * @return The {@link SamplesPerPixelTag}, if this is <code>null</code> than it is a variable number of components.
	 */
	public SamplesPerPixelTag getSamplesTag()
	{
		int count = -1;
		switch(super.getShort(0))
		{
			case WHITE_IS_ZERO:
			case BLACK_IS_ZERO:
			case PALETTE:
			case MASK:
				count = 1;
				break;
			case YCBCR:
			case RGB:
				count = 3;
				break;
			case SEPARATED:
				count = 4;
				break;
			case CIELAB:
			case ICCLAB:
			case ITULAB:
			case CFA:
			case LINEAR_RAW:
				//Doesn't seem to actually have a definite definition of how many components exist.
				break;
		}
		if(count == -1)
		{
			return null;
		}
		else
		{
			return new SamplesPerPixelTag((short)count);
		}
	}
}
