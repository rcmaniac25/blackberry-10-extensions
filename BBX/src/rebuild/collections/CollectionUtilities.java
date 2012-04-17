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
package rebuild.collections;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.util.ObjectUtilities;

/**
 * Various utilities and functions related to collections.
 * @since BBX 1.2.0
 */
public final class CollectionUtilities
{
	private CollectionUtilities()
	{
	}
	
	/**
     * Convert a {@link java.util.Vector} to an array.
     * @param vect The {@link java.util.Vector} to convert to an array.
     * @return If vect is <code>null</code> then <code>null</code> is returned. Else an array is returned of the
     * items in vect.
     */
    public static Object[] toArray(Vector vect)
    {
    	if(vect == null)
    	{
    		return null;
    	}
    	Object[] objs = new Object[vect.size()];
    	vect.copyInto(objs);
    	return objs;
    }
	
    //Taken from PDF Renderer for BlackBerry
	/**
     * Perform a shallow comparison of two Vectors to determine if they are equal.
     * @param main The first Vector for comparison.
     * @param comp The second Vector to compare to the main Vector.
     * @return <code>true</code> if the vectors are the same, <code>false</code> if otherwise.
     */
    public static boolean equals(Vector main, Vector comp)
	{
		if(main != comp)
		{
			if(main.size() != comp.size())
			{
				return false;
			}
			int len = main.size();
			for(int i = 0; i < len; i++)
			{
				if(!ObjectUtilities.objEqual(main.elementAt(i), comp.elementAt(i)))
				{
					return false;
				}
			}
		}
		return true;
	}
    
    /**
     * Perform a shallow comparison of two Hashtables to determine if they are equal.
     * @param main The first Hashtable for comparison.
     * @param comp The second Hashtable to compare to the main Hashtable.
     * @return <code>true</code> if the tables are the same, <code>false</code> if otherwise.
     */
    public static boolean equals(Hashtable main, Hashtable comp)
	{
		if(main != comp)
		{
			if(main.size() != comp.size())
			{
				return false;
			}
			if(main instanceof SynchronizedTable)
			{
				SynchronizedTable stable = (SynchronizedTable)main;
				synchronized(stable.mutex)
				{
					for(Enumeration en = stable.t.keys(); en.hasMoreElements();)
					{
						Object key = en.nextElement();
						if(!comp.containsKey(key) || !ObjectUtilities.objEqual(stable.t.get(key), comp.get(key)))
						{
							return false;
						}
					}
				}
			}
			else
			{
				for(Enumeration en = main.keys(); en.hasMoreElements();)
				{
					Object key = en.nextElement();
					if(!comp.containsKey(key) || !ObjectUtilities.objEqual(main.get(key), comp.get(key)))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
    
    //Taken from PDF Renderer for BlackBerry
    /**
	 * Copies all of the mappings from the specified table to another table.
	 * These mappings will replace any mappings that this table had for
	 * any of the keys currently in the specified table.
	 * 
	 * @param dest the table to store the elements in
	 * @param source mappings to be stored in the dest table
	 * @throws NullPointerException if the specified tables are null
	 */
	public static void putAll(Hashtable dest, Hashtable source)
	{
		if (source.size() == 0)
		{
			return;
		}
		
		//Original Sun code did a conservative resizing of the map, access to internal variables are not available so will let BlackBerry do the work for me.
		
		if(source instanceof SynchronizedTable)
		{
			SynchronizedTable stable = (SynchronizedTable)source;
			synchronized(stable.mutex)
			{
				for (Enumeration k = stable.t.keys(); k.hasMoreElements();)
				{
					Object key = k.nextElement();
					dest.put(key, stable.t.get(key));
				}
			}
		}
		else
		{
			for (Enumeration k = source.keys(); k.hasMoreElements();)
			{
				Object key = k.nextElement();
				dest.put(key, source.get(key));
			}
		}
	}
	
	/**
	 * Copies all of the elements from the specified Vector to another Vector.
	 * 
	 * @param dest the Vector to store the elements in
	 * @param source elements to be stored in the dest Vector
	 * @throws NullPointerException if the specified Vectors are null
	 */
	public static void putAll(Vector dest, Vector source)
	{
		if (source.size() == 0)
		{
			return;
		}
		
		if(source instanceof SynchronizedVector)
		{
			SynchronizedVector svect = (SynchronizedVector)source;
			synchronized (svect.mutex)
			{
				for(Enumeration e = svect.list.elements(); e.hasMoreElements();)
				{
					dest.addElement(e.nextElement());
				}
			}
		}
		else
		{
			for(Enumeration e = source.elements(); e.hasMoreElements();)
			{
				dest.addElement(e.nextElement());
			}
		}
	}
    
    /**
     * Returns a synchronized (thread-safe) Vector backed by the specified Vector. In order to guarantee serial access, it is critical that all access to the backing Vector is 
     * accomplished through the returned Vector.
     * <p>
     * It is imperative that the user manually synchronize on the returned Vector when enumerating over it:
     * <p><code><pre>
     * Vector vector = CollectionUtilities.synchronizedVector(new Vector());
     * ...
     * synchronized (vector) {
     * 	Enumeration e = vector.elements(); // Must be in synchronized block
     * 	while (e.hasMoreElements())
     * 		foo(e.nextElement());
     * }
     * </pre></code></p>
     * Failure to follow this advice may result in non-deterministic behavior.
	 * @param vector the Vector to wrap in a synchronized Vector.
	 * @return a synchronized Vector.
	 */
	public static Vector synchronizedVector(Vector vector)
	{
		return new SynchronizedVector(vector);
	}
	
	/**
	 * Returns a synchronized (thread-safe) table backed by the specified table. In order to guarantee serial access, it is critical that all access to the backing table is 
     * accomplished through the returned table.
     * <p>
     * It is imperative that the user manually synchronize on the returned table when enumerating over it:
     * <p><code><pre>
     * Hashtable table = CollectionUtilities.synchronizedTable(new Hashtable());
     * ...
     * synchronized (table) {
     * 	Enumeration e = table.elements(); // Must be in synchronized block
     * 	while (e.hasMoreElements())
     * 		foo(e.nextElement());
     * 	...
     * 	Enumeration k = table.keys(); // Must be in synchronized block
     * 	while (k.hasMoreElements())
     * 		foo(k.nextElement());
     * }
     * </pre></code></p>
     * Failure to follow this advice may result in non-deterministic behavior.
	 * @param table The table to make synchronized.
	 * @return The synchronized table.
	 */
	public static Hashtable synchronizedTable(Hashtable table)
	{
		return new SynchronizedTable(table);
	}
	
	/**
	 * Returns a wrapper on the specified Vector which has readonly access to the Vector.
	 * @param vector the Vector to wrap in a readonly Vector.
	 * @return a readonly Vector.
	 */
	public static Vector readonlyVector(Vector vector)
	{
		return new ReadonlyVector(vector);
	}
	
	/**
	 * Returns a wrapper on the specified table which has readonly access to the table.
	 * @param table the table to wrap in a readonly table.
	 * @return a readonly Vector.
	 */
	public static Hashtable readonlyTable(Hashtable table)
	{
		return new ReadonlyTable(table);
	}
}
