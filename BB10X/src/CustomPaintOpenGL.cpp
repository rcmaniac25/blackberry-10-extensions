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

#include "CustomPaintInternal.h"

#define CONST_GL_D static_cast<const CustomPaintOpenGLPrivate*>(d_func())
#define GL_D static_cast<CustomPaintOpenGLPrivate*>(d)

CustomPaintOpenGL::CustomPaintOpenGL(bb::cascades::Container* parent, CustomPaintOpenGL::Version ver) : rebuild::ui::component::CustomPaint(parent, new CustomPaintOpenGLPrivate(this, ver))
{
}

CustomPaintOpenGL::~CustomPaintOpenGL()
{
}

CustomPaintOpenGL::Version CustomPaintOpenGL::openGLversion() const
{
	return CONST_GL_D->ver;
}

void CustomPaintOpenGL::setOpenGLversion(CustomPaintOpenGL::Version version)
{
	Q_D(CustomPaint);

	if(GL_D->changeVersion(version))
	{
		emit openGLversionChanged(version);
	}
}

EGLDisplay CustomPaintOpenGL::getEGLDisplay() const
{
	return CONST_GL_D->eglDisp;
}

EGLSurface CustomPaintOpenGL::getEGLSurface() const
{
	return CONST_GL_D->eglSurf;
}

void CustomPaintOpenGL::setupPaintWindow(screen_window_t)
{
	setupOpenGL();
}

void CustomPaintOpenGL::setupOpenGL()
{
}

void CustomPaintOpenGL::paint(screen_window_t)
{
}

void CustomPaintOpenGL::layout(int, int)
{
}
