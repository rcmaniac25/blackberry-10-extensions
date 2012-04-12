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
 * A tag that contains one or more {@link Writer.LONG} items.
 * @since BBX 1.0.1
 */
public class LongTag extends Tag
{
	/**
	 * Create a new {@link LongTag}.
	 * @param tag The tag value.
	 */
	public LongTag(short tag)
	{
		this(tag, 0);
	}
	
	/**
	 * Create a new {@link LongTag}.
	 * @param tag The tag value.
	 * @param val The value the tag should store.
	 */
	public LongTag(short tag, int val)
	{
		this(tag, new int[]{ val });
	}
	
	/**
	 * Create a new {@link LongTag}.
	 * @param tag The tag value.
	 * @param val The values the tag should store.
	 */
	public LongTag(short tag, int[] val)
	{
		this(tag, val, Writer.LONG);
	}
	
	/**
	 * Create a new {@link LongTag}.
	 * @param tag The tag value.
	 * @param val The values the tag should store.
	 */
	protected LongTag(short tag, int[] val, short type)
	{
		super(tag, type, val.length, 0, val);
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
	 * Data type is a {@link Writer.LONG}.
	 * @see setDataType
	 */
	public final void setDataType(short dataType)
	{
	}
	
	/**
	 * Set the number of {@link Writer.LONG} contained in this tag.
	 * @param count The number of {@link Writer.LONG} contained in the tag. If the number is bigger than the current count then it will add 0 {@link Writer.LONG}, else it will remove the {@link Writer.LONG}.
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
			int[] dat = (int[])super.getExtraData();
			int[] nDat = null;
			if(count > super.count)
			{
				nDat = new int[count];
				int ccount = (int)super.count;
				System.arraycopy(dat, 0, nDat, 0, ccount);
			}
			else
			{
				nDat = net.rim.device.api.util.Arrays.copy(dat, 0, count);
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
	 * Get a {@link Writer.LONG} at the specified index.
	 * @param index The index to get the {@link Writer.LONG}.
	 * @return The {@link Writer.LONG} at the specified index.
	 */
	public final int getLong(int index)
	{
		return ((int[])super.getExtraData())[index];
	}
	
	/**
	 * Set a {@link Writer.LONG} at the specified index.
	 * @param index The index to set the {@link Writer.LONG}.
	 * @param val The {@link Writer.LONG} to set at the specified index.
	 */
	public final void setLong(int index, int val)
	{
		((int[])super.getExtraData())[index] = val;
		setupValue(false);
	}
	
	private void setupValue(boolean big)
	{
		super.value = 0L;
		int[] dat = (int[])super.getExtraData();
		int ccount = (int)super.count;
		int max = big ? (ccount > 2 ? -1 : ccount) : (ccount > 1 ? -1 : ccount);
		for(int i = 0; i < max; i++)
		{
			super.value |= Writer.shiftValue(dat[i], i);
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
			return super.count > 2;
		}
		else
		{
			return super.count > 1;
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
		int[] dat = (int[])super.getExtraData();
		for(int i = 0; i < super.count; i++)
		{
			wr.writeUInt(dat[i]);
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
