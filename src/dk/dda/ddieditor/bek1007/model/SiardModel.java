package dk.dda.ddieditor.bek1007.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.sa.bek1007.siardk.ForeignKeysType;
import dk.sa.bek1007.siardk.SiardDiarkDocument;
import dk.sa.bek1007.siardk.SiardDiarkDocument.SiardDiark;
import dk.sa.bek1007.siardk.TableType;

public class SiardModel {
	String id;
	String path;
	SiardDiarkDocument siardkDoc;
	public Map<String, ForeignKeysType> foreignKeyMap = new HashMap<String, ForeignKeysType>();

	// xmlbeans
	public SiardDiarkDocument getSiardkDoc() {
		return siardkDoc;
	}

	public void setSiardkDoc(SiardDiarkDocument siardkDoc) {
		for (TableType table : siardkDoc.getSiardDiark().getTables()
				.getTableList()) {
			foreignKeyMap.put(table.getName(), (ForeignKeysType) table
					.getForeignKeys() == null ? null : (ForeignKeysType) table
					.getForeignKeys().copy());
		}
		this.siardkDoc = siardkDoc;
	}

	private SiardDiark getType() {
		return siardkDoc.getSiardDiark();
	}

	// identification
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// xml model
	public String getVersion() {
		return getType().getVersion();
	}

	public void setVersion(String version) {
		getType().setVersion(version);
	}

	public String getDatabaseProduct() {
		return getType().getDatabaseProduct();
	}

	public void setDatabaseProduct(String databaseProduct) {
		getType().setDatabaseProduct(databaseProduct);
	}

	public String getDbName() {
		return getType().getDbName();
	}

	public void setDbName(String dbName) {
		getType().setDbName(dbName);
	}

	public List<TableType> getTables() {
		return getType().getTables().getTableList();
	}

	public void setTable(TableType table) {
		getType().getTables().getTableList().add(table);
	}
}
