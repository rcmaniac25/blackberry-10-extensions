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
// Created 2008
package rebuild;

import java.util.Hashtable;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.EncodedImage;

/**
 * Library resources
 */
public final class Resources
{
	/*
	public static final int FOLDER_RED = 0;
    public static final int FOLDER_YELLOW = 1;
    public static final int FOLDER_BLUE = 2;
    public static final int FOLDER_GREEN = 3;
    public static final int FOLDER_PURPLE = 4;
    public static final int FOLDER_WHITE = 5;
    */
    
    //private static Hashtable imageTable;
    private static ResourceBundle resources;
    //private static Class thisClass;
    
    static
    {
        resources = ResourceBundle.getBundle(BBXResource.BUNDLE_ID, BBXResource.BUNDLE_NAME);
        //imageTable = new Hashtable(2);
        //try
        //{
        //    thisClass = Class.forName("rebuild.Resources");
        //}
        //catch(Exception e)
        //{
        //}
    }
    
    private Resources()
    {
    }
    
    public static String getString(int key)
    {
        return resources.getString(key);
    }
    
    public static String[] getStringArray(int key)
    {
        return resources.getStringArray(key);
    }
    
    /*
    public static EncodedImage getImage(int im)
    {
    	String path = "/rebuild/images/";
        switch(im)
        {
            case FOLDER_RED:
            	path += "RedFolder.png";
            	break;
            case FOLDER_YELLOW:
            	path += "YellowFolder.png";
            	break;
            case FOLDER_BLUE:
            	path += "BlueFolder.png";
            	break;
            case FOLDER_GREEN:
            	path += "GreenFolder.png";
            	break;
            case FOLDER_PURPLE:
            	path += "PurpleFolder.png";
            	break;
            case FOLDER_WHITE:
            	path += "WhiteFolder.png";
            	break;
            default:
            	path = null;
            	break;
        }
        if(path == null)
        {
        	return null;
        }
        if(imageTable.containsKey(path))
        {
        	return (EncodedImage)imageTable.get(path);
        }
        EncodedImage en = EncodedImage.getEncodedImageResource(path);
        imageTable.put(path, en);
        return en;
    }
    */
}
