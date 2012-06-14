using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace TestApp
{
    public static class PDFWriter
    {
        #region Classes

        private class PDF
        {
            public ISet<PDFObject> Objects;

            public PDF()
            {
                this.Objects = new SortedSet<PDFObject>(new ObjectComparer());
            }

            private List<PDFObject> GetUnusedObjects()
            {
                List<PDFObject> unused = new List<PDFObject>(Objects);

                foreach (PDFObject obj in Objects)
                {
                    FindUsedObjects(obj, unused);
                }

                return unused;
            }

            private static void FindUsedObjects(PDFObject objectToCheck, IDictionary<string, object> dict, List<PDFObject> unused)
            {
                //Look through dictionary
                for (var en = dict.GetEnumerator(); en.MoveNext(); )
                {
                    object obj = en.Current.Value;
                    if (objectToCheck != null)
                    {
                        if (en.Current.Key.Equals("Type"))
                        {
                            if (obj is PDFName)
                            {
                                if (((PDFName)obj).Value.Equals("Catalog"))
                                {
                                    //PDF Catalog, need this
                                    unused.Remove(objectToCheck);
                                }
                            }
                        }
                    }

                    if (obj is PDFObject)
                    {
                        //Don't want to check again
                        if (unused.Contains((PDFObject)obj))
                        {
                            unused.Remove((PDFObject)obj);
                            FindUsedObjects((PDFObject)obj, unused);
                        }
                    }
                    else if (obj is IDictionary<string, object>)
                    {
                        FindUsedObjects(objectToCheck, (IDictionary<string, object>)obj, unused);
                    }
                    else if (obj is object[])
                    {
                        FindUsedObjects(objectToCheck, (object[])obj, unused);
                    }
                }
            }

            private static void FindUsedObjects(PDFObject objectToCheck, object[] arr, List<PDFObject> unused)
            {
                //Look through array
                foreach (object obj in arr)
                {
                    if (obj is PDFObject)
                    {
                        if (unused.Contains((PDFObject)obj))
                        {
                            unused.Remove((PDFObject)obj);
                            FindUsedObjects((PDFObject)obj, unused);
                        }
                    }
                    else if (obj is IDictionary<string, object>)
                    {
                        FindUsedObjects(objectToCheck, (IDictionary<string, object>)obj, unused);
                    }
                    else if (obj is object[])
                    {
                        FindUsedObjects(objectToCheck, (object[])obj, unused);
                    }
                }
            }

            private static void FindUsedObjects(PDFObject objectToCheck, List<PDFObject> unused)
            {
                //Ignore null objects
                if (objectToCheck.Value != null)
                {
                    if (objectToCheck.Value is PDFObject)
                    {
                        if (unused.Contains((PDFObject)objectToCheck.Value))
                        {
                            //Indirect object
                            unused.Remove((PDFObject)objectToCheck.Value);
                            FindUsedObjects((PDFObject)objectToCheck.Value, unused);
                        }
                    }
                    else if (objectToCheck.Value is IDictionary<string, object>)
                    {
                        FindUsedObjects(objectToCheck, (IDictionary<string, object>)objectToCheck.Value, unused);
                    }
                    else if (objectToCheck.Value is object[])
                    {
                        FindUsedObjects(objectToCheck, (object[])objectToCheck.Value, unused);
                    }
                }
            }

            private class ObjectComparer : IComparer<PDFObject>
            {
                public int Compare(PDFObject x, PDFObject y)
                {
                    return x.Number.CompareTo(y.Number);
                }
            }

            public long Write(Stream s)
            {
                MemoryStream ms = new MemoryStream();
                Encoding en = Encoding.GetEncoding("ISO-8859-1");

                //--Write header--
                byte[] buffer = en.GetBytes("%PDF-1.4\n"); //1.4 gets us a lot of features (with the exception of object streams)
                ms.Write(buffer, 0, buffer.Length);
                buffer = new byte[] { (byte)'%', 0xFF, 0xFF, 0xFF, 0xFF, (byte)'\n' }; //Since we will contain binary data, we want to make sure our PDF indicates it has binary data
                ms.Write(buffer, 0, buffer.Length);

                long objectOffset = ms.Position;

                //Find the catalog element
                PDFObject cat = null;
                foreach (PDFObject obj in this.Objects)
                {
                    if (obj.Value is IDictionary<string, object>)
                    {
                        for (var e = ((IDictionary<string, object>)obj.Value).GetEnumerator(); e.MoveNext(); )
                        {
                            if (e.Current.Key.Equals("Type"))
                            {
                                if (e.Current.Value is PDFName)
                                {
                                    if (((PDFName)e.Current.Value).Value.Equals("Catalog"))
                                    {
                                        //PDF Catalog, need this
                                        cat = obj;
                                        break;
                                    }
                                }
                            }
                        }
                        if (cat != null)
                        {
                            //Found catalog, we can stio
                            break;
                        }
                    }
                }
                if (cat == null)
                {
                    //No catalog, we can't continue
                    return -1;
                }

                //Find all unused (free) objects
                List<PDFObject> freeObjects = GetUnusedObjects();

                MemoryStream xrefStream = new MemoryStream();

                //...but we need to be able to use indicies, so put it in a list
                List<PDFObject> sortedObj = new List<PDFObject>(this.Objects);

                //--Write xref--
                //First the header
                buffer = en.GetBytes("xref\n");
                xrefStream.Write(buffer, 0, buffer.Length);

                //Then the xref table info
                buffer = en.GetBytes(string.Format("0 {0}\n", sortedObj.Count + 1));
                xrefStream.Write(buffer, 0, buffer.Length);

                if (freeObjects.Count > 0)
                {
                    //Need to find the first free object
                    foreach (PDFObject obj in sortedObj)
                    {
                        if (freeObjects.Contains(obj))
                        {
                            StringBuilder sb = new StringBuilder();

                            //Get "next free object" number
                            string value = obj.Number.ToString();
                            if (value.Length < 10)
                            {
                                sb.Append(new string('0', 10 - value.Length));
                            }
                            else if (value.Length > 10)
                            {
                                return -1;
                            }
                            sb.Append(value);

                            //Finish first xref
                            sb.Append(" 65536 f \n");

                            buffer = Encoding.ASCII.GetBytes(sb.ToString());
                            xrefStream.Write(buffer, 0, buffer.Length);
                            break;
                        }
                    }
                }
                else
                {
                    //No free objects, we can finish the "linked-list" here
                    buffer = Encoding.ASCII.GetBytes("0000000000 65536 f \n");
                    xrefStream.Write(buffer, 0, buffer.Length);
                }

                //--Write objects--
                foreach (PDFObject obj in sortedObj)
                {
                    //Write for the body
                    obj.Write(ms, en);

                    //Write the xref
                    StringBuilder sb = new StringBuilder();

                    bool free = freeObjects.Contains(obj);
                    freeObjects.Remove(obj);
                    string value;

                    if (free)
                    {
                        //For free objects, we need to get the next free object and write it's value. If there are no more free objects, we write zero
                        if (freeObjects.Count > 0)
                        {
                            for (int i = sortedObj.IndexOf(obj) + 1; i < sortedObj.Count; i++)
                            {
                                if (freeObjects.Contains(sortedObj[i]))
                                {
                                    //Get "next free object" number
                                    value = sortedObj[i].Number.ToString();
                                    if (value.Length < 10)
                                    {
                                        sb.Append(new string('0', 10 - value.Length));
                                    }
                                    else if (value.Length > 10)
                                    {
                                        return -1;
                                    }
                                    sb.Append(value);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            sb.Append("0000000000");
                        }
                    }
                    else
                    {
                        //First the byte offset
                        value = objectOffset.ToString();
                        if (value.Length < 10)
                        {
                            //Need to append zeros
                            sb.Append(new string('0', 10 - value.Length));
                        }
                        else if (value.Length > 10)
                        {
                            return -1;
                        }
                        sb.Append(value);
                    }

                    //Space
                    sb.Append(' ');

                    //Next the generation number
                    value = obj.Revision.ToString();
                    if (value.Length < 5)
                    {
                        //Need to append zeros
                        sb.Append(new string('0', 5 - value.Length));
                    }
                    else if (value.Length > 5)
                    {
                        return -1;
                    }
                    sb.Append(value);

                    //Space
                    sb.Append(' ');

                    //If this is a free object or not
                    sb.Append(free ? 'f' : 'n');

                    //End of line
                    sb.Append(" \n");

                    //Write the value
                    buffer = Encoding.ASCII.GetBytes(sb.ToString()); //Needs to be 20 bytes, so don't use a specific encoding
                    xrefStream.Write(buffer, 0, buffer.Length);

                    //Reset objectOffset
                    objectOffset = ms.Position;
                }

                //Now write the xref table to the output
                xrefStream.WriteTo(ms);

                //--Write trailer--
                buffer = en.GetBytes("trailer");
                ms.Write(buffer, 0, buffer.Length);

                //Need a dictionary for trailer
                Dictionary<string, object> trailerDict = new Dictionary<string, object>();
                trailerDict.Add("Size", sortedObj.Count + 1);
                trailerDict.Add("Root", cat); //Need the catalog object
                PDFHexString ID = new PDFHexString(MD5Hash(en.GetBytes(DateTime.UtcNow.ToString()), en));
                trailerDict.Add("ID", new object[] { ID, ID }); //An ID is recommended
                PDF_WriteDict(ms, en, trailerDict);

                //Now write the byte offset to the xref table
                buffer = en.GetBytes("startxref");
                ms.Write(buffer, 0, buffer.Length);
                buffer = en.GetBytes("\n");
                ms.Write(buffer, 0, buffer.Length);
                buffer = en.GetBytes(objectOffset.ToString()); //After writing the last object, the offset will equal the stream position, which will be at the start of the xref table
                ms.Write(buffer, 0, buffer.Length);

                //Finally, write the end of file marker
                buffer = en.GetBytes("%%EOF");
                ms.Write(buffer, 0, buffer.Length);

                ms.WriteTo(s);
                return ms.Length;
            }

            private static string MD5Hash(byte[] value, Encoding en)
            {
                System.Security.Cryptography.MD5 md5 = System.Security.Cryptography.MD5Cng.Create();
                byte[] hash = md5.ComputeHash(value);
                return en.GetString(hash);
            }
        }

        private class PDFName
        {
            public string Value;

            public PDFName(string value)
            {
                this.Value = value;
            }
        }

        private class PDFHexString
        {
            public string Value;

            public PDFHexString(string value)
            {
                this.Value = value;
            }
        }

        private class PDFStream
        {
            public MemoryStream Stream;
            public IDictionary<string, object> Dictionary;

            public PDFStream(MemoryStream s, IDictionary<string, object> dict)
            {
                this.Stream = s;
                this.Dictionary = dict;
            }
        }

        private class PDFObject
        {
            public uint Number;
            public ushort Revision;
            public object Value;

            public PDFObject(uint num, ushort rev, object value)
            {
                this.Number = num;
                this.Revision = rev;
                this.Value = value;
            }

            private static long PDF_WriteStream(Stream s, Encoding en, MemoryStream value, IDictionary<string, object> additionalDict)
            {
                if (additionalDict != null)
                {
                    //We can't have the additional dictionary replace required keys
                    if (additionalDict.ContainsKey("Length") || additionalDict.ContainsKey("Filter") || additionalDict.ContainsKey("DL"))
                    {
                        return -1;
                    }
                }

                MemoryStream mem = new MemoryStream();

                //Process stream
                MemoryStream sMem = new MemoryStream();
                StreamWriter sw = new StreamWriter(sMem, Encoding.ASCII);

                //Get position
                long pos = value.Position;
                value.Position = 0;

                while (value.Position < value.Length)
                {
                    byte by = (byte)value.ReadByte();
                    string line = Convert.ToString(by, 16);
                    if (line.Length < 2)
                    {
                        sw.Write('0');
                    }
                    sw.Write(line.ToCharArray());
                }

                //Reset stream
                value.Position = pos;

                sw.Write('>'); //EOD
                sw.Flush();

                IDictionary<string, object> stDict = new Dictionary<string, object>();
                stDict.Add("Length", sMem.Length); //Get the length of the encoded string
                stDict.Add("Filter", new PDFName("ASCIIHexDecode")); //For easier management
                stDict.Add("DL", value.Length); //Not needed, but potentially useful
                if (additionalDict != null)
                {
                    //Copy dictionary
                    foreach (KeyValuePair<string, object> kv in additionalDict)
                    {
                        stDict.Add(kv);
                    }
                }

                byte[] newline = en.GetBytes("\n");
                byte[] buffer = en.GetBytes("stream");

                //Write dictionary
                PDF_WriteDict(mem, en, stDict);

                //Write start of stream
                mem.Write(newline, 0, newline.Length);
                mem.Write(buffer, 0, buffer.Length);
                mem.Write(newline, 0, newline.Length);

                //Write stream
                sMem.WriteTo(mem);

                //Write end of stream
                buffer = en.GetBytes("endstream");
                mem.Write(newline, 0, newline.Length);
                mem.Write(buffer, 0, buffer.Length);
                mem.Write(newline, 0, newline.Length);

                mem.WriteTo(s);

                return mem.Length;
            }

            //Writes it like PDF_WriteValue
            public static long WriteIndirect(Stream s, Encoding en, PDFObject pdfobj)
            {
                byte[] buffer = en.GetBytes(string.Format("{0} {1} R", pdfobj.Number, pdfobj.Revision));
                s.Write(buffer, 0, buffer.Length);
                return buffer.Length;
            }

            public long Write(Stream s, Encoding en)
            {
                MemoryStream mem = new MemoryStream();

                byte[] buffer = en.GetBytes(string.Format("{0} {1} obj", this.Number, this.Revision));
                byte[] newline = en.GetBytes("\n");

                //Write first header
                mem.Write(buffer, 0, buffer.Length);
                mem.Write(newline, 0, newline.Length);

                //Write data
                MemoryStream vMem = new MemoryStream();
                if (PDF_WriteValue(vMem, en, this.Value) == -1)
                {
                    if (this.Value is MemoryStream || this.Value is PDFStream)
                    {
                        //Special because streams must be objects
                        MemoryStream ms;
                        IDictionary<string, object> ad = null;

                        if (this.Value is MemoryStream)
                        {
                            ms = (MemoryStream)this.Value;
                        }
                        else
                        {
                            PDFStream pdfs = (PDFStream)this.Value;
                            ms = pdfs.Stream;
                            ad = pdfs.Dictionary;
                        }

                        if (PDF_WriteStream(vMem, en, ms, ad) == -1)
                        {
                            return -1;
                        }
                    }
                    else
                    {
                        return -1;
                    }
                }
                vMem.WriteTo(mem);

                //Write footer
                buffer = en.GetBytes("endobj");
                mem.Write(newline, 0, newline.Length);
                mem.Write(buffer, 0, buffer.Length);
                mem.Write(newline, 0, newline.Length);

                mem.WriteTo(s);

                return mem.Length;
            }
        }

        #endregion

        #region Tests

        public static void MakePDF()
        {
            using (FileStream fs = new FileStream(@"C:\Users\Class2014\Documents\Visual Studio\Eclipse\workspace\Test.pdf", FileMode.Create))
            {
                /*
                int[] imgData;
                using (StreamReader i = new StreamReader(@"C:\Users\Class2014\Documents\Visual Studio\Eclipse\workspace\image.txt"))
                {
                    List<int> img = new List<int>();
                    string str = i.ReadToEnd();
                    for (int k = 0; k < str.Length; k += 6)
                    {
                        img.Add((0x7F << 24) | (Convert.ToByte(str.Substring(k, 2), 16) << 16) | (Convert.ToByte(str.Substring(k + 2, 2), 16) << 8) | (Convert.ToByte(str.Substring(k + 4, 2), 16)));
                    }
                    imgData = img.ToArray();
                }
                Image img1 = new Image(80, 80, imgData);
                Image img2 = new Image(2, 2, new int[] { 0x7F000000, 0x7FFF0000, 0x7F00FF00, 0x7F0000FF });

                MakePDF_Pictures(fs, new Image[] { img1, img2 });
                 */
                int size = 80;
                Random rand = new Random();
                byte[] data = new byte[size * size * 4];
                rand.NextBytes(data);
                int[] idata = new int[size * size];
                Buffer.BlockCopy(data, 0, idata, 0, data.Length);

                Image img1 = new Image(size, size, idata);
                Image img2 = new Image(2, 2, new int[] { 0x7F000000, 0x7FFF0000, 0x7F00FF00, 0x7F0000FF });

                MakePDF_Pictures(fs, new Image[] { img1, img2 });
                //MakePDF_SimpleText(fs);
            }
        }

        //Not a PDF class
        private class Image
        {
            private int width, height;
            private int[] data;

            public Image(int width, int height, int[] data)
            {
                this.width = width;
                this.height = height;
                this.data = data;
            }

            public int GetWidth()
            {
                return width;
            }

            public int GetHeight()
            {
                return height;
            }

            public void getARGB(int[] argbData, int offset, int scanLength, int x, int y, int width, int height)
            {
                //Just for "setup"
                Buffer.BlockCopy(data, 0, argbData, 0, Math.Min(argbData.Length, data.Length) * 4);
            }
        }

        private static long MakePDF_Pictures(Stream s, Image[] images)
        {
            if (images == null || images.Length == 0)
            {
                //Need some image
                return -1;
            }

            //Default width/height will be a 8.5x11 paper at 96dpi
            int width = 816;
            int height = 1056;

            //Find the desired size of the PDF
            for (int i = 0; i < images.Length; i++)
            {
                width = Math.Max(width, images[i].GetWidth());
                height = Math.Max(height, images[i].GetHeight());
            }

            int[] offX = new int[images.Length];
            int[] offY = new int[images.Length];

            //Determine the positions of all the images (center each)
            for (int i = 0; i < images.Length; i++)
            {
                //Center of page
                offX[i] = width / 2;
                offY[i] = height / 2;

                //Offset
                offX[i] -= images[i].GetWidth() / 2;
                offY[i] -= images[i].GetHeight() / 2;
            }

            PDF pdf = new PDF();

            //Make the objects (backwards so we can use them correctly, indirect objects)

            PDFObject[] contents = new PDFObject[images.Length];
            PDFObject[] xobjectImages = new PDFObject[images.Length];
            PDFObject[] resources = new PDFObject[images.Length];
            PDFObject[] pages = new PDFObject[images.Length];

            int totalObjectCount = 3 + (4 * images.Length);
            Dictionary<string, object> dict;

            for (int i = images.Length - 1; i >= 0; i--)
            {
                //Contents
                MemoryStream mem = new MemoryStream();
                StreamWriter sw = new StreamWriter(mem, Encoding.ASCII);
                {
                    sw.WriteLine("q% Save graphics state");
                    sw.WriteLine("1 0 0 1 {0} {1} cm% Translate to ({0},{1})", offX[i], offY[i]);
                    sw.WriteLine("/Im1 Do% Paint image");
                    sw.WriteLine("Q% Restore graphics state");
                    sw.Flush();
                }
                pdf.Objects.Add(contents[i] = new PDFObject((uint)totalObjectCount--, 0, mem));

                //Images
                dict = new Dictionary<string, object>();
                dict.Add("Type", new PDFName("XObject"));
                dict.Add("Subtype", new PDFName("Image"));
                dict.Add("Width", images[i].GetWidth());
                dict.Add("Height", images[i].GetHeight());
                dict.Add("ColorSpace", new PDFName("DeviceRGB"));
                dict.Add("BitsPerComponent", 8);

                //-Get image data
                int[] idata = new int[images[i].GetWidth() * images[i].GetHeight()];
                images[i].getARGB(idata, 0, images[i].GetWidth(), 0, 0, images[i].GetWidth(), images[i].GetHeight());
                mem = new MemoryStream();
                for (int k = 0; k < idata.Length; k++)
                {
                    //Slow, but this isn't a real time game that needs speed (also, alpha is not used as PDF doesn't really "have" alpha for images)
                    mem.WriteByte((byte)((idata[k] & 0x00FF0000) >> 16));
                    mem.WriteByte((byte)((idata[k] & 0x0000FF00) >> 8));
                    mem.WriteByte((byte)(idata[k] & 0x000000FF));
                }
                mem.Flush();
                pdf.Objects.Add(xobjectImages[i] = new PDFObject((uint)totalObjectCount--, 0, new PDFStream(mem, dict)));

                //Resources
                IDictionary<string, object> xobject = dict = new Dictionary<string, object>();
                xobject.Add("Im1", xobjectImages[i]);

                dict = new Dictionary<string, object>();
                dict.Add("ProcSet", new object[] { new PDFName("PDF"), new PDFName("ImageC") });
                dict.Add("XObject", xobject);
                pdf.Objects.Add(resources[i] = new PDFObject((uint)totalObjectCount--, 0, dict));

                //Page
                dict = new Dictionary<string, object>();
                dict.Add("Type", new PDFName("Page"));
                dict.Add("MediaBox", new object[] { 0, 0, width, height });
                dict.Add("Contents", contents[i]);
                dict.Add("Resources", resources[i]);
                pdf.Objects.Add(pages[i] = new PDFObject((uint)totalObjectCount--, 0, dict));
            }

            //Parent page
            PDFObject pPages;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Pages"));
            dict.Add("Kids", pages);
            dict.Add("Count", images.Length);
            pdf.Objects.Add(pPages = new PDFObject((uint)totalObjectCount--, 0, dict));

            //-Set each page's parent
            for (int i = 0; i < images.Length; i++)
            {
                ((IDictionary<string, object>)pages[i].Value).Add("Parent", pPages);
            }

            //Outlines
            PDFObject outlines;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Outlines"));
            dict.Add("Count", 0);
            pdf.Objects.Add(outlines = new PDFObject((uint)totalObjectCount--, 0, dict));

            //The PDF catalog
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Catalog"));
            dict.Add("Outlines", outlines);
            dict.Add("Pages", pPages);
            pdf.Objects.Add(new PDFObject((uint)totalObjectCount--, 0, dict));

            //Write
            return pdf.Write(s);
        }

        private static void MakePDF_Picture(Stream s)
        {
            PDF pdf = new PDF();

            //Make the objects (backwards so we can use them correctly, indirect objects)

            PDFObject contents;
            MemoryStream mem = new MemoryStream();
            StreamWriter sw = new StreamWriter(mem, Encoding.ASCII);
            {
                sw.WriteLine("q% Save graphics state");
                sw.WriteLine("132 0 0 132 45 140 cm% Translate to (45,140) and scale by 132");
                sw.WriteLine("/Im1 Do% Paint image"); //Each image needs a different name
                sw.WriteLine("Q% Restore graphics state");
                sw.Flush();
            }
            pdf.Objects.Add(contents = new PDFObject(7, 0, mem));

            PDFObject image;
            Dictionary<string, object> dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("XObject"));
            dict.Add("Subtype", new PDFName("Image"));
            dict.Add("Width", 2);
            dict.Add("Height", 2);
            dict.Add("ColorSpace", new PDFName("DeviceRGB"));
            dict.Add("BitsPerComponent", 8);
            mem = new MemoryStream();
            mem.Write(new byte[] { 0xFF, 0x00, 0x00 }, 0, 3);
            mem.Write(new byte[] { 0x00, 0xFF, 0x00 }, 0, 3);
            mem.Write(new byte[] { 0x00, 0x00, 0xFF }, 0, 3);
            mem.Write(new byte[] { 0x00, 0x00, 0x00 }, 0, 3);
            mem.Flush();
            pdf.Objects.Add(image = new PDFObject(6, 0, new PDFStream(mem, dict)));

            PDFObject resources;
            IDictionary<string, object> xobject = dict = new Dictionary<string, object>();
            xobject.Add("Im1", image);
            dict = new Dictionary<string, object>();
            dict.Add("ProcSet", new object[] { new PDFName("PDF"), new PDFName("ImageB") });
            dict.Add("XObject", xobject);
            pdf.Objects.Add(resources = new PDFObject(5, 0, dict));

            PDFObject page;
            IDictionary<string, object> pagedict = dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Page"));
            dict.Add("MediaBox", new object[] { 0, 0, 612, 792 });
            dict.Add("Contents", contents);
            dict.Add("Resources", resources);
            pdf.Objects.Add(page = new PDFObject(4, 0, dict));

            PDFObject pages;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Pages"));
            dict.Add("Kids", new object[] { page });
            dict.Add("Count", 1);
            pdf.Objects.Add(pages = new PDFObject(3, 0, dict));

            //Modify the page to reference the parent
            pagedict.Add("Parent", pages);

            PDFObject outlines;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Outlines"));
            dict.Add("Count", 0);
            pdf.Objects.Add(outlines = new PDFObject(2, 0, dict));

            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Catalog"));
            dict.Add("Outlines", outlines);
            dict.Add("Pages", pages);
            pdf.Objects.Add(new PDFObject(1, 0, dict));

            pdf.Write(s);
        }

        private static void MakePDF_SimpleGraphicsMultiPage(Stream s)
        {
            PDF pdf = new PDF();

            //Make the objects (backwards so we can use them correctly, indirect objects)

            PDFObject procSet;
            pdf.Objects.Add(procSet = new PDFObject(7, 0, new object[] { new PDFName("PDF") }));

            PDFObject contents;
            MemoryStream mem = new MemoryStream();
            StreamWriter sw = new StreamWriter(mem, Encoding.ASCII);
            {
                sw.WriteLine("% Draw a black line segment, using the default line width .");
                sw.WriteLine("150 250 m");
                sw.WriteLine("150 350 l");
                sw.WriteLine("S");
                sw.WriteLine("% Draw a thicker, dashed line segment .");
                sw.WriteLine("4 w% Set line width to 4 points");
                sw.WriteLine("[ 4 6 ] 0 d% Set dash pattern to 4 units on, 6 units off");
                sw.WriteLine("150 250 m");
                sw.WriteLine("400 250 l");
                sw.WriteLine("S");
                sw.WriteLine("[ ] 0 d% Reset dash pattern to a solid line");
                sw.WriteLine("1 w% Reset line width to 1 unit");
                sw.WriteLine("% Draw a rectangle with a 1−unit red border, filled with light blue .");
                sw.WriteLine("1.0 0.0 0.0 RG% Red for stroke color");
                sw.WriteLine("0.5 0.75 1.0 rg% Light blue for fill color");
                sw.WriteLine("200 300 50 75 re");
                sw.WriteLine("B");
                sw.WriteLine("% Draw a curve filled with gray and with a colored border .");
                sw.WriteLine("0.5 0.1 0.2 RG");
                sw.WriteLine("0.7 g");
                sw.WriteLine("300 300 m");
                sw.WriteLine("300 400 400 400 400 300 c");
                sw.WriteLine("b");
                sw.Flush();
            }
            pdf.Objects.Add(contents = new PDFObject(6, 0, mem));

            PDFObject page1;
            Dictionary<string, object> dict;
            IDictionary<string, object> resourcedict = dict = new Dictionary<string, object>();
            dict.Add("ProcSet", procSet);
            IDictionary<string, object> pagedict1 = dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Page"));
            dict.Add("MediaBox", new object[] { 0, 0, 612, 792 });
            dict.Add("Contents", contents);
            dict.Add("Resources", resourcedict);
            pdf.Objects.Add(page1 = new PDFObject(5, 0, dict));

            PDFObject page2;
            resourcedict = dict = new Dictionary<string, object>();
            dict.Add("ProcSet", procSet);
            IDictionary<string, object> pagedict2 = dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Page"));
            dict.Add("MediaBox", new object[] { 0, 0, 612, 792 });
            dict.Add("Contents", contents);
            dict.Add("Resources", resourcedict);
            pdf.Objects.Add(page2 = new PDFObject(4, 0, dict));

            PDFObject pages;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Pages"));
            dict.Add("Kids", new object[] { page1, page2 });
            dict.Add("Count", 2);
            pdf.Objects.Add(pages = new PDFObject(3, 0, dict));

            //Modify the page to reference the parent
            pagedict1.Add("Parent", pages);
            pagedict2.Add("Parent", pages);

            PDFObject outlines;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Outlines"));
            dict.Add("Count", 0);
            pdf.Objects.Add(outlines = new PDFObject(2, 0, dict));

            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Catalog"));
            dict.Add("Outlines", outlines);
            dict.Add("Pages", pages);
            pdf.Objects.Add(new PDFObject(1, 0, dict));

            pdf.Write(s);
        }

        private static void MakePDF_SimpleGraphics(Stream s)
        {
            PDF pdf = new PDF();

            //Make the objects (backwards so we can use them correctly, indirect objects)

            //This is based off "Simple Graphics Example"

            PDFObject procSet;
            pdf.Objects.Add(procSet = new PDFObject(6, 0, new object[] { new PDFName("PDF") }));

            PDFObject contents;
            MemoryStream mem = new MemoryStream();
            StreamWriter sw = new StreamWriter(mem, Encoding.ASCII);
            {
                sw.WriteLine("% Draw a black line segment, using the default line width .");
                sw.WriteLine("150 250 m");
                sw.WriteLine("150 350 l");
                sw.WriteLine("S");
                sw.WriteLine("% Draw a thicker, dashed line segment .");
                sw.WriteLine("4 w% Set line width to 4 points");
                sw.WriteLine("[ 4 6 ] 0 d% Set dash pattern to 4 units on, 6 units off");
                sw.WriteLine("150 250 m");
                sw.WriteLine("400 250 l");
                sw.WriteLine("S");
                sw.WriteLine("[ ] 0 d% Reset dash pattern to a solid line");
                sw.WriteLine("1 w% Reset line width to 1 unit");
                sw.WriteLine("% Draw a rectangle with a 1−unit red border, filled with light blue .");
                sw.WriteLine("1.0 0.0 0.0 RG% Red for stroke color");
                sw.WriteLine("0.5 0.75 1.0 rg% Light blue for fill color");
                sw.WriteLine("200 300 50 75 re");
                sw.WriteLine("B");
                sw.WriteLine("% Draw a curve filled with gray and with a colored border .");
                sw.WriteLine("0.5 0.1 0.2 RG");
                sw.WriteLine("0.7 g");
                sw.WriteLine("300 300 m");
                sw.WriteLine("300 400 400 400 400 300 c");
                sw.WriteLine("b");
                sw.Flush();
            }
            pdf.Objects.Add(contents = new PDFObject(5, 0, mem));

            PDFObject page;
            Dictionary<string, object> dict;
            IDictionary<string, object> resourcedict = dict = new Dictionary<string, object>();
            dict.Add("ProcSet", procSet);
            IDictionary<string, object> pagedict = dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Page"));
            dict.Add("MediaBox", new object[] { 0, 0, 612, 792 });
            dict.Add("Contents", contents);
            dict.Add("Resources", resourcedict);
            pdf.Objects.Add(page = new PDFObject(4, 0, dict));

            PDFObject pages;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Pages"));
            dict.Add("Kids", new object[] { page });
            dict.Add("Count", 1);
            pdf.Objects.Add(pages = new PDFObject(3, 0, dict));

            //Modify the page to reference the parent
            pagedict.Add("Parent", pages);

            PDFObject outlines;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Outlines"));
            dict.Add("Count", 0);
            pdf.Objects.Add(outlines = new PDFObject(2, 0, dict));

            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Catalog"));
            dict.Add("Outlines", outlines);
            dict.Add("Pages", pages);
            pdf.Objects.Add(new PDFObject(1, 0, dict));

            pdf.Write(s);
        }

        private static void MakePDF_SimpleText(Stream s)
        {
            PDF pdf = new PDF();

            //Make the objects (backwards so we can use them correctly, indirect objects)

            //This is based off "Simple Text String Example"

            //This will make a free object
            pdf.Objects.Add(new PDFObject(8, 0, new object[] { new PDFName("PDF"), new PDFName("Text") }));

            PDFObject font;
            Dictionary<string, object> dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Font"));
            dict.Add("Subtype", new PDFName("Type1"));
            dict.Add("Name", new PDFName("F1"));
            dict.Add("BaseFont", new PDFName("Helvetica"));
            dict.Add("Encoding", new PDFName("MacRomanEncoding"));
            pdf.Objects.Add(font = new PDFObject(7, 0, dict));

            PDFObject procSet;
            pdf.Objects.Add(procSet = new PDFObject(6, 0, new object[] { new PDFName("PDF"), new PDFName("Text") }));

            PDFObject contents;
            MemoryStream mem = new MemoryStream();
            StreamWriter sw = new StreamWriter(mem, Encoding.ASCII);
            {
                sw.WriteLine("BT");
                sw.WriteLine("/F1 24 Tf");
                sw.WriteLine("100 100 Td");
                sw.WriteLine("(Hello World) Tj");
                sw.WriteLine("ET");
                sw.Flush();
            }
            pdf.Objects.Add(contents = new PDFObject(5, 0, mem));

            PDFObject page;
            IDictionary<string, object> fdict = dict = new Dictionary<string, object>();
            dict.Add("F1", font);
            IDictionary<string, object> resourcedict = dict = new Dictionary<string, object>();
            dict.Add("ProcSet", procSet);
            dict.Add("Font", fdict);
            IDictionary<string, object> pagedict = dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Page"));
            dict.Add("MediaBox", new object[] { 0, 0, 612, 792 });
            dict.Add("Contents", contents);
            dict.Add("Resources", resourcedict);
            pdf.Objects.Add(page = new PDFObject(4, 0, dict));

            PDFObject pages;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Pages"));
            dict.Add("Kids", new object[] { page });
            dict.Add("Count", 1);
            pdf.Objects.Add(pages = new PDFObject(3, 0, dict));

            //Modify the page to reference the parent
            pagedict.Add("Parent", pages);

            PDFObject outlines;
            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Outlines"));
            dict.Add("Count", 0);
            pdf.Objects.Add(outlines = new PDFObject(2, 0, dict));

            dict = new Dictionary<string, object>();
            dict.Add("Type", new PDFName("Catalog"));
            dict.Add("Outlines", outlines);
            dict.Add("Pages", pages);
            pdf.Objects.Add(new PDFObject(1, 0, dict));

            pdf.Write(s);
        }

        #endregion

        #region Writer

        private static long PDF_WriteDict(Stream s, Encoding en, IDictionary<string, object> value)
        {
            MemoryStream mem = new MemoryStream();

            //Head
            byte[] buffer = en.GetBytes("<<");
            mem.Write(buffer, 0, buffer.Length);

            //Get gap
            buffer = en.GetBytes(" ");
            byte[] newline = en.GetBytes("\n");

            bool first = true;

            for (var e = value.GetEnumerator(); e.MoveNext(); )
            {
                KeyValuePair<string, object> kv = e.Current;

                if (first)
                {
                    //We don't want to write a new line for the first element
                    first = false;
                }
                else
                {
                    mem.Write(newline, 0, newline.Length);
                }

                //Write key
                if (PDF_WriteName(mem, en, kv.Key) == -1)
                {
                    return -1;
                }

                //Write gap
                mem.Write(buffer, 0, buffer.Length);

                //Write value
                if (PDF_WriteValue(mem, en, kv.Value) == -1)
                {
                    return -1;
                }
            }

            //Tail
            buffer = en.GetBytes(">>");
            mem.Write(buffer, 0, buffer.Length);

            //Write out
            mem.WriteTo(s);

            return mem.Length;
        }

        private static long PDF_WriteValue(Stream s, Encoding en, object obj)
        {
            long len = -1;
            if (obj == null)
            {
                len = PDF_WriteNull(s, en);
            }
            else if (obj is PDFName)
            {
                len = PDF_WriteName(s, en, ((PDFName)obj).Value);
            }
            else if (obj is PDFObject)
            {
                len = PDFObject.WriteIndirect(s, en, (PDFObject)obj);
            }
            else if (obj is PDFHexString)
            {
                len = PDF_WriteString(s, en, ((PDFHexString)obj).Value, false);
            }
            else if (obj is string)
            {
                len = PDF_WriteString(s, en, (string)obj, true);
            }
            else if (obj is bool)
            {
                len = PDF_WriteBool(s, en, (bool)obj);
            }
            else if (obj is int || obj is long || obj is float || obj is double)
            {
                len = PDF_WriteNumber(s, en, obj);
            }
            else if (obj is object[])
            {
                len = PDF_WriteArray(s, en, (object[])obj);
            }
            else if (obj is IDictionary<string, object>)
            {
                len = PDF_WriteDict(s, en, (IDictionary<string, object>)obj);
            }
            return len;
        }

        private static long PDF_WriteArray(Stream s, Encoding en, object[] value)
        {
            MemoryStream mem = new MemoryStream();

            //Head
            byte[] buffer = en.GetBytes("[");
            mem.Write(buffer, 0, buffer.Length);

            //Get gap
            buffer = en.GetBytes(" ");

            for (int i = 0; i < value.Length; i++)
            {
                if (PDF_WriteValue(mem, en, value[i]) == -1)
                {
                    return -1;
                }

                if (i < value.Length - 1)
                {
                    //Write space
                    mem.Write(buffer, 0, buffer.Length);
                }
            }

            //Tail
            buffer = en.GetBytes("]");
            mem.Write(buffer, 0, buffer.Length);

            //Write out
            mem.WriteTo(s);

            return mem.Length;
        }

        private static long PDF_WriteName(Stream s, Encoding en, string value)
        {
            StringBuilder sb = new StringBuilder();
            sb.Append('/');

            foreach (char c in value)
            {
                if (c == 0)
                {
                    //Can't have a null char
                    return -1;
                }
                else if (c < 0x21 || c > 0x7E)
                {
                    //PDF 1.2 and higher can have any char but \0 in name (so long as outside of the specified range is in a hex format)
                    string format = Convert.ToString((byte)c, 16);
                    sb.Append('#');
                    if (format.Length < 2)
                    {
                        sb.Append('0');
                    }
                    sb.Append(format);
                }
                else
                {
                    sb.Append(c);
                }
            }

            byte[] dat = en.GetBytes(sb.ToString());
            s.Write(dat, 0, dat.Length);
            return dat.Length;
        }

        private static long PDF_WriteString(Stream s, Encoding en, string value, bool literal)
        {
            StringBuilder sb = new StringBuilder();
            if (literal)
            {
                //Process para
                int paraCount = 0;
                foreach (char c in value)
                {
                    if (c == '(')
                    {
                        paraCount++;
                    }
                    else if (c == ')')
                    {
                        paraCount--;
                    }
                }

                sb.Append('(');

                //Format string
                foreach (char c in value)
                {
                    switch (c)
                    {
                        case '\\':
                            sb.Append("\\\\");
                            break;
                        case '\n':
                            sb.Append("\\n");
                            break;
                        case '\r':
                            sb.Append("\\r");
                            break;
                        case '\t':
                            sb.Append("\\t");
                            break;
                        case '\b':
                            sb.Append("\\b");
                            break;
                        case '\f':
                            sb.Append("\\f");
                            break;
                        case '(':
                        case ')':
                            if (paraCount != 0)
                            {
                                //Unbalanced. To keep it simple, simply make them all control chars
                                sb.Append('\\');
                            }
                            sb.Append(c);
                            break;
                        default:
                            if (c < 0x20 || c > 0x7E)
                            {
                                //Outside ASCII range
                                string format = Convert.ToString((int)c, 8);
                                sb.Append('\\');
                                if (format.Length != 3)
                                {
                                    if (format.Length < 3)
                                    {
                                        sb.Append(new string('0', 3 - format.Length));
                                    }
                                    else
                                    {
                                        //Question mark, what do we put here?
                                        sb.Append("077");
                                    }
                                }
                                else
                                {
                                    sb.Append(format);
                                }
                            }
                            else
                            {
                                //Simply add it
                                sb.Append(c);
                            }
                            break;
                    }
                }

                sb.Append(')');
            }
            else
            {
                sb.Append('<');

                //Remove all whitespace
                foreach (char c in value)
                {
                    switch (c)
                    {
                        case '\n':
                        case '\r':
                        case '\t':
                        case '\f':
                        case ' ':
                            break;
                        default:
                            string v = Convert.ToString((byte)c, 16);
                            if (v.Length < 2)
                            {
                                sb.Append('0');
                            }
                            sb.Append(v);
                            break;
                    }
                }

                sb.Append('>');
            }
            byte[] dat = en.GetBytes(sb.ToString());
            s.Write(dat, 0, dat.Length);
            return dat.Length;
        }

        private static long PDF_WriteBool(Stream s, Encoding en, bool value)
        {
            byte[] dat = en.GetBytes(value ? "true" : "false");
            s.Write(dat, 0, dat.Length);
            return dat.Length;
        }

        private static long PDF_WriteNumber(Stream s, Encoding en, object value)
        {
            byte[] dat = null;
            if (value is int)
            {
                dat = en.GetBytes(((int)value).ToString());
            }
            else if (value is long)
            {
                dat = en.GetBytes(((int)((long)value)).ToString());
            }
            else if (value is float)
            {
                dat = en.GetBytes(string.Format("{0:R}", (float)value));
            }
            else if (value is double)
            {
                dat = en.GetBytes(string.Format("{0:R}", (float)((double)value)));
            }
            if (dat != null)
            {
                s.Write(dat, 0, dat.Length);
            }
            return dat != null ? dat.Length : -1;
        }

        private static long PDF_WriteNull(Stream s, Encoding en)
        {
            byte[] dat = en.GetBytes("null");
            s.Write(dat, 0, dat.Length);
            return dat.Length;
        }

        #endregion
    }
}
