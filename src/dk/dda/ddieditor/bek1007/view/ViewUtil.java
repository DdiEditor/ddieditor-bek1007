package dk.dda.ddieditor.bek1007.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.dda.ddieditor.bek1007.perspective.Bek1007Perspective;
import dk.dda.ddieditor.bek1007.util.DotUtil;
import dk.sa.bek1007.archive.ArchiveIndexDocument;
import dk.sa.bek1007.context.ContextDocumentationIndexDocument;
import dk.sa.bek1007.siardk.ColumnType;
import dk.sa.bek1007.siardk.ForeignKeyType;

public class ViewUtil {
	public static String openBek1007StudyDescriptionView(String filePath) {
		Bek1007StudyDescriptionView bek1007StudyDescriptionView = null;
		try {
			PlatformUI.getWorkbench().showPerspective(Bek1007Perspective.ID,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			bek1007StudyDescriptionView = (Bek1007StudyDescriptionView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(Bek1007StudyDescriptionView.ID);

			// file
			File archiveFile = new File(filePath + File.separator + "Indices"
					+ File.separator + "archiveIndex.xml");

			// open archive index
			bek1007StudyDescriptionView.archiveIndexDoc = ArchiveIndexDocument.Factory
					.parse(archiveFile);

			// populate with data
			bek1007StudyDescriptionView.archiveIndexFile = archiveFile;
			String partName = bek1007StudyDescriptionView.archiveIndexDoc
					.getArchiveIndex().getArchiveInformationPackageID();
			bek1007StudyDescriptionView.changePartName(partName);
			bek1007StudyDescriptionView.init();

			return partName;
		} catch (XmlException | WorkbenchException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static void closeBek1007StudyDescriptionView(String partName) {
		try {
			Bek1007StudyDescriptionView bek1007StudyDescriptionView = (Bek1007StudyDescriptionView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(Bek1007StudyDescriptionView.ID);
			if (partName.equals(bek1007StudyDescriptionView.getPartName())) {
				bek1007StudyDescriptionView.archiveIndexFile = null;
				bek1007StudyDescriptionView.archiveIndexDoc = null;
				bek1007StudyDescriptionView.htmlFile = null;
				bek1007StudyDescriptionView.changePartName(Translator
						.trans("bek1007.archive.info"));
				bek1007StudyDescriptionView.init();
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static File getBlankHtmlFile() {
		return new File(new File(".").getAbsolutePath() + File.separator
				+ "resources" + File.separator + "bek1007" + File.separator
				+ "blank.html");
	}

	public static void openBek1007ContextInformationView(String filePath,
			String partName) {
		Bek1007ContextDocumentationView bek1007ContextInformationView = null;
		try {
			PlatformUI.getWorkbench().showPerspective(Bek1007Perspective.ID,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			bek1007ContextInformationView = (Bek1007ContextDocumentationView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(Bek1007ContextDocumentationView.ID);

			bek1007ContextInformationView.contextDocumentationDoc = ModelStore
					.getInstance().getContextInformation(filePath);

			// populate with data
			bek1007ContextInformationView.contextDocumentationFile = ModelStore
					.getInstance().getContextDocumentationIndexFile(filePath);
			bek1007ContextInformationView.changePartName(partName);
			bek1007ContextInformationView.init();
		} catch (XmlException | WorkbenchException | IOException e) {
			Editor.showError(e, "NA");
		}
	}

	public static void closeBek1007ContextInformationView(String partName) {
		try {
			Bek1007ContextDocumentationView bek1007ContextInformationView = (Bek1007ContextDocumentationView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(Bek1007ContextDocumentationView.ID);
			if (partName.equals(bek1007ContextInformationView.getPartName())) {
				bek1007ContextInformationView.contextDocumentationFile = null;
				bek1007ContextInformationView.contextDocumentationDoc = null;
				bek1007ContextInformationView.changePartName(Translator
						.trans("bek1007.document.info"));
				bek1007ContextInformationView.htmlFile = null;
				bek1007ContextInformationView.init();
			}
		} catch (WorkbenchException e) {
			Editor.showError(e, "NA");
		}
	}

	public static void openTableRelationView(String bek1007Id, String partName) {
		try {
			DotUtil dotUtil = new DotUtil();
			File pngFile = dotUtil.createDotFile(bek1007Id);
			PlatformUI.getWorkbench().showPerspective(Bek1007Perspective.ID,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			TableRelationView tableRelationView = (TableRelationView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(TableRelationView.ID);
			tableRelationView.imageFile = pngFile;
			tableRelationView.changePartName(partName);
			tableRelationView.init();
		} catch (WorkbenchException | IOException | InterruptedException e) {
			Editor.showError(e, TableRelationView.ID);
		}
	}

	public static void closeTableRelationView(String partName) {
		try {
			TableRelationView tableRelationView = (TableRelationView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(TableRelationView.ID);
			tableRelationView.imageFile = null;
			tableRelationView.changePartName(Translator
					.trans("bek1007.tablerelation.info"));
			tableRelationView.init();
		} catch (PartInitException e) {
			Editor.showError(e, TableRelationView.ID);
		}
	}

	public static void closeTableView(String partName) {
		TableView tableView;
		try {
			tableView = (TableView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.showView(TableView.ID);
			String tablePartName = ModelStore.getInstance()
					.getSiardNameByTable(tableView.tableName);
			if (partName.equals(tablePartName)) {
				tableView.columnItems = new ArrayList<ColumnType>();
				tableView.columnTableViewer.refresh();
				tableView.changePartName("");
				tableView.foreignkeyItems = new ArrayList<ForeignKeyType>();
				tableView.foreignkeyTableViewer.refresh();
				tableView.descriptionStyledText.setText("");
				tableView.primarykeyText.setText("");
				tableView.rowText.setText("");
				tableView.changePartName(Translator
						.trans("bek1007.tableview.info"));
			}
		} catch (PartInitException e) {
			Editor.showError(e, TableRelationView.ID);
		}
	}
}
