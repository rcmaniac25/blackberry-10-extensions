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


/**
 * A user interface component for picking a file. This imitates the native file picker and is designed to work exactly like the built in one.
 * @since BBX 1.0.1
 */
public abstract class FilePicker
{
	/**
	 * OS 5.0's version of the FilePicker. Default.
	 * @since BBX 1.3.0
	 */
	public static final int VERSION_5 = 5;
	/**
	 * OS 6.0's version of the FilePicker.
	 * @since BBX 1.3.0
	 */
	public static final int VERSION_6 = 6;
	/**
	 * OS 7.0's version of the FilePicker.
	 * @since BBX 1.3.0
	 */
	public static final int VERSION_7 = 7;
	
	/**
	 * Generic file selection dialog type (like File Explorer).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_ALL = 0;
	/**
	 * File selection dialog type for selecting music files (like a Music application).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_MUSIC = 8;
	/**
	 * File selection dialog type for selecting pictures (like a Pictures application).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_PICTURES = 1;
	/**
	 * File selection dialog type for selecting ringtones (like a Ring Tones application).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_RINGTONES = 2;
	/**
	 * File selection dialog type for selecting videos (like a Video application).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_VIDEOS = 4;
	/**
	 * File selection dialog type for selecting voice notes (Like a Voice Notes application).
	 * @since BBX 1.3.0
	 */
	public static final int VIEW_VOICE_NOTES = 16;
	
	/**
	 * Defines the functionality of a listener for when the user has selected a file.
	 */
	public static interface Listener
	{
		/**
		 * Invoked when the user has selected a file.
		 * @param selected The fully qualified URL encoded path to the file selected, for example "file:///store/home/user/documents/MyDoc.doc"
		 */
		void selectionDone(String selected);
	}
	
	FilePicker()
	{
	}
	
	/**
	 * Returns a file picker. By default, the path is the root, and there is no filter. Also by default, it is {@code VERSION_5} of the file picker.
	 * @return The instance of FilePicker.
	 */
	public static final FilePicker getInstance()
	{
		return getInstance(VERSION_5);
	}
	
	/**
	 * Returns a file picker. By default, the path is the root, and there is no filter.
	 * @param version The version of the file picker to create. Unsupported versions will get an {@code IllegalArgumentException}.
	 * @return The instance of FilePicker.
	 * @since BBX 1.3.0
	 */
	public static final FilePicker getInstance(int version)
	{
		switch(version)
		{
			case VERSION_5:
			case VERSION_6:
			case VERSION_7:
				return new FilePickerImpl(version);
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Removes the view of the file picker.
	 */
	public abstract void cancel();
	
	/**
	 * Sets a filter for DRM forward locked files. This function is only available on version 7 of the file picker.
	 * @param exclude If {@code true}, DRM forward locked files are excluded from the picker display. If {@code false}, DRM forward locked files are included in the picker display. 
	 * By default, DRM forward locked files are included in the picker.
	 * @since BBX 1.3.0
	 */
	public abstract void excludeDRMForwardLocked(boolean exclude);
	
	/**
	 * Sets the filter that is used to reduce the set of files presented to the user. You can set multiple filters by separating each filter by a colon (:). For example:
	 * <p><code><pre>
	 * FilePicker filePicker = FilePicker.getInstance();
	 * filePicker.setListener(_listener);
	 * filePicker.setView(FilePicker.VIEW_ALL); //Only available on version 6 and greater of the file picker.
	 * filePicker.setFilter(".doc:.xls:.ppt");
	 * filePicker.setPath("file:///SDCard/BlackBerry/documents/");
	 * filePicker.show());
	 * </pre></code></p>
	 * @param filterString The filter string used to reduce the set of files presented to the user. To present only files with the extension of jpg for example, enter a filter string 
	 * of ".jpg". To set multiple filters, separate each filter with a :. If null, the filter is reset.
	 */
	public abstract void setFilter(String filterString);
	
	/**
	 * Sets a listener for the user selecting a path. When the user accepts or cancels the dialog, the listener class is called.
	 * @param listener The listener that will receive notifications from this picker.
	 */
	public abstract void setListener(Listener listener);
	
	/**
	 * Sets the full URL path that will be initially displayed to the user upon presentation of the picker, for example "file:///store/home/user/documents/".
	 * @param defaultPath The default path is the location on the filesystem that will be initially displayed to the user upon presentation of the dialog. If the path does not exist, the 
	 * root of the device will be presented. If null, the filter is reset to the default file system root path.
	 */
	public abstract void setPath(String defaultPath);
	
	/**
	 * Sets the custom title of the file selection dialog, overrides the default title of a media screen (such as "Select Picture"). This function is only available on version 6 and 
	 * higher of the file picker.
	 * @param title The custom dialog title overriding the default one. If null, the title is reset to the default.
	 * @since BBX 1.3.0
	 */
	public abstract void setTitle(String title);
	
	/**
	 * Sets the view of the file selection dialog based on the currently supported Media application views. This function is only available on version 6 and higher of the file picker.
	 * @param view Type of the file selection dialog view, such as VIEW_ALL, VIEW_PICTURES, VIEW_RINGTONES, VIEW_MUSIC, VIEW_VIDEOS, VIEW_VOICE_NOTES.
	 * @since BBX 1.3.0
	 */
	public abstract void setView(int view);
	
	/**
	 * Displays the File selection popup and returns the full URL encoded path to the selected file. The function will block until a file is selected or the selection process is cancelled. 
	 * The function will return null if the user cancels.
	 * @return the fully qualified URL encoded path to the file selected, for example "file:///store/home/user/documents/MyDoc.doc"
	 */
	public abstract String show();
}
