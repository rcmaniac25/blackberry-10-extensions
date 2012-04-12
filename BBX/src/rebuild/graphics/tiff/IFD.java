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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.rim.device.api.collection.util.BigVector;

/**
 * An "Image File Directory" that defines characteristics about the TIFF image.
 * @since BBX 1.0.1
 */
public final class IFD
{
	private BigVector tagList;
	
	/**
	 * Create a new {@link IFD}.
	 */
	public IFD()
	{
		tagList = new BigVector();
	}
	
	/**
	 * Add a new {@link Tag} to the this {@link IFD}. It is recommended to only add a tag once.
	 * @param tag The {@link Tag} to add.
	 */
	public void addTag(Tag tag)
	{
		/*
		if(indexOf(tag.getTagType()) == -1)
		{
			tagList.addElement(tag);
		}
		*/
		tagList.addElement(tag);
	}
	
	/**
	 * Remove a tag at the specified index.
	 * @param index The index to remove the tag at.
	 */
	public void removeTag(int index)
	{
		tagList.removeElementAt(index);
	}
	
	/**
	 * Clear all tags from the IFD.
	 */
	public void clear()
	{
		tagList.removeAll();
	}
	
	/**
	 * Get a tag at the specified index.
	 * @param index The index to get the tag from.
	 * @return The tag at the specified index.
	 */
	public Tag getTag(int index)
	{
		return (Tag)tagList.elementAt(index);
	}
	
	/**
	 * Get a tag by the type.
	 * @param type The type of tag to get.
	 * @return The tag of the specified type, or null if one does not exist.
	 */
	public Tag getTagByType(int type)
	{
		int index = indexOf(type);
		return (index == -1) ? null : (Tag)tagList.elementAt(index);
	}
	
	/**
	 * Get the index of a tag of the specified type.
	 * @param type The type of tag to get the index of.
	 * @return The index of the tag of the specified type or -1 if one does not exist.
	 */
	public int indexOf(int type)
	{
		return indexOf(type, 0);
	}
	
	/**
	 * Get the index of a tag of the specified type.
	 * @param type The type of tag to get the index of.
	 * @param startingIndex The starting index of the search.
	 * @return The index of the tag of the specified type or -1 if one does not exist.
	 */
	public int indexOf(int type, int startingIndex)
	{
		int count = tagList.size();
		for(int i = startingIndex; i < count; i++)
		{
			if(((Tag)tagList.elementAt(i)).getTagType() == type)
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Insert a tag at the specified index.
	 * @param index The index to place the tag at.
	 * @param tag The tag to place, if this is null then it will be ignored.
	 */
	public void insertTag(int index, Tag tag)
	{
		if(tag != null)
		{
			tagList.insertElementAt(tag, index);
		}
	}
	
	/**
	 * Get the number of tags in this IFD.
	 * @return The number of tags in this IFD.
	 */
	public int getCount()
	{
		return tagList.size();
	}
	
	/**
	 * Get the number of bytes the tags in this IFD takes up.
	 * @return The number of bytes the tags only take up.
	 */
	public int getLength()
	{
		return tagList.size() * 12;
	}
	
	/**
	 * Get the number of bytes the tags in this IFD takes up when in BigTIFF format.
	 * @return The number of bytes the tags only take up.
	 */
	public int getBigLength()
	{
		return tagList.size() * 20;
	}
	
	/**
	 * Used internally, not for public use.
	 */
	public int getOptLength()
	{
		return getIFDLength(12);
	}
	
	/**
	 * Used internally, not for public use.
	 */
	public int getOptBigLength()
	{
		return getIFDLength(20);
	}
	
	private int getIFDLength(int tagSize)
	{
		int tagCount = 0;
		int count = tagList.size();
    	for(int i = 0; i < count; i++)
    	{
    		Tag t = (Tag)tagList.elementAt(i);
    		if(t.hasDefault())
    		{
    			if(t.isDefault())
    			{
    				continue;
    			}
    		}
    		tagCount++;
    	}
    	return tagCount * tagSize;
	}
	
	/**
	 * Sort the tags into a manner that is required by the TIFF specification. This is a time consuming operation and
	 * should only be done when absolutely necessary.
	 */
	public void sort()
	{
		tagList.sort(new TagComparator());
		/* Normal Vector
		int size = tagList.size();
		Object[] objs = new Object[size];
		tagList.copyInto(objs);
		net.rim.device.api.util.Arrays.sort(objs, 0, size, new TagComparator());
		tagList.removeAllElements();
		for(int i = 0; i < size; i++)
		{
			tagList.addElement(objs[i]);
		}
		*/
	}
	
	private final class TagComparator implements net.rim.device.api.util.Comparator
	{
		public TagComparator()
		{
		}
		
		public int compare(Object o1, Object o2)
		{
			if(o1 == null || o2 == null)
			{
				if(o1 == null && o2 == null)
				{
					return 0;
				}
				else if(o1 == null)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
			if(o1 instanceof Tag && o2 instanceof Tag)
			{
				Tag o1t = (Tag)o1;
				Tag o2t = (Tag)o2;
				if (o1t.getTagType() < o2t.getTagType())
			    {
			        return -1;
			    }
			    if (o1t.getTagType() > o2t.getTagType())
			    {
			        return 1;
			    }
			    return 0;
			}
			else if(o1 instanceof Tag)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		
		public boolean equals(Object obj)
		{
			if(obj == null)
			{
				return false;
			}
			return obj instanceof TagComparator;
		}
	}
	
	/**
	 * Write the IFD. The default offset to the next IFD is 0.
	 * @param dat The {@link rebuild.Graphics.tiff.tags.Writer} to write the IFD to.
	 * @param dataOffset The absolute position to write any extra data to.
	 * @return The absolute end position after the IFD and extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public int write(rebuild.graphics.tiff.Writer dat, int dataOffset) throws IOException
	{
		return write(dat, 0, dataOffset);
	}
	
	/**
	 * Write the IFD.
	 * @param dat The {@link rebuild.Graphics.tiff.tags.Writer} to write the IFD to.
	 * @param offset The offset to the next IFD.
	 * @param dataOffset The absolute position to write any extra data to.
	 * @return The absolute end position after the IFD and extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public int write(rebuild.graphics.tiff.Writer dat, int offset, int dataOffset) throws IOException
	{
		return (int)(write(dat, (long)offset, (long)dataOffset, false) & 0x00000000FFFFFFFF);
	}
	
	/**
	 * Write the IFD. The default offset to the next IFD is 0.
	 * @param dat The {@link rebuild.Graphics.tiff.tags.Writer} to write the IFD to.
	 * @param dataOffset The absolute position to write any extra data to.
	 * @return The absolute end position after the IFD and extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public long writeBig(rebuild.graphics.tiff.Writer dat, long dataOffset) throws IOException
	{
		return write(dat, 0L, dataOffset, true);
	}
	
	/**
	 * Write the IFD.
	 * @param dat The {@link rebuild.Graphics.tiff.tags.Writer} to write the IFD to.
	 * @param offset The offset to the next IFD.
	 * @param dataOffset The absolute position to write any extra data to.
	 * @param big If this is writing a BigTIFF.
	 * @return The absolute end position after the IFD and extra data.
	 * @throws IOException If any IO exception occurs.
	 */
	public long write(rebuild.graphics.tiff.Writer dat, long offset, long dataOffset, boolean big) throws IOException
	{
		ByteArrayOutputStream tagBuffer = new ByteArrayOutputStream();
    	ByteArrayOutputStream tagDataBuffer = new ByteArrayOutputStream();
    	rebuild.graphics.tiff.Writer tagDat = new rebuild.graphics.tiff.Writer(dat.getLittleEndian(), tagBuffer);
    	rebuild.graphics.tiff.Writer tagDataDat = new rebuild.graphics.tiff.Writer(dat.getLittleEndian(), tagDataBuffer);
    	
    	//Find all tags that do not have a default value and skip them
    	BigVector toWriteTags = new BigVector();
    	long count = tagList.size();
    	for(int i = 0; i < count; i++)
    	{
    		Tag t = (Tag)tagList.elementAt(i);
    		if(t.hasDefault())
    		{
    			if(t.isDefault())
    			{
    				continue;
    			}
    		}
    		toWriteTags.addElement(t);
    	}
    	
    	//Write out tags
    	count = toWriteTags.size(); //The number of tags
    	if(big)
    	{
    		dat.writeULong(count);
    	}
    	else
    	{
    		dat.writeUShort((int)(count & 0x00000000FFFFFFFF));
    	}
    	Tag tag;
    	boolean extra;
    	for(int i = 0; i < count; i++) //Can't get element using long but shouldn't need to worry right now.
    	{
    		tag = (Tag)toWriteTags.elementAt(i); //Get the tag
    		if(big)
    		{
    			tag.setupForBigTIFF(true);
    		}
    		extra = tag.getIfExtraDataExists(big); //Figure out if it has extended data
    		long d = tagDataBuffer.size(); //Get the current size of the tag data
    		//Write the tag, use value if no extra data exists
    		if(big)
    		{
    			tag.write(tagDat, extra ? dataOffset : -1);
    		}
    		else
    		{
    			tag.write(tagDat, extra ? (int)(dataOffset & 0x00000000FFFFFFFF) : -1);
    		}
    		if(extra)
    		{
    			tag.writeData(tagDataDat);
    			d = tagDataBuffer.size() - d; //Get new tag data size and subtract previous size to get the length of the data
    			dataOffset += d; //Add that length to the dataOffset
    		}
    		if(big)
    		{
    			tag.setupForBigTIFF(false);
    		}
    	}
    	
    	dat.write(tagBuffer.toByteArray());
    	
    	//The offset to the next field which is the data.
    	if(big)
    	{
    		dat.writeULong(offset);
    	}
    	else
    	{
    		dat.writeUInt((int)(offset & 0x00000000FFFFFFFF));
    	}
    	
    	dat.write(tagDataBuffer.toByteArray());
    	
    	tagBuffer.close();
    	tagDataBuffer.close();
		return dataOffset;
	}
	
	/*
	private boolean extraData(Tag tag)
	{
		switch(tag.dataType)
		{
			case BYTE:
			case SHORT:
			case LONG:
			case SBYTE:
			case SSHORT:
			case SLONG:
			case FLOAT:
			default:
				return false;
			case ASCII:
			case RATIONAL:
			case SRATIONAL:
			case DOUBLE:
				return true;
			case UNDEFINED:
				//Todo: Figure out data type and return if needed or not
				return false;
		}
		int count = 1;
		switch(tag.tag)
		{
			case IMAGE_WIDTH_TAG:
			case IMAGE_LENGTH_TAG:
			case ROWS_PER_STRIP_TAG:
				count = (tag.dataType == SHORT) ? 2 : 4;
				break;
			case STRIP_OFFSETS_TAG:
			case STRIP_BYTE_COUNTS_TAG:
				count = ((tag.dataType == SHORT) ? 2 : 4) * tag.count;
				break;
			case EXTRA_SAMPLES_TAG:
			case BITS_PER_SAMPLE_TAG:
			case RESOLUTION_UNIT_TAG:
				count = tag.count * 2;
				break;
			case XRESOLUTION_TAG:
			case YRESOLUTION_TAG:
				count = 4 + 4;
				break;
			case COMPRESSION_TAG:
			case PHOTOMETRIC_INTERPRETATION_TAG:
			case SAMPLES_PER_PIXEL_TAG:
				count = 2;
				break;
		}
		return count > 4;
	}
	*/
}
