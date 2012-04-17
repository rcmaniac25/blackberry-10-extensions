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

import javax.microedition.io.file.FileConnection;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import rebuild.BBXResource;

/**
 * FilePicker UI for OS 5.0
 */
final class FilePickerUI5 extends PopupScreen implements FilePickerImpl.FilePickerUI, FieldChangeListener
{
	private FilePickerImpl fp;
	private String selectedFile;
	private VerticalFieldManager list;
	private FileConnection file;
	
	public FilePickerUI5(FilePickerImpl fp)
	{
		super(new VerticalFieldManager(), PopupScreen.DEFAULT_MENU | PopupScreen.DEFAULT_CLOSE | PopupScreen.USE_ALL_HEIGHT | PopupScreen.USE_ALL_WIDTH);
		this.fp = fp;
		
		setupUI();
	}
	
	public String getSelectedFile()
	{
		return selectedFile;
	}
	
	public void resetSelectedFile()
	{
		selectedFile = null;
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
			add(new LabelField('/' + FilePickerImpl.friendlyName(path.substring(1, pathIndex)) + path.substring(pathIndex), LabelField.FIELD_LEFT | LabelField.ELLIPSIS));
			
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
			String[] roots = FilePickerImpl.getRoots();
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
				
				select.add(new LabelField(FilePickerImpl.friendlyName(roots[i]), LabelField.FIELD_LEFT | LabelField.ELLIPSIS));
				
				list.add(select);
			}
		}
		else
		{
			//TODO
		}
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
