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

#include <bb/cascades/Application>
#include <bb/cascades/Window>

CustomPaintPrivate::CustomPaintPrivate(CustomPaint* customPaint) : fwindow(new ForeignWindowControl), context(NULL), window(NULL),
	valid(false), alwaysInvalidate(false), cleanupFunc(NULL),
	cp(customPaint)
{
	//Setup for creation
	QObject::connect(cp, SIGNAL(creationCompleted()), this, SLOT(onCreate()), Qt::QueuedConnection);

	//Setup the ForeignWindow
	ForeignWindowControl* fw = fwindow.data();

	//Generic setup
	fw->setWindowGroup(Application::instance()->mainWindow()->Window::groupId());
	fw->setWindowId("CustomPaintID");
	fw->setUpdatedProperties(WindowProperty::Position | WindowProperty::Size | WindowProperty::SourceSize | WindowProperty::Visible);

	//The ForeignWindow will never handle anything itself anyway, save the internal system the trouble
	fw->setTouchPropagationMode(TouchPropagationMode::None);
}

CustomPaintPrivate::~CustomPaintPrivate()
{
}

void CustomPaintPrivate::setupWindow(int usage, int bufferCount, int bufferFormat, int setupElements)
{
	valid = false;
	context = NULL;
	window = NULL;

	alwaysInvalidate = setupElements & SETUP_ALWAYS_INVALIDATE;

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
	fwindow.data()->setWindowHandle(window);

	//Create the mutex
	pthread_mutexattr_t mutexAtt;
	pthread_mutexattr_init(&mutexAtt);
	pthread_mutexattr_setrecursive(&mutexAtt, PTHREAD_RECURSIVE_ENABLE);

	pthread_mutex_init(&mutex, &mutexAtt);

	pthread_mutexattr_destroy(&mutexAtt);

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

	//Setup the screen usage, if we should (zero is invalid)
	if(usage != DEFAULT_SCREEN_USAGE)
	{
		if (screen_set_window_property_iv(window, SCREEN_PROPERTY_USAGE, &usage) != 0)
		{
			cleanupWindow();
			return;
		}
	}

	//Setup the buffer format, if we should (zero is invalid)
	if(bufferFormat != DEFAULT_BUFFER_FORMAT)
	{
		if (screen_set_window_property_iv(window, SCREEN_PROPERTY_FORMAT, &bufferFormat) != 0)
		{
			cleanupWindow();
			return;
		}
	}

	//Set the ZOrder of the window (negative so it is below everything else)
	int z = ZDEPTH_DEPTH_MIN;
	if(screen_set_window_property_iv(window, SCREEN_PROPERTY_ZORDER, &z) != 0)
	{
		cleanupWindow();
		return;
	}

	//Last we need to do is create the buffer
	if(screen_create_window_buffers(window, bufferCount) != 0)
	{
		return;
	}

	//TODO: Move this to a handler for
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

void CustomPaintPrivate::privateWindowSetup()
{
	setupWindow();
}

bool CustomPaintPrivate::move(int* pos)
{
	if(valid && pos != NULL)
	{
		return screen_set_window_property_iv(window, SCREEN_PROPERTY_POSITION, pos) == 0;
	}
	return false;
}

bool CustomPaintPrivate::layout(int* size)
{
	bool ret = false;
	if(valid && size != NULL)
	{
		//Simply resize the buffers and invoke the dev-facing layout function

		//Lock a mutex so we don't paint and resize at the same time
		pthread_mutex_lock(&mutex);

		//Resize the buffers
		screen_set_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size);
		screen_set_window_property_iv(window, SCREEN_PROPERTY_SOURCE_SIZE, size);

		//Re-layout
		cp->layout(size[SCREEN_WINDOW_HORZ], size[SCREEN_WINDOW_VERT]);

		pthread_mutex_unlock(&mutex);

		ret = true;
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

bool CustomPaintPrivate::allowScreenUsageToChange() const
{
	return true;
}

bool CustomPaintPrivate::allowCleanupCallback() const
{
	return true;
}

void CustomPaintPrivate::setupSignalsSlots()
{
	//Layout
	QObject::connect(fwindow.data(), SIGNAL(controlFrameChanged(const QRectF&)), this, SLOT(controlFrameChanged(const QRectF&)));
}

void CustomPaintPrivate::controlFrameChanged(const QRectF& frame)
{
	bool invalidate = false;
	bool paint = false;
	int size[2];

	if(valid)
	{
		//Adjust position if we should
		if(screen_get_window_property_iv(window, SCREEN_PROPERTY_POSITION, size) == 0 &&
				(size[SCREEN_WINDOW_HORZ] != frame.x() || size[SCREEN_WINDOW_VERT] != frame.y()))
		{
			size[SCREEN_WINDOW_HORZ] = (int)floorf(frame.x());
			size[SCREEN_WINDOW_VERT] = (int)floorf(frame.y());

			invalidate = move(size);
		}

		//Adjust size if we should
		if(screen_get_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, size) == 0 &&
				(size[SCREEN_WINDOW_HORZ] != frame.width() || size[SCREEN_WINDOW_VERT] != frame.height()))
		{
			size[SCREEN_WINDOW_HORZ] = (int)floorf(frame.width());
			size[SCREEN_WINDOW_VERT] = (int)floorf(frame.height());

			//Resize/relayout the buffers
			invalidate |= paint = layout(size);
		}

		if(invalidate)
		{
			//We need to invalidate, but we might not have to repaint
			this->invalidate(0, 0, INVALIDATE_MAX_SIZE, INVALIDATE_MAX_SIZE, paint);
		}
	}
}

void CustomPaintPrivate::invalidate(int x, int y, int width, int height, bool paint)
{
	int rect[4];
	screen_buffer_t buffers[1];

	//Get size
	if(valid)
	{
		//Lock a mutex so we don't paint and resize at the same time
		pthread_mutex_lock(&mutex);

		if(screen_get_window_property_iv(window, SCREEN_PROPERTY_BUFFER_SIZE, rect) == 0)
		{
			//Get min size (if possible)
			if(fminf(width, rect[SCREEN_WINDOW_HORZ]) >= 0 && fminf(height, rect[SCREEN_WINDOW_VERT]) >= 0)
			{
				width = (int)fminf(width, rect[SCREEN_WINDOW_HORZ]);
				height = (int)fminf(height, rect[SCREEN_WINDOW_VERT]);

				//Get the screen values
				if(screen_get_window_property_pv(window, SCREEN_PROPERTY_RENDER_BUFFERS, (void **)buffers) == 0)
				{
					rect[0] = x;
					rect[1] = y;
					rect[2] = width + x;
					rect[3] = height + y;

					if(paint)
					{
						//Invoke paint signal
						this->invokePaint(rect);
					}

					//Invalidate
					this->swapBuffers(buffers[0], rect);
				}
			}
		}

		pthread_mutex_destroy(&mutex);
	}
}

void CustomPaintPrivate::invokePaint(int*)
{
	cp->paint(window);
}

void CustomPaintPrivate::swapBuffers(screen_buffer_t buffer, int* rect)
{
	screen_post_window(window, buffer, 1, rect, 0);
}

void CustomPaintPrivate::onCreate()
{
	//Setup the window
	privateWindowSetup();

	if(valid)
	{
		//Dev-accessible window setup
		cp->setupPaintWindow(window);

		//Setup signals/slots
		this->setupSignalsSlots();
	}

	//Invoke creation function
	cp->controlCreated(valid);

	if(valid)
	{
		//Invalidate the window
		this->invalidate(0, 0, INVALIDATE_MAX_SIZE, INVALIDATE_MAX_SIZE, true);

		//Set the window as root
		cp->setRoot(fwindow.data());
	}
}
