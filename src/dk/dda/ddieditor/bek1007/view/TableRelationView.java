package dk.dda.ddieditor.bek1007.view;

import java.io.File;
import java.net.URL;

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

public class TableRelationView extends ViewPart implements IPropertyListener {
	public static final String ID = "dk.dda.ddieditor.bek1007.view.TableRelationView";
	private Composite parent;
	private Browser browser;
	public File imageFile;
	OpenBrowserListener openBrowserListener;

	@Override
	public void propertyChanged(Object source, int propId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.parent.setLayout(new GridLayout());
		this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Editor editor = new Editor();

		// description
		Group infoGroup = editor.createGroup(this.parent,
				Translator.trans("bek1007.tablerelation.info"));
		infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));
		browser = editor.createBrowser(infoGroup, Translator.trans("Info"));

		Button openInBrowser = editor
				.createButton(infoGroup, "Open in browser");
		openBrowserListener = new OpenBrowserListener(imageFile);
		openInBrowser.addSelectionListener(openBrowserListener);
	}

	public void changePartName(String newName) {
		setPartName(newName);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void init() {
		try {
			if (imageFile == null) {
				imageFile = ViewUtil.getBlankHtmlFile();
			}

			// open rcp browser
			openBrowserListener.htmlFile = imageFile;
			browser.setUrl(new URL("file://" + imageFile.getAbsolutePath())
					.toURI().toString());
		} catch (Exception e) {
			Editor.showError(e, ID);
		}
	}
}
