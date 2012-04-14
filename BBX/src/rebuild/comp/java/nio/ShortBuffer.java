//#preprocessor

//#implicit BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1

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
//Taken from PDF Renderer for BlackBerry
package rebuild.comp.java.nio;

/**
 * A skimmed down version of the ShortBuffer class for use in versions lower then 5.0. Based off J2SE java.nio.ShortBuffer class but no source code used for it.
 * @since BBX 1.2.0
 */
public abstract class ShortBuffer extends Buffer
{
	/**
	 * Writes the given short into this buffer at the current position, and then increments the position.
	 * @param s The short to be written.
	 * @return This buffer.
	 */
	public abstract ShortBuffer put(short s);
	
	/**
	 * Relative get method. Reads the short at this buffer's current position, and then increments the position.
	 * @return The short at the buffer's current position.
	 */
	public abstract short get();
}
