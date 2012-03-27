//#preprocessor

//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2008-2012 Vincent Simonetti
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
// to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
//---------------------------------------------------------------------------------
//
package rebuild.comp.net.rim.device.api.ui.picker;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import rebuild.BBXResource;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Imitation of 5.0 FilePicker. This works and acts the same way as the actual FilePicker.
 * @since BBX 1.0.1
 */
public abstract class FilePicker
{
	public static interface Listener
	{
		void selectionDone(String selected);
	}
	
	FilePicker()
	{
	}
	
	public static final FilePicker getInstance()
	{
		return new FilePickerImpl();
	}
	
	public abstract void cancel();
	
	public abstract void setFilter(String filterString);
	
	public abstract void setListener(Listener listener);
	
	public abstract void setPath(String defaultPath);
	
	public abstract String show();
	
	private static class FilePickerImpl extends FilePicker
	{
		private static net.rim.device.api.i18n.ResourceBundle resources;
		
		static
		{
			FilePickerImpl.resources = net.rim.device.api.i18n.ResourceBundle.getBundle(BBXResource.BUNDLE_ID, BBXResource.BUNDLE_NAME);
		}
		
		private String rootPath;
		private String[] filters;
		private Listener listener;
		private FilePickerUI currentFPui;
		
		public void cancel()
		{
			if(this.currentFPui != null)
			{
				this.currentFPui.selectedFile = null;
				this.currentFPui.close();
			}
		}
		
		public void setFilter(String filterString)
		{
			if(filterString == null)
			{
				this.filters = null;
			}
			else
			{
				Vector v = new Vector();
				while(filterString.length() > 0)
				{
					int index = filterString.indexOf(':');
					if(index == -1)
					{
						v.addElement(filterString);
						filterString = "";
					}
					else
					{
						v.addElement(filterString.substring(0, index));
						filterString = filterString.substring(index);
					}
				}
				filters = new String[v.size()];
				v.copyInto(filters);
			}
		}
		
		public void setListener(Listener listener)
		{
			this.listener = listener;
		}
		
		public void setPath(String defaultPath)
		{
			//Could do checks for validity but the value that will be passed in will be the same as the value returned to the listener, so it will always be valid.
			this.rootPath = defaultPath;
		}
		
		public String show()
		{
			//Create picker
			final FilePickerUI fpui = this.currentFPui = new FilePickerUI(this);
			//Get UI application
			final UiApplication app = UiApplication.getUiApplication();
			//Push screen on display
			if(UiApplication.isEventDispatchThread())
			{
				app.pushModalScreen(fpui);
			}
			else
			{
				app.invokeAndWait(new Runnable()
				{
					public void run()
					{
						app.pushModalScreen(fpui);
					}
				});
			}
			//Process results
			this.currentFPui = null;
			if(this.listener != null)
			{
				this.listener.selectionDone(fpui.selectedFile);
			}
			return fpui.selectedFile;
		}
		
		private static class FilePickerUI extends PopupScreen implements FieldChangeListener
		{
			private FilePickerImpl fp;
			public String selectedFile;
			private VerticalFieldManager list;
			private FileConnection file;
			
			public FilePickerUI(FilePickerImpl fp)
			{
				super(new VerticalFieldManager(), PopupScreen.DEFAULT_MENU | PopupScreen.DEFAULT_CLOSE | PopupScreen.USE_ALL_HEIGHT | PopupScreen.USE_ALL_WIDTH);
				this.fp = fp;
				
				setupUI();
			}
			
			public void close()
			{
				if(file != null && file.isOpen())
				{
					try
					{
						file.close();
					}
					catch (IOException e)
					{
					}
				}
				super.close();
			}
			
			private void setupUI()
			{
				//Title
				add(new LabelField(FilePickerImpl.resources.getString(BBXResource.FILEPICKER_DEFAULT_TITLE)));
				
				if(this.fp.rootPath == null)
				{
					add(new LabelField(FilePickerImpl.resources.getString(BBXResource.FILEPICKER_ROOT_PATH_LABEL)));
				}
				else
				{
					//Memory and path
					HorizontalFieldManager horz = new HorizontalFieldManager();
					
					//XXX Add memory icon
					
					String path = file.getURL();
					int pathIndex = path.indexOf('/', 1);
					add(new LabelField('/' + friendlyName(path.substring(1, pathIndex)) + path.substring(pathIndex), LabelField.FIELD_LEFT | LabelField.ELLIPSIS));
					
					add(horz);
				}
				add(new SeparatorField());
				
				add(list = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH));
				populateList();
			}
			
			private void populateList()
			{
				SelectField select;
				LabelField lab;
				int len;
				int index = 0;
				list.deleteAll();
				if(this.fp.rootPath == null)
				{
					String[] roots = getRoots();
					len = roots.length;
					for(int i = 0; i < len; i++)
					{
						//In keeping with the original manner the FilePicker works
						if(roots[i].equals("system"))
						{
							//Skip "system"
							continue;
						}
						
						select = new SelectField();
						select.setChangeListener(this);
						
						//XXX Icon
						
						select.add(new LabelField(friendlyName(roots[i]), LabelField.FIELD_LEFT | LabelField.ELLIPSIS));
						
						list.add(select);
					}
				}
				else
				{
					//TODO
				}
			}
			
			private String friendlyName(String name)
			{
				String[] roots = FilePickerImpl.resources.getStringArray(BBXResource.FILEPICKER_FRIENDLY_ROOTS_SRC);
				String[] rootsF = FilePickerImpl.resources.getStringArray(BBXResource.FILEPICKER_FRIENDLY_ROOTS);
				for(int i = roots.length - 1; i >= 0; i--)
				{
					if(name.equals(roots[i]))
					{
						return rootsF[i];
					}
				}
				return name;
			}
			
			private String[] getRoots()
			{
				Enumeration en = FileSystemRegistry.listRoots();
				Vector v = new Vector();
				while(en.hasMoreElements())
				{
					v.addElement(en.nextElement());
				}
				String[] str = new String[v.size()];
				v.copyInto(str);
				int l = v.size();
				for(int i = 0; i < l; i++)
				{
					str[i] = str[i].substring(0, str[i].length() - 1);
				}
				return str;
			}
			
			public void fieldChanged(Field field, int context)
			{
				if(context != FieldChangeListener.PROGRAMMATIC)
				{
					System.out.println("Stuff");
					// TODO Auto-generated method stub
				}
			}
		}
		
		private static class SelectField extends HorizontalFieldManager
		{
			public SelectField()
			{
				this(0);
			}
			
			public SelectField(long style)
			{
				super(style | HorizontalFieldManager.FOCUSABLE | HorizontalFieldManager.READONLY | HorizontalFieldManager.USE_ALL_WIDTH);
			}
			
			protected void paint(Graphics graphics)
			{
				if(this.isFocus())
				{
					int tc = graphics.getColor();
					graphics.setColor(Color.RED);
					graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
					graphics.setColor(tc);
				}
				super.paint(graphics);
			}
			
			//TODO
		}
	}
}
