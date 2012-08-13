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
		//Dev-accessible window setup
		this->setupPaintWindow(d->window);

		//Setup signals/slots
		d->setupSignalsSlots();

		//Set the window as root
		setRoot(d->fwindow.data());

		//Paint the initial window
		this->paint(d->window);
	}
}

CustomPaint::~CustomPaint()
{
	Q_D(CustomPaint);

	//Dev-accessible window cleanup
	this->cleanupPaintWindow(d->window);

	//Cleanup the window
	d->cleanupWindow();
}

void CustomPaint::setupPaintWindow(screen_window_t)
{
}

void CustomPaint::paint(screen_window_t)
{
}

void CustomPaint::cleanupPaintWindow(screen_window_t)
{
}

void CustomPaint::layout(int, int)
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
					this->paint(d->window);

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

CustomPaint::Usage CustomPaint::windowUsage() const
{
	int usage;

	if(screen_get_window_property_iv(d_func()->window, SCREEN_PROPERTY_USAGE, &usage) != 0)
	{
		usage = 0;
	}

	return static_cast<CustomPaint::Usage>(usage);
}

screen_context_t CustomPaint::windowContext() const
{
	return d_func()->context;
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

void CustomPaint::setWindowUsage(CustomPaint::Usage usage)
{
	Q_D(CustomPaint);

	int sUse = static_cast<int>(usage);
	if(screen_set_window_property_iv(d->window, SCREEN_PROPERTY_USAGE, &sUse) == 0) //XXX Does this require a recreation of the buffers?
	{
		emit windowUsageChanged(usage);
	}
}
