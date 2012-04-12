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
 * A tag that contains one or more {@link Writer.SRATIONAL} items.
 * @since BBX 1.0.1
 */
public class SRationalTag extends RationalTag
{
	/**
	 * Create a new {@link SRationalTag}.
	 * @param tag The tag value.
	 */
	public SRationalTag(short tag)
	{
		this(tag, 0, 1);
	}
	
	/**
	 * Create a new {@link SRationalTag}.
	 * @param tag The tag value.
	 * @param num The numerator for the {@link Writer.SRATIONAL}.
	 * @param den The denominator for the {@link Writer.SRATIONAL}.
	 */
	public SRationalTag(short tag, int num, int den)
	{
		this(tag, new int[]{ num }, new int[]{ den });
	}
	
	/**
	 * Create a new {@link RationalTag}.
	 * @param tag The tag value.
	 * @param num The numerators for the {@link Writer.SRATIONAL}.
	 * @param den The denominators for the {@link Writer.SRATIONAL}.
	 */
	public SRationalTag(short tag, int[] num, int[] den)
	{
		super(tag, num, den, Writer.SRATIONAL);
	}
}
