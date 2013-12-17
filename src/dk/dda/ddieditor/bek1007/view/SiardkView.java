package dk.dda.ddieditor.bek1007.view;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.InputSelection;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;

import dk.dda.ddieditor.bek1007.dialog.ExportBek1007Dialog;
import dk.dda.ddieditor.bek1007.dialog.ExportSpssDialog;
import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.dda.ddieditor.bek1007.model.SiardModel;
import dk.dda.ddieditor.bek1007.osgi.Activator;
import dk.dda.ddieditor.bek1007.perspective.Bek1007Perspective;
import dk.dda.ddieditor.bek1007.util.ExportAsSpssSyntax;
import dk.dda.ddieditor.bek1007.util.PdfUtil;
import dk.sa.bek1007.siardk.TableType;

public class SiardkView extends ViewPart implements IPropertyListener {
	public static final String ID = "dk.dda.ddieditor.bek1007.view.SiardkView";
	private TreeViewer viewer;

	Menu menu;
	private MenuItem editMenuItem;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(new Object());
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object obj = ((TreeSelection) event.getSelection())
						.getFirstElement();
				if (obj instanceof String) {
					openBek1007((String) obj);
					return;
				} else if (obj instanceof TableType) {
					openTable((TableType) obj);
					return;
				}
			}
		});

		// set menu
		menu = new Menu(viewer.getTree());
		editMenuItem = createEditMenuItem(menu);
		createExportMenuItem(menu);
		createCloseMenuItem(menu);
		menu.setDefaultItem(editMenuItem);
		viewer.getTree().setMenu(menu);
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	//
	// Getters
	//
	public TreeViewer getViewer() {
		return viewer;
	}

	//
	// Menu
	//
	void createExportMenuItem(Menu menu) {
		final MenuItem exportMenuItem = new MenuItem(menu, SWT.NONE);
		exportMenuItem.setText(Translator
				.trans("bek1007.siardview.exportMenuItem")); //$NON-NLS-1$
		exportMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icon/export_wiz.gif"));

		exportMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				TreeItem[] t = viewer.getTree().getSelection();
				// empty selection
				if (t.length != 1) {
					return;
				}
				// bek 1007 top
				else if (t[0].getData() instanceof String) {
					exportBek1007((String) t[0].getData());
				}
				// table
				else if (t[0].getData() instanceof TableType) {
					try {
						exportSpssSyntax((TableType) t[0].getData());
					} catch (XMLStreamException | IOException e) {
						Editor.showError(e, ID);
					}
				}
			}
		});
	}

	MenuItem createEditMenuItem(Menu menu) {
		MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Translator.trans("bek1007.siardview.editMenuItem")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icon/flatLayout.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				TreeItem[] t = viewer.getTree().getSelection();
				// empty selection
				if (t.length != 1) {
					return;
				}
				// bek 1007 top
				else if (t[0].getData() instanceof String) {
					openBek1007((String) t[0].getData());
					return;
				}
				// table
				else if (t[0].getData() instanceof TableType) {
					openTable((TableType) t[0].getData());
					return;
				}
			}
		});
		return editMenuItem;
	}

	MenuItem createCloseMenuItem(Menu menu) {
		MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem.setText(Translator
				.trans("bek1007.siardview.closeMenuItem")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icon/close.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				TreeItem[] t = viewer.getTree().getSelection();
				// empty selection
				if (t.length != 1) {
					return;
				}
				// bek 1007 top
				else if (t[0].getData() instanceof String) {
					closeBek1007((String) t[0].getData());
					return;
				}
				// table
				else if (t[0].getData() instanceof TableType) {
					String bek1007Id = ModelStore.getInstance()
							.getSiardNameByTable(
									((TableType) t[0].getData()).getName());
					closeBek1007(bek1007Id);
					return;
				}
			}
		});
		return editMenuItem;
	}

	//
	// Actions
	//
	private void openBek1007(final String obj) {
		// update views
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								// export
								monitor.beginTask(
										Translator
												.trans("bek1007.siardview.updateviews"),
										1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												String path = ModelStore
														.getInstance()
														.getSiardk(obj)
														.getPath();
												String partName = ViewUtil
														.openBek1007StudyDescriptionView(path);
												ViewUtil.openBek1007ContextInformationView(
														path, partName);
												ViewUtil.openTableRelationView(
														obj, partName);

												// set fokus to study
												// description view
												try {
													PlatformUI
															.getWorkbench()
															.getActiveWorkbenchWindow()
															.getActivePage()
															.showView(
																	Bek1007StudyDescriptionView.ID);
												} catch (PartInitException e) {
													Editor.showError(e, ID);
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			Editor.showError(e, ID);
		}
	}

	private void openTable(TableType obj) {
		// show view
		TableView tableView = null;
		try {
			PlatformUI.getWorkbench().showPerspective(Bek1007Perspective.ID,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			tableView = (TableView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.showView(TableView.ID);

		} catch (WorkbenchException e) {
			Editor.showError(e, ID);
		}

		// add description
		StringBuilder pKeySb = new StringBuilder();
		for (String pKey : obj.getPrimaryKey().getColumnList()) {
			pKeySb.append(pKey);
			pKeySb.append(" ");
		}
		tableView.primaryKey = pKeySb.toString().trim();
		tableView.primarykeyText.setText(tableView.primaryKey);
		tableView.description = obj.getDescription();
		tableView.descriptionStyledText.setText(obj.getDescription());
		tableView.rowText.setText(obj.getRows().toString());

		// add column model
		tableView.columnItems = obj.getColumns().getColumnList();

		// add foreign key model
		if (obj.getForeignKeys() == null) {
			tableView.foreignkeyItems.clear();
		} else if (obj.getForeignKeys().getForeignKeyList().isEmpty()) {
			tableView.foreignkeyItems = ModelStore.getInstance()
					.getTableByName(obj.getName()).getForeignKeys()
					.getForeignKeyList();
		} else {
			tableView.foreignkeyItems = obj.getForeignKeys()
					.getForeignKeyList();
		}

		// set title
		tableView.tableName = obj.getName();
		tableView.changePartName(obj.getName());

		// refresh
		tableView.columnTableViewer.refresh();
		tableView.foreignkeyTableViewer.refresh();
		tableView.parent.redraw();
		tableView.parent.update();
	}

	private void closeBek1007(String id) {
		// clean views
		ViewUtil.closeTableView(id);
		ViewUtil.closeBek1007StudyDescriptionView(id);
		ViewUtil.closeBek1007ContextInformationView(id);
		ViewUtil.closeTableRelationView(id);

		// remove from viewer
		ModelStore.getInstance().removeSiardk(id);
		viewer.refresh();
	}

	protected void exportSpssSyntax(final TableType obj)
			throws XMLStreamException, IOException {
		final ExportSpssDialog exportSpssDialog = new ExportSpssDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		int result = exportSpssDialog.open();
		if (exportSpssDialog.path == null || result == ExportSpssDialog.CANCEL) {
			return;
		}

		// do export
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								// export
								monitor.beginTask(Translator
										.trans("bek1007.spssexport.title"), 1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												ExportAsSpssSyntax export = new ExportAsSpssSyntax();
												try {
													export.export(
															exportSpssDialog.path,
															ModelStore
																	.getInstance()
																	.getSiardNameByTable(
																			obj.getName()),
															obj,
															exportSpssDialog.checkMd5);
												} catch (
														NoSuchAlgorithmException
														| DDIFtpException
														| XMLStreamException
														| IOException
														| XmlException e) {
													Editor.showError(e, ID);
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			Editor.showError(e, ID);
		}
	}

	protected void exportBek1007(final String bek1007Id) {
		final ExportBek1007Dialog dialog = new ExportBek1007Dialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell(), bek1007Id);
		int result = dialog.open();
		if (dialog.path == null || result == ExportBek1007Dialog.CANCEL) {
			return;
		}

		final TableType obj = null;

		// do export
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								// export
								monitor.beginTask(Translator
										.trans("bek1007.spssexport.title"), 1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												try {
													// docs to pdf
													for (String docId : dialog.docsSelected) {
														new PdfUtil(
																docId,
																dialog.siardModel.getPath(),
																dialog.path)
																.export();
													}
												} catch (Exception e) {
													Editor.showError(e, ID);
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			Editor.showError(e, ID);
		}
	}

	//
	// Content provider
	//
	class ViewContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (String[]) ModelStore.getInstance().getLoadedSiardk()
					.keySet().toArray(new String[] {});
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			String bek1007Id = (String) parentElement;
			return ModelStore.getInstance().getLoadedSiardk().get(bek1007Id)
					.getTables().toArray(new TableType[] {});
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof String) {
				return null;
			}
			for (SiardModel siardModel : ModelStore.getInstance()
					.getLoadedSiardk().values()) {
				for (TableType table : siardModel.getSiardkDoc()
						.getSiardDiark().getTables().getTableList()) {
					if (table.getName().equals(((TableType) element).getName())) {
						return siardModel.getId();
					}
				}
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof String) {
				return true;
			}
			return false;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}
	}

	//
	// Label provider
	//
	class ViewLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof String) {
				return (String) element;
			} else if (element instanceof TableType) {
				return ((TableType) element).getName();
			}
			return "na";
		}

		public void setFocus() {
			viewer.getControl().setFocus();
		}

		public void dispose() {
		}
	}

	//
	// Tree menu provider
	//
	class TreeMenuProvider extends TreeMenu {
		@Override
		public InputSelection defineSelection(TreeViewer treeViewer, String ID) {
			// TODO Auto-generated method stub
			return super.defineSelection(treeViewer, ID);
		}
	}
}