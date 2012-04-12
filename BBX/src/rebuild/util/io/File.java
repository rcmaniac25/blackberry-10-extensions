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

import javax.microedition.io.file.FileConnection;

/**
 * Various file related methods for the Blackberry.
 * @since BBX 1.0.1
 */
public final class File
{
    private File()
    {
    }
    
    /**
     * Determines whether the specified file exists.
     * @param path The file to check.
     * @return true if the caller has the required permissions and path contains the name of an existing file; otherwise, false. This method also returns false if path is null or a zero-length string. If the caller does not have sufficient permissions to read the specified file, no exception is thrown and the method returns false regardless of the existence of path.
     */
    public static boolean Exists(String path)
    {
    	return Directory.InExists(path, false);
    }
    
    /**
     * Deletes a file.
     * @param path The file to delete.
     * @return True if the file was deleted, false if otherwise.
     */
    public static boolean Delete(String path)
    {
    	return Directory.InDelete(path, false);
    }
    
    /**
     * If a file does not exist then this method will make sure any folders and subfolders are created.
     * @param path The path to the file that is to be created.
     * @return true if the file can be created, false if otherwise.
     */
    public static boolean EnsureCreation(String path)
    {
    	return Directory.EnsureCreation(path);
    }
    
    /**
     * Checks if the URL passed to the Connector.open() is a file.
     * @param file The {@link FileConnection} to check if it is a file.
     * @return <code>true</code> if the connection's target is a file, otherwise <code>false</code>.
     */
    public static boolean isFile(FileConnection file)
    {
    	return !Directory.isDirectory(file);
    }
    
    /**
     * Move a file from one location to another.
     * @param originalPath The original file path.
     * @param newPath The new file path.
     * @return <code>true</code> if the file was moved, <code>false</code> if otherwise.
     */
    public static boolean Move(String originalPath, String newPath)
    {
    	return Move(originalPath, newPath, true);
    }
    
    /**
     * Move a file from one location to another.
     * @param originalPath The original file path.
     * @param newPath The new file path.
     * @param sameName If a different filename is given should it be changed to the original filename?
     * @return <code>true</code> if the file was moved, <code>false</code> if otherwise.
     */
    public static boolean Move(String originalPath, String newPath, boolean sameName)
    {
    	return Directory.InMove(originalPath, newPath, false, sameName);
    }
}
