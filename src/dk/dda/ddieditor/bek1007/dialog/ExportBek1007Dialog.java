package dk.dda.ddieditor.bek1007.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.preference.PreferenceUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.dda.ddieditor.bek1007.model.SiardModel;
import dk.sa.bek1007.context.ContextDocumentationIndexDocument;
import dk.sa.bek1007.context.ContextDocumentationIndexDocument.ContextDocumentationIndex.Document;
import dk.sa.bek1007.siardk.TableType;

public class ExportBek1007Dialog extends Dialog {
	public String path;
	public String bek1007Id;
	public SiardModel siardModel;

	public boolean exportArchiveIndex = false;
	public boolean exportDocumentIndex = false;
	public Combo tableCombo;
	public boolean tableRelationsDia = false;
	public final List<Button> docs = new ArrayList<Button>();
	public final Set<String> docsSelected = new LinkedHashSet<String>();

	public ExportBek1007Dialog(Shell parentShell, String bek1007Id) {
		super(parentShell);
		this.bek1007Id = bek1007Id;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("bek1007.bek1007export.properties"));
		this.getShell()
				.setText(Translator.trans("bek1007.bek1007export.title"));

		siardModel = ModelStore.getInstance().getSiardk(bek1007Id);

		// archive description
		Button exportarchiveButton = editor.createCheckBox(group,
				Translator.trans("bek1007.bek1007export.archive"),
				Translator.trans("bek1007.bek1007export.exportarchive"));
		exportarchiveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportArchiveIndex = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});
		exportarchiveButton.setEnabled(false);

		// document list description
		Button exportDocumentButton = editor.createCheckBox(group,
				Translator.trans("bek1007.bek1007export.documents"),
				Translator.trans("bek1007.bek1007export.documentindex"));
		exportDocumentButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportDocumentIndex = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});
		exportDocumentButton.setEnabled(false);

		// table to export
		String[] options = new String[siardModel.getTables().size() + 1];
		options[0] = "";
		int count = 1;
		for (TableType table : siardModel.getTables()) {
			options[count] = table.getName();
			count++;
		}
		editor.createLabel(group,
				Translator.trans("bek1007.bek1007export.table"));
		tableCombo = editor.createCombo(group, options);
		tableCombo.setEnabled(false);

		// table relations diagram
		Button exportTableRelationsDiaButton = editor.createCheckBox(group, "",
				Translator.trans("bek1007.bek1007export.tablerelations"));
		exportTableRelationsDiaButton
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						tableRelationsDia = ((Button) e.widget).getSelection();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});
		exportTableRelationsDiaButton.setEnabled(false);

		// documents to export
		Group docGroup = editor.createGroup(parent,
				Translator.trans("bek1007.bek1007export.documentlisttitle"));
		ScrolledComposite sc = new ScrolledComposite(docGroup, SWT.H_SCROLL
				| SWT.V_SCROLL);
		sc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		layoutData.heightHint = 200;
		sc.setLayoutData(layoutData);

		Composite composite = new Composite(sc, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		ContextDocumentationIndexDocument contextDocumentationIndexDoc = null;
		try {
			contextDocumentationIndexDoc = ModelStore.getInstance()
					.getContextInformation(siardModel.getPath());
		} catch (XmlException | IOException e) {
			Editor.showError(e, "NA");
			return null;
		}

		for (Document doc : contextDocumentationIndexDoc
				.getContextDocumentationIndex().getDocumentList()) {
			Button check = new Button(composite, SWT.CHECK);
			check.setData(doc.getDocumentTitle() + "-" + doc.getDocumentID());
			check.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			check.setText(doc.getDocumentTitle());
			docs.add(check);

			check.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (((Button) e.getSource()).getSelection()) {
						docsSelected.add((String) ((Button) e.getSource())
								.getData());
					} else {
						docsSelected.remove((String) ((Button) e.getSource())
								.getData());
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					docsSelected.remove((String) ((Button) e.getSource())
							.getData());
				}
			});

			Menu menu = new Menu(check);
			menu.setDefaultItem(createSelctAllMenuItem(menu));
			createDeSelctAllMenuItem(menu);
			check.setMenu(menu);
		}
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setContent(composite);

		// export path
		Group exportPathGroup = editor.createGroup(parent,
				Translator.trans("bek1007.bek1007export.path"));
		editor.createLabel(exportPathGroup,
				Translator.trans("bek1007.bek1007export.exportpath"));
		final Text pathText = editor.createText(exportPathGroup, "", false);
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

		Button pathBrowse = editor.createButton(exportPathGroup,
				Translator.trans("ExportDDI3Action.filechooser.browse"));

		pathBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				dirChooser.setText(Translator
						.trans("bek1007.bek1007export.exportpath"));
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
		return null;
	}

	MenuItem createSelctAllMenuItem(Menu menu) {
		final MenuItem selectAllMenuItem = new MenuItem(menu, SWT.NONE);
		selectAllMenuItem.setText(Translator
				.trans("bek1007.bek1007export.selectAllMenuItem")); //$NON-NLS-1$

		selectAllMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				for (Button button : docs) {
					button.setSelection(true);
					button.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
		return selectAllMenuItem;
	}

	void createDeSelctAllMenuItem(Menu menu) {
		final MenuItem deSelectAllMenuItem = new MenuItem(menu, SWT.NONE);
		deSelectAllMenuItem.setText(Translator
				.trans("bek1007.bek1007export.deSelectAllMenuItem")); //$NON-NLS-1$

		deSelectAllMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				for (Button button : docs) {
					button.setSelection(false);
					button.notifyListeners(SWT.DefaultSelection, new Event());
				}
			}
		});
	}
}
