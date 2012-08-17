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

#include "CustomPaintOpenGLImpl.h"

//Private

CustomPaintOpenGLPrivate::CustomPaintOpenGLPrivate(CustomPaintOpenGL* customPaintGL) : CustomPaintPrivate(customPaintGL),
	cp(customPaintGL), ver(CustomPaintOpenGL::V11), impl(CustomPaintOpenGLImpl::generate(CustomPaintOpenGL::V11))
{
}

CustomPaintOpenGLPrivate::~CustomPaintOpenGLPrivate()
{
	if(impl)
	{
		delete impl;
		impl = NULL;
	}
}

bool CustomPaintOpenGLPrivate::changeVersion(CustomPaintOpenGL::Version nVer)
{
	bool ret = false;
	CustomPaintOpenGLImpl* nImpl = CustomPaintOpenGLImpl::generate(ver);

	if(nImpl)
	{
		if(impl)
		{
			delete impl;
		}
		impl = nImpl;
		ver = nVer;
		ret = true;
	}
	return ret;
}

bool CustomPaintOpenGLPrivate::allowScreenUsageToChange()
{
	return false;
}

void CustomPaintOpenGLPrivate::privateWindowSetup()
{
	setupWindow(SCREEN_USAGE_OPENGL_ES1 | SCREEN_USAGE_ROTATION);
}

void CustomPaintOpenGLPrivate::swapBuffers(screen_buffer_t buffer, int* rect)
{
	eglSwapBuffers(eglDisp, eglSurf);
}

//Impl

CustomPaintOpenGLImpl::CustomPaintOpenGLImpl()
{
}

CustomPaintOpenGLImpl::~CustomPaintOpenGLImpl()
{
}

CustomPaintOpenGLImpl* CustomPaintOpenGLImpl::generate(CustomPaintOpenGL::Version ver)
{
	switch(ver)
	{
		case CustomPaintOpenGL::V11:
			return new CustomPaintOpenGLImpl_V11();
		case CustomPaintOpenGL::V20:
			return new CustomPaintOpenGLImpl_V20();
	}
	return NULL;
}
