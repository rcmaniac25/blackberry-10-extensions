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

void CustomPaintPrivate::setupWindow()
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

	int size[2];
	if(screen_get_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size) == 0)
	{
		//Lock a mutex so we don't paint and resize at the same time
		pthread_mutex_lock(&mutex);

		//Start the layout
		cp->layout(size[SCREEN_WINDOW_HORZ], size[SCREEN_WINDOW_VERT]);

		pthread_mutex_unlock(&mutex);
	}

	valid = true;
}

bool CustomPaintPrivate::rebuildBuffers(int* size)
{
	bool ret = false;
	if(valid)
	{
		//Lock a mutex so we don't paint and resize at the same time
		pthread_mutex_lock(&mutex);

		//Destroy the old window buffers
		if(screen_destroy_window_buffers(window) == 0)
		{
			if(size != NULL)
			{
				//Resize the buffers
				screen_set_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size);
				screen_set_window_property_iv(window, SCREEN_PROPERTY_SOURCE_SIZE, size);
			}

			//Create the new buffers
			if(screen_create_window_buffers(window, 1) == 0)
			{
				if(size != NULL)
				{
					//Re-layout
					cp->layout(size[SCREEN_WINDOW_HORZ], size[SCREEN_WINDOW_VERT]);
				}

				ret = true;
			}
		}

		pthread_mutex_unlock(&mutex);
	}
	return ret;
}

void CustomPaintPrivate::cleanupWindow()
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

void CustomPaintPrivate::setupSignalsSlots()
{
	//Layout
	LayoutUpdateHandler::create(cp).onLayoutFrameChanged(this, SLOT(layoutHandlerChange(QRectF)));

	//TODO: Any other signals that should be handle?
}

void CustomPaintPrivate::layoutHandlerChange(const QRectF& component)
{
	bool invalidate = false;
	int size[2];

	if(valid)
	{
		//Adjust position if we should
		if(screen_get_window_property_iv(window, SCREEN_PROPERTY_POSITION, size) == 0 &&
				(size[SCREEN_WINDOW_HORZ] != component.x() || size[SCREEN_WINDOW_VERT] != component.y()))
		{
			size[SCREEN_WINDOW_HORZ] = (int)floorf(component.x());
			size[SCREEN_WINDOW_VERT] = (int)floorf(component.y());

			invalidate = screen_set_window_property_iv(window, SCREEN_PROPERTY_POSITION, size) == 0;
		}

		//Adjust size if we should
		if(screen_get_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size) == 0 &&
				(size[SCREEN_WINDOW_HORZ] != component.width() || size[SCREEN_WINDOW_VERT] != component.height()))
		{
			size[SCREEN_WINDOW_HORZ] = (int)floorf(component.width());
			size[SCREEN_WINDOW_VERT] = (int)floorf(component.height());

			//Rebuild the actual buffers
			invalidate = rebuildBuffers(size);
		}

		if(invalidate)
		{
			cp->invalidate();
		}
	}
}

void CustomPaintPrivate::onCreate()
{
	//Setup the window
	setupWindow();

	if(valid)
	{
		//Dev-accessible window setup
		cp->setupPaintWindow(window);

		//Setup signals/slots
		this->setupSignalsSlots();

		//Set the window as root
		cp->setRoot(fwindow.data());

		//Invalidate the window
		cp->invalidate();
	}

	//Invoke creation function
	cp->controlCreated(valid);
}
