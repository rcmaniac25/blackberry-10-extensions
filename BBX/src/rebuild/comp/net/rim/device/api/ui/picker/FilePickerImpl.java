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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

import rebuild.BBXResource;

/**
 * FilePicker implementation for all OS versions
 */
final class FilePickerImpl extends FilePicker
{
	static net.rim.device.api.i18n.ResourceBundle resources;
	
	static
	{
		FilePickerImpl.resources = net.rim.device.api.i18n.ResourceBundle.getBundle(BBXResource.BUNDLE_ID, BBXResource.BUNDLE_NAME);
	}
	
	private int version;
	boolean excludeDRMForwardLock;
	String rootPath;
	private String[] filters;
	private Listener listener;
	private FilePickerUI currentFPui;
	
	public FilePickerImpl(int version)
	{
		this.version = version;
		this.excludeDRMForwardLock = false;
	}
	
	public void cancel()
	{
		if(this.currentFPui != null)
		{
			this.currentFPui.resetSelectedFile();
			this.currentFPui.close();
		}
	}
	
	public void excludeDRMForwardLocked(boolean exclude)
	{
		if(this.version < VERSION_7)
		{
			throw new UnsupportedOperationException();
		}
		else
		{
			this.excludeDRMForwardLock = exclude;
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
		//Could do checks for validity but the value that will be passed in will be the same as the value returned to the listener, so it will always be valid. If it isn't then the UI will complain.
		this.rootPath = defaultPath;
	}
	
	public void setTitle(String title)
	{
		if(this.version < VERSION_6)
		{
			throw new UnsupportedOperationException();
		}
		else
		{
			//TODO
		}
	}
	
	public void setView(int view)
	{
		if(this.version < VERSION_6)
		{
			throw new UnsupportedOperationException();
		}
		else
		{
			//TODO
		}
	}
	
	public String show()
	{
		//Create picker
		final FilePickerUI fpui;
		switch(version)
		{
			case VERSION_5:
				fpui = new FilePickerUI5(this);
				break;
			case VERSION_6:
				//TODO
			case VERSION_7:
				//TODO
			default:
				throw new UnsupportedOperationException();
		}
		this.currentFPui = fpui;
		
		//Get UI application
		final UiApplication app = UiApplication.getUiApplication();
		
		//Push screen on display
		if(UiApplication.isEventDispatchThread())
		{
			app.pushModalScreen((Screen)fpui);
		}
		else
		{
			app.invokeAndWait(new Runnable()
			{
				public void run()
				{
					app.pushModalScreen((Screen)fpui);
				}
			});
		}
		
		//Process results
		this.currentFPui = null;
		if(this.listener != null)
		{
			this.listener.selectionDone(fpui.getSelectedFile());
		}
		return fpui.getSelectedFile();
	}
	
	//Helper functions
	static String friendlyName(String name)
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
	
	static String[] getRoots()
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
	
	//All that is really needed by the FilePicker from the UI
	interface FilePickerUI
	{
		public String getSelectedFile();
		public void resetSelectedFile();
		public void close();
	}
}
