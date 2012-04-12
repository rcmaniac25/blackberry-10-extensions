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
package rebuild.graphics.tiff;

import java.io.IOException;

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.util.StringUtilities;

/**
 * The abstract tag that will be used in TIFFs.
 * @since BBX 1.0.1
 */
public abstract class Tag
{
	private short tag;
	protected short dataType;
	protected long count;
	protected long value;
	protected Object data;
	private boolean extra;
	
	/**
	 * Create a new {@link Tag}.
	 * @param tag The tag code for this tag.
	 * @param dataType The type of data that will be written.
	 * @param count The number of items that will be written. Note this is not the number of bytes to write but the number of items.
	 * @param value The value that this tag has. The tag must always be left justified, so if a {@link Writer.SHORT} is to be written then it's value must be value << 16. If the tag contains any data bigger then 4 bytes then it must be set in <code>data</code> and this value will be ignored.
	 * @param data Any data that does not fit in the 4 byte <code>value</code> parameter.
	 */
	protected Tag(short tag, short dataType, int count, int value, Object data)
	{
		this(tag, dataType, (long)count, (long)value, data);
		this.extra = extraData(false);
	}
	
	/**
	 * Create a new {@link Tag}.
	 * @param tag The tag code for this tag.
	 * @param dataType The type of data that will be written.
	 * @param count The number of items that will be written. Note this is not the number of bytes to write but the number of items.
	 * @param value The value that this tag has. The tag must always be left justified, so if a {@link Writer.SHORT} is to be written then it's value must be value << 16. If the tag contains any data bigger then 4 bytes then it must be set in <code>data</code> and this value will be ignored.
	 * @param data Any data that does not fit in the 8 byte <code>value</code> parameter.
	 */
	protected Tag(short tag, short dataType, long count, long value, Object data)
	{
		this.tag = tag;
		this.dataType = dataType;
		this.count = count;
		this.value = value;
		this.data = data;
		this.extra = extraData(true);
	}
	
	/**
	 * Write the tag to a {@link Writer}. This should only be called if no extra data exists else the tag will not be written.
	 * @param wr The writer to write the tag to.
	 * @throws IOException If any IO exception occurs.
	 */
	public final void write(rebuild.graphics.tiff.Writer wr) throws IOException
	{
		if(!extra)
		{
			write(wr, -1);
		}
		else
		{
			throw new IOException(Resources.getString(BBXResource.TIFF_WRONG_WRITER));
		}
	}
	
	/**
	 * Write the tag to a {@link Writer}. This should only be called if no extra data exists else the tag will not be written.
	 * @param wr The writer to write the tag to.
	 * @throws IOException If any IO exception occurs.
	 */
	public final void writeBig(rebuild.graphics.tiff.Writer wr) throws IOException
	{
		if(!extra)
		{
			write(wr, -1, true);
		}
		else
		{
			throw new IOException(Resources.getString(BBXResource.TIFF_WRONG_WRITER));
		}
	}
	
	/**
	 * Write the tag to a {@link Writer}.
	 * @param wr The writer to write the tag to.
	 * @param offset The absolute offset to write instead of the <code>value</code> data. If the <code>value</code> should be written then <code>-1</code> should be passed here.
	 * @throws IOException If any IO exception occurs.
	 */
	public final void write(rebuild.graphics.tiff.Writer wr, long offset) throws IOException
	{
		write(wr, offset, false);
	}
	
	/**
	 * Write the tag to a {@link Writer}.
	 * @param wr The writer to write the tag to.
	 * @param offset The absolute offset to write instead of the <code>value</code> data. If the <code>value</code> should be written then <code>-1</code> should be passed here.
	 * @throws IOException If any IO exception occurs.
	 */
	public final void write(rebuild.graphics.tiff.Writer wr, int offset) throws IOException
	{
		write(wr, (long)offset, false);
	}
	
	/**
	 * Write the tag to a {@link Writer}.
	 * @param wr The writer to write the tag to.
	 * @param offset The absolute offset to write instead of the <code>value</code> data. If the <code>value</code> should be written then <code>-1</code> should be passed here.
	 * @param big If this is writing a BigTIFF tag.
	 * @throws IOException If any IO exception occurs.
	 */
	public final void write(rebuild.graphics.tiff.Writer wr, long offset, boolean big) throws IOException
	{
		wr.writeUShort(tag);
		wr.writeUShort(dataType);
		if(big)
		{
			wr.writeULong(count);
			wr.writeTagLongValue((offset > -1) ? offset : value, offset > -1, dataType);
		}
		else
		{
			wr.writeUInt((int)(count & 0x00000000FFFFFFFF));
			wr.writeTagValue((int)((offset > -1) ? offset & 0x00000000FFFFFFFF : value & 0x00000000FFFFFFFF), offset > -1, dataType);
		}
	}
	
	/**
	 * Get the tag type.
	 * @return The tag type.
	 */
	public final short getTagType()
	{
		return tag;
	}
	
	/**
	 * Get the data type.
	 * @return The data type.
	 * @see Writer
	 */
	public final short getDataType()
	{
		return dataType;
	}
	
	/**
	 * Set the data type.
	 * @param dataType The data type to set.
	 * @see Writer
	 */
	public void setDataType(short dataType)
	{
		this.dataType = dataType;
		//extra = extraData();
	}
	
	/**
	 * Get the number of values contained in this tag. If all the values can stay in a 4 byte boundary then the data is stored (left-justified) in the <code>value</code> parameter, else it is in the <code>data</code> parameter.
	 * @return The number of values contained in the tag.
	 */
	public final int getCount()
	{
		return (int)(count & 0x00000000FFFFFFFF);
	}
	
	/**
	 * Get the number of values contained in this tag. If all the values can stay in a 8 byte boundary then the data is stored (left-justified) in the <code>value</code> parameter, else it is in the <code>data</code> parameter.
	 * @return The number of values contained in the tag.
	 */
	public final long getBigCount()
	{
		return count;
	}
	
	/**
	 * Set the number of values contained in this tag.
	 * @param count The number of values contained in the tag.
	 * @see getCount
	 */
	public void setCount(int count)
	{
		this.count = count;
		extra = extraData(false);
	}
	
	/**
	 * Set the number of values contained in this tag.
	 * @param count The number of values contained in the tag.
	 * @see getCount
	 */
	public void setCount(long count)
	{
		this.count = count;
		extra = extraData(true);
	}
	
	/**
	 * Get the left-justified value that the tag contains, any values that do not fit in a 4 byte boundary should have it's value stored in <code>data</code>.
	 * @return The value that the tag contains.
	 */
	public final int getValue()
	{
		return (int)(value & 0x00000000FFFFFFFF);
	}
	
	/**
	 * Get the left-justified value that the tag contains, any values that do not fit in a 8 byte boundary should have it's value stored in <code>data</code>.
	 * @return The value that the tag contains.
	 */
	public final long getBigValue()
	{
		return value;
	}
	
	/**
	 * Set the left-justified value that the tag contains.
	 * @param value The value that the tag should contains.
	 * @see getValue
	 */
	public void setValue(int value)
	{
		this.value = value;
		extra = extraData(false);
	}
	
	/**
	 * Set the left-justified value that the tag contains.
	 * @param value The value that the tag should contains.
	 * @see getValue
	 */
	public void setValue(long value)
	{
		this.value = value;
		extra = extraData(true);
	}
	
	/**
	 * Get any extra data that the tag contains, that is any data that does not fit into 4/8 bytes. Note that this is not the <code>value</code> parameter AND the extra data, it is one or the other.
	 * @return The extra data that the tag contains.
	 */
	public final Object getExtraData()
	{
		return data;
	}
	
	/**
	 * Set any extra data that the tag should contain.
	 * @param obj The extra data that the tag should contain.
	 * @see getExtraData
	 */
	protected void setExtraData(Object obj)
	{
		data = obj;
		extra =  obj != null;
		//extra = extraData();
	}
	
	/**
	 * Get if the tag contains extra data.
	 * @param big <code>true</code> if the tag is being written to a BigTIFF, <code>false</code> if otherwise.
	 * @return true if the tag contains extra data, false if otherwise.
	 */
	public final boolean getIfExtraDataExists(boolean big)
	{
		return extra = extraData(big);
	}
	
	/**
	 * The extra data writer, if any extra data exists then this method will be called and (through internal methods) a offset to the data will be used in place of the <code>value</code> parameter.
	 * @param wr The writer used to write the extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public abstract void writeData(rebuild.graphics.tiff.Writer wr) throws IOException;
	
	/**
	 * This method figures out if any extra data is exists, this method can search the data type, extra data, value, and count. It is called whenever one of these types change.
	 * @param big <code>true</code> if the tag is being written to a BigTIFF, <code>false</code> if otherwise.
	 * @return true if the tag contains extra data, false if otherwise.
	 */
	protected abstract boolean extraData(boolean big);
	
	/**
	 * Setup/un-setup the tag for use in BigTIFF.
	 * @param set <code>true</code> if the tag should be preped for BigTIFF, <code>false</code> if otherwise.
	 */
	protected abstract void setupForBigTIFF(boolean set);
	
	/**
	 * Get if this {@link Tag} can have a default value, often defined by a parameter-less constructor.
	 * @return <code>true</code> if the {@link Tag} has a default value, <code>false</code> if otherwise. Default is <code>false</code>.
	 */
	public abstract boolean hasDefault();
	
	/**
	 * Get if this {@link Tag}'s current value is the default value. This is ignored if {@link Tag#hasDefault()} returns <code>false</code>.
	 * @return <code>true</code> if the {@link Tag}'s value is the default value, <code>false</code> if otherwise.
	 */
	public abstract boolean isDefault();
	
	/**
	 * Get the {@link Tag}'s type.
	 * @return The {@link Tag}'s type.
	 */
	public static short getTagTypeValue()
	{
		return 0;
	}
	
	/**
	 * Determine if an {@link Object} is the same as this {@link Tag}.
	 * @param obj The {@link Object} to compare.
	 * @return true if the objects are the same, false if otherwise.
	 */
	public boolean equals(Object obj)
	{
		if((obj != null) && (obj instanceof Tag))
		{
			Tag t = (Tag)obj;
			return (t.count == this.count) && (t.value == this.value) && (t.extra == this.extra) && 
			(this.extra ? t.data.equals(this.data) : true) && (t.dataType == this.dataType) && (t.tag == this.tag);
		}
		return false;
	}
	
	public int hashCode()
	{
		return  new Long(this.count).hashCode() + new Long(this.value).hashCode() + new Short(this.dataType).hashCode() + 
			new Short(this.tag).hashCode() + new Boolean(this.extra).hashCode() + (this.extra ? this.data.hashCode() : 0);
	}
	
	public String toString()
	{
		return StringUtilities.format("Tag: {0,number,integer}", new Integer(this.tag));
	}
}
