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

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.util.text.StringUtilities;

/**
 * Represents the version number.
 * @since BBX 1.0.1
 */
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.7.0
public final class Version
//#else
public final class Version implements Comparable
//#endif
{
	private int _Build;
    private int _Major;
    private int _Minor;
    private int _Revision;
    
    /**
     * Initializes a new instance of the {@link Version} class.
     */
    public Version()
    {
        this._Build = -1;
        this._Revision = -1;
        this._Major = 0;
        this._Minor = 0;
    }
    
    /**
     * Initializes a new instance of the {@link Version} class using the specified string.
     * @param version A string containing the major, minor, build, and revision numbers, where each number is delimited with a period character ('.').
     * @throws NullPointerException <code>version</code> is null.
     * @throws IllegalArgumentException <ul>
     * <li><code>version</code> has fewer than two components or more than four components</li>
     * <li>A major, minor, build, or revision component is less than zero.</li>
     * </ul>
     */
    public Version(String version)
    {
    	this._Build = -1;
        this._Revision = -1;
        if(version == null)
        {
        	throw new NullPointerException("version");
        }
        String[] strArray = StringUtilities.split(version, '.');
        int length = strArray.length;
        if ((length < 2) || (length > 4))
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONSTRING));
        }
        this._Major = Integer.parseInt(strArray[0]);
        if (this._Major < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        this._Minor = Integer.parseInt(strArray[1]);
        if (this._Minor < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        length -= 2;
        if (length > 0)
        {
        	this._Build = Integer.parseInt(strArray[2]);
            if (this._Build < 0)
            {
            	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
            }
            length--;
            if (length > 0)
            {
            	this._Revision = Integer.parseInt(strArray[3]);
                if (this._Revision < 0)
                {
                	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
                }
            }
        }
    }
    
    /**
     * Initializes a new instance of the {@link Version} class using the specified major and minor values.
     * @param major The major version number.
     * @param minor The minor version number.
     * @throws IllegalArgumentException <code>major</code> or <code>minor</code> is less than zero.
     */
    public Version(int major, int minor)
    {
    	this._Build = -1;
        this._Revision = -1;
        if (major < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        if (minor < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        this._Major = major;
        this._Minor = minor;
    }
    
    /**
     * Initializes a new instance of the {@link Version} class using the specified major, minor, and build values.
     * @param major The major version number.
     * @param minor The minor version number.
     * @param build The build number.
     * @throws IllegalArgumentException <code>major</code>, <code>minor</code>, or <code>build</code> is less than zero.
     */
    public Version(int major, int minor, int build)
    {
    	this._Build = -1;
        this._Revision = -1;
        if (major < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        if (minor < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        if (build < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        this._Major = major;
        this._Minor = minor;
        this._Build = build;
    }
    
    /**
     * Initializes a new instance of the {@link Version} class with the specified major, minor, build, and revision numbers.
     * @param major The major version number.
     * @param minor The minor version number.
     * @param build The build number.
     * @param revision The revision number.
     * @throws IllegalArgumentException <code>major</code>, <code>minor</code>, <code>build</code>, or <code>revision</code> is less than zero.
     */
    public Version(int major, int minor, int build, int revision)
    {
    	this._Build = -1;
        this._Revision = -1;
        if (major < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        if (minor < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        if (build < 0)
        {
        	throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_VERSIONOUTOFRANGE));
        }
        this._Major = major;
        this._Minor = minor;
        this._Build = build;
        this._Revision = revision;
    }
    
    /**
     * Compares the current {@link Version} object to a specified object and returns an indication of their relative values.
     * @param o An object to compare, or <b>null</b>.
     * @return <table>
     * <tr><th>Return Value</th><th>Description</th></tr>
     * <tr><td>Less than zero</td><td>The current Version object is a version before <code>o</code>.</td></tr>
     * <tr><td>Zero</td><td>The current Version object is the same version as <code>o</code>.</td></tr>
     * <tr><td>Greater than zero</td><td><p>The current Version object is a version subsequent to <code>o</code>.</p><p>-or-</p><p><code>o</code> is null.</p></td></tr>
     * </table>
     * @throws IllegalArgumentException <code>version</code> is not of type {@link Version}.
     */
    public int compareTo(Object o)
    {
    	if(o == null)
    	{
    		return compareTo((Version)null);
    	}
    	if(!(o instanceof Version))
    	{
    		throw new IllegalArgumentException();
    	}
    	return compareTo((Version)o);
    }
    
    /**
     * Compares the current {@link Version} object to a specified {@link Version} object and returns an indication of their relative values.
     * @param o A {@link Version} object to compare to the current {@link Version} object, or <b>null</b>.
     * @return <table>
     * <tr><th>Return Value</th><th>Description</th></tr>
     * <tr><td>Less than zero</td><td>The current Version object is a version before <code>o</code>.</td></tr>
     * <tr><td>Zero</td><td>The current Version object is the same version as <code>o</code>.</td></tr>
     * <tr><td>Greater than zero</td><td><p>The current Version object is a version subsequent to <code>o</code>.</p><p>-or-</p><p><code>o</code> is null.</p></td></tr>
     * </table>
     */
    public int compareTo(Version o)
    {
    	if (o == null)
        {
            return 1;
        }
        if (this._Major != o._Major)
        {
            if (this._Major > o._Major)
            {
                return 1;
            }
            return -1;
        }
        if (this._Minor != o._Minor)
        {
            if (this._Minor > o._Minor)
            {
                return 1;
            }
            return -1;
        }
        if (this._Build != o._Build)
        {
            if (this._Build > o._Build)
            {
                return 1;
            }
            return -1;
        }
        if (this._Revision == o._Revision)
        {
            return 0;
        }
        if (this._Revision > o._Revision)
        {
            return 1;
        }
        return -1;
    }
    
    /**
     * Returns a value indicating whether the current {@link Version} object is equal to a specified object.
     * @param obj An object to compare with the current {@link Version} object, or <code>null</code>.
     * @return <code>true</code> if the current {@link Version} object and <code>obj</code> are both {@link Version} objects, and every component of the current {@link Version} object matches the corresponding component of <code>obj</code>; otherwise, <code>false</code>.
     */
    public boolean equals(Object obj)
    {
    	if(obj instanceof Version)
    	{
    		return equals((Version)obj);
    	}
    	return false;
    }
    
    /**
     * Returns a value indicating whether the current {@link Version} object and a specified {@link Version} object represent the same value.
     * @param obj A {@link Version} object to compare to the current {@link Version} object, or <code>null</code>.
     * @return <code>true</code> if every component of the current {@link Version} object matches the corresponding component of the <code>obj</code> parameter; otherwise, <code>false</code>.
     */
    public boolean equals(Version obj)
    {
    	if (obj == null)
        {
            return false;
        }
        return (((this._Major == obj._Major) && (this._Minor == obj._Minor)) && ((this._Build == obj._Build) && (this._Revision == obj._Revision)));
    }
    
    /**
     * Returns a hash code for the current {@link Version} object.
     * @return A 32-bit signed integer hash code.
     */
    public int hashCode()
    {
    	int num = 0;
        num |= (this._Major & 15) << 28;
        num |= (this._Minor & 0xFF) << 20;
        num |= (this._Build & 0xFF) << 12;
        return (num | (this._Revision & 0x0FFF));
    }
    
    /**
     * Determines whether the first specified {@link Version} object is greater than the second specified {@link Version} object.
     * @param v1 The first {@link Version} object.
     * @param v2 The second {@link Version} object.
     * @return <code>true</code> if <code>v1</code> is greater than <code>v2</code>; otherwise, <code>false</code>.
     */
    public static boolean greaterThan(Version v1, Version v2)
    {
    	return lessThan(v2, v1);
    }
    
    /**
     * Determines whether the first specified {@link Version} object is greater than or equal to the second specified {@link Version} object.
     * @param v1 The first {@link Version} object.
     * @param v2 The second {@link Version} object.
     * @return <code>true</code> if <code>v1</code> is greater than or equal to <code>v2</code>; otherwise, <code>false</code>.
     */
    public static boolean greaterThanOrEqual(Version v1, Version v2)
    {
    	return lessThanOrEqual(v2, v1);
    }
    
    /**
     * Determines whether the first specified {@link Version} object is less than the second specified {@link Version} object.
     * @param v1 The first {@link Version} object.
     * @param v2 The second {@link Version} object.
     * @return <code>true</code> if <code>v1</code> is less than <code>v2</code>; otherwise, <code>false</code>.
     */
    public static boolean lessThan(Version v1, Version v2)
    {
    	if (v1 == null)
        {
            throw new NullPointerException("v1");
        }
        return v1.compareTo(v2) < 0;
    }
    
    /**
     * Determines whether the first specified {@link Version} object is less than or equal to the second specified {@link Version} object.
     * @param v1 The first {@link Version} object.
     * @param v2 The second {@link Version} object.
     * @return <code>true</code> if <code>v1</code> is less than or equal to <code>v2</code>; otherwise, <code>false</code>.
     */
    public static boolean lessThanOrEqual(Version v1, Version v2)
    {
    	if (v1 == null)
        {
            throw new NullPointerException("v1");
        }
        return v1.compareTo(v2) <= 0;
    }
    
    /**
     * Converts the value of the current {@link Version} object to its equivalent {@link String} representation.
     * @return The {@link String} representation of the values of the major, minor, build, and revision components of the current {@link Version} object, as depicted in the following format. Each component is separated by a period character ('.'). Square brackets ('[' and ']') indicate a component that will not appear in the return value if the component is not defined: major.minor[.build[.revision]] For example, if you create a {@link Version} object using the constructor <code>Version(1,1)</code>, the returned string is "1.1". If you create a {@link Version} object using the constructor <code>Version(1,3,4,2)</code>, the returned string is "1.3.4.2".
     */
    public String toString()
    {
    	if (this._Build == -1)
        {
            return this.toString(2);
        }
        if (this._Revision == -1)
        {
            return this.toString(3);
        }
        return this.toString(4);
    }
    
    /**
     * Converts the value of the current {@link Version} object to its equivalent {@link String} representation. A specified count indicates the number of components to return.
     * @param fieldCount The number of components to return. The <code>fieldCount</code> ranges from 0 to 4.
     * @return <p>The {@link String} representation of the values of the major, minor, build, and revision components of the current {@link Version} object, each separated by a period character ('.'). The <code>fieldCount</code> parameter determines how many components are returned.</p>
     * <p><table>
     * <tr><th>fieldCount</th><th>Return Value</th></tr>
     * <tr><td>0</td><td>An empty string ("").</td></tr>
     * <tr><td>1</td><td>major</td></tr>
     * <tr><td>2</td><td>major.minor</td></tr>
     * <tr><td>3</td><td>major.minor.build</td></tr>
     * <tr><td>4</td><td>major.minor.build.revision</td></tr>
     * </table></p>
     * <p>For example, if you create {@link Version} object using the constructor <code>Version(1,3,5)</code>, <code>ToString(2)</code> returns "1.3" and <code>ToString(4)</code> throws an exception.</p>
     * @throws IllegalArgumentException <p><code>fieldCount</code> is less than 0, or more than 4.</p><p>-or-</p><p><code>fieldCount</code> is more than the number of components defined in the current {@link Version} object.</p>
     */
    public String toString(int fieldCount)
    {
    	switch (fieldCount)
        {
            case 0:
                return "";

            case 1:
                return Integer.toString(this._Major);

            case 2:
                return Integer.toString(this._Major) + '.' + Integer.toString(this._Minor);
        }
    	if (this._Build == -1)
    	{
    		throw new IllegalArgumentException(StringUtilities.format_java(Resources.getString(BBXResource.ARGUMENT_BOUNDS_LOWER_UPPER_OUTOFRANGE), new Integer(0), new Integer(2)));
    	}
    	if (fieldCount == 3)
    	{
    		return Integer.toString(this._Major) + '.' + Integer.toString(this._Minor) + '.' + Integer.toString(this._Build);
    	}
    	if (this._Revision == -1 || fieldCount != 4)
    	{
    		throw new IllegalArgumentException(StringUtilities.format_java(Resources.getString(BBXResource.ARGUMENT_BOUNDS_LOWER_UPPER_OUTOFRANGE), new Integer(0), new Integer(this._Revision == -1 ? 3 : 4)));
    	}
    	return Integer.toString(this._Major) + '.' + Integer.toString(this._Minor) + '.' + Integer.toString(this._Build) + '.' + Integer.toString(this._Revision);
    }
    
    /**
     * Gets the value of the build component of the version number for the current {@link Version} object.
     * @return The build number, or -1 if the build number is undefined.
     */
    public int getBuild()
    {
    	return this._Build;
    }
    
    /**
     * Gets the value of the major component of the version number for the current {@link Version} object.
     * @return The major version number.
     */
    public int getMajor()
    {
    	return this._Major;
    }
    
    /**
     * Gets the high 16 bits of the revision number.
     * @return A 16-bit signed integer.
     */
    public short getMajorRevision()
    {
    	return (short)(this._Revision >> 0x10);
    }
    
    /**
     * Gets the value of the minor component of the version number for the current {@link Version} object.
     * @return The minor version number.
     */
    public int getMinor()
    {
    	return this._Minor;
    }
    
    /**
     * Gets the low 16 bits of the revision number.
     * @return A 16-bit signed integer.
     */
    public short getMinorRevision()
    {
    	return (short)(this._Revision & 0xFFFF);
    }
    
    /**
     * Gets the value of the revision component of the version number for the current {@link Version} object.
     * @return The revision number, or -1 if the revision number is undefined.
     */
    public int getRevision()
    {
    	return this._Revision;
    }
}
