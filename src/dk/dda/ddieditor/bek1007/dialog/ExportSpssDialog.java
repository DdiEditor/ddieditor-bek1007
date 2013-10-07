package dk.dda.ddieditor.bek1007.dialog;

import java.io.File;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.preference.PreferenceUtil;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ExportSpssDialog extends Dialog {
	public String path;
	public boolean checkMd5 = false;

	public ExportSpssDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("bek1007.spssexport.properties"));
		this.getShell().setText(Translator.trans("bek1007.spssexport.title"));

		// export path
		editor.createLabel(group,
				Translator.trans("bek1007.spssexport.spsssyntaxpath"));
		final Text pathText = editor.createText(group, "", false);
		pathText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				path = ((Text) e.getSource()).getText();
			}
		});
		File lastBrowsedPath = PreferenceUtil.getLastBrowsedPath();
		if (lastBrowsedPath != null) {
			pathText.setText(lastBrowsedPath.getAbsolutePath());
			path = lastBrowsedPath.getAbsolutePath();
		}

		Button pathBrowse = editor.createButton(group,
				Translator.trans("ExportDDI3Action.filechooser.browse"));
		pathBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				dirChooser.setText(Translator
						.trans("bek1007.spssexport.spsssyntaxpath"));
				PreferenceUtil.setPathFilter(dirChooser);
				path = dirChooser.open();
				if (path != null) {
					pathText.setText(path);
					PreferenceUtil.setLastBrowsedPath(path);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// create statistics on numeric variables
		Button numVarStatistic = editor.createCheckBox(group, "",
				Translator.trans("bek1007.spssexport.checkmd5"));
		numVarStatistic.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkMd5 = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});
		return null;
	}
}
