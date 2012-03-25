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

/**
 * The number of pixels per {@link ResolutionUnitTag} in the {@link ImageWidthTag} direction.
 */
public final class XResolutionTag extends RationalTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 282;
	}
	
	/**
	 * Create a new {@link XResolutionTag}.
	 * @param num The numerator, usually the image width but this may not work properly.
	 * @param den The denominator, usually the image unit (inch, cm, undefined) but this may not work properly.
	 */
	public XResolutionTag(int num, int den)
	{
		super(getTagTypeValue(), num, den);
	}
	
	/**
	 * Get the numerator.
	 * @return The numerator, usually the image width but this may not be true.
	 */
	public int getNumerator()
	{
		return super.getNumerator(0);
	}
	
	/**
	 * Get the denominator.
	 * @return The denominator, usually the image unit (inch, cm, undefined) but this may not be true.
	 */
	public int getDenominator()
	{
		return super.getDenominator(0);
	}
	
	/**
	 * Set the numerator.
	 * @param num The numerator, usually the image width but this may not work properly.
	 */
	public void setNumerator(int num)
	{
		super.setNumerator(0, num);
	}
	
	/**
	 * Set the denominator.
	 * @param den The denominator, usually the image unit (inch, cm, undefined) but this may not work properly.
	 */
	public void setDenominator(int den)
	{
		super.setDenominator(0, den);
	}
	
	/**
	 * There is always only 1 element.
	 * @see setCount
	 */
	public void setCount(int count)
	{
	}
	
	/**
	 * Unused
	 * @see setValue
	 */
	public void setValue(int value)
	{
	}
}
