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

#ifndef CUSTOM_PAINT_INTERNAL_H_
#define CUSTOM_PAINT_INTERNAL_H_

#include "../include/CustomPaintOpenGL.h"

#include <math.h>
#include <pthread.h>

#include <bb/cascades/core/touchpropagation.h>
#include <bb/cascades/ForeignWindowControl>
#include <bb/cascades/LayoutUpdateHandler>

#define SCREEN_WINDOW_HORZ 0
#define SCREEN_WINDOW_VERT 1
#define INVALIDATE_MAX_SIZE 0x7FFFFFFF
#define ZDEPTH_DEPTH_MIN 0x80000000

using namespace bb::cascades;
using namespace rebuild::ui::component;

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
			/*
			 * CustomPaintPrivate
			 */

			class CustomPaintPrivate : public QObject
			{
				Q_OBJECT
				friend class CustomPaint;

			public:
				const QScopedPointer<ForeignWindowControl> fwindow;

				screen_context_t context;
				screen_window_t window;

				bool valid;
				bool alwaysInvalidate;
				CustomPaint::cleanupPaintWindowCallback cleanupFunc;
				pthread_mutex_t mutex;
				CustomPaint* cp;

				CustomPaintPrivate(CustomPaint* customPaint);

				virtual ~CustomPaintPrivate();

#define DEFAULT_SCREEN_USAGE 0
#define DEFAULT_BUFFER_COUNT 1
#define DEFAULT_BUFFER_FORMAT 0
#define SETUP_DEFAULT 0
#define SETUP_ALWAYS_INVALIDATE (1 << 0)

				void setupWindow(int usage = DEFAULT_SCREEN_USAGE, int bufferCount = DEFAULT_BUFFER_COUNT, int bufferFormat = DEFAULT_BUFFER_FORMAT, int setupElements = SETUP_DEFAULT);
				virtual void privateWindowSetup();
				bool move(int* pos);
				bool layout(int* size);

				virtual void cleanupWindow(); //Called from destructor! Need to be careful

				virtual void setupSignalsSlots();
				virtual bool allowScreenUsageToChange() const;
				virtual bool allowCleanupCallback() const;

				virtual void invalidate(int x, int y, int width, int height, bool paint);
				virtual void invokePaint(int* rect);
				virtual void swapBuffers(screen_buffer_t buffer, int* rect);

			public slots:
				void controlFrameChanged(const QRectF& frame);
				void onCreate();
			};

			/*
			 * CustomPaintOpenGLPrivate
			 */

			class CustomPaintOpenGLPrivate : public CustomPaintPrivate
			{
				Q_OBJECT
				friend class CustomPaintOpenGL;

			public:
				CustomPaintOpenGL::Version ver;

				EGLDisplay eglDisp;
				EGLSurface eglSurf;

				EGLContext eglCtx;
				EGLConfig eglConf;

				CustomPaintOpenGLPrivate(CustomPaintOpenGL* customPaintGL, CustomPaintOpenGL::Version ver = CustomPaintOpenGL::V11);

				virtual ~CustomPaintOpenGLPrivate();

				bool changeVersion(CustomPaintOpenGL::Version ver);

				bool allowScreenUsageToChange() const;
				bool allowCleanupCallback() const;
				virtual void privateWindowSetup();

				void cleanupWindow();

				void invalidate(int x, int y, int width, int height);
				void invokePaint(int* rect);
				void swapBuffers(screen_buffer_t buffer, int* rect);
			};
		}
	}
}

#endif /* CUSTOM_PAINT_INTERNAL_H_ */
