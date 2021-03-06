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
//Taken from LCMS for BlackBerry
package rebuild.util.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.util.BitConverter;
import rebuild.util.LargeNumber;
import rebuild.util.Utilities;

/**
 * Provides the underlying print functionality for format functions.
 * @since BBX 1.2.0
 */
//#ifdef BBX_INTERNAL_ACCESS & DEBUG
public
//#endif
final class PrintUtility
{
	/* 
	 * This mostly follows the specification mentioned http://pubs.opengroup.org/onlinepubs/009695399/functions/printf.html and http://pubs.opengroup.org/onlinepubs/009695399/functions/scanf.html
	 * 
	 * Formats:
	 *	p: This simply prints out 0000:0000 since pointers don't exist in Java.
	 */
	
	public static int output(PrintStream out, int max, final String format, Object[] args, Object[][] formatCache, int cacheIndex)
	{
		if(max == 0)
        {
			//Regardless of if there is a format or not, nothing is going to get returned.
        	return 0;
        }
		//Cache the format if possible
		Object[] formats;
		if(formatCache != null && formatCache.length > 0 && cacheIndex >= 0 && cacheIndex < formatCache.length && formatCache[cacheIndex] != null)
		{
			formats = formatCache[cacheIndex];
		}
		else
		{
			formats = breakFormat(format, null);
			if(formatCache != null && formatCache.length > 0 && cacheIndex >= 0 && cacheIndex < formatCache.length)
			{
				formatCache[cacheIndex] = formats;
			}
		}
        if (max < 0)
        {
            max = Integer.MAX_VALUE;
        }
        int count = 0;
        int len = formats.length;
        int argLen = args == null ? 0 : args.length;
        int elLen;
        int argPos = 0;
        //Go through all the parts of the format
        for(int i = 0; i < len; i++)
        {
	        Object obj = formats[i];
	        String str = null;
	        if(obj instanceof String)
	        {
	        	//If it's a String then it's easy
		        str = (String)obj;
	        }
	        else
	        {
	        	//Have to actually format the args into a String
		        FormatElement form = (FormatElement)obj;
                if (form == null)
                {
                	throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                }
                //Does the format take any arguments as width or precision?
		        int req = form.requires();
		        if(req > 0)
		        {
			        Long one;
			        Long two = null;
			        if(argPos + req < argLen)
			        {
				        one = getAsLong(args[argPos++]);
				        if(req == 2)
				        {
					        two = getAsLong(args[argPos++]);
				        }
				        form.setInputValue(one, two);
			        }
			        else
			        {
			        	//O no, not enough args
				        argPos += req; //Do this so that it only prints the format out
			        }
		        }
                if (form.getFormat().endsWith("n"))
                {
                	//Have a numeric arg (gets the length of what has been written out so far)
                    ((int[])args[argPos++])[0] = count;
                }
                else
                {
                	//Ok, time to process..
                    if (argPos >= argLen && !form.hasArgLocation())
                    {
                    	//..Or not. Not enough args, return the format
                        str = form.getFormat();
                    }
                    else
                    {
                    	//If the format takes an argument then pass it in; it could be relative or absolute.
                        str = form.format(form.takesArg() ? (form.hasArgLocation() ? args[form.argLocation()] : args[argPos++]) : null);
                    }
                }
	        }
	        //If the String isn't null then print it to the PrintStream
            if (str != null)
            {
                elLen = str.length();
                if (elLen + count > max)
                {
                	//The maximum char-count will be exceeded so truncate the String before writing it and stop execution
                    out.print(str.substring(0, max - count));
                    count = max;
                    break;
                }
                else
                {
                	//Print out the whole String
                    out.print(str);
                    count += elLen;
                }
            }
        }
        if(count < max)
        {
        	out.print('\0');
        }
        //Reset the cache values so that if the cache is reused it doesn't contain the same values used in this current operation
        if(formatCache != null && formatCache.length > 0)
		{
        	for(int i = 0; i < len; i++)
        	{
        		if(formats[i] instanceof FormatElement)
        		{
        			((FormatElement)formats[i]).reset();
        		}
        	}
		}
        return count;
	}
	
	public static int fscanf(final InputStream file, final String format, Object[] argptr, Object[][] formatCache, int cacheIndex)
    {
		//First thing to do is get the actual formated data
		byte[] bytes = null;
		try
		{
			bytes = IOUtilities.streamToBytes(file);
		}
		catch(IOException ioe)
		{
		}
		if(bytes == null)
		{
			//No data, no processing
			return 0;
		}
		//Now process the format
		boolean doValidate = true;
		Object[] formats;
		//..and possibly cache it
		if(formatCache != null && formatCache.length > 0 && cacheIndex >= 0 && cacheIndex < formatCache.length && formatCache[cacheIndex] != null)
		{
			doValidate = false;
			formats = formatCache[cacheIndex];
		}
		else
		{
			formats = breakFormat(format, null);
			if(formatCache != null && formatCache.length > 0 && cacheIndex >= 0 && cacheIndex < formatCache.length)
			{
				formatCache[cacheIndex] = formats;
			}
		}
		//Now get the string data
		String str = null;
		try
		{
			str = new String(bytes, "UTF-8"); //Get string in UTF-8 format, this will allow standard ASCII all the way to international languages to be processed.
		}
		catch(UnsupportedEncodingException ioe)
		{
		}
		if(str == null)
		{
			//No string, no processing
			return 0;
		}
        char[] chars = str.toCharArray();
        int slen = chars.length;
        int len = formats.length;
        int argLen = argptr.length;
        int[] tempVals = new int[2]; //index 0 is "str pos", index 1 is "arg pos"
        //Simplify formats even more, this will not be cached because if the cache was used on output it would create an invalid formatted element
        for (int i = 0; i < len; i++)
        {
            Object obj = formats[i];
            if (obj instanceof String)
            {
            	//If the format is a String then it could possibly get simplified to speed up actual execution (this isn't writing out the format so we don't need to go through the format if we don't need to)
                String tmp = (String)obj;
                tmp = tmp.trim(); //Trim the String
                if (tmp.length() == 0)
                {
                	//Hmm, seems the String was only whitespace so remove it from the processing list
                    Object[] nForms = new Object[len - 1];
                    System.arraycopy(formats, 0, nForms, 0, i);
                    System.arraycopy(formats, i + 1, nForms, i, len - (i + 1));
                    len = nForms.length;
                    i--;
                    formats = nForms;
                }
                else
                {
                	//Ok we still have String content, lets see if we can make heads-or-tails of it
                    Vector nEl = null;
                    int l = tmp.length();
                    for (int k = 0; k < l; k++)
                    {
                        if (isWhiteSpace(tmp.charAt(k)))
                        {
                        	//Found some whitespace, remove it so we are left only with actual String content
                            if (nEl == null)
                            {
                                nEl = new Vector();
                            }
                            nEl.addElement(tmp.substring(0, k).trim());
                            tmp = tmp.substring(k).trim();
                            k = 0;
                            l = tmp.length();
                        }
                    }
                    if (nEl != null)
                    {
                        //Get forgotten element
                        nEl.addElement(tmp);
                        //Copy formats into a temporary array
                        int nElSize;
                        Object[] nForms = new Object[len + (nElSize = nEl.size()) - 1];
                        System.arraycopy(formats, 0, nForms, 0, i);
                        //Copy the cleaned up String to a temporary array
                        Object[] tObj = new Object[nElSize];
                        nEl.copyInto(tObj);
                        //Copy the new String (clean) into the new formats
                        System.arraycopy(tObj, 0, nForms, i, nElSize);
                        //Copy any formats that might have been overwritten to a new position in the new formats
                        System.arraycopy(formats, i + 1, nForms, i + nElSize, len - (i + 1));
                        //Adjust format length for processing
                        len = nForms.length;
                        //Adjust index position in relation to the new String
                        i += nElSize - 1;
                        //Finally replace the formats
                        formats = nForms;
                    }
                    else
                    {
                    	//Replace the format String with the trimmed String
                        formats[i] = tmp;
                    }
                }
            }
        }
        //Process str
        for (int i = 0; i < len && tempVals[0] < slen; i++)
        {
            //Skip Whitespace
            while (isWhiteSpace(chars[tempVals[0]]))
            {
                tempVals[0]++;
            }
            if (tempVals[0] >= slen)
            {
                break;
            }
            
            //Process elements
            Object obj = formats[i];
            if (obj instanceof String)
            {
                String tmp = (String)obj;
                if (str.indexOf(tmp, tempVals[0]) != tempVals[0])
                {
                    break;
                }
                tempVals[0] += tmp.length();
            }
            else
            {
                FormatElement form = (FormatElement)obj;
                if (form == null)
                {
                	throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                }
                if(doValidate)
                {
                	//If this isn't a precached formatter it should get validated
                	if(!validateUnformatter(form.getFormat()))
                	{
                		//Invalid format, return
                		if(formatCache != null && formatCache.length > 0)
                		{
                			//If there is a cache, remove it since the formatting is not valid
                			formatCache[cacheIndex] = null;
                		}
                		break;
                	}
                }
                //Get the current argument position and String position, "unformat" it, and compare the new positions
                int sp = tempVals[0];
                int ap = tempVals[1];
                form.unformat(str, argptr, tempVals);
                if (sp == tempVals[0] && ap == tempVals[1])
                {
                    //This means something went wrong, no changes to the input String or arguments occurred
                    break;
                }
                //count += (tempVals[1] - ap); //This is simply the number of arguments read
                if(tempVals[1] >= argLen)
                {
                	break; //Reached the max number of args that can be parsed, no need to keep processing
                }
            }
        }
        //Reset the formats so they can be reused
        if(formatCache != null && formatCache.length > 0)
		{
        	for(int i = 0; i < len; i++)
        	{
        		if(formats[i] instanceof FormatElement)
        		{
        			((FormatElement)formats[i]).reset();
        		}
        	}
		}
        return tempVals[1];
    }
	
	private static boolean validateUnformatter(String form)
	{
		int len = form.length();
		int section = 0; //Section: 0-"ignore char", 1-width, 2-modifiers, 3-type, (-1)-done
		for(int i = 1; i < len; i++) //First char is always %
		{
			char c = form.charAt(i);
			if (FULL_FORMAT.indexOf(c) >= 0) //Valid identifiers
			{
				switch(section)
				{
					case 0:
						if(c != '*')
						{
							//Not a 'ignore char' so go to next section
							i--;
						}
						section++;
						break;
					case 1:
						if(SCANF_SPECIFIERS.indexOf(c) >= 0)
						{
							//Found specifier, end
							return (i + 1) == len;
						}
						else if(SCANF_WIDTH.indexOf(c) >= 0)
						{
							//Continue execution, eventually it should get to the next component in the format.
						}
						else if(SCANF_LENGTH.indexOf(c) >= 0)
						{
							//Go to next section for checking
							section++;
						}
						else
						{
							section = -1; //Invalid
						}
						break;
					case 2:
						if(SCANF_SPECIFIERS.indexOf(c) >= 0)
						{
							//Found specifier, end
							return (i + 1) == len;
						}
						else if(SCANF_LENGTH.indexOf(c) >= 0)
						{
							//Continue execution, eventually it should get to a specifier or an invalid number
						}
						else
						{
							section = -1; //Invalid
						}
						break;
				}
			}
			else
			{
				section = -1;
			}
			if(section < 0)
			{
				break;
			}
		}
		return false;
	}
	
	private static boolean isWhiteSpace(char c)
    {
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		return net.rim.device.api.util.CharacterUtilities.isWhitespace(c);
//#else
        switch (c)
        {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
            case '\0':
                return true;
            default:
                return false;
        }
//#endif
    }
	
	//Helper function so arguments can be converted to a Long
	private static Long getAsLong(Object arg)
	{
		if(arg instanceof Long)
		{
			return (Long)arg;
		}
		long l;
		if(arg instanceof Byte)
		{
			l = ((Byte)arg).byteValue() & 0xFF;
		}
		else if(arg instanceof Short)
		{
			l = ((Short)arg).shortValue() & 0xFFFF;
		}
		else if(arg instanceof Integer)
		{
			l = ((Integer)arg).longValue() & 0xFFFFFFFFL;
		}
		else
		{
			l = -1; //Invalid (-1 is the default value for width and precision which is where this function will get used)
		}
		return new Long(l);
	}
	
	private static final long SPECIFIERS_UID = 0x318AE6E8A41FCF70L;
	private static final long FLAGS_UID = 0x94E998EE54BD00EL;
	private static final long WIDTH_PRECISION_UID = 0xFF217872C5D4CDDL;
	private static final long LENGTH_UID = 0x9EF53E8BFE248140L;
	private static final long FULL_FORMAT_UID = 0x3024E7CCD507CBE0L;
	
	private static final long SCANF_SPECIFIERS_UID = 0x3C0F209819016600L;
	private static final long SCANF_WIDTH_UID = 0x3889827523F0B67AL;
	private static final long SCANF_LENGTH_UID = 0x50EC7C6D6EC9B264L;
	private static final long SCANF_FULL_FORMAT_UID = 0xAE2D09322C4157CFL;
	
	private static final char THOUS_SEP = '\'';
	
	private static char DECIMAL;
	
	private static String SPECIFIERS;
	private static String FLAGS;
	private static String WIDTH_PRECISION;
	private static String LENGTH;
	private static String FULL_FORMAT;
	
	private static String SCANF_SPECIFIERS;
	private static String SCANF_WIDTH;
	private static String SCANF_LENGTH;
	private static String SCANF_FULL_FORMAT;
	
	static
	{
		DECIMAL = Double.toString(1.1).charAt(1);
//#ifndef NO_SIGNING
		String temp = (String)Utilities.singletonStorageGet(SPECIFIERS_UID);
		if(temp == null)
		{
//#endif
			SPECIFIERS = "cspdieEfFgGouxXn";
			FLAGS = "-+ #" + THOUS_SEP + '0';
			WIDTH_PRECISION = "123456789*0"; //Zero is added at end so that when FULL_FORMAT is generated there isn't two zeros in the format. It wouldn't cause an error but it would be one more char to check that isn't needed.
			LENGTH = "hlLzjt";
			FULL_FORMAT = FLAGS + WIDTH_PRECISION.substring(0, 9) + '.' + LENGTH + SPECIFIERS;
//#ifndef NO_SIGNING
			Utilities.singletonStorageSet(SPECIFIERS_UID, SPECIFIERS);
			Utilities.singletonStorageSet(FLAGS_UID, FLAGS);
			Utilities.singletonStorageSet(WIDTH_PRECISION_UID, WIDTH_PRECISION);
			Utilities.singletonStorageSet(LENGTH_UID, LENGTH);
			Utilities.singletonStorageSet(FULL_FORMAT_UID, FULL_FORMAT);
//#endif
			SCANF_SPECIFIERS = SPECIFIERS.substring(0, SPECIFIERS.length() - 2);
			SCANF_WIDTH = WIDTH_PRECISION.substring(0, WIDTH_PRECISION.length() - 2);
			SCANF_LENGTH = LENGTH.substring(0, LENGTH.length() - 3);
			SCANF_FULL_FORMAT = SCANF_WIDTH + SCANF_LENGTH + SCANF_SPECIFIERS + '*';
//#ifndef NO_SIGNING
			Utilities.singletonStorageSet(SCANF_SPECIFIERS_UID, SCANF_SPECIFIERS);
			Utilities.singletonStorageSet(SCANF_WIDTH_UID, SCANF_WIDTH);
			Utilities.singletonStorageSet(SCANF_LENGTH_UID, SCANF_LENGTH);
			Utilities.singletonStorageSet(SCANF_FULL_FORMAT_UID, SCANF_FULL_FORMAT);
		}
		else
		{
			SPECIFIERS = temp;
			FLAGS = (String)Utilities.singletonStorageGet(FLAGS_UID);
			WIDTH_PRECISION = (String)Utilities.singletonStorageGet(WIDTH_PRECISION_UID);
			LENGTH = (String)Utilities.singletonStorageGet(LENGTH_UID);
			FULL_FORMAT = (String)Utilities.singletonStorageGet(FULL_FORMAT_UID);
			
			SCANF_SPECIFIERS = (String)Utilities.singletonStorageGet(SCANF_SPECIFIERS_UID);
			SCANF_WIDTH = (String)Utilities.singletonStorageGet(SCANF_WIDTH_UID);
			SCANF_LENGTH = (String)Utilities.singletonStorageGet(SCANF_LENGTH_UID);
			SCANF_FULL_FORMAT = (String)Utilities.singletonStorageGet(SCANF_FULL_FORMAT_UID);
		}
//#endif
	}
	
	//Takes the format String, breaks it up into format elements and String literals. If just the format is passed in along with the args argument then the broken down format will be passed into the args argument.
	public static Object[] breakFormat(final String format, String[][] args)
	{
		StringBuffer bu = new StringBuffer();
        Vector parts = new Vector();
        int len = format.length();
        boolean inFormat = false;
        int section = -1;
        Vector argList = args == null ? null : new Vector(6);
        for (int i = 0; i < len; i++)
        {
            char c = format.charAt(i);
            if (inFormat)
            {
                //First remove any arg location parameter
                int argPosIdPos = format.indexOf('$', i);
                if (argPosIdPos >= 0)
                {
                    //Not very efficient but works well
                    String sub = format.substring(i, argPosIdPos);
                    int inLen = sub.length();
                    int k;
                    for (k = 0; k < inLen; k++)
                    {
                        if (!Character.isDigit(sub.charAt(k)))
                        {
                            break;
                        }
                    }
                    if (k == inLen)
                    {
                        if (argList != null)
                        {
                            argList.addElement(sub);
                        }
                        else
                        {
                            bu.append(sub);
                            bu.append('$');
                        }
                        i += inLen;
                        continue;
                    }
                }
                if (FULL_FORMAT.indexOf(c) >= 0) //Valid identifiers
                {
                    bu.append(c);
                    switch (section)
                    {
                        case -1: //Bad format
                        	throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                        case 0: //General (everything is possible)
                            if (SPECIFIERS.indexOf(c) >= 0)
                            {
                                //Found the end, exit
                                section = -1;
                                String str = bu.toString();
                                if (argList != null)
                                {
                                	for(int j = 0; j < 4; j++)
                                	{
                                		argList.addElement(null);
                                	}
                                    argList.addElement(str.substring(1));
                                }
                                else
                                {
                                    //If we don't do this it will become redundant and we will get a stack overflow
                                    parts.addElement(FormatElement.getFormatter(str));
                                }
                                bu.setLength(0);
                                inFormat = false;
                            }
                            else if (FLAGS.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.addElement(bu.toString().substring(1));
                                }
                                section++; //Found flag section, now to check for next section
                            }
                            else if (WIDTH_PRECISION.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.addElement(null);
                                    argList.addElement(bu.toString().substring(1));
                                }
                                section += 2; //Found width section, now to check for next section
                            }
                            else if (c == '.')
                            {
                            	//Precision is prefixed with a decimal, make sure that there is more to the format then just a decimal at the end.
                                if (i + 1 < len)
                                {
                                    if (WIDTH_PRECISION.indexOf(format.charAt(i + 1)) >= 0)
                                    {
                                        if (argList != null)
                                        {
                                        	for(int j = 0; j < 2; j++)
                                        	{
                                        		argList.addElement(null);
                                        	}
                                            argList.addElement(bu.toString().substring(1));
                                        }
                                        section += 3; //Found precision section, now to check for next section
                                    }
                                    else if (SPECIFIERS.indexOf(format.charAt(i + 1)) < 0)
                                    {
                                        throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                    }
                                }
                                else
                                {
                                    throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                }
                            }
                            else if (LENGTH.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                	for(int j = 0; j < 3; j++)
                                	{
                                		argList.addElement(null);
                                	}
                                    argList.addElement(bu.toString().substring(1));
                                }
                                section += 4; //Found length section, now to check for next section
                            }
                            else
                            {
                                throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                            }
                            break;
                        case 1: //Flags
                            if (SPECIFIERS.indexOf(c) >= 0)
                            {
                                //Found the end, exit
                                section = -1;
                                String str = bu.toString();
                                if (argList != null)
                                {
                                	for(int j = 0; j < 3; j++)
                                	{
                                		argList.addElement(null);
                                	}
                                    argList.addElement(c + "");
                                }
                                else
                                {
                                    //If we don't do this it will become redundent and we will get a stack overflow
                                    parts.addElement(FormatElement.getFormatter(str));
                                }
                                bu.setLength(0);
                                inFormat = false;
                            }
                            else if (FLAGS.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                	argList.setElementAt(((String)argList.elementAt(0)) + c, 0);
                                }
                                continue; //Still looking at flag values
                            }
                            else if (WIDTH_PRECISION.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                	argList.addElement(c + "");
                                }
                                section++; //Found width section, now to check for next section
                            }
                            else if (c == '.')
                            {
                            	//Precision is prefixed with a decimal, make sure that there is more to the format then just a decimal at the end.
                                if (i + 1 < len)
                                {
                                    if (WIDTH_PRECISION.indexOf(format.charAt(i + 1)) >= 0)
                                    {
                                        if (argList != null)
                                        {
                                            argList.addElement(null);
                                            argList.addElement(c + "");
                                        }
                                        section += 2; //Found precision section, now to check for next section
                                    }
                                    else if (SPECIFIERS.indexOf(format.charAt(i + 1)) < 0)
                                    {
                                        throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                    }
                                }
                                else
                                {
                                    throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                }
                            }
                            else if (LENGTH.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                	for(int j = 0; j < 2; j++)
                                	{
                                		argList.addElement(null);
                                	}
                                    argList.addElement(c + "");
                                }
                                section += 3; //Found length section, now to check for next section
                            }
                            else
                            {
                                throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                            }
                            break;
                        case 2: //Width
                            if (SPECIFIERS.indexOf(c) >= 0)
                            {
                                //Found the end, exit
                                section = -1;
                                String str = bu.toString();
                                if (argList != null)
                                {
                                	for(int j = 0; j < 2; j++)
                                	{
                                		argList.addElement(null);
                                	}
                                    argList.addElement(c + "");
                                }
                                else
                                {
                                    //If we don't do this it will become redundent and we will get a stack overflow
                                    parts.addElement(FormatElement.getFormatter(str));
                                }
                                bu.setLength(0);
                                inFormat = false;
                            }
                            else if (WIDTH_PRECISION.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.setElementAt(((String)argList.elementAt(1)) + c, 1);
                                }
                                continue; //Still looking at width values
                            }
                            else if (c == '.')
                            {
                            	//Precision is prefixed with a decimal, make sure that there is more to the format then just a decimal at the end.
                                if (i + 1 < len)
                                {
                                    if (WIDTH_PRECISION.indexOf(format.charAt(i + 1)) >= 0)
                                    {
                                        if (argList != null)
                                        {
                                            argList.addElement(c + "");
                                        }
                                        section++; //Found precision section, now to check for next section
                                    }
                                    else if (SPECIFIERS.indexOf(c) < 0)
                                    {
                                        throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                    }
                                }
                                else
                                {
                                    throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                                }
                            }
                            else if (LENGTH.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.addElement(null);
                                    argList.addElement(c + "");
                                }
                                section += 2; //Found length section, now to check for next section
                            }
                            else
                            {
                                throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                            }
                            break;
                        case 3: //Precision
                            if (SPECIFIERS.indexOf(c) >= 0)
                            {
                                //Found the end, exit
                                section = -1;
                                String str = bu.toString();
                                if (argList != null)
                                {
                                    argList.addElement(null);
                                    argList.addElement(c + "");
                                }
                                else
                                {
                                    //If we don't do this it will become redundent and we will get a stack overflow
                                    parts.addElement(FormatElement.getFormatter(str));
                                }
                                bu.setLength(0);
                                inFormat = false;
                            }
                            else if (WIDTH_PRECISION.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.setElementAt(((String)argList.elementAt(2)) + c, 2);
                                }
                                continue; //Still looking at precision values
                            }
                            else if (LENGTH.indexOf(c) >= 0)
                            {
                                if (argList != null)
                                {
                                    argList.addElement(c + "");
                                }
                                section++; //Found length section, now to check for next section
                            }
                            else
                            {
                                throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                            }
                            break;
                        case 4: //Length
                            if (SPECIFIERS.indexOf(c) >= 0)
                            {
                                //Found the end, exit
                                section = -1;
                                String str = bu.toString();
                                if (argList != null)
                                {
                                    argList.addElement(c + "");
                                }
                                else
                                {
                                    //If we don't do this it will become redundant and we will get a stack overflow
                                    parts.addElement(FormatElement.getFormatter(str));
                                }
                                bu.setLength(0);
                                inFormat = false;
                            }
                            else
                            {
                                throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                            }
                            break;
                    }
                }
                //If args isn't null then copy the broken up components into the argument
                if (!inFormat && argList != null)
                {
                	String[] argListAr = new String[argList.size()];
                	argList.copyInto(argListAr);
                    args[0] = argListAr;
                }
            }
            else
            {
            	//Look for a format element
                if (c == '%')
                {
                	//Found one
                    if (i + 1 < len)
                    {
                        if (format.charAt(i + 1) == '%')
                        {
                            i++;
                            bu.append('%');
                        }
                        else
                        {
                            inFormat = true;
                            if (bu.length() > 0)
                            {
                                parts.addElement(bu.toString());
                                bu.setLength(0);
                            }
                            bu.append('%');
                            section = 0; //Used to determine what part of the format is being checked
                        }
                    }
                    else
                    {
                        throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                    }
                }
                else
                {
                    bu.append(c);
                }
            }
        }
        //If anything is left over then process it
        if (bu.length() > 0)
        {
            if (inFormat)
            {
                parts.addElement(FormatElement.getFormatter(bu.toString()));
            }
            else
            {
                parts.addElement(bu.toString());
            }
        }
        Object[] partsAr = new Object[parts.size()];
        parts.copyInto(partsAr);
        return partsAr;
	}
	
	public static abstract class FormatElement
	{
		protected String format;
		
        protected FormatElement(String format)
        {
            this.format = format;
        }
        
        public abstract String format(Object obj);
        
        public abstract void unformat(String value, Object[] refO, int[] vals);
        
        public String getFormat()
        {
            return format;
        }
        
        public abstract void setInputValue(Long one, Long two);
        
        public abstract boolean takesArg();
        
        public abstract int requires();
        
        public abstract int argLocation();
        
        public abstract String getNullParamOutput();
        
        public abstract void reset();
        
        public boolean hasArgLocation()
        {
            return argLocation() >= 0;
        }
        
        public static FormatElement getFormatter(String form)
        {
            if (form.charAt(0) != '%')
            {
                return null;
            }
            switch (form.charAt(form.length() - 1))
            {
            	case 'C':
            	case 'S':
                case 'c': //Character
                case 's': //String
                    return new StringFormatElement(form);
                case 'd':
                case 'i': //Signed decimal integer
                case 'o': //Signed octal
                case 'x':
                case 'X': //Unsigned hexadecimal integer
                case 'u': //Unsigned decimal integer
                    return new IntFormatElement(form);
                //case 'a':
                //case 'A':
                case 'e':
                case 'E': //Scientific notation
                case 'g':
                case 'G': //Takes the smaller output of 'f' and 'e'/'E'
                case 'f':
                case 'F': //Decimal floating point
                    return new FloatFormatElement(form);
                case 'p': //Pointer address
                    return new PointerFormatElement(form);
            }
	        return new GenericFormatElement(form);
        }
        
        protected void argError(String formatter, String defValue, Class element)
        {
        	System.err.println(formatter + Resources.getString(BBXResource.PRINTUTILITY_UNK_ARG) + defValue + ". Arg:" + element);
        }
        
        public String ToString()
        {
            return format;
        }
	}
	
	private static abstract class GeneralFormatElement extends FormatElement
    {
        private boolean arg, lengthDoubleSize;
        protected String flags;
        protected char length, type;
        protected int width, precision, requiresInput, argPos;
        
        public GeneralFormatElement(String format)
        {
        	super(format);
        	reset();
            this.arg = true; //Not sure why this should be included but could be useful in the future or depending on implementation.
            parseFormat();
        }
        
        public void reset()
        {
        	this.precision = -1;
            this.width = -1;
        }
        
        private void parseFormat()
        {
            String[][] parts = new String[1][];
            PrintUtility.breakFormat(this.format, parts);
            String[] elements = parts[0];
            int pos = 0;
            if (elements.length == 6)
            {
                pos++;
                argPos = Integer.parseInt(elements[0]) - 1;
            }
            else
            {
                argPos = -1;
            }
            if (elements[pos++] != null)
            {
                //Flags
                this.flags = elements[pos - 1];
            }
            if (elements[pos++] != null)
            {
                //Width
                String el = elements[pos - 1];
                int loc;
                if ((loc = el.indexOf('*')) >= 0)
                {
                    requiresInput = 1;
                }
                if (el.length() > loc + 1)
                {
                    width = Integer.parseInt(loc >= 0 ? el.substring(loc + 1) : el);
                }
            }
            if (elements[pos++] != null)
            {
                //Precision
                String el = elements[pos - 1];
                if (el.indexOf('*') >= 0)
                {
                    requiresInput++;
                    if (requiresInput == 1)
                    {
                        //No first element, need to make sure only second element is retrieved
                        requiresInput |= 1 << 2;
                    }
                }
                else
                {
                    precision = Integer.parseInt(el.substring(1));
                }
            }
            if (elements[pos++] != null)
            {
                //Length
                String el = elements[pos - 1];
                char c1 = el.charAt(0);
                if (el.length() > 1)
                {
                	char c2 = el.charAt(1);
                    if (c1 != c2 || (c1 != 'h' || c1 != 'l'))
                    {
                    	throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                    }
                    this.lengthDoubleSize = true;
                }
                this.length = c1;
            }
            type = elements[pos].charAt(0);
        }

        public String format(Object obj)
        {
            String str = inFormat(obj);
            if (flags != null)
            {
                if (flags.indexOf('-') >= 0)
                {
                	/*
                    if (flags.indexOf('0') >= 0)
                    {
                    	throw new IllegalArgumentException(Resources.getString(BBXResource.PRINTUTILITY_BAD_STRING_FORMAT));
                    }
                    */
                    //Left align
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                	str = StringUtilities.pad(str, ' ', width, false);
//#else
                    if (str.length() < width)
                    {
                    	char[] chars = new char[width - str.length()];
                    	Arrays.fill(chars, ' ');
                        str += new String(chars);
                    }
//#endif
                }
                else if (this.precision == -1 && flags.indexOf('0') >= 0 && SPECIFIERS.indexOf(this.type) > 2)
                {
                	//Pad with zeros (for everything but char, string, and pointer) when precision not specified
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                	str = StringUtilities.pad(str, '0', width, true);
//#else
                    if (str.length() < width)
                    {
                    	char[] chars = new char[width - str.length()];
                    	Arrays.fill(chars, '0');
                        str = new String(chars) + str;
                    }
//#endif
                }
            }
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
            str = StringUtilities.pad(str, ' ', width, true);
//#else
            if (str.length() < width)
            {
            	//Right align
            	char[] chars = new char[width - str.length()];
            	Arrays.fill(chars, ' ');
                str = new String(chars) + str;
            }
//#endif
            return str;
        }
        
        public abstract String inFormat(Object obj);

        public void setInputValue(Long one, Long two)
        {
            if (one != null)
            {
                if ((requiresInput & (1 << 2)) != 0)
                {
                    precision = (int)one.longValue();
                }
                else
                {
                    width = (int)one.longValue();
                }
            }
            if (two != null)
            {
                precision = (int)two.longValue();
            }
        }

        public boolean takesArg()
        {
            return arg;
        }

        public int requires()
        {
            return requiresInput & 3;
        }

        public int argLocation()
        {
            return argPos;
        }
    }
	
	private static class GenericFormatElement extends GeneralFormatElement
    {
        public GenericFormatElement(String format)
        {
        	super(format);
        }

        public String inFormat(Object obj)
        {
        	argError("GenericFormat", "$format", obj.getClass());
            return this.format;
        }
        
        public String getNullParamOutput()
        {
        	return inFormat(null);
        }

        public void unformat(String value, Object[] refO, int[] vals)
        {
            throw new UnsupportedOperationException();
        }
    }
	
	private static class StringFormatElement extends GeneralFormatElement
    {
        public StringFormatElement(String format)
        {
        	super(format);
        }

        public String inFormat(Object obj)
        {
            boolean charType = this.type == 'c';
            String str = null;
            if (obj instanceof String)
            {
                str = (String)obj;
            }
            else if (obj instanceof StringBuffer)
            {
                str = ((StringBuffer)obj).toString();
                int len = strlen(str.toCharArray(), 0);
                if(len != str.length())
                {
                	str = str.substring(0, len);
                }
            }
            if (str == null)
            {
                if (obj instanceof char[])
                {
                    if (this.length == 'l')
                    {
                        str = new String((char[])obj, 0, strlen((char[])obj, 0));
                    }
                    else
                    {
                        char[] chars = (char[])obj;
                        int len;
                        byte[] nBytes = new byte[len = charType ? 1 : strlen(chars, 0)];
                        for (int i = 0; i < len; i++)
                        {
                            nBytes[i] = (byte)chars[i];
                        }
                        str = new String(nBytes);
                    }
                }
                else if (obj instanceof Character)
                {
                	char c = ((Character)obj).charValue();
                	if(this.length != 'l')
                	{
                		c = (char)(c & 0xFF);
                	}
                    str = String.valueOf(c);
                }
                else if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long)
                {
                    long val;
                    int mask = this.length == 'l' ? 0xFFFF : 0xFF;
                    if (obj instanceof Byte)
                    {
                        val = ((Byte)obj).byteValue() & mask;
                    }
                    else if (obj instanceof Short)
                    {
                        val = ((Short)obj).shortValue() & mask;
                    }
                    else if (obj instanceof Integer)
                    {
                        val = ((Integer)obj).intValue() & mask;
                    }
                    else
                    {
                        val = ((Long)obj).longValue() & mask;
                    }
                    str = String.valueOf((char)val);
                }
                else
                {
                	argError("StringFormat", "obj.toString()", obj.getClass());
                    str = obj.toString(); //This will return ASCII
                }
            }
            else if (this.length != 'l')
            {
                char[] chars = str.toCharArray();
                int len;
                byte[] nBytes = new byte[len = charType ? 1 : chars.length];
                for (int i = 0; i < len; i++)
                {
                    nBytes[i] = (byte)chars[i];
                }
                str = new String(nBytes);
            }
            if (charType)
            {
                if (str.length() > 1)
                {
                    str = str.substring(0, 1);
                }
            }
            else if (this.precision >= 0 && this.type != 'c')
            {
                if (str.length() > this.precision)
                {
                    str = str.substring(0, this.precision);
                }
            }
            return str;
        }
        
        public String getNullParamOutput()
        {
        	return this.type == 'c' ? "\0" : "(null)";
        }

        public void unformat(String value, Object[] refO, int[] vals)
        {
        	int w = this.width;
            if (w < 0)
            {
                w = 1;
            }
            int len = value.length();
            int org = vals[0];
            if (this.type == 'c')
            {
                char[] items = new char[w];
                value.getChars(org, org + w, items, 0);
                vals[0] += w;
                int t = vals[1] + w;
                if (this.requiresInput == 1)
                {
                    return;
                }
                for (int i = vals[1], e = 0; i < t; i++, e++)
                {
                    vals[1]++;
                    Object obj = refO[i];
                    if (obj == null || !obj.getClass().isArray())
                    {
                    	if(obj != null)
                    	{
                    		if(!(obj instanceof StringBuffer))
                    		{
                    			return;
                    		}
                    	}
                		else
                		{
                			return;
                		}
                    }
                    if (obj instanceof char[])
                    {
                        ((char[])obj)[0] = items[e];
                    }
                    else if(obj instanceof byte[])
                    {
                    	((byte[])obj)[0] = (byte)items[e];
                    }
                    else if(obj instanceof short[])
                    {
                    	((short[])obj)[0] = (short)items[e];
                    }
                    else if(obj instanceof int[])
                    {
                    	((int[])obj)[0] = items[e];
                    }
                    else if(obj instanceof long[])
                    {
                    	((long[])obj)[0] = items[e];
                    }
                    else if(obj instanceof StringBuffer)
                    {
                    	StringBuffer buf = (StringBuffer)obj;
                    	if(buf.length() == buf.capacity())
                    	{
                    		buf.setCharAt(0, items[e]);
                    	}
                    	else
                    	{
                    		buf.append(items[e]);
                    	}
                    }
                    else
                    {
                    	argError("StringFormat", "null", obj.getClass());
                    }
                }
            }
            else
            {
                for (w = 0; w < len; w++)
                {
                    if (PrintUtility.isWhiteSpace(value.charAt(org + w)))
                    {
                        break;
                    }
                }
                if (this.width < w && this.width != -1)
                {
                    w = this.width;
                }
                vals[0] += w;
                if (this.requiresInput == 1)
                {
                    return; //Skip argument
                }
                Object obj = refO[vals[1]];
                vals[1]++;
                if (obj == null || !obj.getClass().isArray())
                {
                	if(obj != null)
                	{
                		if(!(obj instanceof StringBuffer))
                		{
                			return;
                		}
                	}
            		else
            		{
            			return;
            		}
                }
                String sVal = value.substring(org, org + w);
                if (obj instanceof String[])
                {
                    ((String[])refO)[0] = sVal;
                }
                else if (obj instanceof char[])
                {
                    System.arraycopy(sVal.toCharArray(), 0, (char[])obj, 0, w);
                }
                else if(obj instanceof byte[])
                {
                	System.arraycopy(sVal.getBytes(), 0, (byte[])obj, 0, w);
                }
                else if(obj instanceof short[])
                {
                	short[] sh = (short[])obj;
                	char[] ch = sVal.toCharArray();
                	for(int i = 0; i < w; i++)
                	{
                		sh[i] = (short)ch[i];
                	}
                }
                else if(obj instanceof StringBuffer)
                {
                	StringBuffer buf = (StringBuffer)obj;
                	if(buf.length() == buf.capacity())
                	{
                		strncpy(buf, 0, sVal.toCharArray(), 0, w);
                	}
                	else
                	{
                		buf.append(sVal);
                	}
                }
                else
                {
                	argError("StringFormat", "null", obj.getClass());
                }
            }
        }
        
        //From LCMS for BlackBerry
        
        private static int strlen(char[] chars, int origin)
        {
        	//This will work on both C style strings and Java strings
        	int len = origin;
        	int aLen = chars.length;
        	if(aLen == 0)
        	{
        		return 0;
        	}
        	while(len < aLen && chars[len++] != 0);
        	if(len == aLen && chars[len - 1] != 0)
        	{
        		len++;
        	}
        	return (len - origin) - 1;
        }
        
        private static int strncpy(StringBuffer dst, int dstOffset, char[] src, int srcOffset, int count)
        {
        	int len = Math.min(strlen(src, srcOffset), count);
        	int appendPos = dst.length();
        	len += dstOffset;
        	int d, s;
        	for(d = dstOffset, s = srcOffset; d < len; d++, s++)
        	{
        		if(d >= appendPos)
        		{
        			dst.append(src[s]);
        		}
        		else
        		{
        			dst.setCharAt(d, src[s]);
        		}
        	}
        	if(d >= appendPos)
        	{
        		dst.append('\0');
        	}
        	else
        	{
        		dst.setCharAt(d, '\0');
        	}
        	return len - dstOffset;
        }
    }
	
	private static class IntFormatElement extends GeneralFormatElement
    {
		private static LargeNumber[] MAX;
		
        private boolean signed;
        private boolean basicType;

        public IntFormatElement(String format)
        {
        	super(format);
            switch (this.type)
            {
                default:
                case 'd':
                case 'i':
                    //Signed decimal integer
                    signed = true;
                    basicType = true;
                    break;
                case 'o':
                    //Signed octal
                    signed = true;
                    basicType = false;
                    break;
                case 'x':
                case 'X':
                    //Unsigned hexadecimal integer
                    signed = false;
                    basicType = false;
                    break;
                case 'u':
                    //Unsigned decimal integer
                    signed = false;
                    basicType = true;
                    break;
            }
        }

        public String inFormat(Object obj)
        {
            StringBuffer bu = new StringBuffer();
            long value = 0;
            int bCount = 1;
            switch(this.length)
        	{
        		default:
        			if(obj instanceof Byte)
                	{
                		value = ((Byte)obj).byteValue();
                		bCount = 1;
                	}
                	else if(obj instanceof Short)
                	{
                		value = ((Short)obj).shortValue();
                		bCount = 2;
                	}
                	else if(obj instanceof Integer)
                	{
                		value = ((Integer)obj).intValue();
                		bCount = 4;
                	}
                	else if(obj instanceof Long)
                	{
                		value = ((Long)obj).longValue();
                		bCount = 8;
                	}
                	else if(obj instanceof Float)
                	{
                		value = Float.floatToIntBits(((Float)obj).floatValue());
                		bCount = 4;
                	}
                	else if(obj instanceof Double)
                	{
                		value = Double.doubleToLongBits(((Double)obj).doubleValue());
                		bCount = 8;
                	}
                	else
                	{
                		argError("IntFormat", "0", obj.getClass());
                		value = 0;
                	}
        			break;
        		case 'h':
        			if(super.lengthDoubleSize)
        			{
        				value = ((Character)obj).charValue();
        				bCount = 2;
        			}
        			else
        			{
        				value = ((Short)obj).shortValue();
        				bCount = 2;
        			}
        			break;
        		case 'z':
        		case 'j':
        		case 't':
        			value = ((Integer)obj).intValue();
        			bCount = 4;
        			break;
        		case 'l':
        			if(super.lengthDoubleSize)
        			{
        				value = ((Long)obj).longValue();
        				bCount = 8;
        			}
        			else
        			{
        				value = ((Integer)obj).longValue();
        				bCount = 4;
        			}
        			break;
        	}
            if (this.flags != null)
            {
                if (value >= 0)
                {
                    if (flags.indexOf(' ') >= 0)
                    {
                        bu.append(' ');
                    }
                    else if (flags.indexOf('+') >= 0)
                    {
                        bu.append('+');
                    }
                }
            }
            String str;
            if (signed)
            {
                if (basicType)
                {
                	str = thousandsSep(flags, Long.toString(value));
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                	bu.append(StringUtilities.pad(str, '0', this.length, true));
//#else
                    if (str.length() < this.length)
                    {
                    	char[] chars = new char[this.length - str.length()];
                    	Arrays.fill(chars, '0');
                        bu.append(chars);
                    }
                    bu.append(str);
//#endif
                }
                else
                {
                	//It's actually an unsigned print out
                	if(value < 0)
                	{
                		str = negNumberToUnsigned(bCount, value).toString(8);
                	}
                	else
                	{
                		str = Long.toString(value, 8);
                	}
                    if (flags != null && flags.indexOf('#') >= 0 && value != 0)
                    {
                        bu.append('0');
                    }
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                    bu.append(StringUtilities.pad(str, '0', this.length - bu.length(), true));
//#else
                    if (str.length() + bu.length() < this.length)
                    {
                    	char[] chars = new char[(this.length + bu.length()) - str.length()];
                    	Arrays.fill(chars, '0');
                        bu.append(chars);
                    }
                    bu.append(str);
//#endif
                }
            }
            else
            {
                if (basicType)
                {
                	if(value < 0)
                	{
                		str = negNumberToUnsigned(bCount, value).toString();
                	}
                	else
                	{
                		str = Long.toString(value);
                	}
                	str = thousandsSep(flags, str);
                	//str = ulongToString(value);
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                	bu.append(StringUtilities.pad(str, '0', this.length, true));
//#else
                	if (str.length() < this.length)
                    {
                    	char[] chars = new char[this.length - str.length()];
                    	Arrays.fill(chars, '0');
                        bu.append(chars);
                    }
                    bu.append(str);
//#endif
                }
                else
                {
                	if(value < 0)
                	{
                		//If the value is negative then converting to hex with the built in functions will append a '-' sign to it. This is not how printf works so do it with LargeNumber.
                		str = negNumberToUnsigned(bCount, value).toString(16);
                	}
                	else
                	{
                		str = Long.toString(value, 16);
                	}
                    if (flags != null && flags.indexOf('#') >= 0 && value != 0)
                    {
                        bu.append('0');
                        bu.append(this.type);
                    }
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
                    bu.append(StringUtilities.pad(str, '0', this.length - bu.length(), true));
//#else
                    if (str.length() + bu.length() < this.length)
                    {
                    	char[] chars = new char[(this.length + bu.length()) - str.length()];
                    	Arrays.fill(chars, '0');
                        bu.append(chars);
                    }
                    bu.append(str);
//#endif
                }
            }
            return bu.toString();
        }
        
        private static String thousandsSep(String flags, String value)
        {
        	int start = Character.isDigit(value.charAt(0)) ? 0 : 1;
        	int len = value.length() - start;
        	if(flags != null && flags.indexOf(THOUS_SEP) >= 0 && len > 3)
        	{
        		StringBuffer bu = new StringBuffer(value);
        		int sep = len / 3;
        		int front = len % 3;
        		if(front == 0)
        		{
        			sep--;
        			front = 3;
        		}
        		for(int i = front + start; sep > 0; sep--, i += 4)
        		{
        			bu.insert(i, ',');
        		}
        		value = bu.toString();
        	}
        	return value;
        }
        
        private static LargeNumber negNumberToUnsigned(int byteCount, long value)
        {
        	LargeNumber num = new LargeNumber(value);
        	if(IntFormatElement.MAX == null)
        	{
        		IntFormatElement.MAX = new LargeNumber[4];
        	}
        	LargeNumber and = null;
        	for(int i = 0; i < 4; i++)
        	{
        		if(byteCount == 1 << i)
        		{
        			if(IntFormatElement.MAX[i] == null)
        			{
        				byte[] bytes = new byte[8];
        				Arrays.fill(bytes, (byte)255, 8 - byteCount, byteCount);
        				IntFormatElement.MAX[i] = new LargeNumber(BitConverter.toInt64(bytes, 0));
        			}
        			and = IntFormatElement.MAX[i];
        			break;
        		}
        	}
    		return num.and(and);
        }
        
        /*
        private String ulongToString(long _val)
        {
        	//Not very efficient but works well
        	StringBuffer bu = new StringBuffer();
            if ((_val & 0x8000000000000000L) != 0)
            {
            	_val = _val & 0x7FFFFFFFFFFFFFFFL;
                if (_val != 0L)
                {
                	long value = _val;
                    //Get the value as an array
                    byte[] result = new byte[] { 0, 9, 2, 2, 3, 3, 7, 2, 0, 3, 6, 8, 5, 4, 7, 7, 5, 8, 0, 8 };
                    
                    byte[] addValues = new byte[19];
                    int i = 19;
                    while (value != 0)
                    {
                        addValues[--i] = (byte)(value % 10);
                        value /= 10;
                    }
                    
                    //Add "addValues" to result
                    int a = 18;
                    int r = 19;
                    int rem = 0;
                    for (int k = 19; k >= 0; k--, r--, a--)
                    {
                        byte add = (byte)(a >= i ? addValues[a] : 0);
                        byte val = (byte)(result[r] + add + rem);
                        rem = 0;
                        if (val >= 10)
                        {
                            rem = 1;
                            val -= 10;
                        }
                        result[r] = val;
                    }
                    
                    //Convert array to string
                    boolean writing = false;
                    for (int k = 0; k < 20; k++)
                    {
                        if (writing)
                        {
                            bu.append(result[k]);
                        }
                        else if (result[k] != 0)
                        {
                            writing = true;
                            k--;
                        }
                    }
                }
                else
                {
                    bu.append("9223372036854775808");
                }
            }
            else
            {
                //Simply write the value
                bu.append(_val);
            }
            return bu.toString();
        }
        */
        
        public String getNullParamOutput()
        {
        	return "0";
        }
        
        public void unformat(String value, Object[] refO, int[] vals)
        {
        	int len = value.length();
            int org = vals[0];
            int w;
            boolean sign = false;
            char c;
            char type = this.type;
            boolean basicType = this.basicType;
            boolean signed = this.signed;
            for (w = 0; org + w < len; w++) //Make sure we don't reach the end of the string and try to continue checking digits
            {
            	c = value.charAt(org + w);
                if (!sign)
                {
                    if (Character.isDigit(c))
                    {
                        sign = true;
                        if (type == 'i' && c == '0')
                        {
                            //We might be on to something
                            if (w + 1 < len)
                            {
                                if (value.charAt(org + w + 1) == 'x')
                                {
                                    //A ha, hex
                                    type = 'Z'; //Do this to trick the rest of the parser into reading hex
                                    basicType = false;
                                    if (signed && c == '-')
                                    {
                                        //Hmm, if this is a hex integer it can't be negative
                                        return;
                                    }
                                    signed = false;
                                    w++;
                                }
                                else
                                {
                                    //Since it wasn't hex then it must be octal
                                    type = 'o';
                                    basicType = false;
                                    signed = true;
                                }
                            }
                        }
                        else
                        {
                            w--;
                        }
                    }
                    else if (c == '+' || (signed && c == '-'))
                    {
                        sign = true;
                        if (type == 'i')
                        {
                            //Do a quick check to see what the next value is
                            if (w + 1 < len)
                            {
                                if (value.charAt(org + w + 1) == '0')
                                {
                                    w++;
                                    //We might be on to something
                                    if (w + 1 < len)
                                    {
                                        if (value.charAt(org + w + 1) == 'x')
                                        {
                                            //A ha, hex
                                            type = 'Z'; //Do this to trick the rest of the parser into reading hex
                                            basicType = false;
                                            if (signed && c == '-')
                                            {
                                                //Hmm, if this is a hex integer it can't be negative
                                                return;
                                            }
                                            signed = false;
                                            w++;
                                        }
                                        else
                                        {
                                            //Since it wasn't hex then it must be octal
                                            type = 'o';
                                            basicType = false;
                                            signed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    if (Character.isDigit(c))
                    {
                        if (!basicType && signed && c > '7')
                        {
                            //Octal
                            break;
                        }
                        continue;
                    }
                    else if (!basicType)
                    {
                        if (!signed)
                        {
                            //Hex
                            if (type <= 'X')
                            {
                                //Upper case
                                if (c >= 'A' && c <= 'F')
                                {
                                    continue;
                                }
                            }
                            else if (type <= 'x')
                            {
                                //Lower case
                                if (c >= 'a' && c <= 'f')
                                {
                                    continue;
                                }
                            }
                            else if (type == 'Z')
                            {
                                //Dynamic hex
                                if ((c >= 'A' && c <= 'F') && (c >= 'a' && c <= 'f'))
                                {
                                    continue;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            vals[0] += w;
            if (this.requiresInput == 1)
            {
                return;
            }
            String stval = value.substring(org, org + w);
            short sval = 0;
            int ival = 0;
            long lval = 0;
            int slen = stval.length();
            if(slen > 65) //Maximum length of a integer string if the maximum value of an unsigned long is converted to binary and an extra char is added, anything less then this should be processed quickly without problem
            {
            	//Do some preliminary size checks, if a number is 800 chars it's not going to get parsed (unless every digit is a zero or something similar)
            	for(int i = 0, minStart = slen - 65; i < slen; i++)
            	{
            		c = stval.charAt(i);
            		if(c != '0' && charNumRangeComp(c, 'f', 'F'))
            		{
            			if(i < minStart)
            			{
            				//Too many digits. The program will throw an exception anyway so save the extra execution.
                    		throw new NumberFormatException();
            			}
            			break;
            		}
            	}
            }
            try
            {
                switch (type)
                {
                    case 'd':
                    case 'i':
                        //Signed decimal integer
                        switch (this.length)
                        {
                            default:
                            	ival = Integer.parseInt(stval);
                                break;
                            case 'h':
                                sval = Short.parseShort(stval);
                                break;
                            case 'l':
                                lval = Long.parseLong(stval);
                                break;
                        }
                        break;
                    case 'o':
                        //Signed octal
                    	switch (this.length)
                        {
                            default:
                            	ival = Integer.parseInt(stval, 8);
                                break;
                            case 'h':
                                sval = Short.parseShort(stval, 8);
                                break;
                            case 'l':
                                lval = Long.parseLong(stval, 8);
                                break;
                        }
                        break;
                        
                    case 'x':
                    case 'X':
                    case 'Z': //Small hack
                        //Unsigned hexadecimal integer
                    	boolean handled = false;
                    	LargeNumber num;
                    	//First try to see if parsing can be done with normal functions
                    	switch (this.length)
                        {
                            default:
                            	if(stval.length() <= 8)
                            	{
                            		if(stval.charAt(0) < '8')
                            		{
                            			ival = Integer.parseInt(stval, 16);
                            			handled = true;
                            		}
                            	}
                                break;
                            case 'h':
                            	if(stval.length() <= 4)
                            	{
                            		if(stval.charAt(0) < '8')
                            		{
                            			sval = Short.parseShort(stval, 16);
                            			handled = true;
                            		}
                            	}
                                break;
                            case 'l':
                            	if(stval.length() <= 16)
                            	{
                            		if(stval.charAt(0) < '8')
                            		{
                            			lval = Long.parseLong(stval, 16);
                            			handled = true;
                            		}
                            	}
                                break;
                        }
                    	if(!handled)
                    	{
                    		//Since the hex value wasn't handled then it must be larger then what can actually be processed. So use a larger type and see if we can go down.
	                    	switch (this.length)
	                        {
	                            default:
	                            	lval = Long.parseLong(stval, 16);
	                            	if(lval > Integer.MAX_VALUE * 2L)
	                            	{
	                            		//Out of range, let it fail by default
	                            		Integer.parseInt(stval, 16);
	                            	}
	                            	ival = (int)(lval & 0xFFFFFFFF);
	                                break;
	                            case 'h':
	                            	ival = Integer.parseInt(stval, 16);
	                            	if(ival > Short.MAX_VALUE * 2)
	                            	{
	                            		//Out of range, let it fail by default
	                            		Short.parseShort(stval, 16);
	                            	}
	                            	sval = (short)(ival & 0xFFFF);
	                                break;
	                            case 'l':
	                            	num = LargeNumber.parse(stval, 16);
	                            	if(!num.canReturnLong())
	                            	{
	                            		//Out of range, let it fail by default
	                            		Long.parseLong(stval, 16);
	                            	}
	                                lval = num.longValue();
	                                break;
	                        }
                    	}
                        break;
                    case 'u':
                        //Unsigned decimal integer
                    	switch (this.length)
                        {
                            default:
                            	ival = (int)(Long.parseLong(stval) & 0xFFFFFFFF);
                                break;
                            case 'h':
                                sval = (short)(Integer.parseInt(stval) & 0xFFFF);
                                break;
                            case 'l':
                            	num = LargeNumber.parse(stval);
                            	if(!num.canReturnLong())
                            	{
                            		throw new NumberFormatException(Resources.getString(BBXResource.PRINTUTILITY_UNSIGNED_NUMBER_UNPARSEABLE_LONG));
                            	}
                            	lval = num.longValue();
                                break;
                        }
                        break;
                }
            }
            catch (NumberFormatException e)
            {
                return;
            }
            Object obj = refO[vals[1]];
            if (obj == null || !obj.getClass().isArray())
            {
            	//TODO: For "ref" types
            	if(obj == null)
            	{
            		return;
            	}
            }
            vals[1]++;
            boolean written = false;
            switch (this.length)
            {
                default:
                    if (obj instanceof int[])
                    {
                        written = true;
                        ((int[])obj)[0] = ival;
                    }
                    break;
                case 'h':
                    if (obj instanceof short[])
                    {
                        written = true;
                        ((short[])obj)[0] = sval;
                    }
                    break;
                case 'l':
                    if (obj instanceof long[])
                    {
                        written = true;
                        ((long[])obj)[0] = lval;
                    }
                    break;
            }
            if (!written)
            {
                //Error
                vals[1]--;
                vals[0] -= w;
            }
        }
        
        //From LCMS for BlackBerry
        
        private static boolean charNumRangeComp(int c, int lowerEnd, int upperEnd)
        {
        	return Character.isDigit((char)c) || (((c >= 'a') && (c <= lowerEnd)) || ((c >= 'A') && (c <= upperEnd)));
        }
    }
	
	private static class FloatFormatElement extends GeneralFormatElement
    {
		private static final int DEFAULT_PRECISION = 6; //Nearly every reference says 6 except www.cplusplus.com
		private static final LargeNumber TEN = new LargeNumber(10L);
		
        public FloatFormatElement(String format)
        {
        	super(format);
        }
        
        public String inFormat(Object obj)
        {
        	double value = 0;
            if (this.length == 'L')
            {
                value = Double.longBitsToDouble(((Long)obj).longValue());
            }
            else
            {
            	if (obj instanceof Float)
	            {
	                value = ((Float)obj).doubleValue();
	            }
	            else if(obj instanceof Double)
	            {
	                value = ((Double)obj).doubleValue();
	            }
	            else
	            {
	            	argError("FloatFormat", "0", obj.getClass());
	        		value = 0;
	            }
            }
            
            //Determine what to do
            StringBuffer fixedBuf = null;
            StringBuffer expBuf = null;
            boolean caps = type <= 'Z';
            boolean alt = this.flags != null && this.flags.indexOf('#') >= 0;
            if (type == 'g' || type == 'G')
            {
                fixedBuf = new StringBuffer();
                expBuf = new StringBuffer();
            }
            else if (type == 'f' || type == 'F')
            {
                fixedBuf = new StringBuffer();
            }
            else if (type == 'e' || type == 'E')
            {
                expBuf = new StringBuffer();
            }
            
            //To string for the specified type
            if (fixedBuf != null)
            {
            	if (this.flags != null)
                {
                    if (value >= 0)
                    {
                        if (flags.indexOf(' ') >= 0)
                        {
                        	fixedBuf.append(' ');
                        }
                        else if (flags.indexOf('+') >= 0)
                        {
                        	fixedBuf.append('+');
                        }
                    }
                }
                writeFloatingPoint(fixedBuf, value, this.precision);
            }
            if (expBuf != null)
            {
                if (fixedBuf == null)
                {
                	if (this.flags != null)
                    {
                        if (value >= 0)
                        {
                            if (flags.indexOf(' ') >= 0)
                            {
                            	expBuf.append(' ');
                            }
                            else if (flags.indexOf('+') >= 0)
                            {
                            	expBuf.append('+');
                            }
                        }
                    }
                    writeFloatingPoint(expBuf, value, this.precision);
                }
                else
                {
                    //expBuf.append(fixedBuf.toString());
                	StringUtilities.append(expBuf, fixedBuf);
                }
                if(!Double.isInfinite(value) && !Double.isNaN(value))
                {
	                String temp = expBuf.toString();
	                int tlen = temp.length();
	            	int index = temp.indexOf(DECIMAL);
	            	
	            	//Get the exponent
	            	int exp = 0;
	            	int off = Character.isDigit(temp.charAt(0)) ? 0 : 1;
	            	int i;
	            	if(index == 1 + off)
	            	{
	            		//Either don't adjust decimal or move it backwards
	            		if(temp.charAt(off) == '0')
	            		{
	            			//Ok, move decimal backwards
	            			for(i = index + 1; i < tlen; i++)
	            			{
	            				if(temp.charAt(i) != '0')
	            				{
	            					exp = -(i - index);
	            					break;
	            				}
	            			}
	            		}
	            	}
	            	else
	            	{
	            		//Move decimal forwards
	            		for(i = off; i < index; i++)
	        			{
	            			if(temp.charAt(i) != '0')
	        				{
	        					exp = index - i - 1;
	        					break;
	        				}
	        			}
	            	}
	            	
	            	//Start writing out the value
	            	expBuf.setLength(0); //Reset the StringBuffer
	            	if(off == 1)
	            	{
	            		//If a formatter or sign was added then make sure the final value has it too.
	            		expBuf.append(temp.charAt(0));
	            	}
	            	if(index != -1)
	            	{
	            		StringUtilities.append(expBuf, temp, off, index - off); //Copy whole number
	            		StringUtilities.append(expBuf, temp, index + 1, tlen - (index + 1)); //Copy decimal
	            	}
	            	else
	            	{
	            		StringUtilities.append(expBuf, temp, 0, tlen); //Copy everything
	            	}
	            	//Trim zeros before finishing formatting
	            	//-First from the front
	            	boolean dec = false;
	            	for(i = off; i < tlen - 1; i++)
	        		{
	            		char c = temp.charAt(i);
	        			if(c != '0')
	        			{
	        				if(c != DECIMAL)
	        				{
	        					break;
	        				}
	        				else
	        				{
	        					//If a decimal point is found (since temp is has a decimal point) then make sure to ignore the extra count.
	        					dec = true;
	        				}
	        			}
	        		}
	            	if(i != off)
	            	{
	            		expBuf.delete(off, i - (dec ? 1 : 0));
	            	}
	            	//-Now from the back
	            	for(i = tlen - 1; i > off; i--)
	        		{
	        			if(temp.charAt(i) != '0')
	        			{
	        				break;
	        			}
	        		}
	        		if(((i - 1) == off) && (this.precision == -1))
	        		{
	        			//Only remove zeros if it is an integer and the precision isn't predefined
	        			if(alt)
	        			{
	        				//Adjust
	        				expBuf.delete(off + 2, expBuf.length()).insert(off + 1, DECIMAL); //Insert the decimal point
	        			}
	        			else
	        			{
	        				expBuf.delete(off + 1, expBuf.length());
	        			}
	        		}
	        		else
	        		{
	        			expBuf.insert(off + 1, DECIMAL); //Insert the decimal point
	        			//If not enough digits exist to match precision
	        			int l = expBuf.length() - 2;
	        			int p = this.precision == -1 ? DEFAULT_PRECISION : this.precision;
	        			if(l < p)
	        			{
		        			char[] chars = new char[p - l];
		        			Arrays.fill(chars, '0');
		        			expBuf.append(chars);
	        			}
	        		}
	            	
	            	//Now write out the exponent
	            	expBuf.append(caps ? 'E' : 'e').append(exp < 0 ? '-' : '+');
	            	exp = Math.abs(exp);
	            	if(exp < 10)
	            	{
	            		//Must be at least 2 digits for the exponent
	            		expBuf.append('0');
	            	}
	            	expBuf.append(exp);
	            	thousandsSep(caps, flags, expBuf);
                }
            }
            if (fixedBuf != null && !Double.isInfinite(value) && !Double.isNaN(value))
            {
            	thousandsSep(caps, flags, fixedBuf);
            	/* Leave decimal digits
            	String temp = fixedBuf.toString();
            	int index = temp.indexOf(decimalPoint);
            	
            	//Trim the decimal if possible
        		int len = temp.length();
        		int i;
        		for(i = index + 1; i < len; i++)
        		{
        			if(temp.charAt(i) != '0')
        			{
        				break;
        			}
        		}
        		if((index != -1) && ((i - 1) == (len - 1)) && (this.precision == -1))
        		{
        			//Only remove zeros if it is an integer and the precision isn't predefined
        			if(alt)
        			{
        				fixedBuf.delete(index + 2, len);
        			}
        			else
        			{
        				fixedBuf.delete(index, len);
        			}
        		}
        		*/
            }
            
            //Return the correct type
            /*
             * f: fixed-point, if no decimal portion [integer] exists then trailing zeros and no decimal point exists (alt: decimal point always used)
             * e: exponent form, always 2 digits in exponent, if no decimal portion [integer] exists then trailing zeros and no decimal point exists (alt: decimal point always used)
             * g: smaller of either f/e, trailing zeros removed, if integer then decimal point removed (alt: trailing zeros not removed, always has decimal point)
             */
            String result;
            if (type == 'g' || type == 'G')
            {
            	StringBuffer builder;
            	if (!Double.isNaN(value) && !Double.isInfinite(value))
            	{
            		String temp;
                	int index;
	            	if(alt)
	            	{
	            		//Alt form, g/G requires the decimal point
	            		
	            		//First fixed-point
	            		temp = fixedBuf.toString();
	            		index = temp.indexOf(DECIMAL);
	            		
	            		if(index == -1)
	            		{
	            			//Decimal point doesn't exist, add it
	            			fixedBuf.append(".0");
	            		}
	            		
	            		//Now process the exponent
	            		temp = expBuf.toString();
	            		index = temp.indexOf(DECIMAL);
	            		
	            		if(index == -1)
	            		{
	            			expBuf.insert(temp.indexOf(caps ? 'e' : 'E'), ".0");
	            		}
	            	}
	            	else
	            	{
	            		//If not alternative then trailing zeros should be removed (decimal included [in removal] if the number is an integer)
	            		
		            	//First process the fixed point
	            		temp = fixedBuf.toString();
	            		index = temp.indexOf(DECIMAL);
	            		if(index != -1)
	            		{
	            			//Remove excess zeros
		            		for(int i = fixedBuf.length() - 1; i >= 0; i--)
		            		{
		            			char c = temp.charAt(i);
		            			if(c != '0')
		            			{
		            				int start = i;
		            				if(c != DECIMAL)
		            				{
		            					start++;
		            				}
		            				fixedBuf.delete(start, fixedBuf.length());
		            				break;
		            			}
		            		}
	            		}
		            	
		            	//Now process the exponent
	            		temp = expBuf.toString();
	            		index = temp.indexOf(DECIMAL);
	            		if(index != -1)
	            		{
		            		for(int i = temp.indexOf('e') - 1; i >= 0; i--)
		            		{
		            			char c = temp.charAt(i);
		            			if(c != '0')
		            			{
		            				int start = i;
		            				if(c != DECIMAL)
		            				{
		            					start++;
		            				}
		            				expBuf.delete(start, temp.indexOf('e'));
		            				break;
		            			}
		            		}
	            		}
	            	}
	            	
	            	//Compare length. Shortest one is returned.
	                builder = expBuf.length() < fixedBuf.length() ? expBuf : fixedBuf; //Fixed point is the "false" value so if they are equal in length then fixed-point takes precedence over exponent
            	}
            	else
            	{
            		builder = fixedBuf; //Simply take the fixed-point buffer for infinite/NaN results
            	}
                result = builder.toString();
            }
            else
            {
                result = fixedBuf != null ? fixedBuf.toString() : expBuf.toString();
            }
            if (caps)
            {
                result = result.toUpperCase();
            }
            return result;
        }
        
        private static void thousandsSep(boolean caps, String flags, StringBuffer value)
        {
        	if(flags != null && flags.indexOf(THOUS_SEP) >= 0)
        	{
	        	int start = Character.isDigit(value.charAt(0)) ? 0 : 1;
	        	int len = value.toString().indexOf(DECIMAL);
	        	if(len == -1)
	        	{
	        		len = value.toString().indexOf(caps ? 'e' : 'E'); //Decimal might not exist but exponent does
	        		if(len == -1)
	        		{
	        			len = value.length(); //use the whole length
	        		}
	        	}
	        	len -= start;
	        	if(len > 3)
	        	{
	        		int sep = len / 3;
	        		int front = len % 3;
	        		if(front == 0)
	        		{
	        			sep--;
	        			front = 3;
	        		}
	        		for(int i = front + start; sep > 0; sep--, i += 4)
	        		{
	        			value.insert(i, ',');
	        		}
	        	}
        	}
        }
        
        private static void writeFloatingPoint(StringBuffer buf, double v, int p)
        {
            if (Double.isNaN(v) || Double.isInfinite(v))
            {
                if (Double.isNaN(v))
                {
                    buf.append("nan");
                }
                else
                {
                	if (v == Double.NEGATIVE_INFINITY)
	                {
	                    buf.append('-');
	                }
	                buf.append("inf");
                }
                return;
            }
            long temp = Double.doubleToLongBits(v);
            int exp = ((int)((temp & 0x7FF0000000000000L) >>> 52)) - 1023;
            if((temp & 0x8000000000000000L) == 0x8000000000000000L)
            {
            	buf.append('-');
            }
            long mantissa = (temp & ((1L << 52) - 1)) | (exp == -1023 ? 0 : (1L << 52));
            exp -= 52;
            if (p < 0)
            {
                p = DEFAULT_PRECISION;
            }
            int tp = p; //Used for trimming later
            
            if (exp == 0 || mantissa == 0)
            {
                buf.append(mantissa);
                //To maintain decimal
                if(p > 0)
                {
                	buf.append(DECIMAL);
                	char[] chars = new char[p];
                    Arrays.fill(chars, '0');
                    buf.append(chars);
                }
            }
            else
            {
                if (Math.abs(exp) >= 60)
                {
                    if (exp > 0)
                    {
                        //Create the floating point value by multiplication (mantissa * 2^exp), using really big numbers
                        //-Precision is not needed since it only covers decimal point
                        new LargeNumber(mantissa).multiply(new LargeNumber(exp)).toString(buf, true);
                        buf.append(DECIMAL);
                        char[] chars = new char[p];
                        Arrays.fill(chars, '0');
                        buf.append(chars);
                        
                        /*
                        double value = mantissa;
                        for (int i = exp; i > 0; i--)
                        {
                            value *= 2;
                        }
                         */
                    }
                    else
                    {
                        //Create the floating point value by division (mantissa / 2^exp), using really big numbers
                        LargeNumber man = new LargeNumber(mantissa);
                        LargeNumber exponent = new LargeNumber(exp);
                        
                        LargeNumber rem;
                        LargeNumber[] mod = null;
                        //Whole number
                        if (man.greaterThenOrEqual(exponent))
                        {
                            if (mod == null)
                            {
                                mod = new LargeNumber[1];
                            }
                            man.divideAndMod(exponent, mod).toString(buf, true);
                            rem = mod[0].multiply(TEN);
                        }
                        else
                        {
                            buf.append('0');
                            rem = man.multiply(TEN);
                        }
                        //Decimal point
                        if (!rem.zero() && p >= 0)
                        {
                            buf.append(DECIMAL);
                            while (p-- >= 0)
                            {
                                if (rem.greaterThenOrEqual(exponent))
                                {
                                    if (mod == null)
                                    {
                                        mod = new LargeNumber[1];
                                    }
                                    rem.divideAndMod(exponent, mod).toString(buf, true);
                                    if(p >= 0)
                                    {
                                    	rem = mod[0].multiply(TEN);
                                    }
                                }
                                else
                                {
                                    buf.append('0');
                                    if(p >= 0 && !rem.zero())
                                    {
                                    	rem = rem.multiply(TEN);
                                    }
                                }
                            }
                        }
                        else
                        {
                        	buf.append(DECIMAL);
                        	char[] chars = new char[p];
                            Arrays.fill(chars, '0');
                            buf.append(chars);
                        }
                        
                        //Round the value to the specified precision
                        if(!rem.zero()) //Do these zero checks to avoid unnecessary memory allocation and execution
                        {
                        	if (rem.greaterThenOrEqual(exponent))
                        	{
	                        	int carry = (int)((rem.divide(exponent).longValue() + 1) / 10); //Rough rounding
	                        	String temps = buf.toString();
		                        for(int i = temps.length() - 1; i >= 0; i--)
		                        {
		                        	if(carry == 0)
		                        	{
		                        		break;
		                        	}
		                        	char c = temps.charAt(i);
		                        	if(c == DECIMAL)
		                        	{
		                        		continue;
		                        	}
		                        	int tempV = (c - '0') + carry;
		                        	carry = 0;
		                        	if(tempV > 9)
		                        	{
		                        		buf.setCharAt(i, (char)((tempV % 10) + '0'));
		                        		carry = tempV / 10;
		                        	}
		                        	else
		                        	{
		                        		buf.setCharAt(i, (char)(tempV + '0'));
		                        	}
		                        }
		                        if(carry != 0)
		                        {
		                        	buf.insert(0, carry);
		                        }
                        	}
                        }
                        
                        /*
                        double value = mantissa;
                        for (int i = exp; i < 0; i++)
                        {
                            value /= 2;
                        }
                         */
                    }
                }
                else
                {
                    //Built in
                	
                    //Ints only
                	long twoPower = 1L << Math.abs(exp);
                    if (exp > 0)
                    {
                        long result = mantissa * twoPower;
                        if (result < 0) //Possible highest exponent before overflow occurs: 10 (0x1FFFFFFFFFFFFF * 2^10 = 0x7FFFFFFFFFFFFC00, 0x3FF diff before overflow. 0x1FFFFFFFFFFFFF * 2^11 = 1844674407370954956, which is overflow for a signed long)
                        {
                            //Overflow
                            new LargeNumber(mantissa).multiply(new LargeNumber(exp)).toString(buf, true);
                        }
                        else
                        {
                            buf.append(result);
                        }
                        buf.append(DECIMAL);
                        char[] chars = new char[p];
                        Arrays.fill(chars, '0');
                        buf.append(chars);
                    }
                    else
                    {
                        //Create the floating point value by division (mantissa / 2^exp)
                        //-Make the whole number
                        buf.append(mantissa / twoPower);
                        long rem = (mantissa % twoPower) * 10;
                        //Now calculate the decimal portion if necessary
                        if (rem != 0 && p >= 0)
                        {
                            buf.append(DECIMAL);
                            while (p-- >= 0) //Precision only affects the decimal portion so calculate until the number precision digits is met
                            {
                                buf.append(rem / twoPower);
                                if(p >= 0 && rem != 0)
                                {
                                	rem = (rem % twoPower) * 10;
                                }
                            }
                        }
                        else
                        {
                        	buf.append(DECIMAL);
                        	char[] chars = new char[p];
                            Arrays.fill(chars, '0');
                            buf.append(chars);
                        }
                        
                        //Round the value to the specified precision
                        if(rem != 0) //Do these zero checks to avoid unnecessary memory allocation and execution
                        {
	                        int carry = (int)(((rem / twoPower) + 1) / 10); //Rough rounding
	                        if(carry != 0)
	                        {
		                        String temps = buf.toString();
		                        for(int i = temps.length() - 1; i >= 0; i--)
		                        {
		                        	if(carry == 0)
		                        	{
		                        		break;
		                        	}
		                        	char c = temps.charAt(i);
		                        	if(c == DECIMAL)
		                        	{
		                        		continue;
		                        	}
		                        	int tempV = (c - '0') + carry;
		                        	carry = 0;
		                        	if(tempV > 9)
		                        	{
		                        		buf.setCharAt(i, (char)((tempV % 10) + '0'));
		                        		carry = tempV / 10;
		                        	}
		                        	else
		                        	{
		                        		buf.setCharAt(i, (char)(tempV + '0'));
		                        	}
		                        }
		                        if(carry != 0)
		                        {
		                        	buf.insert(0, carry);
		                        }
	                        }
                        }
                    }
                }
            }
            
            //Trim precision if necessary (any of the division operations add an extra digit). Only handle greater lengths.
            int index = buf.toString().indexOf(DECIMAL);
            if(buf.length() - 1 - index > tp)
            {
            	//Not rounding
            	buf.delete(tp + index + 1, buf.length());
            }
        }
        
        public String getNullParamOutput()
        {
        	switch(this.type)
        	{
        		default:
	        	case 'f':
	        	case 'F':
	        	case 'g':
	        	case 'G':
	        		return "0";
	        	case 'e':
	        		return "0e+00";
	        	case 'E':
	        		return "0E+00";
        	}
        }

        public void unformat(String value, Object[] refO, int[] vals)
        {
        	int len = value.length();
            int org = vals[0];
            int w;
            boolean sign = false;
            char c;
            for (w = 0; w < len; w++)
            {
                c = value.charAt(org + w);
                if (!sign)
                {
                    if (Character.isDigit(c))
                    {
                        sign = true;
                        w--;
                    }
                    else if (c == '+' || c == '-')
                    {
                        sign = true;
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    if (c == DECIMAL && (w < len && Character.isDigit(value.charAt(org + w + 1))))
                    {
                        //Decimal and correct format
                        w++;
                        continue;
                    }
                    else if (Character.isDigit(c))
                    {
                        continue;
                    }
                    char type = this.type;
                    boolean caps = type <= 'Z'; //Lower case is after upper case
                    if (type == 'f' || type == 'F')
                    {
                        //Not a digit? End
                        break;
                    }
                    switch (type)
                    {
                        case 'g':
                        case 'G':
                        case 'e':
                        case 'E':
                            if (c == '+' || c == '-')
                            {
                                continue;
                            }
                            else if ((caps && c == 'E') || (!caps && c == 'e'))
                            {
                                continue;
                            }
                            break;
                    }
                    break;
                }
            }
            vals[0] += w;
            if (this.requiresInput == 1)
            {
                return;
            }
            float valf = 0;
            double vald = 0;
            try
            {
                if (this.length == 'l' || this.length == 'L')
                {
                    vald = Double.parseDouble(value.substring(org, org + w));
                }
                else
                {
                    valf = Float.parseFloat(value.substring(org, org + w));
                }
            }
            catch (NumberFormatException e)
            {
                return;
            }
            Object obj = refO[vals[1]];
            if (obj == null || !obj.getClass().isArray())
            {
            	//TODO: For "ref" types
            	if(obj == null)
            	{
            		return;
            	}
            }
            vals[1]++;
            boolean written = false;
            if (this.length == 'l' || this.length == 'L')
            {
                if (obj instanceof double[])
                {
                    written = true;
                    ((double[])obj)[0] = vald;
                }
            }
            else
            {
                if (obj instanceof float[])
                {
                    written = true;
                    ((float[])obj)[0] = valf;
                }
            }
            if (!written)
            {
                //Error
                vals[1]--;
                vals[0] -= w;
            }
        }
    }
	
	private static class PointerFormatElement extends GeneralFormatElement
    {
        public PointerFormatElement(String format)
        {
        	super(format);
        }
        
        public String inFormat(Object obj)
        {
        	argError("PointerFormat", Resources.getString(BBXResource.PRINTUTILITY_NULL_POINTER_ERR), obj.getClass());
        	return "0000:0000";
        }
        
        public String getNullParamOutput()
        {
        	return "0000:0000";
        }

        public void unformat(String value, Object[] refO, int[] vals)
        {
            throw new UnsupportedOperationException();
        }
    }
}
