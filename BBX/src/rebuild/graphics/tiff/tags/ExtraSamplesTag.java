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
 * Description of extra components.
 * @since BBX 1.0.1
 */
public final class ExtraSamplesTag extends ShortTag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 338;
	}
	
	/**
	 * Unspecified data
	 */
	public static final short UNSPECIFIED = 0;
	/**
	 * Associated alpha data (with pre-multiplied color)
	 */
	public static final short ASSOCALPHA = 1;
	/**
	 * Unassociated alpha data
	 */
	public static final short UNASSALPHA = 2;
	
	/**
	 * Create a new ExtraSamples tag.
	 * @param samples A single extra sample, what type of sample it represents.
	 */
	public ExtraSamplesTag(short sample)
	{
		this(new short[] { sample });
	}
	
	/**
	 * Create a new ExtraSamples tag.
	 * @param samples Multiple extra samples, what type of samples it represents.
	 */
	public ExtraSamplesTag(short[] samples)
	{
		super(getTagTypeValue(), samples);
		int c = samples.length;
		for(int i = 0; i < c; i++)
		{
			if(samples[i] < UNSPECIFIED || samples[i] > UNASSALPHA)
			{
				throw new IllegalArgumentException("samples[i] < UNSPECIFIED || samples[i] > UNASSALPHA, i=" + i);
			}
		}
	}
	
	/**
	 * Count determined can be changed with {@link addSample}/{@link removeSample}.
	 * @see setCount
	 */
	public void setCount(int count)
	{
	}
	
	/**
	 * Set the first sample value in the ExtraSamples tag.
	 * @param sample The value to set the sample to.
	 */
	public void setValue(int sample)
	{
		short val = (short)sample;
		if(val < UNSPECIFIED || val > UNASSALPHA)
		{
			throw new IllegalArgumentException("sample < UNSPECIFIED || sample > UNASSALPHA");
		}
		this.setSample(0, val);
	}
	
	/**
	 * Add a sample to the {@link ExtraSamplesTag}.
	 * @param sample The sample type to add.
	 */
	public void addSample(short sample)
	{
		if(sample < UNSPECIFIED || sample > UNASSALPHA)
		{
			throw new IllegalArgumentException("sample < UNSPECIFIED || sample > UNASSALPHA");
		}
		int pos = super.getCount();
		super.setCount(pos + 1);
		super.setShort(pos, sample);
	}
	
	/**
	 * Remove a sample at the specified index.
	 * @param index The index to remove.
	 */
	public void removeSample(int index)
	{
		//To make sure that index is with'in range.
		super.getShort(index);
		//See if I can take the simple way out or if I need to do work.
		int count = super.getCount() - 1;
		short[] data = null;
		if(index != count)
		{
			//Very messy and memory intensive, hopefully it never really gets here.
			int k = 0;
			data = new short[count];
			for(int i = 0; i < count + 1; i++)
			{
				if(i != index)
				{
					data[k++] = super.getShort(i);
				}
			}
		}
		super.setCount(count);
		if(index != count)
		{
			for(int i = 0; i < count; i++)
			{
				super.setShort(i, data[i]);
			}
		}
	}
	
	/**
	 * Get a sample at the specified index.
	 * @param index The index to get the sample.
	 * @return The sample at that index.
	 */
	public short getSample(int index)
	{
		return super.getShort(index);
	}
	
	/**
	 * Set a sample at the specified index.
	 * @param index The index to set the sample.
	 * @param sample The extra sample to add.
	 */
	public void setSample(int index, short sample)
	{
		if(sample < UNSPECIFIED || sample > UNASSALPHA)
		{
			throw new IllegalArgumentException("sample < UNSPECIFIED || sample > UNASSALPHA");
		}
		super.setShort(index, sample);
	}
}
