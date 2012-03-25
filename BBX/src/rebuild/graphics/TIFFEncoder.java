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
package rebuild.graphics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.XYRect;
import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.graphics.tiff.IFD;
import rebuild.graphics.tiff.Tag;
import rebuild.graphics.tiff.tags.BitsPerSampleTag;
import rebuild.graphics.tiff.tags.CompressionTag;
import rebuild.graphics.tiff.tags.ExtraSamplesTag;
import rebuild.graphics.tiff.tags.ImageLengthTag;
import rebuild.graphics.tiff.tags.ImageWidthTag;
import rebuild.graphics.tiff.tags.PhotometricInterpretationTag;
import rebuild.graphics.tiff.tags.PlanarConfigurationTag;
import rebuild.graphics.tiff.tags.ResolutionUnitTag;
import rebuild.graphics.tiff.tags.RowsPerStripTag;
import rebuild.graphics.tiff.tags.SamplesPerPixelTag;
import rebuild.graphics.tiff.tags.StripByteCountsTag;
import rebuild.graphics.tiff.tags.StripOffsetsTag;
import rebuild.graphics.tiff.tags.XResolutionTag;
import rebuild.graphics.tiff.tags.YResolutionTag;
import rebuild.util.GraphicsUtilities;

//TODO: When multiple image types (RGB, YCbCd, etc. are supported, remove the private tag on the constructors.

//Format: http://partners.adobe.com/public/developer/en/tiff/TIFF6.pdf
//Info: http://www.awaresystems.be/imaging/tiff.html

/**
 * A Tagged Image File Format (TIFF) encoder.
 * <p>Currently only the Baseline TIFF is supported for writing (with the exception of alpha). Any tags that
 * modify the image format are not supported even if the variables are there. Only one image, non-compressed,
 * RGB/A, 8bits per sample is supported.</p>
 */
public class TIFFEncoder extends ImageEncoder
{
	/*
	//Tag types
	private static final int BITS_PER_SAMPLE_TAG = 258;
	private static final int COMPRESSION_TAG = 259;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG = 262;
	private static final int STRIP_OFFSETS_TAG = 273;
	private static final int SAMPLES_PER_PIXEL_TAG = 277;
	private static final int ROWS_PER_STRIP_TAG = 278;
	private static final int STRIP_BYTE_COUNTS_TAG = 279;
	private static final int XRESOLUTION_TAG = 282;
	private static final int YRESOLUTION_TAG = 283;
	private static final int PLANAR_CONFIGURATION_TAG = 284;
	private static final int RESOLUTION_UNIT_TAG = 296;
	private static final int EXTRA_SAMPLES_TAG = 338;
	
	//Other constants
	private static final int EXTRA_SAMPLES_TAG_UNSPECIFIED = 0;
	private static final int EXTRA_SAMPLES_TAG_ASSOCIATED = 1;
	private static final int EXTRA_SAMPLES_TAG_UNASSOCIATED = 2;
	private static final int COMPRESSION_TAG_NO_COMPRESSION = 1;
	private static final int COMPRESSION_TAG_CCITT_1D = 2;
	private static final int COMPRESSION_TAG_Group_3_Fax = 3;
	private static final int COMPRESSION_TAG_Group_4_Fax = 4;
	private static final int COMPRESSION_TAG_LZW = 5;
	private static final int COMPRESSION_TAG_JPEG = 6;
	private static final int COMPRESSION_TAG_PACKBITS = 32773;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_WHITEISZERO = 0;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_BLACKISZERO = 1;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_RGB = 2;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_PALETTE_COLOR = 3;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_TRANSPARENCY_MASK = 4;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_CMYK = 5;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_YCbCr = 6;
	private static final int PHOTOMETRIC_INTERPRETATION_TAG_CIELab = 8;
	private static final int RESOLUTION_UNIT_TAG_NO_MEASUREMENT = 1;
	private static final int RESOLUTION_UNIT_TAG_INCH = 2;
	private static final int RESOLUTION_UNIT_TAG_NO_CENTIMETER = 3;
	*/
	
	private boolean littleEndian;
	private IFD ifd;
	//This is to determine what type of image to write.
	private short type;
	private boolean bigTiff;
	
	/**
	 * Get the MIME type of the image encoder.
	 * @return The MIME type of the image encoder.
	 */
	public String getMime()
	{
		return "image/tiff";
	}
	
	/**
     * Class constructor
     */
    public TIFFEncoder()
    {
    	this(null, false);
    }
    
    /**
     * Class constructor
     * @param type The TIFF type to encode.
     */
    private TIFFEncoder(int type)
    {
    	//TODO: Replace RGB with type when more types are supported
    	this(null, false, PhotometricInterpretationTag.RGB);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     */
    public TIFFEncoder(Bitmap image)
    {
    	this(image, false);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     * @param type The TIFF type to encode.
     */
    private TIFFEncoder(Bitmap image, int type)
    {
    	//TODO: Replace RGB with type when more types are supported
    	this(image, false, PhotometricInterpretationTag.RGB);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     * @param encodeAlpha boolean false = no alpha, true = encode alpha
     */
    public TIFFEncoder(Bitmap image, boolean encodeAlpha)
    {
    	this(image, encodeAlpha, PhotometricInterpretationTag.RGB);
    }
    
    /**
     * Class constructor specifying {@link Bitmap} to encode, with no alpha channel encoding.
     * @param image A Java Image object which uses the DirectColorModel.
     * @param encodeAlpha boolean false = no alpha, true = encode alpha
     * @param type The TIFF type to encode.
     */
    private TIFFEncoder(Bitmap image, boolean encodeAlpha, int type)
    {
    	super(image, encodeAlpha);
    	this.littleEndian = true;
    	this.ifd = new IFD();
    	this.type = (short)type;
    	this.bigTiff = false;
    	this.compressionLevel = CompressionTag.NO_COMPRESSION;
    	try
    	{
    		//Test to make sure that "type" is a valid photometric
    		new PhotometricInterpretationTag(this.type);
    	}
    	catch(Exception e)
    	{
    		throw new IllegalArgumentException(Resources.getString(BBXResource.TIFF_INVALID_PHOTOMETRIC));
    	}
    }
    
    /**
     * Get this TIFF encoder's {@link TIFFTags.IFD} so that new tags can be added or removed.
     * @return
     */
    public IFD getIFD()
    {
    	return ifd;
    }
    
    /**
     * Get if the TIFFEncoder should write a BigTIFF.
     * @return <code>true</code> if the encoder will write a BigTIFF, <code>false</code> if otherwise.
     */
    public final boolean getWritingBigTIFF()
    {
    	return bigTiff;
    }
    
    /**
     * Set if the TIFFEncoder should write a BigTIFF.
     * @param bTiff <code>true</code> if the encoder should write a BigTIFF, <code>false</code> if otherwise.
     */
    public final void setWritingBigTIFF(boolean bTiff)
    {
    	bigTiff = bTiff;
    }
    
    /**
     * Set the compression level to use.
     * @param The compression level to set.
     */
    public void setCompressionLevel(int level)
    {
    	if(level != CompressionTag.NO_COMPRESSION)
    	{
    		throw new IllegalArgumentException("level != CompressionTag.NO_COMPRESSION");
    	}
    	this.compressionLevel = level;
    }
    
    private long initializeIFD(boolean encodeAlpha) throws IOException
    {
    	//Make sure that all required tags are present and get information to aid in modifying certain tags for encoding.
    	ensureRequiredTags(encodeAlpha);
    	
    	//-Write tags and data to their buffers
    	ByteArrayOutputStream temp = new ByteArrayOutputStream();
    	rebuild.graphics.tiff.Writer dat = new rebuild.graphics.tiff.Writer(littleEndian, temp);
    	long dataOff = bigTiff ? ifd.getOptBigLength() : ifd.getOptLength(); //Get the length of the tags only
    	dataOff += bigTiff ? 16L : 8L; //Add the header length
    	dataOff += bigTiff ? 16L : 6L; //Add the tag count and IFD offset
    	
    	//Make sure that all tags have the proper values.
    	//-Get the offset to the end of the IFD (including extra data) so that the correct image offsets can be added.
    	long nLength = 0L;
    	if(bigTiff)
    	{
    		nLength = ifd.writeBig(dat, dataOff);
    	}
    	else
    	{
    		nLength = ifd.write(dat, (int)(dataOff & 0x00000000FFFFFFFFL));
    	}
    	
    	//-Setup Strip sizes with correct strip size
    	short samples = ((SamplesPerPixelTag)ifd.getTagByType(SamplesPerPixelTag.getTagTypeValue())).getSamples();
    	int totalByteCount = width * height * samples;
    	int maxStripCount = (((RowsPerStripTag)ifd.getTagByType(RowsPerStripTag.getTagTypeValue())).getRowsPerStrip() * width) * samples;
    	
    	StripByteCountsTag byteCountTag = (StripByteCountsTag)ifd.getTagByType(StripByteCountsTag.getTagTypeValue());
    	int stripCount = byteCountTag.getCount();
    	for(int i = 0; i < stripCount; i++)
    	{
    		//TODO-Figure out how to handle compression (don't know if compression resets for each strip)
    		byteCountTag.setByteCount(i, maxStripCount > totalByteCount ? totalByteCount : maxStripCount);
    		totalByteCount -= maxStripCount;
    	}
    	
    	//-Replace Strip offsets with correct offset
    	StripOffsetsTag offsetTag = (StripOffsetsTag)ifd.getTagByType(StripOffsetsTag.getTagTypeValue());
    	for(int i = 0; i < stripCount; i++)
    	{
    		offsetTag.setStripOffset(i, nLength);
    		nLength += byteCountTag.getByteCount(i);
    	}
    	
    	temp.close();
    	return dataOff;
    }
    
    /**
     * Creates an array of bytes that is the TIFF equivalent of the current image, specifying whether to encode alpha or not.
     * @param encodeAlpha boolean false = no alpha, true = encode alpha
     * @return an array of bytes, or null if there was a problem
     */
    protected byte[] inEncode(boolean encodeAlpha) throws IOException
    {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream();
    	ByteArrayOutputStream tagBuffer = new ByteArrayOutputStream();
    	ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
    	rebuild.graphics.tiff.Writer dat = null;
    	char tempChar = '\0';
    	
    	//Write the header-START
    	dat = new rebuild.graphics.tiff.Writer(littleEndian, headerBuffer);
    	if(littleEndian)
    	{
    		tempChar = (char)0x49; //I
    	}
    	else
    	{
    		tempChar = (char)0x4D; //M
    	}
    	//-Write if this is little or big endian.
    	dat.writeByte(tempChar);
    	dat.writeByte(tempChar);
    	//-Write the TIFF identifier
    	if(bigTiff)
    	{
    		dat.writeUShort(43);
    		//-Write offset size
    		dat.writeUShort(8); //For now it is 8 bytes (a long).
    		//-Write unused value (always 0)
    		dat.writeUShort(0);
    		//-Write the offset to the first tag (located directly after the header [endian byte + endian byte + TIFF identifier (2 bytes) + offsetSize (2 bytes) + unused (2 bytes) + offset (8 bytes)])
    		dat.writeULong(8);
    	}
    	else
    	{
    		dat.writeUShort(42);
    		//-Write the offset to the first tag (located directly after the header [endian byte + endian byte + TIFF identifier (2 bytes) + offset (4 bytes)])
        	dat.writeUInt(8);
    	}
    	//Write the header-END
    	
    	//Write the tags, anything that has a value greater then 4/8 bytes should be written to another buffer-START
    	long dataOff = initializeIFD(encodeAlpha);
    	
    	ifd.sort();
    	dat = new rebuild.graphics.tiff.Writer(littleEndian, tagBuffer);
    	//Write the tags and their data to a stream
    	if(bigTiff)
    	{
    		dataOff = ifd.writeBig(dat, dataOff);
    	}
    	else
    	{
    		dataOff = ifd.write(dat, (int)(dataOff & 0x00000000FFFFFFFFL));
    	}
    	
    	//TODO: Later in life if I want to support multiple IFD's I should take the dataOff - start of the IFD and 
    	//put it as offset, that way when the TIFF decoder looks at it, it will see another IFD is located after the 
    	//first one, then second, etc. This is not needed but would be a nice addition.
    	
    	//Write the tags-END
    	
    	//Write image data-START
    	dat = new rebuild.graphics.tiff.Writer(littleEndian, imageBuffer);
    	
    	//TODO: Later in life I want to support writing multiple images. This is so that layered/animated images can
    	//be written to the TIFF.
    	
    	writeImageData(dat, encodeAlpha);
    	//Write image data-END
        
    	//Write data to main stream
    	stream.write(headerBuffer.toByteArray());
    	stream.write(tagBuffer.toByteArray());
    	stream.write(imageBuffer.toByteArray());
    	
    	//Get full data
    	byte[] bdata = stream.toByteArray();
    	
    	//Close streams and return
    	headerBuffer.close();
    	tagBuffer.close();
    	imageBuffer.close();
        stream.close();
        
    	return bdata;
    }
    
    /*
    /**
     * Get the whole image data length. If -1 is returned than the image is has different length of data per row.
     *
    private long getImageDataLength()
    {
    	long row = getImageDataRowLength();
    	if(row == -1L)
    	{
    		return -1L;
    	}
    	return row * height;
    }
    
    /**
     * Get the whole image data length when image is compressed.
     *
    private long getImageDataComLength()
    {
    	long len = getImageDataRowLength(0);
    	for(int i = 1; i < height; i++)
    	{
    		len += getImageDataRowLength(i);
    	}
    	return len;
    }
    
    /**
     * Get the first row length.
     *
    private long getImageDataRowLength()
    {
    	return getImageDataRowLength(0);
    }
    
    /**
     * Get a row length when image is uncompressed.
     *
    private long getImageDataRowLength(int row)
    {
    	return getImageDataRowLength(row, false);
    }
    
    /**
     * Get a row length.
     *
    private long getImageDataRowLength(int row, boolean supportCom)
    {
    	if(!supportCom)
    	{
    		if(((CompressionTag)ifd.getTagByType(CompressionTag.getTagTypeValue())).getCompression() != CompressionTag.NO_COMPRESSION)
    		{
    			return -1L;
    		}
    	}
    	switch(((PhotometricInterpretationTag)ifd.getTagByType(PhotometricInterpretationTag.getTagTypeValue())).getImageType())
    	{
    		case PhotometricInterpretationTag.RGB:
    			switch(((CompressionTag)ifd.getTagByType(CompressionTag.getTagTypeValue())).getCompression())
    			{
    				case CompressionTag.NO_COMPRESSION:
    					int pixelLength = ifd.indexOf(EXTRA_SAMPLES_TAG) == -1 ? 3 : 4;
    					return pixelLength * width;
					default:
						throw new java.lang.UnsupportedOperationException(Resources.getString(BBXResource.TIFF_UNSUPPORTED_COMPRESSION));
    			}
    		default:
    			throw new java.lang.UnsupportedOperationException(Resources.getString(BBXResource.TIFF_UNSUPPORTED_PHOTOMETRIC));
    	}
    }
    */
    
    private void writeImageData(rebuild.graphics.tiff.Writer dat, boolean encodeAlpha) throws IOException
    {
    	switch(((PhotometricInterpretationTag)ifd.getTagByType(PhotometricInterpretationTag.getTagTypeValue())).getImageType())
    	{
    		case PhotometricInterpretationTag.RGB:
    			//Extracted the image data writer because it could be greyscale/mono/rgb/lab/CMYK/YCbCr and can be compressed
    	    	int[] data = super.getARGBInt(0, width, new XYRect(0, 0, width, height));
    	    	ExtraSamplesTag extraSamples = (ExtraSamplesTag)ifd.getTagByType(ExtraSamplesTag.getTagTypeValue());
    	    	boolean unassociatedAlpha = extraSamples == null ? false : extraSamples.getSample(0) == ExtraSamplesTag.UNASSALPHA; //Temp since extra samples only supports unassociated alpha
    	    	for(int y = 0; y < height; y++)
    	    	{
    	    		for(int x = 0; x < width; x++)
    	    		{
    	    			int pixel = data[getIndex(x, y, 0)];
    	    			
    	    			dat.writeByte(GraphicsUtilities.colorGetRed(pixel));
    	    			dat.writeByte(GraphicsUtilities.colorGetGreen(pixel));
    	    			dat.writeByte(GraphicsUtilities.colorGetBlue(pixel));
    	    			if(encodeAlpha)
    	    			{
    	    				if(unassociatedAlpha)
    	    				{
    	    					dat.writeByte(GraphicsUtilities.colorGetAlpha(pixel)); //Unassociated alpha
    	    				}
    	    				else
    	    				{
    	    					//TODO: Code the ability for a 5th byte to be written (associated alpha).
    	    				}
    	    			}
    	    		}
    	    	}
    			break;
    		default:
    			throw new java.lang.UnsupportedOperationException(Resources.getString(BBXResource.TIFF_UNSUPPORTED_PHOTOMETRIC));
    	}
    }
    
    private void ensureRequiredTags(boolean encodeAlpha)
    {
    	//Technically if the Tag value/dataType matches the default on a Tag (if one exists) than the Tag does not need
    	//to be included. Tags I know don't have defaults will always be included, ones with defaults will be checked if
    	//they have the default value. If it has the default value then it is not included, else it is.
    	
    	/*
    	 * Color
    	 * -----
    	 * Format
    	Bilevel						Greyscale					Palette						RGB							CMYK						YCbCr						Lab
    	-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    	ImageWidth					ImageWidth					ImageWidth					ImageWidth					ImageWidth					ImageWidth					ImageWidth
    	ImageLength					ImageLength					ImageLength					ImageLength					ImageLength					ImageLength					ImageLength
    								BitsPerSample				BitsPerSample				BitsPerSample				BitsPerSample				BitsPerSample				BitsPerSample
    	Compression					Compression					Compression					Compression					Compression					Compression					Compression
    	PhotometricInterpretation	PhotometricInterpretation	PhotometricInterpretation	PhotometricInterpretation	PhotometricInterpretation	PhotometricInterpretation	PhotometricInterpretation
    	StripOffsets				StripOffsets				StripOffsets				StripOffsets				StripOffsets				StripOffsets				StripOffsets
    																						SamplesPerPixel				SamplesPerPixel				SamplesPerPixel				SamplesPerPixel
    	RowsPerStrip				RowsPerStrip				RowsPerStrip				RowsPerStrip				RowsPerStrip				RowsPerStrip				RowsPerStrip
    	StripByteCounts				StripByteCounts				StripByteCounts				StripByteCounts				StripByteCounts				StripByteCounts				StripByteCounts
    	Xresolution					Xresolution					Xresolution					Xresolution					Xresolution					Xresolution					Xresolution
    	Yresolution					Yresolution					Yresolution					Yresolution					Yresolution					Yresolution					Yresolution
    	ResolutionUnit				ResolutionUnit				ResolutionUnit				ResolutionUnit				ResolutionUnit				ResolutionUnit				ResolutionUnit
    															ColorMap																			ReferenceBlackWhite
    	*/
    	Tag tag = null;
    	int ty = 0;
    	int index = 0;
    	int tCount;
    	
    	//-Image width
    	ty = ImageWidthTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new ImageWidthTag(width);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((ImageWidthTag)tag).getWidth() != width)
			{
				((ImageWidthTag)tag).setValue(width);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Image length (height)
    	ty = ImageLengthTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new ImageLengthTag(height);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((ImageLengthTag)tag).getLength() != height)
			{
				((ImageLengthTag)tag).setValue(height);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Photometric interpretation
    	ty = PhotometricInterpretationTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new PhotometricInterpretationTag(PhotometricInterpretationTag.RGB);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((PhotometricInterpretationTag)tag).getImageType() != PhotometricInterpretationTag.RGB)
			{
				((PhotometricInterpretationTag)tag).setValue(PhotometricInterpretationTag.RGB);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Samples per pixel
    	ty = SamplesPerPixelTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	short samples = (short)(encodeAlpha ? 4 : 3);
    	if(tCount == 0)
    	{
    		tag = new SamplesPerPixelTag(samples);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((SamplesPerPixelTag)tag).getSamples() != samples)
			{
				((SamplesPerPixelTag)tag).setValue(samples);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Bits per sample
    	ty = BitsPerSampleTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new BitsPerSampleTag(samples);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((BitsPerSampleTag)tag).getCount() != samples)
			{
				((BitsPerSampleTag)tag).setCount(samples);
			}
			for(int i = 0; i < samples; i++)
			{
				if(((BitsPerSampleTag)tag).getSample(i) != 8)
				{
					((BitsPerSampleTag)tag).setSample(i, (short)8);
				}
			}
			ifd.addTag(tag);
    	}
    	
    	//-Compression
    	ty = CompressionTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new CompressionTag((short)compressionLevel);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((CompressionTag)tag).getCompression() != compressionLevel)
			{
				((CompressionTag)tag).setValue(compressionLevel);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Resolution unit
    	ty = ResolutionUnitTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new ResolutionUnitTag(ResolutionUnitTag.INCH);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((ResolutionUnitTag)tag).getResolutionUnit() != ResolutionUnitTag.INCH)
			{
				((ResolutionUnitTag)tag).setValue(ResolutionUnitTag.INCH);
			}
			ifd.addTag(tag);
    	}
    	
    	//-X resolution
    	ty = XResolutionTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new XResolutionTag(72, 1);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((XResolutionTag)tag).getNumerator() != 72)
			{
				((XResolutionTag)tag).setNumerator(72);
			}
			if(((XResolutionTag)tag).getDenominator() != 1)
			{
				((XResolutionTag)tag).setDenominator(1);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Y resolution
    	ty = YResolutionTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new YResolutionTag(72, 1);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((YResolutionTag)tag).getNumerator() != 72)
			{
				((YResolutionTag)tag).setNumerator(72);
			}
			if(((YResolutionTag)tag).getDenominator() != 1)
			{
				((YResolutionTag)tag).setDenominator(1);
			}
			ifd.addTag(tag);
    	}
    	
    	//-Add a alpha component-Tell ExtraSamples to include one more field of unassociated (non-premultiplied alpha)
		ty = ExtraSamplesTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0 && encodeAlpha)
    	{
    		tag = new ExtraSamplesTag(ExtraSamplesTag.UNASSALPHA);
    		ifd.addTag(tag);
    	}
    	else if(encodeAlpha)
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((ExtraSamplesTag)tag).getSample(0) != ExtraSamplesTag.UNASSALPHA)
			{
				((ExtraSamplesTag)tag).setValue(ExtraSamplesTag.UNASSALPHA);
			}
			if(encodeAlpha) //All tags have been removed in case there was any but if alpha is going to be encoded then add the tag back in.
			{
				ifd.addTag(tag);
			}
    	}
    	
    	//Get the Planar configuration value
    	ty = PlanarConfigurationTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new PlanarConfigurationTag();
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			ifd.removeTag(index);
			if(((PlanarConfigurationTag)tag).getPlanarFormat() != PlanarConfigurationTag.CHUNKY)
			{
				((PlanarConfigurationTag)tag).setValue(PlanarConfigurationTag.CHUNKY);
			}
			ifd.addTag(tag);
    	}
    	short planarConfig = ((PlanarConfigurationTag)tag).getPlanarFormat();
    	
    	//-Rows per strip
    	int rowsPerStrip;
    	ty = RowsPerStripTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		//Recommended to have about 8K bytes per strip so need to do some math.
    		//TODO: Figure out how to handle compression (don't know if compression resets for each strip)
    		int widthByteCount = width * samples;
    		tag = new RowsPerStripTag(rowsPerStrip = height * widthByteCount > 8192 ? (widthByteCount >= 8192 ? 1 : (int)Math.floor(8192 / widthByteCount)) : height/*Math.max((int)Math.floor(byteCount / widthByteCount), 1)*/);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			//Make sure that only one tag exists
			rowsPerStrip = ((RowsPerStripTag)tag).getRowsPerStrip();
    	}
    	int stripsPerImage = ((RowsPerStripTag)tag).getStripsPerImage(height);
    	
    	//-Strip byte counts
    	ty = StripByteCountsTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new StripByteCountsTag(stripsPerImage, planarConfig, samples);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			((StripByteCountsTag)tag).setCount(stripsPerImage, planarConfig, samples);
			//Make sure that only one tag exists
    	}
    	
    	//Figure out the largest length
    	((StripByteCountsTag)tag).setByteCount(-1, (rowsPerStrip * width) * samples);
    	
    	//-Strip offsets
    	ty = StripOffsetsTag.getTagTypeValue();
    	tCount = getTagCount(ty);
    	if(tCount == 0)
    	{
    		tag = new StripOffsetsTag(stripsPerImage, planarConfig, samples);
    		ifd.addTag(tag);
    	}
    	else
    	{
    		if(tCount > 1)
    		{
    			for(int i = 1; i < tCount; i++)
    			{
    				ifd.removeTag(ifd.indexOf(ty));
    			}
    		}
    		index = ifd.indexOf(ty);
			tag = ifd.getTag(index);
			((StripOffsetsTag)tag).setCount(stripsPerImage, planarConfig, samples);
			//Make sure that only one tag exists
    	}
    }
    
    private int getTagCount(int type)
    {
    	int count = 0;
    	int index = ifd.indexOf(type);
    	if(index != -1)
    	{
    		count++;
    		while((index = ifd.indexOf(type, index + 1)) != -1)
    		{
    			count++;
    		}
    	}
    	return count;
    }
}
