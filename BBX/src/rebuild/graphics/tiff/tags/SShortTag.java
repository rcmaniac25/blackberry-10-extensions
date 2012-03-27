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

import rebuild.graphics.tiff.Writer;

/**
 * A tag that contains one or more {@link Writer.SSHORT} items.
 */
public class SShortTag extends ShortTag
{
	/**
	 * Create a new {@link SShortTag}.
	 * @param tag The tag value.
	 */
	public SShortTag(short tag)
	{
		this(tag, (short)0);
	}
	
	/**
	 * Create a new {@link SShortTag}.
	 * @param tag The tag value.
	 * @param val The value the tag should store.
	 */
	public SShortTag(short tag, short val)
	{
		this(tag, new short[]{ val });
	}
	
	/**
	 * Create a new {@link SShortTag}.
	 * @param tag The tag value.
	 * @param val The values the tag should store.
	 */
	public SShortTag(short tag, short[] val)
	{
		super(tag, val, Writer.SSHORT);
	}
	
	/**
	 * Get a {@link Writer.SSHORT} at the specified index.
	 * @param index The index to get the {@link Writer.SSHORT}.
	 * @return The {@link Writer.SSHORT} at the specified index.
	 */
	public final short getSShort(int index)
	{
		return super.getShort(index);
	}
	
	/**
	 * Set a {@link Writer.SSHORT} at the specified index.
	 * @param index The index to set the {@link Writer.SSHORT}.
	 * @param val The {@link Writer.SSHORT} to set at the specified index.
	 */
	public final void setSShort(int index, short val)
	{
		super.setShort(index, val);
	}
}
