/*
 * BlackBerry 10 Extension library
 * Copyright (c) 2012-2014 Vincent Simonetti
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
#define OPENGL_BUFFER_COUNT 2

CustomPaintOpenGLPrivate::CustomPaintOpenGLPrivate(CustomPaintOpenGL* customPaintGL, CustomPaintOpenGL::Version glVer) : CustomPaintPrivate(static_cast<CustomPaint*>(customPaintGL)),
		ver(glVer), eglDisp(EGL_NO_DISPLAY), eglSurf(EGL_NO_SURFACE), eglCtx(EGL_NO_CONTEXT), eglConf(NULL)
{
}

CustomPaintOpenGLPrivate::~CustomPaintOpenGLPrivate()
{
}

bool CustomPaintOpenGLPrivate::changeVersion(CustomPaintOpenGL::Version nVer)
{
	if(valid)
	{
		//TODO: screen usage changes and EGL context changes
	}
	else
	{
		//Just change version, nothing has been setup yet
		ver = nVer;
	}
	return true;
}

bool CustomPaintOpenGLPrivate::allowScreenUsageToChange() const
{
	return false;
}

bool CustomPaintOpenGLPrivate::allowCleanupCallback() const
{
	return false;
}

void CustomPaintOpenGLPrivate::privateWindowSetup()
{
	EGLint interval;
	EGLint* attributes = NULL;

	EGLint attrib_list[] = {EGL_RED_SIZE,        8,
							EGL_GREEN_SIZE,      8,
							EGL_BLUE_SIZE,       8,
							EGL_SURFACE_TYPE,    EGL_WINDOW_BIT,
							EGL_RENDERABLE_TYPE, 0,
							EGL_NONE};

	int usage = 0;
	switch(ver)
	{
		case CustomPaintOpenGL::V11:
			usage = SCREEN_USAGE_OPENGL_ES1 | SCREEN_USAGE_ROTATION;
			attrib_list[9] = EGL_OPENGL_ES_BIT;
			break;
		case CustomPaintOpenGL::V20:
			usage = SCREEN_USAGE_OPENGL_ES2 | SCREEN_USAGE_ROTATION;
			attrib_list[9] = EGL_OPENGL_ES2_BIT;
			attributes = (EGLint*)calloc(3, sizeof(EGLint));
			if(!attributes)
			{
				return;
			}
			attributes[0] = EGL_CONTEXT_CLIENT_VERSION;
			attributes[1] = 2;
			attributes[2] = EGL_NONE;
			break;
	}

	//EGL setup
	eglDisp = eglGetDisplay(EGL_DEFAULT_DISPLAY);
	if(eglDisp == EGL_NO_DISPLAY)
	{
		free(attributes);
		return;
	}

	if(eglInitialize(eglDisp, NULL, NULL) != EGL_TRUE)
	{
		goto PRE_WINDOW_ERROR;
	}

	if(eglBindAPI(EGL_OPENGL_ES_API) != EGL_TRUE)
	{
		goto PRE_WINDOW_ERROR;
	}

	EGLint numConfigs;
	if(!eglChooseConfig(eglDisp, attrib_list, &eglConf, 1, &numConfigs))
	{
		goto PRE_WINDOW_ERROR;
	}

	if((eglCtx = eglCreateContext(eglDisp, eglConf, EGL_NO_CONTEXT, attributes)) == EGL_NO_CONTEXT)
	{
		goto PRE_WINDOW_ERROR;
	}
	free(attributes);
	attributes = NULL;

	//Window setup
	setupWindow(usage, OPENGL_BUFFER_COUNT, SCREEN_FORMAT_RGBX8888);

	if(valid)
	{
		//Set the remaining components so OpenGL operations can be performed
		if((eglSurf = eglCreateWindowSurface(eglDisp, eglConf, window, NULL)) == EGL_NO_SURFACE)
		{
			cleanupWindow();
			return;
		}

		if(eglMakeCurrent(eglDisp, eglSurf, eglSurf, eglCtx) != EGL_TRUE)
		{
			cleanupWindow();
			return;
		}

		interval = 1;
		if(eglSwapInterval(eglDisp, interval) != EGL_TRUE)
		{
			cleanupWindow();
		}
		return;
	}

PRE_WINDOW_ERROR:
	free(attributes);
	eglTerminate(eglDisp);
	eglConf = NULL;
	eglDisp = EGL_NO_DISPLAY;
	eglCtx = EGL_NO_CONTEXT;
	eglReleaseThread();
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
