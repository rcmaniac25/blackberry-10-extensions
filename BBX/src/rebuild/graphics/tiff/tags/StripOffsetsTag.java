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
package rebuild.graphics.tiff.tags;

import java.io.IOException;

import rebuild.graphics.tiff.Tag;
import rebuild.graphics.tiff.Writer;

/**
 * For each strip, the byte offset of that strip.
 */
public final class StripOffsetsTag extends Tag
{
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 273;
	}
	
	/**
	 * Create a new StripOffsets tag.
	 * @param stripsPerImage The number of strips per image. Must be at least 1.
	 * @param planarConfig The planar config to determine the number of strip planes needed.
	 * @param sampleCount The number of samples per pixel.
	 */
	public StripOffsetsTag(int stripsPerImage, short planarConfig, short sampleCount)
	{
		super(getTagTypeValue(), Writer.SHORT, 1, 0, null);
		setCount(stripsPerImage, planarConfig, sampleCount);
	}
	
	/**
	 * Unused.
	 * @see getValue
	 */
	public void setValue(int value)
	{
	}
	
	/**
	 * Data type is determined by the value.
	 * @see setDataType
	 */
	public void setDataType(short dataType)
	{
	}
	
	/**
	 * Unused, use {@link StripOffsetsTag#setCount(int, short, short)} instead.
	 * @see {@link Tag#setCount(int)}
	 */
	public void setCount(int count)
	{
		setCount((long)count);
	}
	
	/**
	 * Unused, use {@link StripOffsetsTag#setCount(int, short, short)} instead.
	 * @see {@link Tag#setCount(long)}
	 */
	public void setCount(long count)
	{
		if(count <= 0)
		{
			throw new IllegalArgumentException("count <= 0");
		}
		Object obj = super.getExtraData();
		int c = (int)count;
		if(obj == null)
		{
			switch(super.dataType)
			{
				case Writer.SHORT:
					super.setExtraData(new short[c]);
					break;
				case Writer.LONG:
					super.setExtraData(new int[c]);
					break;
				case Writer.LONG8:
					super.setExtraData(new long[c]);
					break;
			}
		}
		else
		{
			if(count != super.count)
			{
				if(count > super.count)
				{
					switch(super.dataType)
					{
						case Writer.SHORT:
							short[] sDat = new short[c];
							System.arraycopy((short[])super.getExtraData(), 0, sDat, 0, c);
							super.setExtraData(sDat);
							break;
						case Writer.LONG:
							int[] iDat = new int[c];
							System.arraycopy((int[])super.getExtraData(), 0, iDat, 0, c);
							super.setExtraData(iDat);
							break;
						case Writer.LONG8:
							long[] lDat = new long[c];
							System.arraycopy((long[])super.getExtraData(), 0, lDat, 0, c);
							super.setExtraData(lDat);
							break;
					}
				}
				else
				{
					switch(super.dataType)
					{
						case Writer.SHORT:
							super.setExtraData(net.rim.device.api.util.Arrays.copy((short[])super.getExtraData(), 0, c));
							break;
						case Writer.LONG:
							super.setExtraData(net.rim.device.api.util.Arrays.copy((int[])super.getExtraData(), 0, c));
							break;
						case Writer.LONG8:
							super.setExtraData(net.rim.device.api.util.Arrays.copy((long[])super.getExtraData(), 0, c));
							break;
					}
				}
			}
		}
		super.count = count;
	}
	
	private void changeValueTypes(short dataType)
	{
		long[] holding = null;
		//Get the data
		switch(super.dataType)
		{
			case Writer.SHORT:
				holding = new long[(int)super.count];
				short[] sh = (short[])super.getExtraData();
				for(int i = 0; i < super.count; i++)
				{
					holding[i] = sh[i];
				}
				break;
			case Writer.LONG:
				holding = new long[(int)super.count];
				int[] in = (int[])super.getExtraData();
				for(int i = 0; i < super.count; i++)
				{
					holding[i] = in[i];
				}
				break;
			case Writer.LONG8:
				holding = (long[])super.getExtraData();
				break;
		}
		super.dataType = dataType;
		//Set the data
		switch(super.dataType)
		{
			case Writer.SHORT:
				short[] sh = new short[(int)super.count];
				for(int i = 0; i < super.count; i++)
				{
					sh[i] = (short)holding[i];
				}
				super.setExtraData(sh);
				break;
			case Writer.LONG:
				int[] in = new int[(int)super.count];
				for(int i = 0; i < super.count; i++)
				{
					in[i] = (int)holding[i];
				}
				super.setExtraData(in);
				break;
			case Writer.LONG8:
				super.setExtraData(holding);
				break;
		}
	}
	
	/**
	 * Set the number of strip items.
	 * @param stripsPerImage The number of strips per image. Must be at least 1.
	 * @param planarConfig The planar config to determine the number of strip planes needed.
	 * @param sampleCount The number of samples per pixel.
	 */
	public void setCount(int stripsPerImage, short planarConfig, short sampleCount)
	{
		if(stripsPerImage < 1)
		{
			throw new IllegalArgumentException("stripsPerImage < 1");
		}
		new PlanarConfigurationTag(planarConfig); //Test to make sure that it uses a valid value.
		if(sampleCount <= 0)
		{
			throw new IllegalArgumentException("sampleCount <= 0");
		}
		this.setCount(stripsPerImage * (planarConfig == PlanarConfigurationTag.CHUNKY ? 1 : sampleCount));
	}
	
	/**
	 * The extra data writer, if any extra data exists then this method will be called and (through internal methods) a offset to the data will be used in place of the <code>value</code> parameter.
	 * @param wr The writer used to write the extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public void writeData(Writer wr) throws IOException
	{
		switch(super.dataType)
		{
			case Writer.SHORT:
				short[] sC = (short[])super.getExtraData();
				for(int i = 0; i < super.count; i++)
				{
					wr.writeUShort(sC[i]);
				}
				break;
			case Writer.LONG:
				int[] iC = (int[])super.getExtraData();
				for(int i = 0; i < super.count; i++)
				{
					wr.writeUInt(iC[i]);
				}
				break;
			case Writer.LONG8:
				long[] lC = (long[])super.getExtraData();
				for(int i = 0; i < super.count; i++)
				{
					wr.writeULong(lC[i]);
				}
				break;
		}
	}
	
	/**
	 * This method figures out if any extra data is exists, this method can search the data type, extra data, value, and count. It is called whenever one of these types change.
	 * @param big <code>true</code> if the tag is being written to a BigTIFF, <code>false</code> if otherwise.
	 * @return true if the tag contains extra data, false if otherwise.
	 */
	protected boolean extraData(boolean big)
	{
		if(super.dataType == Writer.LONG8)
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
		else if(super.dataType == Writer.LONG)
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
		else
		{
			if(big)
			{
				return super.count > 4;
			}
			else
			{
				return super.count > 2;
			}
		}
	}
	
	/**
	 * Setup the tag for writing in a BigTIFF.
	 */
	protected void setupForBigTIFF(boolean set)
	{
		if(super.dataType == Writer.LONG8 && set)
		{
			System.out.println("Data type not supported, will convert to LONG but might cause issues.");
		}
		if(!extraData(set)) //If it had extra data then this would not be needed
		{
			switch(super.dataType)
			{
				case Writer.SHORT:
					//Hack since the code for this is written already
					ShortTag stag = new ShortTag((short)0, (short[])super.getExtraData());
					stag.setupForBigTIFF(set);
					this.value = stag.getBigValue();
					break;
				case Writer.LONG:
					//Hack since the code for this is written already
					LongTag ltag = new LongTag((short)0, (int[])super.getExtraData());
					ltag.setupForBigTIFF(set);
					this.value = ltag.getBigValue();
					break;
				case Writer.LONG8:
					if(set)
					{
						this.value = ((long[])super.getExtraData())[0];
					}
					break;
			}
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
	 * Unused.
	 */
	public boolean isDefault()
	{
		return false;
	}
	
	/**
	 * Get the strip offset for a particular strip.
	 * @param strip The strip to get the offset of.
	 * @return The offset for that strip.
	 */
	public long getStripOffset(int strip)
	{
		long val = 0L;
		switch(super.dataType)
		{
			case Writer.SHORT:
				val = ((short[])super.getExtraData())[strip];
				break;
			case Writer.LONG:
				val = ((int[])super.getExtraData())[strip];
				break;
			case Writer.LONG8:
				val = ((long[])super.getExtraData())[strip];
				break;
		}
		return val;
	}
	
	/**
	 * Set the strip offset for a particular strip.
	 * @param strip The strip to set the offset for.
	 * @param byteCount The offset to set for that strip.
	 */
	public void setStripOffset(int strip, long offset)
	{
		short req = super.dataType;
		if(super.dataType != (req = dataTypeRequired(offset)))
		{
			changeValueTypes(req);
		}
		if(strip < 0)
		{
			//This is just making sure the required size is implemented. So just exit since it was done.
			return;
		}
		switch(super.dataType)
		{
			case Writer.SHORT:
				((short[])super.getExtraData())[strip] = (short)(offset & 0x000000000000FFFF);
				break;
			case Writer.LONG:
				((int[])super.getExtraData())[strip] = (int)(offset & 0x00000000FFFFFFFF);
				break;
			case Writer.LONG8:
				((long[])super.getExtraData())[strip] = offset;
				break;
		}
		//Setup for normal TIFF because if a BigTIFF is being created "setupForBigTIFF" will get called.
		if(super.dataType == Writer.SHORT && super.count <= 2)
		{
			super.value = 0L;
			for(int i = 0; i < super.count; i++)
			{
				super.value |= Writer.shiftValue(((short[])super.getExtraData())[i], i);
			}
		}
		else if(super.dataType == Writer.LONG && super.count == 1)
		{
			super.value = Writer.shiftValue(((int[])super.getExtraData())[0], 0);
		}
		else
		{
			super.value = 0L;
		}
	}
	
	private static short dataTypeRequired(long value)
	{
		if(value < 0)
		{
			throw new IllegalArgumentException("value < 0");
		}
		if(value > Short.MAX_VALUE)
		{
			if(value > Integer.MAX_VALUE)
			{
				return Writer.LONG8;
			}
			return Writer.LONG;
		}
		return Writer.SHORT;
	}
}
