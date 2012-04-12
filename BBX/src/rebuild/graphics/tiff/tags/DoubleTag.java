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
 * A tag that contains one or more {@link Writer.DOUBLE} items.
 * @since BBX 1.0.1
 */
public class DoubleTag extends Tag
{
	/**
	 * Create a new {@link DoubleTag}.
	 * @param tag The tag value.
	 */
	public DoubleTag(short tag)
	{
		this(tag, 0.0);
	}
	
	/**
	 * Create a new {@link DoubleTag}.
	 * @param tag The tag value.
	 * @param val The value the tag should store.
	 */
	public DoubleTag(short tag, double val)
	{
		this(tag, new double[]{ val });
	}
	
	/**
	 * Create a new {@link DoubleTag}.
	 * @param tag The tag value.
	 * @param val The values the tag should store.
	 */
	public DoubleTag(short tag, double[] val)
	{
		super(tag, Writer.DOUBLE, val.length, 0, val);
		if(val == null)
		{
			throw new NullPointerException("val");
		}
		if(val.length == 0)
		{
			throw new IllegalArgumentException("val.length == 0");
		}
		setupValue(false);
	}
	
	/**
	 * Data type is a {@link Writer.DOUBLE}.
	 * @see setDataType
	 */
	public final void setDataType(short dataType)
	{
	}
	
	/**
	 * Set the number of {@link Writer.DOUBLE} contained in this tag.
	 * @param count The number of {@link Writer.DOUBLE} contained in the tag. If the number is bigger than the current count then it will add 0.0 {@link Writer.DOUBLE}, else it will remove the {@link Writer.DOUBLE}.
	 * @see getCount
	 */
	public void setCount(int count)
	{
		if(count <= 0)
		{
			throw new IllegalArgumentException("count <= 0");
		}
		if(count != super.count)
		{
			double[] dat = (double[])super.getExtraData();
			double[] nDat = new double[count];
			if(count > super.count)
			{
				int ccount = (int)super.count;
				System.arraycopy(dat, 0, nDat, 0, ccount);
			}
			else
			{
				System.arraycopy(dat, 0, nDat, 0, count);
			}
			super.setExtraData(nDat);
			super.count = count;
			setupValue(false);
		}
	}
	
	/**
	 * Unused.
	 * @see getExtraData
	 */
	protected final void setExtraData(Object obj)
	{
	}
	
	/**
	 * Get a {@link Writer.DOUBLE} at the specified index.
	 * @param index The index to get the {@link Writer.DOUBLE}.
	 * @return The {@link Writer.DOUBLE} at the specified index.
	 */
	public final double getDouble(int index)
	{
		return ((double[])super.getExtraData())[index];
	}
	
	/**
	 * Set a {@link Writer.DOUBLE} at the specified index.
	 * @param index The index to set the {@link Writer.DOUBLE}.
	 * @param val The {@link Writer.DOUBLE} to set at the specified index.
	 */
	public final void setDouble(int index, double val)
	{
		((double[])super.getExtraData())[index] = val;
		setupValue(false);
	}
	
	private void setupValue(boolean big)
	{
		super.value = 0L;
		if(big && super.count == 1L)
		{
			super.value = Double.doubleToLongBits(((double[])super.getExtraData())[0]);
		}
	}
	
	/**
	 * This method figures out if any extra data is exists, this method can search the data type, extra data, value, and count. It is called whenever one of these types change.
	 * @param big <code>true</code> if the tag is being written to a BigTIFF, <code>false</code> if otherwise.
	 * @return true if the tag contains extra data, false if otherwise.
	 */
	protected boolean extraData(boolean big)
	{
		if(big)
		{
			return super.count > 1;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Setup the tag for writing in a BigTIFF.
	 */
	protected void setupForBigTIFF(boolean set)
	{
		setupValue(set);
	}

	/**
	 * The extra data writer, if any extra data exists then this method will be called and (through internal methods) a offset to the data will be used in place of the <code>value</code> parameter.
	 * @param wr The writer used to write the extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public void writeData(Writer wr) throws IOException
	{
		double[] dat = (double[])super.getExtraData();
		for(int i = 0; i < super.count; i++)
		{
			wr.writeDouble(dat[i]);
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
	 * Get if this {@link Tag}'s current value is the default value. This is ignored if {@link Tag#hasDefault()} returns <code>false</code>.
	 * @return <code>true</code> if the {@link Tag}'s value is the default value, <code>false</code> if otherwise.
	 */
	public boolean isDefault()
	{
		return false;
	}
}
