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
// The "format" function is from BBing (Bing for BlackBerry) and is licensed under MSPL.
//---------------------------------------------------------------------------------
//
// Created 2008
package rebuild.util.text;

import rebuild.BBXResource;
import rebuild.Resources;

/**
 * Various String utilities.
 * @since BBX 1.0.1
 */
public final class StringUtilities
{
	/**
     * The return value includes array elements that contain an empty string.
     */
    public static final short STRINGSPLITOPTIONS_NONE = 0;
    /**
     * The return value does not include array elements that contain an empty string.
     */
    public static final short STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES = 1;
	/**
     * Does not insert line breaks after every 76 characters in the string representation.
     */
    public static final int BASE64FORMATTINGOPTIONS_NONE = 0;
    /**
     * Inserts line breaks after every 76 characters in the string representation.
     */
    public static final int BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS = 1;
	
	private StringUtilities()
	{
	}
	
	/**
     * Indicates whether the specified String object is null or an Empty string.
     * @param value A String reference.
     * @return true if the value parameter is null or an empty string (""); otherwise, false.
     */
    public static boolean isNullOrEmpty(String value)
    {
        if (value != null)
        {
            return (value.length() == 0);
        }
        return true;
    }
    
    /**
     * Indicates whether the specified String object is null, an Empty string, or whitespace.
     * @param value A String reference.
     * @return true if the value parameter is null, an empty string (""), or whitespace; otherwise, false.
     * @since BBX 1.1.0
     */
    public static boolean isNullEmptyOrWhitespace(String value)
    {
        if (value != null)
        {
        	return value.length() == 0 || value.trim().length() == 0;
        }
        return true;
    }
    
    /**
     * Returns a value indicating whether the specified String object occurs within this string.
     * @param stringToCheck The String that should be checked.
     * @param value The String object to seek.
     * @return true if the value parameter occurs within this string, or if value is the empty string (""); otherwise, false.
     * @throws NullPointerException If value or stringToCheck is null.
     */
    public static boolean contains(String stringToCheck, String value)
    {
        if(stringToCheck == null)
        {
            throw new NullPointerException(Resources.getString(BBXResource.UTIL_CONTAINS_CHECKSTRINGNULL));
        }
        if(value == null)
        {
            throw new NullPointerException(Resources.getString(BBXResource.UTIL_CONTAINS_VALUESTRINGNULL));
        }
        if(value.equals(""))
        {
            return true;
        }
        return stringToCheck.indexOf(value) >= 0;
    }
    
    /**
     * Copies the characters in a specified substring in this instance to a Unicode character array.
     * @param str The String to get the chars from.
     * @param startIndex The starting position of a substring in this instance.
     * @param length The length of the substring in this instance.
     * @return A Unicode character array whose elements are the length number of characters in this instance starting from character position startIndex.
     * @throws StringIndexOutOfBoundsException startIndex or length is less than zero.-or- startIndex plus length is greater than the length of this instance.
     */
    public static char[] toCharArray(String str, int startIndex, int length)
    {
        int sL = str.length();
        if (((startIndex < 0) || (startIndex > sL)) || (startIndex > (sL - length)))
        {
            throw new StringIndexOutOfBoundsException(Resources.getString(BBXResource.ARGUMENT_INDEXOUTOFRANGE) + "startIndex");
        }
        if (length < 0)
        {
            throw new StringIndexOutOfBoundsException(Resources.getString(BBXResource.ARGUMENT_INDEXOUTOFRANGE) + "length");
        }
        char[] out = new char[length];
        char[] c = str.toCharArray();
        System.arraycopy(c, startIndex, out, 0, length);
        return out;
    }
    
    /**
     * Compare two strings up to a certain length.
     * @param str1 String to compare.
     * @param str2 String to compare.
     * @param length How many chars to compare up to.
     * @return The value 0 if the argument string is equal to this string; a value less than 0 if this string is lexicographically less than the string argument; and a value greater than 0 if this string is lexicographically greater than the string argument.
     * @throws NullPointerException str1 or str2 is null.
     * @throws StringIndexOutOfBoundsException length is less then 0 (zero).
     */
    public static int compareToLength(String str1, String str2, int length)
    {
    	if(str1 == null)
    	{
    		throw new NullPointerException("str1");
    	}
    	if(str2 == null)
    	{
    		throw new NullPointerException("str2");
    	}
    	if(length < 0)
    	{
    		throw new StringIndexOutOfBoundsException("length < 0");
    	}
    	int s1 = str1.length();
    	int s2 = str2.length();
    	String str1s = str1.substring(0, Math.min(s1 == length ? length : s1 - 1, length));
    	String str2s = str2.substring(0, Math.min(s2 == length ? length : s2 - 1, length));
    	return str1s.compareTo(str2s);
    }
    
    /**
     * Retrieves a substring from this instance. The substring starts at a specified character position and has a specified length.
     * @param str The String to split.
     * @param startIndex The index of the start of the substring.
     * @param length The number of characters in the substring.
     * @return A String equivalent to the substring of length length that begins at startIndex in this instance, or "" if startIndex is equal to the length of this instance and length is zero.
     * @throws StringIndexOutOfBoundsException startIndex plus length indicates a position not within this instance.-or- startIndex or length is less than zero.
     */
    public static String substring(String str, int startIndex, int length)
    {
        return str.substring(startIndex, startIndex + length);
    }
    
    /**
     * Replaces all occurrences of a specified Unicode character in this instance with another specified Unicode character. This is exactly the same as <code>str.replace(oldChar, newChar)</code>.
     * @param str The {@link String} to replace chars from.
     * @param oldChar A Unicode character to be replaced.
     * @param newChar A Unicode character to replace all occurrences of oldChar.
     * @return A {@link String} equivalent to this instance but with all instances of oldChar replaced with newChar.
     */
    public static String replace(String str, char oldChar, char newChar)
    {
    	return str.replace(oldChar, newChar);
    }
    
    /**
     * Replaces all occurrences of a specified {@link String} in a {@link String}, with another specified {@link String}.
     * @param str The {@link String} to replace something from.
     * @param oldValue A {@link String} to be replaced.
     * @param newValue A {@link String} to replace all occurrences of oldValue.
     * @return A {@link String} equivalent to str but with all instances of oldValue replaced with newValue.
     * @throws NullPointerException oldValue or str is null.
     * @throws IllegalArgumentException oldValue is the empty string ("").
     */
    public static String replace(String str, String oldValue, String newValue)
    {
    	if(str == null)
    	{
    		throw new NullPointerException("str");
    	}
    	if(oldValue == null)
    	{
    		throw new NullPointerException("oldValue");
    	}
    	if(oldValue.length() == 0)
    	{
    		throw new IllegalArgumentException(Resources.getString(BBXResource.EMPTY_STRING_EXCEPTION) + "oldValue");
    	}
    	if(oldValue.equals(newValue))
    	{
    		return str;
    	}
    	String temp = str;
    	StringBuffer buf = new StringBuffer(temp);
    	
    	int index = 0;
        int lastIndex = 0;
        int oldLength = oldValue.length();
        int newLength = newValue.length();
        while ((index = temp.indexOf(oldValue, lastIndex)) != -1)
        {
            lastIndex = index + newLength;
            buf.delete(index, index + oldLength);
            buf.insert(index, newValue);
            temp = buf.toString();
        }
    	
    	return temp;
    }
    
    /**
     * Returns a String array containing the substrings in this instance that are delimited by a separator.
     * @param str The String to split.
     * @param separator A Unicode character that delimit the substrings in this instance.
     * @return An array whose elements contain the substrings in this instance that are delimited by the separator.
     */
    public static String[] split(String str, char separator)
    {
        return split(str, new char[] { separator }, Integer.MAX_VALUE, STRINGSPLITOPTIONS_NONE);
    }
    
    /**
     * Returns a String array containing the substrings in this instance that are delimited by elements of a specified char array.
     * @param str The String to split.
     * @param separator An array of Unicode characters that delimit the substrings in this instance, an empty array containing no delimiters, or null.
     * @return An array whose elements contain the substrings in this instance that are delimited by one or more characters in separator.
     */
    public static String[] split(String str, char[] separator)
    {
        return split(str, separator, Integer.MAX_VALUE, STRINGSPLITOPTIONS_NONE);
    }
    
    /**
     * Returns a String array containing the substrings in this instance that are delimited by elements of a specified char array. A parameter specifies the maximum number of substrings to return.
     * @param str The String to split.
     * @param separator An array of Unicode characters that delimit the substrings in this instance, an empty array containing no delimiters, or null.
     * @param count The maximum number of substrings to return.
     * @return An array whose elements contain the substrings in this instance that are delimited by one or more characters in separator.
     * @throws StringIndexOutOfBoundsException count is negative.
     */
    public static String[] split(String str, char[] separator, int count)
    {
        return split(str, separator, count, STRINGSPLITOPTIONS_NONE);
    }
    
    /**
     * Returns a String array containing the substrings in this string that are delimited by elements of a specified char array. A parameter specifies whether to return empty array elements.
     * @param str The String to split.
     * @param separator An array of Unicode characters that delimit the substrings in this string, an empty array containing no delimiters, or null.
     * @param options Specify STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES to omit empty array elements from the array returned, or STRINGSPLITOPTIONS_NONE to include empty array elements in the array returned.
     * @return An array whose elements contain the substrings in this string that are delimited by one or more characters in separator.
     * @throws IllegalArgumentException options is not one of the STRINGSPLITOPTIONS_* values.
     */
    public static String[] split(String str, char[] separator, short options)
    {
        return split(str, separator, Integer.MAX_VALUE, options);
    }
    
    /**
     * Returns a String array containing the substrings in this string that are delimited by elements of a specified String array. A parameter specifies whether to return empty array elements.
     * @param str The String to split.
     * @param separator An array of strings that delimit the substrings in this string, an empty array containing no delimiters, or null.
     * @param options Specify STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES to omit empty array elements from the array returned, or STRINGSPLITOPTIONS_NONE to include empty array elements in the array returned.
     * @return An array whose elements contain the substrings in this string that are delimited by one or more strings in separator.
     * @throws IllegalArgumentException options is not one of the STRINGSPLITOPTIONS_* values.
     */
    public static String[] split(String str, String[] separator, short options)
    {
        return split(str, separator, Integer.MAX_VALUE, options);
    }
    
    /**
     * Returns a String array containing the substrings in this string that are delimited by elements of a specified char array. Parameters specify the maximum number of substrings to return and whether to return empty array elements.
     * @param str The String to split.
     * @param separator An array of Unicode characters that delimit the substrings in this string, an empty array containing no delimiters, or null.
     * @param count The maximum number of substrings to return.
     * @param options Specify STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES to omit empty array elements from the array returned, or STRINGSPLITOPTIONS_NONE to include empty array elements in the array returned.
     * @return An array whose elements contain the substrings in this stringthat are delimited by one or more characters in separator.
     * @throws StringIndexOutOfBoundsException count is negative.
     * @throws IllegalArgumentException options is not one of the STRINGSPLITOPTIONS_* values.
     */
    public static String[] split(String str, char[] separator, int count, short options)
    {
    	//Check the arguments
    	if(splitPreChecks(str, count, options))
        {
        	return new String[0];
        }
    	//Split the string (logically)
    	int[] sep = new int[str.length()];
        int splitCount = determineSubstrings(str, separator, sep, count);
        if(splitCount == 0 || count == 1) //If no split occurred or we only want one element
        {
        	return new String[]{str};
        }
        //Split the string (physically)
        return split(str, sep, null, Math.min(splitCount + 1, count), options == STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES);
    }
    
    /**
     * Returns a String array containing the substrings in this string that are delimited by elements of a specified String array. Parameters specify the maximum number of substrings to return and whether to return empty array elements.
     * @param str The String to split.
     * @param separator An array of strings that delimit the substrings in this string, an empty array containing no delimiters, or null.
     * @param count The maximum number of substrings to return.
     * @param options Specify STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES to omit empty array elements from the array returned, or STRINGSPLITOPTIONS_NONE to include empty array elements in the array returned.
     * @return An array whose elements contain the substrings in this string that are delimited by one or more strings in separator.
     * @throws StringIndexOutOfBoundsException count is negative.
     * @throws IllegalArgumentException options is not one of the STRINGSPLITOPTIONS_* values.
     */
    public static String[] split(String str, String[] separator, int count, short options)
    {
    	//If separator is null or empty, we can skip everything else and just let the char separator handle splitting the string
    	if(separator == null || separator.length == 0)
    	{
    		return split(str, (char[])null, count, options);
    	}
    	
    	//Check the arguments
    	if(splitPreChecks(str, count, options))
        {
        	return new String[0];
        }
    	//Split the string (logically)
    	int[] sep = new int[str.length()];
        int[] len = new int[sep.length];
        int splitCount = determineSubstrings(str, separator, sep, len, count);
        if(splitCount == 0 || count == 1) //If no split occurred or we only want one element
        {
        	return new String[]{str};
        }
        //Split the string (physically)
        return split(str, sep, len, Math.min(splitCount + 1, count), options == STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES);
    }
    
    private static boolean splitPreChecks(String str, int count, short options)
    {
    	if(str == null)
    	{
    		throw new NullPointerException("str");
    	}
    	if(count < 0)
        {
            throw new StringIndexOutOfBoundsException(Resources.getString(BBXResource.INDEXOUTOFRANGE_NEGATIVECOUNT));
        }
        if ((options < STRINGSPLITOPTIONS_NONE) || (options > STRINGSPLITOPTIONS_REMOVEEMPTYENTRIES))
        {
            throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_ENUMILLEGALVAL) + Integer.toString(options));
        }
        return count == 0; //Return an empty string
    }
    
    private static int determineSubstrings(String str, char[] sep, int[] index, int count)
    {
    	String[] sepStrings;
    	if(sep == null || sep.length == 0)
    	{
    		sepStrings = new String[]{" "};
    	}
    	else
    	{
    		int l;
    		sepStrings = new String[l = sep.length];
    		for(int i = 0; i < l; i++)
    		{
    			sepStrings[i] = String.valueOf(sep[i]);
    		}
    	}
    	return determineSubstrings(str, sepStrings, index, null, count);
    }
    
    private static int determineSubstrings(String str, String[] sep, int[] index, int[] len, int count)
    {
    	int seplen = sep.length;
    	int slen = str.length();
    	int ilen = Math.min(index.length, count);
    	
    	int splits = 0;
    	
    	for(int i = 0; i < slen && splits < ilen; i++)
    	{
    		for(int k = 0; k < seplen; k++)
    		{
    			String s = sep[k];
    			if(!isNullOrEmpty(s)) //Only want "actual" separators
    			{
    				int separatorLen = s.length();
    				if(str.charAt(i) == s.charAt(0) && //See if the first char matches (quick elimination)
    						separatorLen <= (slen - i) && //Check the the separator would even fit in the string
    						((separatorLen == 1) || (compareOrdinal(str, i, s, 0, separatorLen) == 0))) //Check if the String matches (or if it was just that single char we checked)
    				{
    					index[splits] = i;
    					if(len != null)
    					{
    						len[splits] = separatorLen;
    					}
    					splits++;
    					i += separatorLen - 1;
    				}
    			}
    		}
    	}
    	return splits;
    }
    
    private static String[] split(String str, int[] sep, int[] len, int count, boolean ignoreEmpties)
    {
    	String[] result = new String[count--];
    	int c = 0;
    	int start = 0;
    	
    	//Get the sections of the string
    	for(int i = 0; i < count; i++)
    	{
    		int end = sep[i];
    		if(!((end - start) == 0 && ignoreEmpties))
    		{
    			result[c++] = str.substring(start, end);
    		}
    		start += (end - start) + (len != null ? len[i] : 1);
    	}
    	
    	//Get the remaining string
    	if(!((str.length() - start) <= 0 && ignoreEmpties))
    	{
    		result[c++] = str.substring(start, str.length());
    	}
    	
    	//If the actual number of elements is less then the total number of elements, resize the array
    	if(ignoreEmpties && c <= count)
    	{
    		String[] r = new String[c];
    		System.arraycopy(result, 0, r, 0, c);
    		result = r;
    	}
    	return result;
    }
    
    /**
     * Compares substrings of two specified String objects by evaluating the numeric values of the corresponding char objects in each substring.
     * @param strA The first String.
     * @param indexA The starting index of the substring in strA.
     * @param strB The second String.
     * @param indexB The starting index of the substring in strB.
     * @param length The maximum number of characters in the substrings to compare.
     * @return A 32-bit signed integer indicating the lexical relationship between the two comparands. ValueCondition Less than zero: The substring in strA is less than the substring in strB. Zero: The substrings are equal, or length is zero. Greater than zero: The substring in strA is greater than the substring in strB.
     */
    public static int compareOrdinal(String strA, int indexA, String strB, int indexB, int length)
    {
        if ((strA != null) && (strB != null))
        {
            return nativeCompareOrdinalEx(strA, indexA, strB, indexB, length);
        }
        if (strA == strB)
        {
            return 0;
        }
        if (strA != null)
        {
            return 1;
        }
        return -1;
    }
    
    private static int nativeCompareOrdinalEx(String strA, int indexA, String strB, int indexB, int count)
    {
        String a = strA.substring(indexA);
        String b = strB.substring(indexB);
        if(a.length() >= count)
        {
            a = substring(a, 0, count);
        }
        if(b.length() >= count)
        {
            b = substring(b, 0, count);
        }
        return a.compareTo(b);
    }
	
	/**
	 * Format a string.
	 * @param format The string format to use.
	 * @param arg0 The argument to apply to the string format.
	 * @return The formatted string.
	 */
	public static String format_java(String format, Object arg0)
	{
		return format_java(format, new Object[]{arg0});
	}
	
	/**
	 * Format a string.
	 * @param format The string format to use.
	 * @param arg0 The argument to apply to the string format.
	 * @param arg1 The argument to apply to the string format.
	 * @return The formatted string.
	 */
	public static String format_java(String format, Object arg0, Object arg1)
	{
		return format_java(format, new Object[]{arg0, arg1});
	}
	
	/**
	 * Format a string.
	 * @param format The string format to use.
	 * @param arg0 The argument to apply to the string format.
	 * @param arg1 The argument to apply to the string format.
	 * @param arg2 The argument to apply to the string format.
	 * @return The formatted string.
	 */
	public static String format_java(String format, Object arg0, Object arg1, Object arg2)
	{
		return format_java(format, new Object[]{arg0, arg1, arg2});
	}
	
	//This is from one of my other libraries (Bing for BlackBerry)
	/**
	 * Format a string. Based off java.text.MessageFormat
	 * @param format The string format to use.
	 * @param args The arguments to apply to the string format.
	 * @return The formatted string.
	 */
	public static String format_java(String format, Object[] args)
	{
		//Until net.rim.device.api.i18n.MessageFormat supports the "number" property this has to be done manually.
		int len = format.length();
		StringBuffer buf = new StringBuffer();
		boolean onFormat = false;
		for(int i = 0; i < len; i++)
		{
			char c;
			if(onFormat)
			{
				StringBuffer index = new StringBuffer();
				boolean hasIndex = false;
				String elementFormat = null;
				String styleFormat = null;
				for(int k = i; k < len; k++, i++)
				{
					c = format.charAt(k);
					if(c == '}')
					{
						onFormat = false;
						break;
					}
					else if(Character.isDigit(c) && !hasIndex)
					{
						index.append(c);
					}
					else
					{
						if(c != '}')
						{
							String sub = format.substring(k + 1, format.indexOf('}', k));
							int l = sub.length();
							k += l;
							i += l;
							if(sub.indexOf(',') != -1)
							{
								elementFormat = sub.substring(0, sub.indexOf(','));
								styleFormat = sub.substring(sub.indexOf(',') + 1);
							}
							else
							{
								elementFormat = sub;
							}
						}
						hasIndex = true;
					}
				}
				buf.append(inFormat(args[Integer.parseInt(index.toString())], elementFormat, styleFormat));
			}
			else
			{
				c = format.charAt(i);
				if(c == '{')
				{
					onFormat = true;
				}
				else
				{
					buf.append(c);
				}
			}
		}
		return buf.toString();
	}
	
	private static String inFormat(Object obj, String element, String style)
	{
		if(element != null)
		{
			if(element.equals("number"))
			{
				//Numbers have to be supported manually
				return numFormat(obj, style);
			}
			else if(element.equals("time") || element.equals("date"))
			{
				//Time and date is supported by BlackBerry so use the built in system.
				String format = "{0," + element;
				if(style != null)
				{
					format += "," + style;
				}
				format += "}";
				return net.rim.device.api.i18n.MessageFormat.format(format, new Object[]{ obj });
			}
		}
		return obj.toString();
	}
	
	private static String numFormat(Object obj, String style)
	{
		javax.microedition.global.Formatter formatter = new javax.microedition.global.Formatter();
		//Numbers are the only supported element type right now
		if(obj instanceof Double || obj instanceof Float)
		{
			double dob = 0.0;
			if(obj instanceof Double)
			{
				dob = ((Double)obj).doubleValue();
			}
			else
			{
				dob = ((Float)obj).doubleValue();
			}
			if(style != null)
			{
				return numStyleFormat(formatter, dob, style);
			}
			return formatter.formatNumber(dob);
		}
		else if(obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof Byte)
		{
			long l = 0;
			if(obj instanceof Integer)
			{
				l = ((Integer)obj).longValue();
			}
			else if(obj instanceof Long)
			{
				l = ((Long)obj).longValue();
			}
			else if(obj instanceof Short)
			{
				l = ((Short)obj).shortValue();
			}
			else
			{
				l = ((Byte)obj).byteValue();
			}
			if(style != null)
			{
				return numStyleFormat(formatter, l, style);
			}
			return formatter.formatNumber(l);
		}
		else
		{
			return obj.toString();
		}
	}
	
	private static String numStyleFormat(javax.microedition.global.Formatter formatter, long l, String style)
	{
		if(style.equals("currency"))
		{
			return formatter.formatCurrency(l);
		}
		else if(style.equals("percent"))
		{
			return formatter.formatPercentage((float)l, 4);
		}
		else
		{
			return Long.toString(l);
		}
	}
	
	private static String numStyleFormat(javax.microedition.global.Formatter formatter, double dob, String style)
	{
		if(style.equals("currency"))
		{
			return formatter.formatCurrency(dob);
		}
		else if(style.equals("percent"))
		{
			return formatter.formatPercentage((float)dob, 4);
		}
		else if(style.equals("integer"))
		{
			return Long.toString((long)dob);
		}
		else
		{
			//Formatting such as ##.### is not supported because it would be a pain in the butt to implement, I already went above and beyond for actually writing a format function
			return Double.toString(dob);
		}
	}
	
	/**
     * Appends a specified number of copies of the string representation of a Unicode character to the end of the StringBuffer.
     * @param buf The StringBuffer to append the chars to.
     * @param value The character to append.
     * @param repeatCount The number of times to append value.
     * @return A reference to the StringBuffer after the append operation has completed.
     * @throws IndexOutOfBoundsException repeatCount is less than zero.
     */
    public static StringBuffer append(StringBuffer buf, char value, int repeatCount)
    {
    	return append(buf, value, repeatCount, -1);
    }
    
    /**
     * Appends a specified number of copies of the string representation of a Unicode character to the end of the StringBuffer.
     * @param buf The StringBuffer to append the chars to.
     * @param value The character to append.
     * @param repeatCount The number of times to append value.
     * @param index The location to append the chars. If the chars should be added to the end of the StringBuffer then -1 should be passed, if the value is greater then the current length of the StringBuffer then it will be added to the end. This cannot be less then -1.
     * @return A reference to the StringBuffer after the append operation has completed.
     * @throws IndexOutOfBoundsException repeatCount is less than zero. -or- index is less then -1.
     */
    public static StringBuffer append(StringBuffer buf, char value, int repeatCount, int index)
    {
        if(repeatCount != 0)
        {
            if (repeatCount < 0 || index < -1)
            {
                throw new IndexOutOfBoundsException(Resources.getString(BBXResource.ARGUMENT_INDEXOUTOFRANGE_NEGCOUNT) + "repeatCount");
            }
            if(index > buf.length())
            {
            	index = -1;
            }
            if(index < 0)
            {
	            for(int i = 0; i < repeatCount; i++)
	            {
	                buf.append(value);
	            }
            }
            else
            {
            	for(int i = 0; i < repeatCount; i++)
	            {
            		buf.insert(index++, value);
	            }
            }
        }
        return buf;
    }
    
    /**
     * Appends a section of string to the specified StringBuffer.
     * @param buf The StringBuffer to append the String to.
     * @param value The String to append.
     * @param startIndex The starting index of <code>value</code> to append.
     * @param index A value (see <i>indexIsLength</i>).
     * @param indexIsLength If <code>true</code> then <i>index</i> is the number of chars to append, if 
     * <code>false</code> then it is the ending index of the String to append.
     * @return A reference to the StringBuffer after the append operation has completed.
     */
    public static StringBuffer append(StringBuffer buf, String value, int startIndex, int index, boolean indexIsLength)
    {
    	if(!indexIsLength)
    	{
    		//Always use index as length
    		index -= startIndex;
    	}
    	/*
    	if(value == null)
    	{
    		if ((startIndex != 0) || (index != 0))
            {
                throw new NullPointerException("value");
            }
    		return buf;
    	}
    	if (index <= 0)
        {
            if (index != 0)
            {
                throw new IndexOutOfBoundsException(Resources.getString(BBXResource.ARGUMENT_INDEXOUTOFRANGE_POSITIVE) + "count");
            }
            return buf;
        }
    	if ((startIndex < 0) || (startIndex > (value.length() - index)))
        {
            throw new IndexOutOfBoundsException(Resources.getString(BBXResource.ARGUMENT_INDEXOUTOFRANGE) + "startIndex");
        }
        */
    	return buf.append(value.toCharArray(), startIndex, index);
    }
    
    /**
     * Removes the characters in a substring of a StringBuffer.
     * @param buf The StringBuffer to remove chars from.
     * @param startingIndex The beginning index, inclusive.
     * @param length The number of characters to remove.
     * @return The string buffer.
     * @throws StringIndexOutOfBoundsException If <code>startingIndex</code> is negative, greater than length(), or, when length is added, greater than end.
     */
    public static StringBuffer delete(StringBuffer buf, int startingIndex, int length)
    {
    	return buf.delete(startingIndex, length - startingIndex);
    }
    
    /**
     * Converts the value of an array of 8-bit signed integers to its equivalent {@link String} representation encoded with base 64 digits.
     * @param inArray An array of 8-bit signed integers.
     * @return The {@link String} representation, in base 64, of the contents of inArray.
     * @throws NullPointerException inArray is null.
     */
    public static String toBase64String(byte[] inArray)
    {
        return toBase64String(inArray, 0, inArray.length, BASE64FORMATTINGOPTIONS_NONE);
    }
    
    /**
     * Converts a subset of an array of 8-bit signed integers to its equivalent {@link String} representation encoded with base 64 digits. A parameter specifies whether to insert line breaks in the return value.
     * @param inArray An array of 8-bit signed integers.
     * @param options {@link BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS} to insert a line break every 76 characters, or {@link BASE64FORMATTINGOPTIONS_NONE} to not insert line breaks.
     * @return The {@link String} representation in base 64 of the elements in inArray.
     * @throws IllegalArgumentException options is not a valid Base64FormattingOptions_* value.
     * @throws NullPointerException inArray is null.
     */
    public static String toBase64String(byte[] inArray, int options)
    {
        return toBase64String(inArray, 0, inArray.length, options);
    }
    
    /**
     * Converts a subset of an array of 8-bit signed integers to its equivalent {@link String} representation encoded with base 64 digits. Parameters specify the subset as an offset in the input array, and the number of elements in the array to convert.
     * @param inArray An array of 8-bit signed integers.
     * @param offset An offset in inArray.
     * @param length The number of elements of inArray to convert.
     * @return The {@link String} representation in base 64 of length elements of inArray starting at position offset.
     * @throws ArrayIndexOutOfBoundsException offset or length is negative.-or- offset plus length is greater than the length of inArray.
     * @throws NullPointerException inArray is null.
     */
    public static String toBase64String(byte[] inArray, int offset, int length)
    {
        return toBase64String(inArray, offset, length, BASE64FORMATTINGOPTIONS_NONE);
    }
    
    /**
     * Converts a subset of an array of 8-bit signed integers to its equivalent {@link String} representation encoded with base 64 digits. Parameters specify the subset as an offset in the input array, the number of elements in the array to convert, and whether to insert line breaks in the return value.
     * @param inArray An array of 8-bit signed integers.
     * @param offset An offset in inArray.
     * @param length The number of elements of inArray to convert.
     * @param options {@link BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS} to insert a line break every 76 characters, or {@link BASE64FORMATTINGOPTIONS_NONE} to not insert line breaks.
     * @return The {@link String} representation in base 64 of length elements of inArray starting at position offset.
     * @throws ArrayIndexOutOfBoundsException offset or length is negative.-or- offset plus length is greater than the length of inArray.
     * @throws IllegalArgumentException options is not a valid Base64FormattingOptions_* value.
     * @throws NullPointerException inArray is null.
     */
    public static String toBase64String(byte[] inArray, int offset, int length, int options)
    {
    	if ((options < BASE64FORMATTINGOPTIONS_NONE) || (options > BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS))
        {
            throw new IllegalArgumentException(Resources.getString(BBXResource.ARGUMENT_ENUMILLEGALVAL) + Integer.toString(options));
        }
    	boolean breakLine = options == BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS;
    	try
    	{
    		return net.rim.device.api.io.Base64OutputStream.encodeAsString(inArray, offset, length, breakLine, breakLine);
    	}
    	catch(java.io.IOException e)
    	{
    		return "";
    	}
    }
    
    /**
     * Converts a subset of an 8-bit signed integer array to an equivalent subset of a Unicode character array encoded with base 64 digits. Parameters specify the subsets as offsets in the input and output arrays, the number of elements in the input array to convert, and whether line breaks are inserted in the output array.
     * @param inArray An input array of 8-bit unsigned integers.
     * @param offsetIn A position within inArray.
     * @param length The number of elements of inArray to convert.
     * @param outArray An output array of Unicode characters.
     * @param offsetOut A position within outArray.
     * @return A 32-bit signed integer containing the number of bytes in outArray.
     * @throws ArrayIndexOutOfBoundsException offsetIn, offsetOut, or length is negative.-or- offsetIn plus length is greater than the length of inArray.-or- offsetOut plus the number of elements to return is greater than the length of outArray.
     * @throws NullPointerException inArray or outArray is null.
     */
    public static int toBase64CharArray(byte[] inArray, int offsetIn, int length, char[] outArray, int offsetOut)
    {
    	return toBase64CharArray(inArray, offsetIn, length, outArray, offsetOut, BASE64FORMATTINGOPTIONS_NONE);
    }
    
    /**
     * Converts a subset of an 8-bit signed integer array to an equivalent subset of a Unicode character array encoded with base 64 digits. Parameters specify the subsets as offsets in the input and output arrays, the number of elements in the input array to convert, and whether line breaks are inserted in the output array.
     * @param inArray An input array of 8-bit unsigned integers.
     * @param offsetIn A position within inArray.
     * @param length The number of elements of inArray to convert.
     * @param outArray An output array of Unicode characters.
     * @param offsetOut A position within outArray.
     * @param options {@link BASE64FORMATTINGOPTIONS_INSERTLINEBREAKS} to insert a line break every 76 characters, or {@link BASE64FORMATTINGOPTIONS_NONE} to not insert line breaks.
     * @return A 32-bit signed integer containing the number of bytes in outArray.
     * @throws ArrayIndexOutOfBoundsException offsetIn, offsetOut, or length is negative.-or- offsetIn plus length is greater than the length of inArray.-or- offsetOut plus the number of elements to return is greater than the length of outArray.
     * @throws IllegalArgumentException options is not a valid Base64FormattingOptions_* value.
     * @throws NullPointerException inArray or outArray is null.
     */
    public static int toBase64CharArray(byte[] inArray, int offsetIn, int length, char[] outArray, int offsetOut, int options)
    {
    	if(outArray == null)
    	{
    		throw new NullPointerException("outArray");
    	}
    	if(offsetOut < 0)
    	{
    		throw new ArrayIndexOutOfBoundsException(Resources.getString(BBXResource.INDEXOUTOFRANGE_NEGATIVECOUNT) + "offsetOut");
    	}
    	char[] iArray = toBase64String(inArray, offsetIn, length, options).toCharArray();
    	System.arraycopy(iArray, 0, outArray, offsetOut, iArray.length);
    	return iArray.length;
    }
    
    /**
     * Converts a subset of a Unicode character array, which encodes binary data as base 64 digits, to an equivalent 8-bit unsigned integer array. Parameters specify the subset in the input array and the number of elements to convert.
     * @param inArray A Unicode character array.
     * @param offset A position within inArray.
     * @param length The number of elements in inArray to convert.
     * @return An array of 8-bit signed integers equivalent to length elements at position offset in inArray.
     * @throws java.io.IOException The length of s, ignoring white space characters, is not zero or a multiple of 4. -or-The format of s is invalid. s contains a non-base 64 character, more than two padding characters, or a non-white space character among the padding characters.
     * @throws ArrayIndexOutOfBoundsException offset or length is less than 0.-or- offset plus length indicates a position not within inArray.
     * @throws NullPointerException inArray is null.
     */
    public static byte[] fromBase64CharArray(char[] inArray, int offset, int length)
    {
    	return fromBase64String(new String(inArray, offset, length));
    }
    
    /**
     * Converts the specified {@link String}, which encodes binary data as base 64 digits, to an equivalent 8-bit signed integer array.
     * @param s A {@link String}.
     * @return An array of 8-bit signed integers equivalent to s.
     * @throws NullPointerException s is null.
     * @throws java.io.IOException The length of s, ignoring white space characters, is not zero or a multiple of 4. -or-The format of s is invalid. s contains a non-base 64 character, more than two padding characters, or a non-white space character among the padding characters.
     */
    public static byte[] fromBase64String(String s)
    {
    	try
    	{
    		return net.rim.device.api.io.Base64InputStream.decode(s);
    	}
    	catch(java.io.IOException e)
    	{
    		return null;
    	}
    }
    
    //Based off J2SE docs version 6
    /**
     * Get the character value for the specified numeric digit.
     * @param digit The numeric digit to retrieve.
     * @param radix The radix the resulting character should be.
     * @return The digit converted to a character of the specified radix. Or a null char (value of zero) will be returned if parameters are not correct.
     * @since BBX 1.2.0
     */
    public static char forDigit(int digit, int radix)
    {
    	if(radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
    	{
    		return '\0';
    	}
    	if(digit < 0 || digit > radix)
    	{
    		return '\0';
    	}
    	if(digit < 10)
    	{
    		return (char)('0' + digit);
    	}
    	return (char)('a' + digit - 10);
    }
}
