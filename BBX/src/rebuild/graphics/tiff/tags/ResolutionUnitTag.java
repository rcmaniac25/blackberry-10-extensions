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

/**
 * The unit of measurement for XResolution and YResolution.
 * @since BBX 1.0.1
 */
public final class ResolutionUnitTag extends ShortTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 296;
	}
	
	/**
	 * No absolute unit of measurement. Used for images that may have a non-square aspect ratio, but no meaningful absolute dimensions.
	 */
	public static final short NONE = 1;
	/**
	 * Inch
	 */
	public static final short INCH = 2;
	/**
	 * Centimeter
	 */
	public static final short CENTIMETER = 3;
	
	/**
	 * Create a new ResolutionUnit tag.
	 */
	public ResolutionUnitTag()
	{
		this(INCH);
	}
	
	/**
	 * Create a new ResolutionUnit tag.
	 * @param type The type of resolution unit.
	 */
	public ResolutionUnitTag(short type)
	{
		super(getTagTypeValue(), type);
		if(type < NONE || type > CENTIMETER)
		{
			throw new IllegalArgumentException("type < NONE || type > CENTIMETER");
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
	 * Set the type of resolution unit to use.
	 * @param type The resolution unit to use.
	 */
	public void setValue(int type)
	{
		if(type < NONE || type > CENTIMETER)
		{
			throw new IllegalArgumentException("type < NONE || type > CENTIMETER");
		}
		super.setShort(0, (short)type);
	}
	
	/**
	 * Get the resolution unit used.
	 * @return The resolution unit used.
	 */
	public short getResolutionUnit()
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
		return getResolutionUnit() == INCH;
	}
}
