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

#ifndef CUSTOM_PAINT_H_
#define CUSTOM_PAINT_H_

#include <screen/screen.h>

#include <QRect>
#include <QRectF>

#include <bb/cascades/CustomControl>

namespace rebuild
{
	namespace ui
	{
		namespace component
		{
			class CustomPaintPrivate;
			class CustomPaint : public bb::cascades::CustomControl
			{
			private:
				Q_OBJECT
				Q_FLAGS(Usage)

				Q_PROPERTY(QString windowGroup READ windowGroup WRITE setWindowGroup NOTIFY windowGroupChanged FINAL)
				Q_PROPERTY(QString windowId READ windowId WRITE setWindowId NOTIFY windowIdChanged FINAL)
				Q_PROPERTY(CustomPaint::Usage windowUsage READ windowUsage WRITE setWindowUsage NOTIFY windowUsageChanged FINAL)

			public:
				enum Usage
				{
					Read = SCREEN_USAGE_READ,
					Write = SCREEN_USAGE_WRITE,
					Native = SCREEN_USAGE_NATIVE,
					OpenGL_ES1 = SCREEN_USAGE_OPENGL_ES1,
					OpenGL_ES2 = SCREEN_USAGE_OPENGL_ES2,
					OpenVG = SCREEN_USAGE_OPENVG,
					Video = SCREEN_USAGE_VIDEO,
					Capture = SCREEN_USAGE_CAPTURE,
					Rotation = SCREEN_USAGE_ROTATION,
					Overlay = SCREEN_USAGE_OVERLAY
				};

				explicit CustomPaint(bb::cascades::Container* parent = NULL);

				virtual ~CustomPaint();

				Q_INVOKABLE void invalidate();
				void invalidate(const QRect* size);
				void invalidate(const QRect& size);
				void invalidate(const QRectF* size);
				void invalidate(const QRectF& size);
				void invalidate(float x, float y, float width, float height);
				void invalidate(int x, int y, int width, int height);

				QString windowGroup() const;
				QString windowId() const;
				Usage windowUsage() const;

				void setWindowGroup(const QString &windowGroup);
				void setWindowId(const QString &windowId);
				void setWindowUsage(Usage usage);

			protected:
				screen_context_t windowContext() const;

				virtual void setupPaintWindow(screen_window_t window);
				virtual void paint(screen_window_t window);
				virtual void cleanupPaintWindow(screen_window_t window);
				virtual void layout(int width, int height);

			Q_SIGNALS:
				void windowGroupChanged(const QString& windowGroup);
				void windowIdChanged(const QString& windowId);
				void windowUsageChanged(Usage usage);

			private:
				/*! @cond PRIVATE */
				const QScopedPointer<CustomPaintPrivate> d_ptr;

				Q_DECLARE_PRIVATE(CustomPaint)
				Q_DISABLE_COPY(CustomPaint)
				/*! @endcond */
			};
		}
	}
}
QML_DECLARE_TYPE(rebuild::ui::component::CustomPaint)

#endif /* CUSTOM_PAINT_H_ */
