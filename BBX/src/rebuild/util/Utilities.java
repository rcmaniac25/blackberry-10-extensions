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

import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

//#ifndef NO_SIGNING
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.RuntimeStore;
//#endif
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
//#ifndef BlackBerrySDK4.5.0
import net.rim.device.api.util.LongVector;
//#endif
import rebuild.BBXResource;
import rebuild.Resources;

/**
 * A collection of various utility functions.
 * @since BBX 1.1.0
 */
public final class Utilities
{
	private static final long SINGLETON_STORAGE_ID = 0xF57E30F85CB3A690L;
	
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
	
//#ifndef NO_SIGNING
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
//#endif
    
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
    
    /**
     * Format a Calendar in the same manner as the Standard C function "ctime"
     * @param timer The calendar to format.
     * @return The formatted calendar String.
     * @since BBX 1.2.0
     */
    public static String ctime(Calendar timer)
	{
    	//ctime format: "Www Mmm dd hh:mm:ss yyyy" and appends \n to the end
    	return new SimpleDateFormat(Resources.getString(BBXResource.UTIL_CTIME_FORMAT)).format(timer, new StringBuffer(), null).toString();//Ex: Sun Mar 08 2:10:20 2006
	}
    
    //Based off of the singleton functions from LCMS for BlackBerry (which is a slightly modified version of the one from PDF Renderer for BlackBerry)
    /**
	 * Get a singleton object.
	 * This is not the same as just calling RuntimeStore and is managed for memory usage. It will be cleaned up when {@link #singltonStorageCleanup()} is called.
	 * @param uid The ID of the object to get.
	 * @return The object (if it was set using {@link #singletonStorageSet(long, Object)}) or null if it doesn't exist or was not set using {@link #singletonStorageSet(long, Object)}.
	 * @since BBX 1.2.0
	 */
//#ifdef NO_SIGNING
    /*
//#endif
	public synchronized static Object singletonStorageGet(long uid)
	{
		RuntimeStore store = RuntimeStore.getRuntimeStore();
		Object obj;
		if((obj = store.get(SINGLETON_STORAGE_ID)) != null)
		{
//#ifndef BlackBerrySDK4.5.0
			LongVector v = (LongVector)obj;
			if(v.contains(uid))
			{
				return store.get(uid);
			}
//#else
			long[] v = (long[])obj;
			int len = v.length;
			boolean hasIndex = false;
			for(int i = 1; i < len; i++)
			{
				if(v[i] == uid)
				{
					hasIndex = true;
					break;
				}
			}
			if(hasIndex)
			{
				return store.get(uid);
			}
//#endif
		}
		return null;
	}
//#ifdef NO_SIGNING
	*/
//#endif
	
	/**
	 * Set a singleton object.
	 * @param uid The ID of the object to set. If this happens to be an object that already exists but was not set using this function then an exception will be thrown.
	 * @param obj The singleton object to set or null if the current object should be removed.
	 * @return The previous object (if it was set using {@link #singletonStorageSet(long, Object)}) or null if it didn't exist or was not set using {@link #singletonStorageSet(long, Object)}.
	 * @since BBX 1.2.0
	 */
//#ifdef NO_SIGNING
	/*
//#endif
	public synchronized static Object singletonStorageSet(long uid, Object obj)
	{
		RuntimeStore store = RuntimeStore.getRuntimeStore();
		Object objS;
//#ifndef BlackBerrySDK4.5.0
		LongVector v;
//#else
		long[] v;
//#endif
		if((objS = store.get(SINGLETON_STORAGE_ID)) != null) //Singleton list exists
		{
//#ifndef BlackBerrySDK4.5.0
			v = (LongVector)objS;
			if(v.contains(uid))
			{
				objS = store.get(uid); //Get previous value
				if(obj != null)
				{
					store.replace(uid, obj); //Replace the current object
				}
				else
				{
					store.remove(uid); //Remove the object
					v.removeElement(uid);
				}
				return objS; //Return previous object
			}
			else if(obj != null) //Does not exist in Singleton list exists, new
			{
				store.put(uid, obj);
				v.addElement(uid);
				return null;
			}
//#else
			v = (long[])objS;
			int len = v.length;
			int index = -1;
			for(int i = 1; i < len; i++)
			{
				if(v[i] == uid)
				{
					index = i;
					break;
				}
			}
			if(index >= 1)
			{
				objS = store.get(uid); //Get previous value
				if(obj != null)
				{
					store.replace(uid, obj); //Replace the current object
				}
				else
				{
					store.remove(uid); //Remove the object
					System.arraycopy(v, index + 1, v, index, (int)((--v[0]) - index));
				}
				return objS; //Return previous object
			}
			else if(obj != null) //Does not exist in Singleton list exists, new
			{
				store.put(uid, obj);
				if(v[0] >= v.length)
				{
					long[] t = new long[v.length * 2];
					System.arraycopy(v, 0, t, 0, v.length);
					v = t;
					store.replace(SINGLETON_STORAGE_ID, v);
				}
				v[(int)(v[0]++)] = uid;
				return null;
			}
//#endif
		}
		if(obj != null) //If the function hasn't returned yet and the object is not null then the Singleton list doesn't exist yet
		{
//#ifndef BlackBerrySDK4.5.0
			v = new LongVector(); //Create the list and add the object
//#else
			v = new long[1 + 4];
			v[0] = 1;
//#endif
			store.put(SINGLETON_STORAGE_ID, v);
			store.put(uid, obj); //Will throw an exception if already there
//#ifndef BlackBerrySDK4.5.0
			v.addElement(uid);
//#else
			v[(int)(v[0]++)] = uid;
//#endif
		}
		return null;
	}
//#ifdef NO_SIGNING
	*/
//#endif
	
	/**
	 * Remove all singleton objects.
	 * @since BBX 1.2.0
	 */
//#ifdef NO_SIGNING
	/*
//#endif
	public synchronized static void singltonStorageCleanup()
	{
		RuntimeStore store = RuntimeStore.getRuntimeStore();
		Object obj;
		if((obj = store.get(SINGLETON_STORAGE_ID)) != null)
		{
//#ifndef BlackBerrySDK4.5.0
			LongVector v = (LongVector)obj;
			store.remove(SINGLETON_STORAGE_ID);
			int len = v.size();
			for(int i = 0; i < len; i++)
			{
				store.remove(v.elementAt(i));
			}
//#else
			long[] v = (long[])obj;
			store.remove(SINGLETON_STORAGE_ID);
			int len = v.length;
			for(int i = 1; i < len; i++)
			{
				store.remove(v[i]);
			}
//#endif
		}
	}
//#ifdef NO_SIGNING
	*/
//#endif
}
