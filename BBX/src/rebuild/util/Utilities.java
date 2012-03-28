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
// Created 2009
package rebuild.util;

import java.util.Random;
import java.util.Vector;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import rebuild.BBXResource;
import rebuild.Resources;

/**
 * A collection of various utility functions.
 */
public final class Utilities
{
	private static Random ran1;
    private static Random ran2;
    private static Random ran3;
    private static Random ran4;
    private static Random ran5;
    
    static
    {
    	//The worst possible method for generating a random value. It was done a while ago and is still pusdo-random.
    	
        try
        {
            Application app = UiApplication.getUiApplication();
            if(app == null)
            {
                app = UiApplication.getApplication();
            }
            ran1 = new Random(app.getProcessId());
        }
        catch(Exception e)
        {
            ran1 = new Random();
            try
            {
                Thread.sleep(300L);
            }
            catch(Exception k)
            {
            }
        }
        ran2 = new Random();
        ran3 = new Random((ran1.nextInt() % ran2.nextInt() < ran1.nextInt()) ? ran2.nextLong() : ran1.nextLong());
        long ll = ran3.nextLong() - ran2.nextLong();
        if(ll < 0L)
        {
            ll = -ll;
        }
        ran4 = new Random(ll);
        try
        {
            ll = (ran1.nextLong() + ran2.nextInt()) / ran3.nextInt(ran4.nextInt());
        }
        catch(Exception e)
        {
            ll = ran4.nextLong();
        }
        if(ll < 0L)
        {
            ll = -ll;
        }
        ran5 = new Random(ll);
    }
    
    private Utilities()
    {
    }
    
    /**
     * Get multiple random numbers.
     * @param data The array of bytes to fill with random numbers.
     * @throws NullPointerException If data is null.
     */
    public static void RNGGetBytes(byte[] data)
    {
        int l = data.length;
        int[] d = new int[l];
        RNGGetBytes(d);
        for(int i = 0; i < l; i++)
        {
            data[i] = (byte)d[i];
        }
    }
    
    /**
     * Get multiple random numbers., no zeros
     * @param data The array of bytes to fill with random numbers.
     * @throws NullPointerException If data is null.
     */
    public static void GetNonZeroBytes(byte[] data)
    {
        int l = data.length;
        int[] d = new int[l];
        GetNonZeroBytes(d);
        for(int i = 0; i < l; i++)
        {
            data[i] = (byte)d[i];
        }
    }
    
    /**
     * Get multiple random numbers.
     * @param data The array of bytes to fill with random numbers.
     * @throws NullPointerException If data is null.
     */
    public static void RNGGetBytes(int[] data)
    {
        if (data == null)
        {
            throw new NullPointerException("data");
        }
        if(data.length == 0)
        {
            return;
        }
        int l = data.length;
        int t = 0;
        for(int i = 0; i < l; i++)
        {
            t = ran1.nextInt();
            data[i] = (int)(ran2.nextInt() / t);
            if(data[i] > ran2.nextInt())
            {
                data[i] = (int)(ran4.nextInt() - data[i]);
            }
            if(ran3.nextInt() > ran5.nextInt())
            {
                data[i] = -data[i];
            }
        }
    }
    
    /**
     * Get multiple random numbers., no zeros
     * @param data The array of bytes to fill with random numbers.
     * @throws NullPointerException If data is null.
     */
    public static void GetNonZeroBytes(int[] data)
    {
        RNGGetBytes(data);
        int l = data.length;
        for(int i = 0; i < l; i++)
        {
            while(data[i] == 0)
            {
                data[i] = ran5.nextInt();
            }
        }
    }
    
    /**
     * Convert a {@link java.util.Vector} to an array.
     * @param vect The {@link java.util.Vector} to convert to an array.
     * @return If vect is <code>null</code> then <code>null</code> is returned. Else an array is returned of the
     * items in vect.
     */
    public static Object[] ToArray(Vector vect)
    {
    	if(vect == null)
    	{
    		return null;
    	}
    	Object[] objs = new Object[vect.size()];
    	vect.copyInto(objs);
    	return objs;
    }
    
    /**
	 * Checks for a condition; if the condition is false, outputs two specified messages and displays a message box that shows the message.
	 * @param condition The conditional expression to evaluate. If this is <code>true</code>, the specified messages are not sent and the message box is not displayed. 
	 */
	public static void assert(boolean condition)
	{
		assert(condition, "");
	}
	
	/**
	 * Checks for a condition; if the condition is false, outputs two specified messages and displays a message box that shows the message.
	 * @param condition The conditional expression to evaluate. If this is <code>true</code>, the specified messages are not sent and the message box is not displayed. 
	 * @param message A general message.
	 */
	public static void assert(boolean condition, String message)
	{
		assert(condition, message, null);
	}
	
	/**
	 * Checks for a condition; if the condition is false, outputs two specified messages and displays a message box that shows the message.
	 * @param condition The conditional expression to evaluate. If this is <code>true</code>, the specified messages are not sent and the message box is not displayed. 
	 * @param message A general message.
	 * @param detailMessage A detailed message.
	 */
	public static void assert(boolean condition, String message, String detailMessage)
	{
		if(!condition)
		{
			StringBuffer build = new StringBuffer(Resources.getString(BBXResource.ASSERT_FAIL));
			build.append(' ');
			if(message != null)
			{
				build.append(message);
			}
			else
			{
				build.append(Resources.getString(BBXResource.ASSERT_UNKNOWN_ERROR));
			}
			if(detailMessage != null)
			{
				build.append(' ');
				build.append(detailMessage);
			}
			//TODO: Later implement the ability for other applications to catch asserts
			System.err.println(build.toString());
			Dialog.alert(message); //Would have liked to have the stack trace.
		}
	}
	
	/**
     * Setup application permissions, this will assign a set of permissions to a application. If no permissions need to be
     * set then nothing will change. If there are any permissions need to be assigned then the permission dialog is
     * displayed to the user.
     * @param requiredPerms An array of the required permissions, this should be permissions from {@link ApplicationPermissions}.
     * @return true if the user accepts all of the requested permissions or no permissions are required, or false if 
     * the user or device policies reject at least one of the requested permissions.
     */
    public static boolean setupPermissions(int[] requiredPerms)
    {
    	if(requiredPerms == null)
    	{
    		throw new NullPointerException();
    	}
    	if(requiredPerms.length == 0)
    	{
	        ApplicationPermissionsManager man = ApplicationPermissionsManager.getInstance();
	        ApplicationPermissions perms = man.getApplicationPermissions();
	        boolean change = false;
	        for(int i = 0; i < requiredPerms.length; i++)
	        {
	            if(perms.containsPermissionKey(requiredPerms[i]))
	            {
	                if(perms.getPermission(requiredPerms[i]) != ApplicationPermissions.VALUE_ALLOW)
	                {
	                    change = true;
	                    perms.addPermission(requiredPerms[i]);
	                }
	            }
	            else
	            {
	                change = true;
	                perms.addPermission(requiredPerms[i]);
	            }
	        }
	        if(change)
	        {
	            change = man.invokePermissionsRequest(perms);
	        }
	        return change;
    	}
    	return true;
    }
    
    //Not the greatest UI components, but still an attempt
    
    /**
     * Set the field to either enabled/disabled.
     * @param f The field to enabled/disabled.
     * @param isDisabled True if the field should be disabled, false if otherwise.
     */
//#ifdef BlackBerrySDK4.5.0
    /*
//#endif
    public static void setDisabled(Field f, boolean isDisabled)
    {
        f.setEditable(!isDisabled);
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK5.0.0
        f.setEnabled(!isDisabled);
//#endif
        int state = f.getVisualState();
        if(isDisabled)
        {
            state &= ~Field.VISUAL_STATE_NORMAL;
            state |= Field.VISUAL_STATE_DISABLED;
        }
        else
        {
            state &= ~Field.VISUAL_STATE_DISABLED;
            state |= Field.VISUAL_STATE_NORMAL;
        }
        f.setVisualState(state);
    }
//#ifdef BlackBerrySDK4.5.0
    */
//#endif
    
    /**
     * Determine if the specified field is disabled.
     * @param f The field to check.
     * @return True if the field is disabled, false if otherwise.
     */
//#ifdef BlackBerrySDK4.5.0
    /*
//#endif
    public static boolean isDisabled(Field f)
    {
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK5.0.0
    	boolean edit = !f.isEnabled();
//#else
    	boolean edit = !f.isEditable();
//#endif
        boolean dis = (f.getVisualState() & Field.VISUAL_STATE_DISABLED) == Field.VISUAL_STATE_DISABLED;
        return edit && dis;
    }
//#ifdef BlackBerrySDK4.5.0
    */
//#endif
}
