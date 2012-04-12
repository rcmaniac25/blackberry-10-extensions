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

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import rebuild.util.text.StringUtilities;

/**
 * Various directory related methods for the Blackberry.
 * @since BBX 1.0.1
 */
public final class Directory
{
	private static final int BUFFER_SIZE = 1024;
	
    private Directory()
    {
    }
    
    /**
     * Determines whether the given path refers to an existing directory on the device.
     * @param path The path to test.
     * @return <code>true</code> if path refers to an existing directory; otherwise, <code>false</code>.
     */
    public static boolean Exists(String path)
    {
    	return InExists(path, true);
    }
    
    /**
     * Determines whether the given path refers to an existing directory/file on the device.
     * @param path The path to test.
     * @param isdir <code>true</code> if the path is for a directory, <code>false</code> if otherwise.
     * @return <code>true</code> if path refers to an existing directory; otherwise, <code>false</code>.
     */
    static boolean InExists(String path, boolean isdir)
    {
    	boolean flag = false;
        FileConnection dir = null;
        if(!Path.IsProperType(path, Path.FILE_TYPE))
        {
            return false;
        }
        try
        {
            dir = (FileConnection)Connector.open(path, Connector.READ);
            if(isDirectory(dir) == isdir)
            {
            	flag = dir.exists();
            }
        }
        catch(Exception e)
        {
        }
        finally
        {
        	try
        	{
        		if(dir != null)
        		{
        			dir.close();
        		}
        	}
        	catch(Exception e)
        	{
        	}
        }
        return flag;
    }
    
    /**
     * If a directory does not exist then this method will make sure any folders and subfolders are created.
     * @param path The path to the directory that is to be created.
     * @return <code>true</code> if the directory can be created, <code>false</code> if otherwise.
     */
    public static boolean EnsureCreation(String path)
    {
    	boolean result = false;
    	if(!Path.IsProperType(path, Path.FILE_TYPE))
        {
            return false;
        }
    	FileConnection file = null;
    	try
    	{
    		file = (FileConnection)Connector.open(path, Connector.READ_WRITE);
    		if(file.exists())
    		{
    			result = true;
    		}
    		else
    		{
    			//Now for the fun of figuring out how to get this file/directory to actually exist
    			
    			//First lets find out what exists
    			String existsURL = file.getURL(); //Get the desired URL
    			String temp;
    			//Get the parent folder (could have used getPath but needed a full URL)
    			if(isDirectory(file))
    			{
    				temp = existsURL.substring(0, existsURL.length() - 1);
    				existsURL = existsURL.substring(0, temp.lastIndexOf(Path.DirectorySeparatorChar) + 1);
    			}
    			else
    			{
    				existsURL = existsURL.substring(0, existsURL.lastIndexOf(Path.DirectorySeparatorChar) + 1);
    			}
    			while(true)
    			{
    				FileConnection searchingFile = (FileConnection)Connector.open(existsURL, Connector.READ);
    				if(searchingFile.exists())
    				{
    					//Found one that exists
    					searchingFile.close();
    					break;
    				}
    				searchingFile.close();
    				
    				//Remove a folder
    				temp = existsURL.substring(0, existsURL.length() - 1);
    				existsURL = existsURL.substring(0, temp.lastIndexOf(Path.DirectorySeparatorChar) + 1);
    			}
    			
    			//Get what needs to be done before the full path has been created
    			String todo = file.getURL().substring(existsURL.length());
    			existsURL += todo.substring(0, todo.indexOf(Path.DirectorySeparatorChar) + 1);
    			todo = todo.substring(todo.indexOf(Path.DirectorySeparatorChar) + 1);
    			while(todo != null)
    			{
    				//Create the file or directory
    				FileConnection processFile = (FileConnection)Connector.open(existsURL, Connector.READ_WRITE);
    				if(!processFile.exists())
    				{
	    				if(isDirectory(processFile))
	    				{
	    					processFile.mkdir();
	    				}
	    				else
	    				{
	    					processFile.create();
	    				}
    				}
    				processFile.close();
    				
    				//Remove the added component
    				int index = todo.indexOf(Path.DirectorySeparatorChar);
    				if(index >= 0)
    				{
    					//Still has a directory separator
    					existsURL += todo.substring(0, index + 1);
    	    			todo = todo.substring(index + 1);
    				}
    				else
    				{
    					if(todo.length() > 0)
    					{
    						existsURL += todo;
        	    			todo = "";
    					}
    					else
    					{
    						todo = null;
    					}
    				}
    			}
    		}
    		result = file.exists();
    	}
    	catch(Exception e)
    	{
    		result = false;
    	}
    	finally
    	{
    		try
    		{
    			if(file != null)
    			{
    				file.close();
    			}
    		}
    		catch(Exception e)
    		{
    		}
    	}
    	return result;
    }
    
    /**
     * Checks if the URL passed to the Connector.open() is a directory. The difference between this and the 
     * {@link FileConnection}'s {@link FileConnection#isDirectory()} function is this will check if it is a directory
     * regardless of if it exists already.
     * @param file The {@link FileConnection} to check if it is a directory.
     * @return <code>true</code> if the connection's target is a directory, otherwise <code>false</code>.
     */
    public static boolean isDirectory(FileConnection file)
    {
    	return file.exists() ? file.isDirectory() : file.getURL().endsWith("/");
    }
    
    /**
     * Deletes a directory.
     * @param path The directory to delete.
     * @return <code>true</code> if the directory was deleted, <code>false</code> if otherwise.
     */
    public static boolean Delete(String path)
    {
    	return InDelete(path, true);
    }
    
    /**
     * Deletes a file/directory.
     * @param path The file/directory to delete.
     * @param isdir <code>true</code> if the path is for a directory, <code>false</code> if otherwise.
     * @return <code>true</code> if the file/directory was deleted, <code>false</code> if otherwise.
     */
    static boolean InDelete(String path, boolean isdir)
    {
    	boolean flag = false;
    	if(!Path.IsProperType(path, Path.FILE_TYPE))
        {
            return false;
        }
    	FileConnection file = null;
    	try
    	{
    		file = (FileConnection)Connector.open(path, Connector.READ_WRITE);
    		if(isDirectory(file) == isdir)
    		{
			BREAK_DRI:
    			if(isDirectory(file))
    			{
    				//Complex, make sure the directory is empty, then delete
    				java.util.Enumeration en = file.list("*", true);
    				while(en.hasMoreElements())
    				{
    					String nPath = StringUtilities.format_java("file://{0}{1}{2}", file.getPath(), file.getName(), (String)en.nextElement());
    					FileConnection nFile = (FileConnection)Connector.open(nPath, Connector.READ);
    					boolean nFileDir = isDirectory(nFile);
    					nFile.close();
    					if(!InDelete(nPath, nFileDir))
    					{
    						break BREAK_DRI;
    					}
    				}
    				file.delete();
    			}
    			else
    			{
    				//Simple, delete the file
    				file.delete();
    			}
    			flag = !file.exists();
    		}
    	}
    	catch(Exception e)
        {
    		flag = false;
        }
    	finally
    	{
    		try
    		{
    			if(file != null)
    			{
    				file.close();
    			}
    		}
    		catch(Exception e)
    		{
    		}
    	}
    	return flag;
    }
    
    /**
     * Move a directory from one location to another.
     * @param originalPath The original directory path.
     * @param newPath The new directory path.
     * @return <code>true</code> if the directory was moved, <code>false</code> if otherwise.
     */
    public static boolean Move(String originalPath, String newPath)
    {
    	return InMove(originalPath, newPath, true, true);
    }
    
    /**
     * Move a directory/file from one location to another. Note this does an exact copy so any file moving that has different names will have the name changed back.
     * @param originalPath The original directory/file path.
     * @param newPath The new directory/file path.
     * @param isdir <code>true</code> if the path is for a directory/file, <code>false</code> if otherwise.
     * @param sameName <code>true</code> if the filenames should match for cut and paste, <code>false</code> if otherwise. ONLY APPLYS TO FILES.
     * @return <code>true</code> if the directory/file was moved, <code>false</code> if otherwise.
     */
    static boolean InMove(String originalPath, String newPath, boolean isdir, boolean sameName)
    {
    	if(originalPath.compareTo(newPath) == 0)
    	{
    		return false;
    	}
    	if((!Path.IsProperType(originalPath, Path.FILE_TYPE)) || (!Path.IsProperType(newPath, Path.FILE_TYPE)))
        {
            return false;
        }
    	boolean flag = false;
    	boolean rename = false;
    	FileConnection orFile = null;
    	FileConnection newFile = null;
    	try
    	{
    		orFile = (FileConnection)Connector.open(originalPath, Connector.READ_WRITE);
    		newFile = (FileConnection)Connector.open(newPath, Connector.READ_WRITE);
    		if(isDirectory(orFile) == isdir && isDirectory(newFile) == isdir)
    		{
    			if(!orFile.exists())
    			{
    				//Original file doesn't exist, can't do anything
    				throw new Exception();
    			}
    			if(orFile.getPath().equals(newFile.getPath()))
        		{
    				//URLs are not the same but the paths are... So this is a renaming? OK, simpilfies my life
    				if(newFile.exists())
    				{
    					if(!InDelete(newFile.getURL(), isDirectory(newFile)))
    					{
    						throw new Exception();
    					}
    				}
    				String nName = newFile.getName();
    				nName = isDirectory(newFile) ? nName.substring(0, nName.length() - 2) : nName;
    				orFile.rename(nName);
    				rename = flag = orFile.exists();
        		}
    			else
    			{
	    			if(isDirectory(orFile))
	    			{
	    				//Make sure there is enough room for a duplicate of the folder
	    				long size = orFile.directorySize(true);
	    				if(newFile.totalSize() - newFile.usedSize() < size)
	    				{
	    					throw new Exception();
	    				}
	    				
	    				//Move a directory
	    				if(!newFile.exists())
	    				{
	    					if(!EnsureCreation(newFile.getURL()))
	    					{
	    						throw new Exception();
	    					}
	    				}
	    				
	    				//-First get all files and directories
	    				java.util.Enumeration en = orFile.list("*", true);
	    				Vector files = new Vector();
	    				Vector directories = new Vector();
	    				while(en.hasMoreElements())
	    				{
	    					String nPath = (String)en.nextElement();
	    					FileConnection nFile = (FileConnection)Connector.open(nPath, Connector.READ_WRITE);
	    					String nPathF = newFile.getURL() + nFile.getURL().substring(orFile.getURL().length());
	    					if(isDirectory(nFile))
	    					{
	    						directories.addElement(nFile.getURL());
	    						directories.addElement(nPathF);
	    					}
	    					else
	    					{
	    						files.addElement(nFile.getURL());
	    						files.addElement(nPathF);
	    					}
	    					nFile.close();
	    				}
	    				
	    				//-Now move the files over
	    				int count = files.size();
	    				for(int i = 0; i < count; i += 2)
	    				{
	    					if(!InMove((String)files.elementAt(i), (String)files.elementAt(i + 1), false, true))
	    					{
	    						throw new Exception();
	    					}
	    				}
	    				count = directories.size();
	    				for(int i = 0; i < count; i += 2)
	    				{
	    					if(!InMove((String)directories.elementAt(i), (String)directories.elementAt(i + 1), true, true))
	    					{
	    						throw new Exception();
	    					}
	    				}
	    			}
	    			else
	    			{
	    				//Make sure there is enough room for a duplicate of the file
	    				long size = orFile.fileSize();
	    				if(newFile.totalSize() - newFile.usedSize() < size)
	    				{
	    					throw new Exception();
	    				}
	    				
	    				//Move a file
	    				if(!newFile.getName().equals(orFile.getName()) && sameName)
	    				{
	    					//Different file name, need to change it
	    					String url = newFile.getURL();
	    					int index = url.lastIndexOf(Path.DirectorySeparatorChar);
	    					if(index == -1)
	    					{
	    						throw new Exception();
	    					}
	    					url = url.substring(index, url.length());
	    					url += orFile.getName();
	    					newFile.close();
	    					newFile = (FileConnection)Connector.open(url, Connector.READ_WRITE);
	    				}
	    				if(newFile.exists())
	    				{
	    					newFile.delete();
	    				}
	    				if(!EnsureCreation(newFile.getURL()))
    					{
    						throw new Exception();
    					}
	    				java.io.OutputStream newIn = newFile.openOutputStream();
	    				java.io.InputStream orIn = orFile.openInputStream();
	    				int count = 0;
	    				byte[] buf = new byte[BUFFER_SIZE];
	    				while(size > 0)
	    				{
	    					count = orIn.read(buf, 0, BUFFER_SIZE);
	    					if(count < 0)
	    					{
	    						newIn.close();
	    						orIn.close();
	    						throw new Exception();
	    					}
	    					newIn.write(buf, 0, count);
	    					size -= count;
	    				}
	    				newIn.close();
	    				orIn.close();
	    			}
	    			flag = true;
    			}
    		}
    	}
    	catch(Exception e)
        {
    		flag = false;
        }
    	finally
    	{
    		//If the file was renamed then flag will be true and it will delete the renamed file/folder.
    		if(!rename)
    		{
	    		//Get rid of either the old data if move was successful or new data if moving was unsuccessful.
	    		if(!InDelete(flag ? orFile.getURL() : newFile.getURL(), isdir))
				{
	    			//ERROR cleaning up old data if it worked successfully, or new stuff if an error occurred
	    			flag = false;
				}
    		}
    		try
    		{
    			if(newFile != null)
    			{
    				newFile.close();
    			}
    			if(orFile != null)
    			{
    				orFile.close();
    			}
    		}
    		catch(Exception e)
    		{
    		}
    	}
    	return flag;
    }
}
