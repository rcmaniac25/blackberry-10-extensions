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

#include "../include/CustomPaint.h"

#include <math.h>
#include <pthread.h>

#include <bb/cascades/core/touchpropagation.h>
#include <bb/cascades/ForeignWindow>
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

			class CustomPaintPrivate : QObject
			{
				Q_OBJECT
				friend class CustomPaint;

			public:
				const QScopedPointer<ForeignWindow> fwindow;

				screen_context_t context;
				screen_window_t window;

				bool valid;
				pthread_mutex_t mutex;
				CustomPaint* cp;

				CustomPaintPrivate(CustomPaint* customPaint) : fwindow(new ForeignWindow), cp(customPaint)
				{
					//Setup for creation
					QObject::connect(cp, SIGNAL(creationCompleted()), this, SLOT(onCreate()));

					//Setup the ForeignWindow
					ForeignWindow* fw = fwindow.data();

					//Generic setup
					fw->setWindowGroup(ForeignWindow::mainWindowGroupId());
					fw->setWindowId("CustomPaintID");

					//The ForeignWindow will never handle anything itself anyway, save the internal system the trouble
					fw->setTouchPropagationMode(TouchPropagationMode::None);
				}

				virtual ~CustomPaintPrivate()
				{
				}

				void setupWindow();

				void cleanupWindow();

				void setupSignalsSlots();

			public slots:
				void layoutHandlerChange(const QRectF& component);
				void onCreate();
			};
		}
	}
}

#endif /* CUSTOM_PAINT_INTERNAL_H_ */
