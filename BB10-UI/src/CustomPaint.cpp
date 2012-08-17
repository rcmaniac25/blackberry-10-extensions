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

#include <Qt>

/*
 * CustomPaint functions
 */

CustomPaint::CustomPaint(bb::cascades::Container* parent) : bb::cascades::CustomControl(parent), d_ptr(new CustomPaintPrivate(this))
{
}

CustomPaint::CustomPaint(bb::cascades::Container* parent, CustomPaintPrivate* cpp) : bb::cascades::CustomControl(parent), d_ptr(cpp)
{
}

CustomPaint::~CustomPaint()
{
	Q_D(CustomPaint);

	//!!!!These are virtual functions, so be careful, though, being a variable, it gets destroyed later!!!!!!

	//Dev-accessible window cleanup
	d->invokeCleanupCallback();

	//Cleanup the window
	d->cleanupWindow();
}

void CustomPaint::setupPaintWindow(screen_window_t)
{
}

void CustomPaint::controlCreated(bool)
{
}

bool CustomPaint::registerCleanup(cleanupPaintWindowCallback cleanupFunc)
{
	Q_D(CustomPaint);

	if(!d->cleanupFunc)
	{
		d->cleanupFunc = cleanupFunc;
	}
	return d->cleanupFunc != NULL;
}

bool CustomPaint::unregisterCleanup(cleanupPaintWindowCallback cleanupFunc)
{
	Q_D(CustomPaint);

	if(d->cleanupFunc)
	{
		if(d->cleanupFunc == cleanupFunc)
		{
			d->cleanupFunc = NULL;
		}
	}

	return d->cleanupFunc == NULL;
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
	Q_D(CustomPaint);

	d->invalidate(x, y, width, height);
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

bool CustomPaint::canChangeWindowUsage() const
{
	return d_func()->allowScreenUsageToChange();
}

bool CustomPaint::createdSuccessfully() const
{
	return d_func()->valid;
}

int CustomPaint::width() const
{
	int size[2];

	if(screen_get_window_property_iv(d_func()->window, SCREEN_PROPERTY_BUFFER_SIZE, size) != 0)
	{
		size[SCREEN_WINDOW_HORZ] = 0;
	}
	return size[SCREEN_WINDOW_HORZ];
}

int CustomPaint::height() const
{
	int size[2];

	if(screen_get_window_property_iv(d_func()->window, SCREEN_PROPERTY_BUFFER_SIZE, size) != 0)
	{
		size[SCREEN_WINDOW_VERT] = 0;
	}
	return size[SCREEN_WINDOW_VERT];
}

screen_context_t CustomPaint::windowContext() const
{
	return d_func()->context;
}

void CustomPaint::setWindowGroup(const QString &windowGroup)
{
	Q_D(CustomPaint);

	const QString& group = d->fwindow->windowGroup();
	if(group != windowGroup && screen_leave_window_group(d->window) == 0)
	{
		d->fwindow->setWindowGroup(windowGroup);

		QByteArray groupArr = windowGroup.toAscii();
		if(screen_join_window_group(d->window, groupArr.constData()) != 0)
		{
			emit windowGroupChanged(windowGroup);
		}
	}
}

void CustomPaint::setWindowId(const QString &windowId)
{
	Q_D(CustomPaint);

	const QString& id = d->fwindow->windowId();
	if(id != windowId)
	{
		d->fwindow->setWindowId(windowId);

		QByteArray idArr = windowId.toAscii();
		if(screen_set_window_property_cv(d->window, SCREEN_PROPERTY_ID_STRING, idArr.length(), idArr.constData()) == 0)
		{
			emit windowIdChanged(windowId);
		}
	}
}

void CustomPaint::setWindowUsage(CustomPaint::Usage usage)
{
	Q_D(CustomPaint);

	int sUse = static_cast<int>(usage);
	int pUse;

	//Make sure we aren't doing unneeded buffer recreation
	if(d->allowScreenUsageToChange() && screen_get_window_property_iv(d->window, SCREEN_PROPERTY_USAGE, &pUse) == 0 && pUse != sUse)
	{
		//Set the usage property
		if(screen_set_window_property_iv(d->window, SCREEN_PROPERTY_USAGE, &sUse) == 0)
		{
			//Rebuild the buffers
			if(d->rebuildBuffers(NULL))
			{
				//We can signal the change now
				emit windowUsageChanged(usage);

				//We also want to repaint
				invalidate();
			}
		}
	}
}
