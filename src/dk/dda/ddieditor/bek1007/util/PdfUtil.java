package dk.dda.ddieditor.bek1007.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;

public class PdfUtil {
	Map<Integer, BufferedImage> images = new HashMap<Integer, BufferedImage>();
	String docId, bek1007Id, name, path;

	public PdfUtil(String docId, String bek1007Id, String path) {
		int index = docId.lastIndexOf("-");
		this.name = docId.substring(0, index - 1);
		this.docId = docId.substring(index + 1, docId.length());
		this.bek1007Id = bek1007Id;
		this.path = path;
	}

	public void export() throws COSVisitorException, IOException {
		// System.out.println(docId + " " + bek1007Id + " " + name + " " +
		// path);
		File file = new File(bek1007Id + File.separator
				+ "ContextDocumentation" + File.separator + "docCollection1"
				+ File.separator + docId);
		File[] files = file.listFiles();
		File tiff = null;
		for (File test : files) {
			if (test.getName() != "." || test.getName() != "..") {
				tiff = test;
			}
		}

		writeTiffImagesToFiles(tiff.getAbsolutePath());
		createPdf(path + File.separator + docId + "-" + name + ".pdf");
		images.clear();
	}

	public void writeTiffImagesToFiles(String path) throws IOException {
		SeekableStream s = new FileSeekableStream(new File(path));
		TIFFDecodeParam param = null;
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

		int numberOfPages = dec.getNumPages();
		// System.out.println("Number of pages: " + numberOfPages);

		JPEGEncodeParam jpegEncodeParam = null;
		for (int i = 0; i < dec.getNumPages(); i++) {
			// get image
			NullOpImage renderedImage = new NullOpImage(
					dec.decodeAsRenderedImage(i), null, OpImage.OP_IO_BOUND,
					null);

			// output file
			File outputfile = File.createTempFile(i + "-bek-1007-", ".jpg");
			FileOutputStream fos = new FileOutputStream(outputfile);

			// jpeg encode output file
			ImageEncoder imgEncoder = JPEGCodec.createImageEncoder("jpeg", fos,
					jpegEncodeParam);
			imgEncoder.encode(renderedImage);

			// finalize
			fos.close();
			outputfile.delete();
			images.put(i, renderedImage.getAsBufferedImage());
		}
	}

	public void createPdf(String path) throws IOException, COSVisitorException {
		PDDocument doc = new PDDocument();

		for (BufferedImage image : images.values()) {
			PDPage page = new PDPage();
			page.setMediaBox(new PDRectangle(image.getWidth(), image
					.getHeight()));

			// System.out.println("MediaBox: "+page.getMediaBox());
			// System.out.println("CropBox: "+page.getCropBox());

			// http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/
			// Scalr.resize(image, Scalr.Method.ULTRA_QUALITY,
			// Scalr.Mode.FIT_TO_WIDTH,
			// Math.round(page.getMediaBox().getWidth() - 100),
			// Scalr.OP_ANTIALIAS);

			doc.addPage(page);

			PDXObjectImage ximage = new PDJpeg(doc, image, 1.0f);
			PDPageContentStream content = new PDPageContentStream(doc, page);
			content.drawXObject(ximage, 0, 0, image.getWidth(),
					image.getHeight());

			content.close();
			ximage = null;
		}

		// finalize
		doc.save(path);
		doc.close();
	}

	/**
	 * Solution install: download.java.net/media/jai/builds/release/1_1_3/
	 */
	public static boolean testforJaiCodecInstall() {
		boolean mediaLib = false;
		Class mediaLibImage = null;
		try {
			mediaLibImage = Class
					.forName("com.sun.media.jai.codec.TIFFDecodeParam");
		} catch (ClassNotFoundException e) {
		}
		mediaLib = (mediaLibImage != null);
		return mediaLib;
	}
}
