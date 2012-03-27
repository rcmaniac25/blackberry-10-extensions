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
 * The number of components per pixel.
 */
public final class SamplesPerPixelTag extends ShortTag
{
	private static final short DEFAULT = 1;
	
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 277;
	}
	
	/**
	 * Create a new SamplesPerPixel tag.
	 */
	public SamplesPerPixelTag()
	{
		this(DEFAULT);
	}
	
	/**
	 * Create a new SamplesPerPixel tag.
	 * @param samples The number of components per pixel.
	 */
	public SamplesPerPixelTag(short samples)
	{
		super(getTagTypeValue(), samples);
		if(samples <= 0)
		{
			throw new IllegalArgumentException("samples <= 0");
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
	 * Set the samples per pixel to use.
	 * @param samples The samples per pixel to use.
	 */
	public void setValue(int samples)
	{
		if(samples <= 0)
		{
			throw new IllegalArgumentException("samples <= 0");
		}
		super.setShort(0, (short)samples);
	}
	
	/**
	 * Get the current number of samples.
	 * @return The number of samples that this tag is set to.
	 */
	public short getSamples()
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
		return getSamples() == DEFAULT;
	}
}
