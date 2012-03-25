//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2011-2012 Vincent Simonetti
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

import javax.microedition.lcdui.Image;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.XYRect;
import rebuild.graphics.ImageEncoder;
import rebuild.graphics.JPEGEncoder;
import rebuild.graphics.PNGEncoder;
import rebuild.graphics.TIFFEncoder;
import rebuild.graphics.WBMPEncoder;
import rebuild.util.io.File;

/**
 * A collection of various Graphics utilities.
 */
public final class GraphicsUtilities
{
	/**
     * Windows Bitmap (BMP) format.
     */
    public static final int BMP = 0;
    /**
     * GIF format.
     */
    public static final int GIF = 1;
    /**
     * JPEG format.
     */
    public static final int JPEG = 2;
    /**
     * PNG format.
     */
    public static final int PNG = 3;
    /**
     * TIFF format.
     */
    public static final int TIFF = 4;
    /**
     * Windows Bitmap (WBMP) format. A very basic version of {@link BMP}.
     */
    public static final int WBMP = 5;
    
    private static final int ARGBAlphaShift = 24;
    private static final int ARGBRedShift = 16;
    private static final int ARGBGreenShift = 8;
    private static final int ARGBBlueShift = 0;
    
    private static final int ARGBAlphaMask = 0xFF000000;
    private static final int ARGBRedMask = 0x00FF0000;
    private static final int ARGBGreenMask = 0x0000FF00;
    private static final int ARGBBlueMask = 0x000000FF;
    
    private static Object lock = new Object();
    private static long tempValue = 0L;
    
	private GraphicsUtilities()
	{
	}
	
	/**
     * Convert a {@link Bitmap} to an {@link EncodedImage}. Alpha channel (if present) is decoded by default.
     * @param map The {@link Bitmap} to convert.
     * @return The resulting {@link EncodedImage} (in the format of a {@link PNGEncodedImage}). Null if an error occurred.
     */
    public static EncodedImage bitmapToEncodedImage(Bitmap map)
    {
    	return bitmapToEncodedImage(map, map.hasAlpha(), PNG);
    }
    
    /**
     * Convert a {@link Bitmap} to an {@link EncodedImage}.
     * @param map The {@link Bitmap} to convert.
     * @param encodeAlpha True if alpha channel (if present) should be decoded/encoded.
     * @return The resulting {@link EncodedImage} (in the format of a {@link PNGEncodedImage}). Null if an error occurred.
     */
    public static EncodedImage bitmapToEncodedImage(Bitmap map, boolean encodeAlpha)
    {
    	return bitmapToEncodedImage(map, encodeAlpha, PNG);
    }
    
    /**
     * Convert a {@link Bitmap} to an {@link EncodedImage} in the specified format.
     * @param map The {@link Bitmap} to convert.
     * @param encodeAlpha True if alpha channel (if present) should be decoded/encoded. This will be ignored if the type does not support alpha.
     * @param type One of the following formats to convert the {@link Bitmap} to: {@link BMP}, {@link GIF}, {@link JPEG}, {@link PNG}, {@link TIFF}, or {@link WBMP}. If a invalid format is specified then {@link BMP} is used.
     * @return The resulting {@link EncodedImage}. Null if an error occurred.
     */
    public static EncodedImage bitmapToEncodedImage(Bitmap map, boolean encodeAlpha, int type)
    {
    	if((type < BMP) || (type > WBMP))
    	{
    		type = BMP;
    	}
    	if(map == null)
    	{
    		throw new NullPointerException("map");
    	}
    	EncodedImage im = null;
    	ImageEncoder enc = null;
    	switch(type)
    	{
    		case BMP:
    			//can have alpha
    			//TODO
    			break;
    		case GIF:
    			//can have alpha
    			//TODO
    			break;
    		case JPEG:
    			//No alpha
    			enc = new JPEGEncoder(map, 90);
    			break;
    		case PNG:
    		default:
    			//can have alpha
    			enc = new PNGEncoder(map, encodeAlpha);
		        break;
    		case TIFF:
    			//can have alpha
    			enc = new TIFFEncoder(map, encodeAlpha);
    			break;
    		case WBMP:
    			//No alpha
    			enc = new WBMPEncoder(map);
    			break;
    	}
    	try
    	{
    		byte[] imageBytes = enc.encode(encodeAlpha);
    		/*
    		try
    		{
    			javax.microedition.io.file.FileConnection fileCon = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open("file:///SDCard/BlackBerry/pictures/pic.tif", javax.microedition.io.Connector.READ_WRITE);
    			if(fileCon.exists())
    			{
    				fileCon.delete();
    			}
    			fileCon.create();
    			java.io.OutputStream out = fileCon.openOutputStream();
    			out.write(imageBytes);
    			out.close();
    			fileCon.close();
    		}
    		catch(Exception e)
    		{
    		}
    		*/
    		if(imageBytes != null && imageBytes.length > 0)
    		{
    			im = EncodedImage.createEncodedImage(imageBytes, 0, imageBytes.length, enc.getMime());
    		}
    	}
    	catch(Exception e)
    	{
    		im = null;
    	}
    	return im;
    }
    
    /**
     * Get a pixel from a bitmap in AARRGGBB format.
     * @param map The bitmap to get the pixel from.
     * @param x The x pixel to get.
     * @param y The y pixel to get.
     * @return The resulting pixel in AARRGGBB format, -1 will be returned if map is null.
     */
    public static int getPixel(Bitmap map, int x, int y)
    {
        if(map == null)
        {
            return -1;
        }
        int[] pix = new int[1];
        map.getARGB(pix, 0, map.getWidth(), x, y, 1, 1);
        return pix[0];
    }
    
    /**
     * Set a pixel in a bitmap in AARRGGBB format.
     * @param map The bitmap to set the pixel on.
     * @param color The pixel to set in AARRGGBB format.
     * @param The x pixel to set.
     * @param The y pixel to set.
     */
    public static void setPixel(Bitmap map, int color, int x, int y)
    {
        if(map == null)
        {
            return;
        }
        map.setARGB(new int[] { color }, 0, map.getWidth(), x, y, 1, 1);
    }
    
    /**
     * Creates a {@link Color} from a 32-bit ARGB value.
     * @param argb A value specifying the 32-bit ARGB value.
     * @return The {@link Color} that this method creates.
     */
    public static int colorFromArgb(int argb)
    {
    	return argb;
    }
    
    /**
     * Creates a {@link Color} from the specified {@link Color}, but with the new specified alpha value. Although this method allows a 32-bit value to be passed for the alpha value, the value is limited to 8 bits.
     * @param alpha The alpha value for the new {@link Color}. Valid values are 0 through 255.
     * @param baseColor The {@link Color} from which to create the new {@link Color}.
     * @return The {@link Color} that this method creates.
     */
    public static int colorFromArgb(int alpha, int baseColor)
    {
    	return (baseColor & ~ARGBAlphaMask) | ((alpha & 0xFF) << ARGBAlphaShift);
    }
    
    /**
     * Creates a {@link Color} from the specified 8-bit color values (red, green, and blue). The alpha value is implicitly 255 (fully opaque). Although this method allows a 32-bit value to be passed for each color component, the value of each component is limited to 8 bits.
     * @param red The red component value for the new {@link Color}. Valid values are 0 through 255.
     * @param green The green component value for the new {@link Color}. Valid values are 0 through 255.
     * @param blue The blue component value for the new {@link Color}. Valid values are 0 through 255.
     * @return The {@link Color} that this method creates.
     */
    public static int colorFromArgb(int red, int green, int blue)
    {
    	return colorFromArgb(255, red, green, blue);
    }
    
    /**
     * Creates a {@link Color} from the four ARGB component (alpha, red, green, and blue) values. Although this method allows a 32-bit value to be passed for each component, the value of each component is limited to 8 bits.
     * @param alpha The alpha component. Valid values are 0 through 255.
     * @param red The red component. Valid values are 0 through 255.
     * @param green The green component. Valid values are 0 through 255.
     * @param blue The blue component. Valid values are 0 through 255.
     * @return The {@link Color} that this method creates.
     */
    public static int colorFromArgb(int alpha, int red, int green, int blue)
    {
    	return (((((red & 0xFF) << ARGBRedShift) | ((green & 0xFF) << ARGBGreenShift)) | 
        		((blue & 0xFF) << ARGBBlueShift)) | ((alpha & 0xFF) << ARGBAlphaShift));
    }
    
    /**
     * Get the red channel from the specified color.
     * @param argb The ARGB color to get the red channel from.
     * @return The red channel from the specified color.
     */
    public static int colorGetRed(int argb)
    {
    	return (argb & ARGBRedMask) >> ARGBRedShift;
    }
    
    /**
     * Get the green channel from the specified color.
     * @param argb The ARGB color to get the green channel from.
     * @return The green channel from the specified color.
     */
    public static int colorGetGreen(int argb)
    {
    	return (argb & ARGBGreenMask) >> ARGBGreenShift;
    }
    
    /**
     * Get the blue channel from the specified color.
     * @param argb The ARGB color to get the blue channel from.
     * @return The blue channel from the specified color.
     */
    public static int colorGetBlue(int argb)
    {
    	return (argb & ARGBBlueMask) >> ARGBBlueShift;
    }
    
    /**
     * Get the alpha channel from the specified color.
     * @param argb The ARGB color to get the alpha channel from.
     * @return The alpha channel from the specified color.
     */
    public static int colorGetAlpha(int argb)
    {
    	return (argb & ARGBAlphaMask) >> ARGBAlphaShift;
    }
    
    /**
     * Set the red channel in the specified color.
     * @param argb The color to set the red channel for.
     * @param value The value to set the red channel. Range is 0 - 255.
     * @return The resulting color.
     */
    public static int colorSetRed(int argb, int value)
    {
    	return (argb & ~ARGBRedMask) | ((value & 0xFF) << ARGBRedShift);
    }
    
    /**
     * Set the green channel in the specified color.
     * @param argb The color to set the green channel for.
     * @param value The value to set the green channel. Range is 0 - 255.
     * @return The resulting color.
     */
    public static int colorSetGreen(int argb, int value)
    {
    	return (argb & ~ARGBGreenMask) | ((value & 0xFF) << ARGBGreenShift);
    }
    
    /**
     * Set the blue channel in the specified color.
     * @param argb The color to set the blue channel for.
     * @param value The value to set the blue channel. Range is 0 - 255.
     * @return The resulting color.
     */
    public static int colorSetBlue(int argb, int value)
    {
    	return (argb & ~ARGBBlueMask) | ((value & 0xFF) << ARGBBlueShift);
    }
    
    /**
     * Set the alpha channel in the specified color.
     * @param argb The color to set the alpha channel for.
     * @param value The value to set the alpha channel. Range is 0 - 255.
     * @return The resulting color.
     */
    public static int colorSetAlpha(int argb, int value)
    {
    	return (argb & ~ARGBAlphaMask) | ((value & 0xFF) << ARGBAlphaShift);
    }
    
    /**
     * Save a {@link Image} to a file in a specified format.
     * @param file The path to save the {@link Image} to.
     * @param map The {@link Image} to write.
     * @param format One of the following formats to write the {@link Image} as: {@link BMP}, {@link GIF}, {@link JPEG}, {@link PNG}, {@link TIFF}, or {@link WBMP}. If a invalid format is specified then {@link BMP} is used.
     * @return true if the {@link Image} was saved, false if otherwise.
     */
    public static boolean saveImage(String file, Image map, int format)
    {
    	boolean saved = false;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ_WRITE);
    		if(!fil.exists())
    		{
    			if(!File.EnsureCreation(fil.getURL()))
    			{
    				fil.close();
    				return saved;
    			}
    		}
    		java.io.OutputStream out = fil.openOutputStream();
    		try
    		{
    			saveImage(out, map, format);
    			saved = true;
    		}
    		catch(Exception e)
        	{
    			saved = false;
        	}
    		finally
    		{
    			out.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    		saved = false;
    	}
    	return saved;
    }
    
    /**
     * Save a {@link Image} to a {@link java.io.OutputStream} in a specified format.
     * @param out The {@link java.io.OutputStream} to write the {@link Bitmap} to.
     * @param map The {@link Image} to write.
     * @param format One of the following formats to write the {@link Image} as: {@link BMP}, {@link GIF}, {@link JPEG}, {@link PNG}, {@link TIFF}, or {@link WBMP}. If a invalid format is specified then {@link BMP} is used.
     */
    public static void saveImage(java.io.OutputStream out, Image map, int format)
    {
    	//Create a bitmap
    	Bitmap nMap = new Bitmap(map.getWidth(), map.getHeight());
    	XYRect rect = new XYRect(0, 0, map.getWidth(), map.getHeight());
    	int scanwidth = rect.width;
    	//Create a data buffer
    	int[] data = new int[(0 + ((rect.width - 1) - rect.x) + ((rect.height - 1) - rect.y) * rect.width) + 1];
    	//Transfer data from the image to the bitmap
    	map.getRGB(data, 0, scanwidth, rect.x, rect.y, rect.width, rect.height);
    	nMap.setARGB(data, 0, scanwidth, rect.x, rect.y, rect.width, rect.height);
    	//Use the saveBitmap function to save the image
    	saveBitmap(out, nMap, format);
    }
    
    /**
     * Save a {@link Bitmap} to a file in a specified format.
     * @param file The path to save the {@link Bitmap} to.
     * @param map The {@link Bitmap} to write.
     * @param format One of the following formats to write the {@link Bitmap} as: {@link BMP}, {@link GIF}, {@link JPEG}, {@link PNG}, {@link TIFF}, or {@link WBMP}. If a invalid format is specified then {@link BMP} is used.
     * @return true if the {@link Bitmap} was saved, false if otherwise.
     */
    public static boolean saveBitmap(String file, Bitmap map, int format)
    {
    	boolean saved = false;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ_WRITE);
    		if(!fil.exists())
    		{
    			if(!File.EnsureCreation(fil.getURL()))
    			{
    				fil.close();
    				return saved;
    			}
    		}
    		java.io.OutputStream out = fil.openOutputStream();
    		try
    		{
    			saveBitmap(out, map, format);
    			saved = true;
    		}
    		catch(Exception e)
        	{
    			saved = false;
        	}
    		finally
    		{
    			out.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    		saved = false;
    	}
    	return saved;
    }
    
    /**
     * Save a {@link Bitmap} to a {@link java.io.OutputStream} in a specified format.
     * @param out The {@link java.io.OutputStream} to write the {@link Bitmap} to.
     * @param map The {@link Bitmap} to write.
     * @param format One of the following formats to write the {@link Bitmap} as: {@link BMP}, {@link GIF}, {@link JPEG}, {@link PNG}, {@link TIFF}, or {@link WBMP}. If a invalid format is specified then {@link BMP} is used.
     */
    public static void saveBitmap(java.io.OutputStream out, Bitmap map, int format)
    {
    	if((format < BMP) || (format > WBMP))
    	{
    		format = BMP;
    	}
    	if(map == null)
    	{
    		throw new NullPointerException("map");
    	}
    	EncodedImage e = bitmapToEncodedImage(map, true, format);
    	saveEncodedImage(out, e);
    }
    
    /**
     * Save a {@link EncodedImage} to a file.
     * @param file The path to save the {@link EncodedImage} to.
     * @param map The {@link EncodedImage} to write.
     * @return true if the {@link EncodedImage} was saved, false if otherwise.
     */
    public static boolean saveEncodedImage(String file, EncodedImage map)
    {
    	boolean saved = false;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ_WRITE);
    		if(!fil.exists())
    		{
    			if(!File.EnsureCreation(fil.getURL()))
    			{
    				fil.close();
    				return saved;
    			}
    		}
    		java.io.OutputStream out = fil.openOutputStream();
    		try
    		{
    			saveEncodedImage(out, map);
    			saved = true;
    		}
    		catch(Exception e)
        	{
    			saved = false;
        	}
    		finally
    		{
    			out.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    		saved = false;
    	}
    	return saved;
    }
    
    /**
     * Save a {@link EncodedImage} to a {@link java.io.OutputStream}.
     * @param out The {@link java.io.OutputStream} to write the {@link EncodedImage} to.
     * @param map The {@link EncodedImage} to write.
     */
    public static void saveEncodedImage(java.io.OutputStream out, EncodedImage map)
    {
    	if(map == null)
    	{
    		throw new NullPointerException("map");
    	}
    	java.io.DataOutputStream dat = new java.io.DataOutputStream(out);
    	byte[] b = map.getData();
    	try
    	{
    		dat.write(b, 0, b.length);
    		dat.flush();
    	}
    	catch(Exception e)
    	{
    	}
    }
    
    /**
     * Load the specified {@link Image} object.
     * @param file The path to the {@link Image}.
     * @return The loaded {@link Image}.
     */
    public static Image loadImage(String file)
    {
    	Image map = null;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ);
    		java.io.InputStream in = fil.openInputStream();
    		try
    		{
    			synchronized(lock)
    			{
    				tempValue = fil.fileSize();
    				map = loadImage(in);
    			}
    		}
    		catch(Exception e)
        	{
        	}
    		finally
    		{
    			in.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    	}
    	return map;
    }
    
    /**
     * Load the specified {@link Image} object.
     * @param file The {@link java.io.InputStream} to load the {@link Image} from.
     * @return The loaded {@link Image}.
     */
    public static Image loadImage(java.io.InputStream in)
    {
    	try
    	{
    		return Image.createImage(in);
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
    
    /**
     * Load the specified {@link Bitmap} object.
     * @param file The path to the {@link Bitmap}.
     * @return The loaded {@link Bitmap}.
     */
    public static Bitmap loadBitmap(String file)
    {
    	Bitmap map = null;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ);
    		java.io.InputStream in = fil.openInputStream();
    		try
    		{
    			synchronized(lock)
    			{
    				tempValue = fil.fileSize();
    				map = loadBitmap(in);
    			}
    		}
    		catch(Exception e)
        	{
        	}
    		finally
    		{
    			in.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    	}
    	return map;
    }
    
    /**
     * Load the specified {@link Bitmap} object.
     * @param file The {@link java.io.InputStream} to load the {@link Bitmap} from.
     * @return The loaded {@link Bitmap}.
     */
    public static Bitmap loadBitmap(java.io.InputStream in)
    {
    	return loadEncodedImage(in).getBitmap();
    }
    
    /**
     * Load the specified {@link EncodedImage} object.
     * @param file The path to the {@link EncodedImage}.
     * @return The loaded {@link EncodedImage}.
     */
    public static EncodedImage loadEncodedImage(String file)
    {
    	EncodedImage map = null;
    	javax.microedition.io.file.FileConnection fil;
    	try
    	{
    		fil = (javax.microedition.io.file.FileConnection)javax.microedition.io.Connector.open(file, javax.microedition.io.Connector.READ);
    		java.io.InputStream in = fil.openInputStream();
    		try
    		{
    			synchronized(lock)
    			{
    				tempValue = fil.fileSize();
    				map = loadEncodedImage(in);
    			}
    		}
    		catch(Exception e)
        	{
        	}
    		finally
    		{
    			in.close();
    		}
    		fil.close();
    	}
    	catch(Exception e)
    	{
    	}
    	return map;
    }
    
    /**
     * Load the specified {@link loadEncodedImage} object.
     * @param file The {@link java.io.InputStream} to load the {@link loadEncodedImage} from.
     * @return The loaded {@link loadEncodedImage}.
     */
    public static EncodedImage loadEncodedImage(java.io.InputStream in)
    {
    	//for now read the whole stream and load the image
    	//later versions will read the image and find out how large the image is so it can be encoded into the scene
    	int count = (int)tempValue;
    	byte[] data = new byte[count];
    	int size = -1;
    	try
    	{
    		size = in.read(data, 0, count);
    	}
    	catch(Exception e)
    	{
    	}
    	if(size < 1)
    	{
    		return null;
    	}
    	return EncodedImage.createEncodedImage(data, 0, size);
    }
}
