package dk.dda.ddieditor.bek1007.util;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

public class PrintUtil extends org.ddialliance.ddieditor.ui.util.PrintUtil {
	final String bek1007archiveIndexXsltLocation = "resources/bek1007/dda-archiveIndex.xslt";
	final String bek1007contextDocumentationIndexXsltLocation = "resources/bek1007/dda-contextdocumentationIndex.xslt";

	public Transformer getArchiveIndexTransformer() throws Exception {
		Source archiveIndex = getXsltSource(bek1007archiveIndexXsltLocation);
		Transformer transformer = getTransformer(archiveIndex);

		return transformer;
	}
	
	public Transformer getContextDocumentationTransformer() throws Exception {
		Source archiveIndex = getXsltSource(bek1007contextDocumentationIndexXsltLocation);
		Transformer transformer = getTransformer(archiveIndex);

		return transformer;
	}
}
