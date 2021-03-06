package dk.dda.ddieditor.bek1007.view;

import java.io.File;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.part.ViewPart;

import dk.dda.ddieditor.bek1007.util.PrintUtil;
import dk.sa.bek1007.context.ContextDocumentationIndexDocument;

public class Bek1007ContextDocumentationView extends ViewPart implements
		IPropertyListener {
	public static final String ID = "dk.dda.ddieditor.bek1007.view.Bek1007ContextDocumentationView";

	public ContextDocumentationIndexDocument contextDocumentationDoc;
	public File contextDocumentationFile;

	Editor editor;
	private Composite parent;
	File htmlFile = ViewUtil.getBlankHtmlFile();
	Browser browser;
	OpenBrowserListener openBrowserListener;
	String basePath = "";

	@Override
	public void propertyChanged(Object source, int propId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.parent.setLayout(new GridLayout());
		this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editor = new Editor();

		// description
		Group infoGroup = editor.createGroup(this.parent,
				Translator.trans("bek1007.document.info"));
		infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));
		browser = editor.createBrowser(infoGroup, Translator.trans("Info"));

		Button openInBrowser = editor
				.createButton(infoGroup, "Open in browser");
		openBrowserListener = new OpenBrowserListener(htmlFile);
		openInBrowser.addSelectionListener(openBrowserListener);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void changePartName(String newName) {
		setPartName(newName);
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void init() {
		try {
			if (htmlFile == null) {
				htmlFile = ViewUtil.getBlankHtmlFile();
			} else {
				// xslt transformer
				Transformer contextDocumentationTransformer = new PrintUtil()
						.getContextDocumentationTransformer();
				contextDocumentationTransformer.setParameter("avId",
						getPartName());

				contextDocumentationTransformer.setParameter("baselocation",
						basePath);

				// xslt transform
				htmlFile = File.createTempFile("contextDocumentation", ".html");
				htmlFile.deleteOnExit();
				openBrowserListener.htmlFile = htmlFile;

				contextDocumentationTransformer.transform(new StreamSource(
						contextDocumentationFile.toURI().toURL().toString()),
						new StreamResult(htmlFile.toURI().toURL().toString()));
			}
			// open rcp browser
			browser.setUrl(new URL("file://" + htmlFile.getAbsolutePath())
					.toURI().toString());
		} catch (Exception e) {
			Editor.showError(e, ID);
		}
	}
}
