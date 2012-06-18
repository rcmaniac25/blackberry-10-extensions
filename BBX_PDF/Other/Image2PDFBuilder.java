package pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import rebuild.util.pdf.writer.PDF;
import rebuild.util.pdf.writer.PDFName;
import rebuild.util.pdf.writer.PDFObject;
import rebuild.util.pdf.writer.PDFStream;

public class Image2PDFBuilder
{
	private Vector images;
	
	public Image2PDFBuilder()
	{
		images = new Vector();
	}
	
	public Image2PDFBuilder addImage(Bitmap img)
	{
		if(img != null)
		{
			images.addElement(img);
		}
		return this;
	}
	
	public Image2PDFBuilder removeImage(Bitmap img)
	{
		if(img != null)
		{
			images.removeElement(img);
		}
		return this;
	}
	
	public void reset()
	{
		images.removeAllElements();
	}
	
	public void build(OutputStream os) throws IOException
	{
		int imageCount = images.size();
		
		if (imageCount > 0)
        {
            //Default width/height will be a 8.5x11 paper at 96dpi
            int width = 816;
            int height = 1056;
            
            //Find the desired size of the PDF
            for (int i = 0; i < imageCount; i++)
            {
            	Bitmap bmp = (Bitmap)images.elementAt(i);
                width = Math.max(width, bmp.getWidth());
                height = Math.max(height, bmp.getHeight());
            }
            
            int[] offX = new int[imageCount];
            int[] offY = new int[imageCount];
            
            //Determine the positions of all the images (center each)
            for (int i = 0; i < imageCount; i++)
            {
                //Center of page
                offX[i] = width / 2;
                offY[i] = height / 2;
                
                //Offset
                Bitmap bmp = (Bitmap)images.elementAt(i);
                offX[i] -= bmp.getWidth() / 2;
                offY[i] -= bmp.getHeight() / 2;
            }
            
            PDF pdf = new PDF();
            
            //Make the objects (backwards so we can use them correctly, indirect objects)
            
            PDFObject[] contents = new PDFObject[imageCount];
            PDFObject[] xobjectImages = new PDFObject[imageCount];
            PDFObject[] resources = new PDFObject[imageCount];
            PDFObject[] pages = new PDFObject[imageCount];
            
            int totalObjectCount = 3 + (4 * imageCount);
            Hashtable table;
            
            for (int i = imageCount - 1; i >= 0; i--)
            {
                //Contents
            	ByteArrayOutputStream mem = new ByteArrayOutputStream();
            	StringBuffer sb = new StringBuffer();
                {
                	Bitmap bmp = (Bitmap)images.elementAt(i);
                	sb.append("q% Save graphics state\n");
                	sb.append("" + bmp.getWidth() + " 0 0 " + bmp.getHeight() + " " + offX[i] + " " + offY[i] + " cm% Translate to (" + offX[i] + "," + offY[i] + ")\n");
                	sb.append("/Im1 Do% Paint image\n");
                	sb.append("Q% Restore graphics state\n");
                	
                	try
                	{
                		mem.write(sb.toString().getBytes("US-ASCII"));
                	}
                	catch(UnsupportedEncodingException e)
                	{
                	}
                	mem.flush();
                }
                pdf.addObject(contents[i] = new PDFObject(totalObjectCount--, 0, mem));
                
                //Images
                Bitmap bmp = (Bitmap)images.elementAt(i);
                table = new Hashtable();
                table.put("Type", new PDFName("XObject"));
                table.put("Subtype", new PDFName("Image"));
                table.put("Width", new Integer(bmp.getWidth()));
                table.put("Height", new Integer(bmp.getHeight()));
                table.put("ColorSpace", new PDFName("DeviceRGB"));
                table.put("BitsPerComponent", new Integer(8));
                
                //-Get image data
                int[] idata = new int[bmp.getWidth() * bmp.getHeight()];
                bmp.getARGB(idata, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
                mem = new ByteArrayOutputStream();
                for (int k = 0; k < idata.length; k++)
                {
                    //Slow, but this isn't a real time game that needs speed (also, alpha is not used as PDF doesn't really "have" alpha for images)
                    mem.write((idata[k] & 0x00FF0000) >> 16);
                    mem.write((idata[k] & 0x0000FF00) >> 8);
                    mem.write(idata[k] & 0x000000FF);
                }
                mem.flush();
                pdf.addObject(xobjectImages[i] = new PDFObject(totalObjectCount--, 0, new PDFStream(mem, table)));
                
                //Resources
                Hashtable xobject = table = new Hashtable();
                xobject.put("Im1", xobjectImages[i]);
                
                table = new Hashtable();
                table.put("ProcSet", new Object[] { new PDFName("PDF"), new PDFName("ImageC") });
                table.put("XObject", xobject);
                pdf.addObject(resources[i] = new PDFObject(totalObjectCount--, 0, table));
                
                //Page
                table = new Hashtable();
                table.put("Type", new PDFName("Page"));
                table.put("MediaBox", new Object[] { new Integer(0), new Integer(0), new Integer(width), new Integer(height) });
                table.put("Contents", contents[i]);
                table.put("Resources", resources[i]);
                pdf.addObject(pages[i] = new PDFObject(totalObjectCount--, 0, table));
            }
            
            //Parent page
            PDFObject pPages;
            table = new Hashtable();
            table.put("Type", new PDFName("Pages"));
            table.put("Kids", pages);
            table.put("Count", new Integer(imageCount));
            pdf.addObject(pPages = new PDFObject(totalObjectCount--, 0, table));
            
            //-Set each page's parent
            for (int i = 0; i < imageCount; i++)
            {
                ((Hashtable)pages[i].getValue()).put("Parent", pPages);
            }
            
            //Outlines
            PDFObject outlines;
            table = new Hashtable();
            table.put("Type", new PDFName("Outlines"));
            table.put("Count", new Integer(0));
            pdf.addObject(outlines = new PDFObject(totalObjectCount--, 0, table));
            
            //The PDF catalog
            table = new Hashtable();
            table.put("Type", new PDFName("Catalog"));
            table.put("Outlines", outlines);
            table.put("Pages", pPages);
            pdf.addObject(new PDFObject(totalObjectCount--, 0, table));
            
            //Write
            pdf.write(os);
        }
	}
}
