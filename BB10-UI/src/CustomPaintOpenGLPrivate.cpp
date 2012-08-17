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

#define CP_GL static_cast<CustomPaintOpenGL*>(cp)

//Private

CustomPaintOpenGLPrivate::CustomPaintOpenGLPrivate(CustomPaintOpenGL* customPaintGL) : CustomPaintPrivate(customPaintGL),
	ver(CustomPaintOpenGL::V11), eglDisp(EGL_NO_DISPLAY), eglSurf(EGL_NO_SURFACE), eglCtx(EGL_NO_CONTEXT)
{
}

CustomPaintOpenGLPrivate::~CustomPaintOpenGLPrivate()
{
}

bool CustomPaintOpenGLPrivate::changeVersion(CustomPaintOpenGL::Version nVer)
{
	//TODO: screen usage changes and EGL context changes
	return false;
}

bool CustomPaintOpenGLPrivate::allowScreenUsageToChange() const
{
	return false;
}

void CustomPaintOpenGLPrivate::privateWindowSetup()
{
	int usage = 0;
	switch(ver)
	{
		case CustomPaintOpenGL::V11:
			usage = SCREEN_USAGE_OPENGL_ES1 | SCREEN_USAGE_ROTATION;
			break;
		case CustomPaintOpenGL::V20:
			usage = SCREEN_USAGE_OPENGL_ES2 | SCREEN_USAGE_ROTATION;
			break;
	}
	setupWindow(usage);
}

void CustomPaintOpenGLPrivate::cleanupWindow()
{
	//EGL Cleanup
	if (eglDisp != EGL_NO_DISPLAY)
	{
		eglMakeCurrent(eglDisp, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
		if (eglSurf != EGL_NO_SURFACE) {
			eglDestroySurface(eglDisp, eglSurf);
			eglSurf = EGL_NO_SURFACE;
		}
		if (eglCtx != EGL_NO_CONTEXT) {
			eglDestroyContext(eglDisp, eglCtx);
			eglCtx = EGL_NO_CONTEXT;
		}
		eglTerminate(eglDisp);
		eglDisp = EGL_NO_DISPLAY;
	}
	eglReleaseThread();

	//Normal cleanup
	CustomPaintPrivate::cleanupWindow();
}

void CustomPaintOpenGLPrivate::invokePaint(int*)
{
	pthread_mutex_lock(&mutex);

	eglMakeCurrent(eglDisp, eglSurf, eglSurf, eglCtx);

	CP_GL->paint();

	pthread_mutex_destroy(&mutex);
}

void CustomPaintOpenGLPrivate::invalidate(int, int, int, int)
{
	//This is unneeded but exists for speed boost

	if(valid)
	{
		pthread_mutex_lock(&mutex);

		eglMakeCurrent(eglDisp, eglSurf, eglSurf, eglCtx);

		//Invoke paint signal
		CP_GL->paint();

		//Invalidate
		eglSwapBuffers(eglDisp, eglSurf);

		pthread_mutex_destroy(&mutex);
	}
}

void CustomPaintOpenGLPrivate::swapBuffers(screen_buffer_t, int*)
{
	eglSwapBuffers(eglDisp, eglSurf);
}
