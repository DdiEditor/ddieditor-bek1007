package dk.dda.ddieditor.bek1007.model;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

import dk.sa.bek1007.context.ContextDocumentationIndexDocument;
import dk.sa.bek1007.file.FileIndexDocument;
import dk.sa.bek1007.file.FileIndexType.F;
import dk.sa.bek1007.siardk.ForeignKeysType;
import dk.sa.bek1007.siardk.TableType;

public class ModelStore {
	private static ModelStore instance;
	private Map<String, SiardModel> loadedSiardk = new HashMap<String, SiardModel>();

	public static synchronized ModelStore getInstance() {
		if (instance == null) {
			instance = new ModelStore();
		}
		return instance;
	}

	public Map<String, SiardModel> getLoadedSiardk() {
		return loadedSiardk;
	}

	public void addSiardk(String id, SiardModel siardkModel) {
		this.loadedSiardk.put(id, siardkModel);
	}

	public SiardModel getSiardk(String id) {
		return this.loadedSiardk.get(id);
	}

	public void removeSiardk(String id) {
		loadedSiardk.remove(id);
	}

	public TableType getTableByName(String name) {
		for (Entry<String, SiardModel> entry : loadedSiardk.entrySet()) {
			for (TableType table : entry.getValue().getTables()) {
				if (table.getName().equals(name)) {
					return table;
				}
			}
		}
		return null;
	}

	public ForeignKeysType getForeignKeysTypeByTableName(String name) {
		for (SiardModel siardModel : loadedSiardk.values()) {
			for (String tableName : siardModel.foreignKeyMap.keySet()) {
				if (tableName.equals(name)) {
					return siardModel.foreignKeyMap.get(tableName);
				}
			}
		}
		return null;
	}

	public String getSiardNameByTable(String tableName) {
		for (Entry<String, SiardModel> entry : loadedSiardk.entrySet()) {
			for (TableType table : entry.getValue().getTables()) {
				if (table.getName().equals(tableName)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public String getMd5ForFile(String bek1007BasePath, String tableFolderName)
			throws XmlException, IOException {
		FileIndexDocument fileIndexDoc = FileIndexDocument.Factory
				.parse(new File(bek1007BasePath + File.separator + "Indices"
						+ File.separator + "fileIndex.xml"));

		StringBuilder query = new StringBuilder();
		query.append("declare namespace sa= 'http://www.sa.dk/xmlns/diark/1.0';");
		query.append(" for $x in $this/*/sa:f");
		query.append(" where $x/sa:foN/text()='%1$s\\Tables\\%2$s'");
		query.append(" and $x/sa:fiN/text()='%2$s.xml'");
		query.append(" return $x/sa:md5");

		Formatter formatter = new Formatter();
		formatter.format(query.toString(), new File(bek1007BasePath).getName(),
				tableFolderName);
		XmlObject[] result = fileIndexDoc.execQuery(formatter.toString());

		if (result.length > 0) {
			return XmlBeansUtil.getTextOnMixedElement(result[0]);
		}
		return null;
	}

	public ContextDocumentationIndexDocument getContextInformation(
			String basePath) throws XmlException, IOException {
		return ContextDocumentationIndexDocument.Factory
				.parse(getContextDocumentationIndexFile(basePath));
	}

	public File getContextDocumentationIndexFile(String basePath) {
		return new File(basePath + File.separator + "Indices" + File.separator
				+ "contextDocumentationIndex.xml");
	}
}
