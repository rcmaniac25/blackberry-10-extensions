//#preprocess

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
package rebuild.util.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.DeviceInfo;
import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.util.Utilities;
import rebuild.util.text.StringUtilities;

/**
 * Various path related functions for the Blackberry.
 * @since BBX 1.0.1
 */
public final class Path
{
	/**
     * Get the path to the folder icons. Type is automatically determined.
     */
    public static final int PATH_FOLDER_ICONS = 0;
    /**
     * Get the path to the folder icons. Path for Blackberry device only.
     */
    public static final int PATH_FOLDER_ICONS_BB = 1;
    /**
     * Get the path to the folder icons. Path for Blackberry simulator only.
     */
    public static final int PATH_FOLDER_ICONS_SIM = 2;
    /**
     * The root flash memory for the Blackberry.
     */
    public static final int PATH_FOLDER_SYSTEM = 3;
    /**
     * The root or the removable memory on the Blackberry.
     */
    public static final int PATH_FOLDER_REMOVABLE_MEMORY = 4;
    
    /**
     * Provides a platform-specific character used to separate directory levels in a path string that reflects a hierarchical file system organization.
     */
    public static char DirectorySeparatorChar;
    /**
     * Provides a platform-specific string used to separate directory levels in a path string that reflects a hierarchical file system organization.
     */
    public static String DirectorySeparatorString;
    /**
     * Gets the newline string defined for this environment.
     */
    public static String NewLine;
    /**
     * Provides a platform-specific volume separator character.
     */
    public static char VolumeSeparatorChar;
    
    /**
     * The file connection type.
     */
    public static Class FILE_TYPE;
//#ifdef DEBUG
    private static boolean isSim; //If currently in simulator mode.
//#endif
    private static char[] validChar; //Valid path chars
    
    static
    {
//#ifdef DEBUG
        isSim = DeviceInfo.isSimulator();
//#endif
        
        String[] t = Resources.getStringArray(BBXResource.VALID_CHARS);
        int l = t.length;
        validChar = new char[l];
        for(int i = 0; i < l; i++)
        {
            validChar[i] = t[i].charAt(0);
        }
        
        DirectorySeparatorString = System.getProperty("file.separator").trim();
        DirectorySeparatorChar = DirectorySeparatorString.charAt(0);
        VolumeSeparatorChar = ':';
        
        try
        {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            PrintStream p = new PrintStream(o);
            p.println();
            byte[] b = o.toByteArray();
            o.close();
            l = b.length;
            if(l > 0)
            {
                if(l > 1)
                {
                    char[] c = new char[l];
                    for(int i = 0; i < l; i++)
                    {
                        c[i] = (char)b[i];
                    }
                    NewLine = String.valueOf(c);
                }
                else
                {
                    NewLine = String.valueOf((char)b[0]);
                }
            }
            else
            {
                NewLine = "\n";
            }
        }
        catch(java.io.IOException e)
        {
        }
        
        try
        {
        	FileConnection fileCon = (FileConnection)Connector.open(getSystemPath(Path.PATH_FOLDER_SYSTEM), Connector.READ);
            FILE_TYPE = fileCon.getClass();//Class.forName("javax.microedition.io.file.FileConnection");
            fileCon.close();
        }
        catch(Exception e)
        {
        }
    }
    
    private Path()
    {
    }
    
    /**
     * Get a standard system path.
     * @param type Which type of folder to get, one of the PATH_FOLDER_* fields.
     * @return The system path or null if an invalid type was inputed.
     */
    public static String getSystemPath(int type)
    {
        switch(type)
        {
            case PATH_FOLDER_ICONS:
//#ifdef DEBUG
                if(isSim)
                {
                    return Resources.getString(BBXResource.STANDARD_SIM_PATH_FOLDER_ICONS);
                }
//#endif
            case PATH_FOLDER_ICONS_BB:
                return Resources.getString(BBXResource.STANDARD_BB_PATH_FOLDER_ICONS);
            case PATH_FOLDER_ICONS_SIM:
                return Resources.getString(BBXResource.STANDARD_SIM_PATH_FOLDER_ICONS);
            case PATH_FOLDER_SYSTEM:
                return Resources.getString(BBXResource.FILE_SYSTEM);
            case PATH_FOLDER_REMOVABLE_MEMORY:
                return Resources.getString(BBXResource.FILE_SDCARD);
        }
        return null;
    }
    
    /**
     * Returns the directory information for the specified path string.
     * @param path The path of a file or directory.
     * @return A String containing directory information for path, or null if an error occurs.
     */
    public static String getDirectoryName(String path)
    {
        try
        {
            FileConnection fi = (FileConnection)Connector.open(path, Connector.READ);
            String dir = fi.getPath();
            fi.close();
            return dir;
        }
        catch(Exception e)
        {
        }
        return null;
    }
    
    /**
     * Returns the extension of the specified path string.
     * @param path The path string from which to get the extension.
     * @return A String containing the extension of the specified path (including the "."), null, or Empty. If path is null, GetExtension returns null. If path does not have extension information, GetExtension returns "".
     */
    public static String getExtension(String path)
    {
        if (path == null)
        {
            return null;
        }
        int length = path.length();
        int startIndex = length;
        while (--startIndex >= 0)
        {
            char ch = path.charAt(startIndex);
            if (ch == '.')
            {
                if (startIndex != (length - 1))
                {
                    return StringUtilities.substring(path, startIndex, length - startIndex);
                }
                return "";
            }
            if ((ch == DirectorySeparatorChar) || (ch == VolumeSeparatorChar))
            {
                break;
            }
        }
        return "";
    }
    
    /**
     * Returns the file name and extension of the specified path string.
     * @param path The path string from which to obtain the file name and extension.
     * @return A String consisting of the characters after the last directory character in path.
     */
    public static String getFileName(String path)
    {
        try
        {
            FileConnection fi = (FileConnection)Connector.open(path, Connector.READ);
            String fil = fi.getName();
            fi.close();
            return fil;
        }
        catch(Exception e)
        {
        }
        return null;
    }
    
    /**
     * Returns the file name of the specified path string without the extension.
     * @param path The path of the file.
     * @return A String containing the string returned by GetFileName, minus the last period (.) and all characters following it.
     */
    public static String getFileNameWithoutExtension(String path)
    {
        path = getFileName(path);
        if (path == null)
        {
            return null;
        }
        int length = path.lastIndexOf('.');
        if (length == -1)
        {
            return path;
        }
        return StringUtilities.substring(path, 0, length);
    }
    
    /**
     * Gets a value indicating whether the specified path string contains absolute or relative path information.
     * @param path The path to test.
     * @return true if path contains an absolute path; otherwise, false.
     */
    public static boolean isPathRooted(String path)
    {
        if (path != null)
        {
            //int length = path.length();
            //if(length < 1)
            //{
            //    return false;
            //}
            //boolean flag = false;
            //String[] roots = Resources.getStringArray(BBXResource.PATH_ROOTS);
            //int l = roots.length;
            //int i = 0;
            //for(i = 0; i < l; i++)
            //{
            //    if(path.startsWith(roots[i]))
            //    {
            //        flag = true;
            //        break;
            //    }
            //}
            //if(!flag)
            //{
            //    return false;
            //}
            //l = roots[i].length();
            //if(length < (l + 3))
            //{
            //    return false;
            //}
            //if((path.charAt(l) != VolumeSeparatorChar) || ((path.charAt(l + 1) == DirectorySeparatorChar) || (path.charAt(l + 2) == DirectorySeparatorChar)))
            //{
            //    return false;
            //}
            //return true;
            try
            {
                Connection con = Connector.open(path, Connector.READ);
                con.close();
                return true;
            }
            catch(Exception e)
            {
            }
        }
        return false;
    }
    
    /**
     * Compares the path submitted and the desired type to determine if they are the same.
     * @param path The path to check.
     * @param c The desired type.
     * @return True if the path and type are the same, false if the path and type are not the same or path is null or c is null.
     */
    public static boolean isProperType(String path, Class c)
    {
        if(path != null)
        {
            if(c == null)
            {
                return false;
            }
            try
            {
                Connection con = Connector.open(path, Connector.READ);
                boolean flag = con.getClass().getName().equals(c.getName());
                con.close();
                return flag;
            }
            catch(Exception e)
            {
            }
        }
        return false;
    }
    
    /**
     * Returns a random folder name or file name.
     * @return A random folder name or file name.
     */
    public static String getRandomFileName()
    {
        byte[] data = new byte[12];
        Utilities.RNGGetBytes(data);
        char[] chArray = toBase32StringSuitableForDirName(data).toCharArray();
        chArray[8] = '.';
        return String.valueOf(chArray, 0, 12);
    }
    
    private static String toBase32StringSuitableForDirName(byte[] buff)
    {
        StringBuffer buf = new StringBuffer();
        int length = buff.length;
        int max = length - 1;
        for(int i = 0; i < length; i++)
        {
            buf.append(validChar[getIndex(buff[i], max)]);
        }
        return buf.toString();
    }
    
    private static int getIndex(int variableIndex, int maxIndex)
    {
        if(variableIndex == 0)
        {
            return 0;
        }
        else if(variableIndex > 0)
        {
            //Pos
            if(variableIndex > maxIndex)
            {
                while(variableIndex > maxIndex)
                {
                    variableIndex -= maxIndex;
                }
                return variableIndex;
            }
            else
            {
                return variableIndex;
            }
        }
        else
        {
            //Neg
            while(variableIndex < 0)
            {
                variableIndex += maxIndex;
            }
            return variableIndex;
        }
    }
}
