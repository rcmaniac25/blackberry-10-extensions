/*
 * BlackBerry 10 UI library
 * Copyright (c) 2012 Vincent Simonetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

#ifndef CUSTOM_PAINT_OPENGL_IMPL_H_
#define CUSTOM_PAINT_OPENGL_IMPL_H_

#include "CustomPaintInternal.h"

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
			class CustomPaintOpenGLImpl_V11 : public CustomPaintOpenGLImpl
			{
				Q_OBJECT

			public:
				CustomPaintOpenGLImpl_V11();

				virtual ~CustomPaintOpenGLImpl_V11();

				int getScreenUsage();

				//TODO
			};

			class CustomPaintOpenGLImpl_V20 : public CustomPaintOpenGLImpl
			{
				Q_OBJECT

			public:
				CustomPaintOpenGLImpl_V20();

				virtual ~CustomPaintOpenGLImpl_V20();

				int getScreenUsage();

				//TODO
			};
		}
	}
}

#endif /* CUSTOM_PAINT_OPENGL_IMPL_H_ */