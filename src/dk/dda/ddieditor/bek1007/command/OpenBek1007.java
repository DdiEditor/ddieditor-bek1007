package dk.dda.ddieditor.bek1007.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.preference.PreferenceUtil;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.dda.ddieditor.bek1007.model.SiardModel;
import dk.dda.ddieditor.bek1007.perspective.Bek1007Perspective;
import dk.dda.ddieditor.bek1007.view.Bek1007StudyDescriptionView;
import dk.dda.ddieditor.bek1007.view.SiardkView;
import dk.dda.ddieditor.bek1007.view.ViewUtil;
import dk.sa.bek1007.siardk.SiardDiarkDocument;

public class OpenBek1007 extends org.eclipse.core.commands.AbstractHandler {

	private String exportPath;
	String bek1007Id;
	File tableFile;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get siardk location
		DirectoryDialog dirChooser = new DirectoryDialog(
				PlatformUI
						.getWorkbench()
						.getDisplay()
						.getActiveShell());
		dirChooser.setText(Translator
				.trans("bek1007.open.1007location"));
		PreferenceUtil
				.setPathFilter(dirChooser);
		exportPath = dirChooser.open();
		if (exportPath != null) {
			PreferenceUtil
					.setLastBrowsedPath(exportPath);
		} else {
			return null;
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
												// show archive view
												String partName = ViewUtil
														.openBek1007StudyDescriptionView(exportPath);

												// id
												bek1007Id = partName; 
												// new File(exportPath)
												// .getName();

												// files
												tableFile = new File(exportPath
														+ File.separator
														+ "Indices"
														+ File.separator
														+ "tableIndex.xml");

												// ingest resource
												// try {
												// FilesystemManager.getInstance().addResource(
												// new File(exportPath +
												// File.separator + "Indices"
												// + File.separator +
												// "tableIndex.xml"));
												// } catch (Exception e) {
												// // TODO Auto-generated catch
												// block
												// e.printStackTrace();
												// }

												// open resource
												SiardDiarkDocument siardkDoc = null;
												try {
													siardkDoc = SiardDiarkDocument.Factory
															.parse(tableFile);
												} catch (XmlException
														| IOException e) {
													Editor.showError(
															e,
															Translator
																	.trans("bek1007.siardopen.error"));
												}

												try {
													// open perspective
													PlatformUI
															.getWorkbench()
															.showPerspective(
																	Bek1007Perspective.ID,
																	PlatformUI
																			.getWorkbench()
																			.getActiveWorkbenchWindow());

													// show siardk view
													SiardkView siardkView = (SiardkView) PlatformUI
															.getWorkbench()
															.getActiveWorkbenchWindow()
															.getActivePage()
															.showView(
																	SiardkView.ID);

													// add model
													SiardModel siardModel = new SiardModel();
													siardModel.setId(bek1007Id);
													siardModel
															.setPath(exportPath);
													siardModel
															.setSiardkDoc((SiardDiarkDocument) siardkDoc
																	.copy());

													ModelStore.getInstance()
															.addSiardk(
																	bek1007Id,
																	siardModel);

													// refresh
													siardkView.getViewer()
															.refresh();

													

													// show document view
													ViewUtil.openBek1007ContextInformationView(
															exportPath,
															partName);

													// show table relation view
													ViewUtil.openTableRelationView(
															bek1007Id, partName);
													
													// set fokus to study description view
													PlatformUI
													.getWorkbench().getActiveWorkbenchWindow().getActivePage()
													.showView(Bek1007StudyDescriptionView.ID);
												} catch (WorkbenchException e) {
													Editor.showError(
															e,
															Translator
																	.trans("bek1007.siardviewopen.error"));
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
			Editor.showError(e, "NA");
		}
		return null;
	}
}
