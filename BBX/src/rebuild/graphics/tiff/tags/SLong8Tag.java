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

import rebuild.graphics.tiff.Writer;

/**
 * A tag that contains one or more {@link Writer.SLONG8} items.
 */
public class SLong8Tag extends Long8Tag
{
	/**
	 * Create a new {@link SLong8Tag}.
	 * @param tag The tag value.
	 */
	public SLong8Tag(short tag)
	{
		this(tag, 0L);
	}
	
	/**
	 * Create a new {@link SLong8Tag}.
	 * @param tag The tag value.
	 * @param val The value the tag should store.
	 */
	public SLong8Tag(short tag, long val)
	{
		this(tag, new long[]{ val });
	}
	
	/**
	 * Create a new {@link SLong8Tag}.
	 * @param tag The tag value.
	 * @param val The values the tag should store.
	 */
	public SLong8Tag(short tag, long[] val)
	{
		super(tag, val, Writer.SLONG8);
	}
	
	/**
	 * Get a {@link Writer.SLONG8} at the specified index.
	 * @param index The index to get the {@link Writer.SLONG8}.
	 * @return The {@link Writer.SLONG8} at the specified index.
	 */
	public final long getSLong(int index)
	{
		return super.getLong8(index);
	}
	
	/**
	 * Set a {@link Writer.SLONG} at the specified index.
	 * @param index The index to set the {@link Writer.SLONG}.
	 * @param val The {@link Writer.SLONG} to set at the specified index.
	 */
	public final void setSLong(int index, long val)
	{
		super.setLong8(index, val);
	}
}
