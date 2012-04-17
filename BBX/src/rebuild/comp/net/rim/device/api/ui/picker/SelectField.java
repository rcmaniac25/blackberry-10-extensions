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
package rebuild.comp.net.rim.device.api.ui.picker;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.HorizontalFieldManager;

/**
 * A field that defines a selected file.
 */
final class SelectField extends HorizontalFieldManager
{
	public SelectField()
	{
		this(0);
	}
	
	public SelectField(long style)
	{
		super(style | HorizontalFieldManager.FOCUSABLE | HorizontalFieldManager.READONLY | HorizontalFieldManager.USE_ALL_WIDTH);
	}
	
	protected void paint(Graphics graphics)
	{
		if(this.isFocus())
		{
			int tc = graphics.getColor();
			graphics.setColor(Color.RED);
			graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
			graphics.setColor(tc);
		}
		super.paint(graphics);
	}
	
	//TODO
}
