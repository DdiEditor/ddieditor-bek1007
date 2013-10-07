package dk.dda.ddieditor.bek1007.view;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenBrowserListener implements SelectionListener {
	public File htmlFile = null;

	public OpenBrowserListener(File htmlFile) {
		this.htmlFile = htmlFile;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL("file://" + htmlFile.getAbsolutePath()));
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyCss(File htmlFile) {
		// copy css
		// FileUtils.copyFile(new
		// File("resources/landingpagexslt/style.css"),
		// new File(htmlFile.getParent() + File.separator
		// + "style.css"));
	}
}
