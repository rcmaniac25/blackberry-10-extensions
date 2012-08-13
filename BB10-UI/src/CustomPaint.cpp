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

#include "../include/CustomPaint.h"

#include <math.h>
#include <pthread.h>

#include <bb/cascades/ForeignWindow>
#include <bb/cascades/LayoutUpdateHandler>

using namespace bb::cascades;
using namespace rebuild::ui::component;

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
#define SCREEN_WINDOW_HORZ 0
#define SCREEN_WINDOW_VERT 1
#define INVALIDATE_MAX_SIZE 0x7FFFFFFF
#define ZDEPTH_DEPTH_MIN 0x80000000

			/*
			 * CustomPaintPrivate
			 */

			class CustomPaintPrivate : QObject
			{
				Q_OBJECT

			public:
				const QScopedPointer<ForeignWindow> fwindow;

				screen_context_t context;
				screen_window_t window;

				bool valid;
				pthread_mutex_t mutex;
				CustomPaint* cp;

				CustomPaintPrivate(CustomPaint* customPaint) : fwindow(new ForeignWindow), cp(customPaint)
				{
					ForeignWindow* fw = fwindow.data();

					fw->setWindowGroup(ForeignWindow::mainWindowGroupId());
					fw->setWindowId("CustomPaintID");
				}

				virtual ~CustomPaintPrivate()
				{
				}

				void setupWindow()
				{
					valid = false;
					context = NULL;
					window = NULL;

					QByteArray groupArr = fwindow->windowGroup().toAscii();
					QByteArray idArr = fwindow->windowId().toAscii();

					//Create the screen context
					if(screen_create_context(&context, SCREEN_APPLICATION_CONTEXT) != 0)
					{
						return;
					}

					//Create the window
					if(screen_create_window_type(&window, context, SCREEN_CHILD_WINDOW) != 0)
					{
						screen_destroy_context(context);
						context = NULL;
						return;
					}
					fwindow.data()->setWindowHandle((unsigned long)window);

					//Setup group and ID
					if(screen_join_window_group(window, groupArr.constData()) != 0)
					{
						cleanupWindow();
						return;
					}
					if(screen_set_window_property_cv(window, SCREEN_PROPERTY_ID_STRING, idArr.length(), idArr.constData()) != 0)
					{
						cleanupWindow();
						return;
					}

					//Set the ZOrder of the window (negative so it is below everything else)
					int z = ZDEPTH_DEPTH_MIN;
					if(screen_set_window_property_iv(window, SCREEN_PROPERTY_ZORDER, &z) != 0)
					{
						cleanupWindow();
						return;
					}

					//Last we need to do is create the buffer
					if(screen_create_window_buffers(window, 1) != 0)
					{
						return;
					}

					//Create the mutex
					pthread_mutex_init(&mutex, NULL);

					valid = true;
				}

				void cleanupWindow()
				{
					//Cleanup window
					screen_destroy_window(window);
					fwindow.data()->setWindowHandle(0);

					//Cleanup context
					screen_destroy_context(context);

					//Cleanup the mutex
					pthread_mutex_destroy(&mutex);

					valid = false;
				}

				void setupSignalsSlots()
				{
					//Layout
					LayoutUpdateHandler::create(cp).onLayoutFrameChanged(this, SLOT(layoutHandlerChange(QRectF)));

					//TODO: Any other signals that should be handle?
				}

			public slots:
				void layoutHandlerChange(const QRectF& component)
				{
					int size[2];

					if(valid)
					{
						//Adjust position if we should
						if(screen_get_window_property_iv(window, SCREEN_PROPERTY_POSITION, size) == 0 &&
								(size[SCREEN_WINDOW_HORZ] != component.x() || size[SCREEN_WINDOW_VERT] != component.y()))
						{
							size[SCREEN_WINDOW_HORZ] = (int)floorf(component.x());
							size[SCREEN_WINDOW_VERT] = (int)floorf(component.y());

							screen_set_window_property_iv(window, SCREEN_PROPERTY_POSITION, size);
						}

						//Adjust size if we should
						if(screen_get_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size) == 0 &&
								(size[SCREEN_WINDOW_HORZ] != component.width() || size[SCREEN_WINDOW_VERT] != component.height()))
						{
							size[SCREEN_WINDOW_HORZ] = (int)floorf(component.width());
							size[SCREEN_WINDOW_VERT] = (int)floorf(component.height());

							//Lock a mutex so we don't paint and resize at the same time
							pthread_mutex_lock(&mutex);

							//Destroy the old window buffers
							if(screen_destroy_window_buffers(window) == 0)
							{
								//Resize the buffers
								screen_set_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size);
								screen_set_window_property_iv(window, SCREEN_PROPERTY_SOURCE_SIZE, size);

								//Create the new buffers
								screen_create_window_buffers(window, 1);
							}

							pthread_mutex_unlock(&mutex);

							//Invalidate window
							cp->invalidate();
						}
					}
				}
			};
		}
	}
}

/*
 * CustomPaint functions
 */

CustomPaint::CustomPaint(bb::cascades::Container* parent) : bb::cascades::CustomControl(parent), d_ptr(new CustomPaintPrivate(this))
{
	Q_D(CustomPaint);

	//Setup the window
	d->setupWindow();

	if(d->valid)
	{
		//Setup signals/slots
		d->setupSignalsSlots();

		//Set the window as root
		setRoot(d->fwindow.data());
	}
}

CustomPaint::~CustomPaint()
{
	Q_D(CustomPaint);

	d->cleanupWindow();
}

void CustomPaint::paint(screen_window_t)
{
}

/*
 * Invalidate functions
 */

void CustomPaint::invalidate()
{
	//The system gets the min-size
	invalidate(0, 0, INVALIDATE_MAX_SIZE, INVALIDATE_MAX_SIZE);
}

void CustomPaint::invalidate(const QRect* size)
{
	if(size)
	{
		invalidate(size->x(), size->y(), size->width(), size->height());
	}
}

void CustomPaint::invalidate(const QRect& size)
{
	invalidate(&size);
}

void CustomPaint::invalidate(const QRectF* size)
{
	if(size)
	{
		invalidate(size->x(), size->y(), size->width(), size->height());
	}
}

void CustomPaint::invalidate(const QRectF& size)
{
	invalidate(&size);
}

void CustomPaint::invalidate(float x, float y, float width, float height)
{
	if(width >= 0 && height >= 0)
	{
		invalidate((int)floorf(x), (int)floorf(y), (int)floorf(width), (int)floorf(height));
	}
}

void CustomPaint::invalidate(int x, int y, int width, int height)
{
	int rect[4];
	screen_buffer_t buffers[1];
	Q_D(CustomPaint);

	//Get size
	if(d->valid)
	{
		//Lock a mutex so we don't paint and resize at the same time
		pthread_mutex_lock(&d->mutex);

		if(screen_get_window_property_iv(d->window, SCREEN_PROPERTY_BUFFER_SIZE, rect) == 0)
		{
			//Get min size (if possible)
			if(fminf(width, rect[SCREEN_WINDOW_HORZ]) >= 0 && fminf(height, rect[SCREEN_WINDOW_VERT]) >= 0)
			{
				width = (int)fminf(width, rect[SCREEN_WINDOW_HORZ]);
				height = (int)fminf(height, rect[SCREEN_WINDOW_VERT]);

				//Get the screen values
				if(screen_get_window_property_pv(d->window, SCREEN_PROPERTY_RENDER_BUFFERS, (void **)buffers) == 0)
				{
					rect[0] = x;
					rect[1] = y;
					rect[2] = width + x;
					rect[3] = height + y;

					//Invoke paint signal
					paint(d->window);

					//Invalidate
					screen_post_window(d->window, buffers[0], 1, rect, 0);
				}
			}
		}

		pthread_mutex_destroy(&d->mutex);
	}
}

/*
 * Properties
 */

QString CustomPaint::windowGroup() const
{
	return d_func()->fwindow->windowGroup();
}

QString CustomPaint::windowId() const
{
	return d_func()->fwindow->windowId();
}

int CustomPaint::windowUsage() const
{
	int usage;

	if(screen_get_window_property_iv(d_func()->window, SCREEN_PROPERTY_USAGE, &usage) != 0)
	{
		usage = 0;
	}

	return usage;
}

void CustomPaint::setWindowGroup(const QString &windowGroup)
{
	Q_D(CustomPaint);

	d->fwindow->setWindowGroup(windowGroup);

	emit windowGroupChanged(windowGroup);
}

void CustomPaint::setWindowId(const QString &windowId)
{
	Q_D(CustomPaint);

	d->fwindow->setWindowId(windowId);

	emit windowIdChanged(windowId);
}

void CustomPaint::setWindowUsage(int usage)
{
	Q_D(CustomPaint);

	if(screen_set_window_property_iv(d->window, SCREEN_PROPERTY_USAGE, &usage) == 0)
	{
		emit windowUsageChanged(usage);
	}
}
