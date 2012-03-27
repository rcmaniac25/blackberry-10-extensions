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
 * How the components of each pixel are stored.
 */
public final class PlanarConfigurationTag extends ShortTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 284;
	}
	
	/**
	 * The component values for each pixel are stored contiguously. For example, for RGB data, the data is stored as 
	 * RGBRGBRGB.
	 */
	public static final short CHUNKY = 1;
	/**
	 * The components are stored in separate component planes. For example, RGB data is stored with the Red components 
	 * in one component plane, the Green in another, and the Blue in another.
	 */
	public static final short PLANAR = 2;
	
	/**
	 * Create a new PlanarConfiguration tag.
	 */
	public PlanarConfigurationTag()
	{
		this(CHUNKY);
	}
	
	/**
	 * Create a new PlanarConfiguration tag.
	 * @param format The format value to use.
	 */
	public PlanarConfigurationTag(short format)
	{
		super(getTagTypeValue(), format);
		switch(format)
		{
			case CHUNKY:
			case PLANAR:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_PLANAR_FORMAT));
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
	 * Set the planar format value to use.
	 * @param format The format to use.
	 */
	public void setValue(int format)
	{
		switch(format)
		{
			case CHUNKY:
			case PLANAR:
				break;
			default:
				throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_PLANAR_FORMAT));
		}
		super.setShort(0, (short)format);
	}
	
	/**
	 * Get the current planar format value.
	 * @return The planar format that this tag is set to.
	 */
	public short getPlanarFormat()
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
		return getPlanarFormat() == CHUNKY;
	}
}
