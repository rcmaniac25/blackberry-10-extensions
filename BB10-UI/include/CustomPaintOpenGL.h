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

#ifndef CUSTOM_PAINT_OPENGL_H_
#define CUSTOM_PAINT_OPENGL_H_

#include "CustomPaint.h"

#include <EGL/egl.h>

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
			class CustomPaintOpenGLPrivate;
			class CustomPaintOpenGL : public CustomPaint
			{
			private:
				Q_OBJECT
				Q_FLAGS(Version)

				Q_PROPERTY(CustomPaintOpenGL::Version openGLversion READ openGLversion NOTIFY openGLversionChanged FINAL)

			public:
				enum Version
				{
					V11,
					V20
				};

				explicit CustomPaintOpenGL(bb::cascades::Container* parent = NULL);

				virtual ~CustomPaintOpenGL();

				Version openGLversion() const;

				void setOpenGLversion(Version version);

			protected:
				EGLDisplay getEGLDisplay() const;
				EGLSurface getEGLSurface() const;

				void paint(screen_window_t window);
				virtual void paint() = 0;

			Q_SIGNALS:
				void openGLversionChanged(Version usage);

			private:
				/*! @cond PRIVATE */
				Q_DISABLE_COPY(CustomPaintOpenGL)
				/*! @endcond */
			};
		}
	}
}
QML_DECLARE_TYPE(rebuild::ui::component::CustomPaintOpenGL)

#endif /* CUSTOM_PAINT_OPENGL_H_ */
