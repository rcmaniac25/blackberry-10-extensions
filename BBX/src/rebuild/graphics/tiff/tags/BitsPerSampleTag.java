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
 * The number of bits per component in the image.
 * @since BBX 1.0.1
 */
public final class BitsPerSampleTag extends ShortTag
{
	private static final short DEFAULT = 1;
	
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 258;
	}
	
	/**
	 * Create a new BitsPerSample tag.
	 */
	public BitsPerSampleTag()
	{
		this(/*new SamplesPerPixelTag().getSamples()--default is*/1, DEFAULT);
	}
	
	/**
	 * Create a new BitsPerSample tag.
	 * @param numberOfSamples The number of samples that the image has. Each sample will be set to 8 for 8 bits per sample.
	 */
	public BitsPerSampleTag(int numberOfSamples)
	{
		this(numberOfSamples, (short)8);
	}
	
	/**
	 * Create a new BitsPerSample tag.
	 * @param numberOfSamples The number of samples that the image has.
	 * @param value The value of all the samples. If this is less than 1 bit it could make the image unreaable or cause errors.
	 */
	public BitsPerSampleTag(int numberOfSamples, short value)
	{
		this(new short[numberOfSamples]);
		for(int i = 0; i < numberOfSamples; i++)
		{
			super.setShort(i, value);
		}
	}
	
	/**
	 * Create a new BitsPerSample tag.
	 * @param samples The value of each sample.
	 */
	public BitsPerSampleTag(short[] samples)
	{
		super(getTagTypeValue(), samples);
	}
	
	/**
	 * Set the bit count for all the samples.
	 * @param bitCount The number of bits in each sample.
	 */
	public void setValue(int bitCount)
	{
		int count = super.getCount();
		short bit = (short)bitCount;
		for(int i = 0; i < count; i++)
		{
			super.setShort(i, bit);
		}
	}
	
	/**
	 * Set the sample value at the specified index.
	 * @param index The index of the sample.
	 * @param value The value to set at the specified index.
	 */
	public void setSample(int index, short value)
	{
		super.setShort(index, value);
	}
	
	/**
	 * Get the sample value at the specified index.
	 * @param index The index of the sample.
	 * @return Return the value at the specified index.
	 */
	public short getSample(int index)
	{
		return super.getShort(index);
	}
	
	/**
	 * Set the number of samples that the BitsPerSample tag has.
	 * @param numberOfSamples The number of samples; Usually 1 for bilevel, grayscale, and palette-color images, 3 for RGB. The value is based off of SamplesPerPixel.
	 */
	public void setCount(int numberOfSamples)
	{
		super.setCount(numberOfSamples);
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
		if(super.getCount() == 1)
		{
			return super.getShort(0) == DEFAULT;
		}
		return false;
	}
}
