package dk.dda.ddieditor.bek1007.view;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.part.ViewPart;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.sa.bek1007.siardk.ColumnType;
import dk.sa.bek1007.siardk.ForeignKeyType;

public class TableView extends ViewPart implements IPropertyListener {
	public static final String ID = "dk.dda.ddieditor.bek1007.view.TableView";

	public String tableName;
	public Composite parent;
	public String description;
	public StyledText descriptionStyledText;
	public String primaryKey;
	public Text primarykeyText;
	public Text rowText;

	public TableViewer columnTableViewer;
	ColumnLabelProvider columnLabelProvider;
	public List<ColumnType> columnItems = new ArrayList<ColumnType>();

	public TableViewer foreignkeyTableViewer;
	ForeignkeyLabelProvider foreignkeyLabelProvider;
	public List<ForeignKeyType> foreignkeyItems = new ArrayList<ForeignKeyType>();

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
				Translator.trans("bek1007.tableview.table"));
		infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));
		descriptionStyledText = editor.createTextAreaInput(infoGroup,
				Translator.trans("bek1007.tableview.description"), "", false);
		primarykeyText = editor.createTextInput(infoGroup,
				Translator.trans("bek1007.tableview.primarykey"), "", false);
		rowText = editor.createTextInput(infoGroup,
				Translator.trans("bek1007.tableview.rows"), "", false);
		
		// columns
		Group columnGroup = editor.createGroup(this.parent,
				Translator.trans("bek1007.tableview.columns"));
		columnGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		columnTableViewer = new TableViewer(columnGroup, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		columnTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		columnTableViewer.setContentProvider(new ColumnContentProvider());
		columnLabelProvider = new ColumnLabelProvider();
		columnLabelProvider.createColumns(columnTableViewer);
		columnTableViewer.setLabelProvider(columnLabelProvider);
		columnTableViewer.setInput(columnItems);

		// foreign keys
		Group foreignGroup = editor.createGroup(this.parent,
				Translator.trans("bek1007.tableview.foreignkeys"));
		foreignGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		foreignkeyTableViewer = new TableViewer(foreignGroup, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		foreignkeyTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		foreignkeyTableViewer
				.setContentProvider(new ForeignkeyContentProvider());
		foreignkeyLabelProvider = new ForeignkeyLabelProvider();
		foreignkeyLabelProvider.createColumns(foreignkeyTableViewer);
		foreignkeyTableViewer.setLabelProvider(foreignkeyLabelProvider);
		foreignkeyTableViewer.setInput(foreignkeyItems);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void changePartName(String newName) {
		setPartName(newName);
	}

	//
	// Column
	//
	class ColumnLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		/**
		 * Creates table columns
		 * 
		 * @param viewer
		 *            to be created on
		 */
		public void createColumns(final TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					Translator.trans("bek1007.tableview.columnItemName"),
					Translator.trans("bek1007.tableview.columnItemDescription"),
					Translator.trans("bek1007.tableview.columnItemType"),
					Translator.trans("bek1007.tableview.columnItemOrgType") };

			int[] widths = { 100, 410, 225, 225 };
			int[] style = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
			}

			Editor.resizeTableFont(table);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			ColumnType column = (ColumnType) element;
			LabelType label = null;

			switch (columnIndex) {
			// 0=name, 1=description, 2=type, 3=org type
			case 0:
				return column.getName();
			case 1:
				return column.getDescription();
			case 2:
				return column.getType();
			case 3:
				return column.getTypeOriginal();
			default:
				DDIFtpException e = new DDIFtpException(
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$

				Editor.showError(e, ID);
			}
			return "";
		}
	}

	public class ColumnContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			return columnItems.toArray();
		}

		public List<ColumnType> getItems() {
			return columnItems;
		}

		@Override
		public void dispose() {
			// noting to do
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// noting to do
		}
	}

	class ForeignkeyLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		/**
		 * Creates table columns
		 * 
		 * @param viewer
		 *            to be created on
		 */
		public void createColumns(final TableViewer viewer) {
			Table table = viewer.getTable();

			// 0=name, 1=reftable, 2=foreign column, 3=column
			String[] titles = {
					Translator.trans("bek1007.tableview.foreignkeyItemName"),
					Translator
							.trans("bek1007.tableview.foreignkeyReferencedTable"),
					Translator
							.trans("bek1007.tableview.foreignkeyForeignColumn"),
					Translator.trans("bek1007.tableview.foreignkeyColumn") };

			int[] widths = { 150, 225, 225, 225 };
			int[] style = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
			}

			Editor.resizeTableFont(table);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			ForeignKeyType column = (ForeignKeyType) element;
			LabelType label = null;

			switch (columnIndex) {
			// 0=name, 1=reftable, 2=foreign column, 3=column
			case 0:
				return column.getName();
			case 1:
				return column.getReferencedTable();
			case 2:
				return column.getReferenceArray(0).getReferenced();
			case 3:
				return column.getReferenceArray(0).getColumn();
			default:
				DDIFtpException e = new DDIFtpException(
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$

				Editor.showError(e, ID);
			}
			return "";
		}
	}

	public class ForeignkeyContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			if (getPartName().equals("")) {
				return foreignkeyItems.toArray();
			}
			if (ModelStore.getInstance().getTableByName(tableName) == null
					|| ModelStore.getInstance().getTableByName(tableName)
							.getForeignKeys() == null) {
				return foreignkeyItems.toArray();
			} else {
				return ModelStore.getInstance()
						.getForeignKeysTypeByTableName(tableName)
						.getForeignKeyList().toArray();
			} 
		}

		public List<ForeignKeyType> getItems() {
			return foreignkeyItems;
		}

		@Override
		public void dispose() {
			// noting to do
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// noting to do
		}
	}
}
