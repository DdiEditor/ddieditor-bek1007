package dk.dda.ddieditor.bek1007.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.runtime.Platform;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.sa.bek1007.siardk.ColumnType;
import dk.sa.bek1007.siardk.ForeignKeyType;
import dk.sa.bek1007.siardk.ForeignKeysType;
import dk.sa.bek1007.siardk.ReferenceType;
import dk.sa.bek1007.siardk.TableType;

public class ExportAsSpssSyntax {
	String baseBek1007Path = null;
	TableDataUtil tableDataUtil = new TableDataUtil();
	BufferedWriter writer = null;

	Map<String, String[]> spssDataTypeToSql99DataType = new HashMap<String, String[]>();

	//
	// SPSS
	//
	StringBuffer spssSyntaxTxt = new StringBuffer();

	// VARIABLE
	List<String> variables = new ArrayList<String>();

	// VARIABLE LABELS
	List<String> variableLabels = new ArrayList<String>();

	// VALUE LABELS
	List<String> valueLabels = new ArrayList<String>();

	// MISSING VALUES
	List<String> missingValues = new ArrayList<String>();

	// VARIABLE LEVEL
	List<String> variableLevels = new ArrayList<String>();

	public ExportAsSpssSyntax() {
		// string -plus number character
		spssDataTypeToSql99DataType.put("A", new String[] { "character",
				"char", "character varying", "char varying", "varchar",
				"national character", "national char", "nchar",
				"national character varying", "national char varying",
				"nchar varying" });

		// integer
		spssDataTypeToSql99DataType.put("F8 ", new String[] { "integer", "int",
				"smallint" });

		// numeric -plus: decimal digits and fractional digits
		spssDataTypeToSql99DataType.put("F8.2", new String[] { "numeric",
				"decimal", "dec", "float" });

		// undefined number
		// "real", "double precision"

		// date
		spssDataTypeToSql99DataType.put("SDATE10", new String[] { "date" });
		// plus conversion from yyyy-mm-dd to yyyy/mm/dd

		// time datetime timestamp
		spssDataTypeToSql99DataType.put("A40", new String[] { "time",
				"datetime", "timestamp" });

		// undefined time
		// "interval"
	}

	public String convertSql99ToSpssDataType(String sql99)
			throws DDIFtpException {
		String sql99Strip = sql99;

		int index = sql99.indexOf("(");
		String charNo = null;
		if (index > -1) {
			sql99Strip = sql99.substring(0, index);
			charNo = sql99.substring(index + 1, sql99.indexOf(")"));
		}
		sql99Strip = sql99Strip.toLowerCase();

		String spssType = null;
		for (Entry<String, String[]> entry : spssDataTypeToSql99DataType
				.entrySet()) {
			for (int i = 0; i < entry.getValue().length; i++) {
				if (entry.getValue()[i].equals(sql99Strip)) {
					spssType = entry.getKey();
					break;
				}
			}
			if (spssType != null) {
				break;
			}
		}

		if (spssType == null) {
			throw new DDIFtpException(
					Translator.trans("bek1007.spssdatatypedefine.error"),
					new String[] { sql99Strip }, new Throwable());
		}
		if (spssType.equals("F")) {
			if (charNo == null) {
				// charNo = ".0"; // commented out: 20131001 jvj
				charNo = "";
			}
			spssType += charNo;
		}
		if (spssType.equals("A")) {
			int tmp = Integer.parseInt(charNo);
			spssType += "" + (tmp + 3);
		}
		return spssType;
	}

	private void setBaseBek1007Path(String bek1007Id) {
		baseBek1007Path = ModelStore.getInstance().getSiardk(bek1007Id)
				.getPath();
	}

	public void export(String exportPath, String bek1007Id, TableType table,
			boolean checkMd5) throws DDIFtpException, XMLStreamException,
			IOException, NoSuchAlgorithmException, XmlException {
		setBaseBek1007Path(bek1007Id);
		String tableDataPath = baseBek1007Path + File.separator + "Tables"
				+ File.separator + table.getFolder() + File.separator
				+ table.getFolder() + ".xml";

		// check md5 on data file
		if (checkMd5) {
			String md5 = CheckTableData.byteArrayToString(
					CheckTableData.md5file(new File(tableDataPath)))
					.toLowerCase();
			String md51007 = ModelStore.getInstance()
					.getMd5ForFile(baseBek1007Path, table.getFolder())
					.toLowerCase();
			if (!md5.equals(md51007.toLowerCase())) {
				throw new DDIFtpException(
						Translator.trans("bek1007.md5.notequal"), new String[] {
								tableDataPath, md5, md51007 }, new Throwable());
			}
		}

		StringBuilder strTmp = new StringBuilder();

		// variables - V1 F5.0
		for (ColumnType column : table.getColumns().getColumnList()) {
			strTmp.append(column.getName());
			strTmp.append(" ");
			strTmp.append(convertSql99ToSpssDataType(column.getType()));
			variables.add(strTmp.toString());
			strTmp.delete(0, strTmp.length());
		}

		// variable labels - V1 "DDA STUDY NUMBER"
		for (ColumnType column : table.getColumns().getColumnList()) {
			strTmp.append(column.getName());
			strTmp.append(" ");
			strTmp.append("\"");
			strTmp.append(column.getDescription());
			strTmp.append("\"");
			variableLabels.add(strTmp.toString());
			strTmp.delete(0, strTmp.length());
		}

		// value labels
		// V9
		// 0 "Not mentioned"
		// 1 "Mentioned"
		// 9 "Unknown"
		ForeignKeysType foreignKeys = ModelStore.getInstance()
				.getForeignKeysTypeByTableName(table.getName());

		if (foreignKeys != null) {
			String forignColumn = null;
			String referenced = null; // aka variable name
			for (ForeignKeyType foreignKey : foreignKeys.getForeignKeyList()) {
				for (ReferenceType ref : foreignKey.getReferenceList()) {
					forignColumn = ref.getReferenced();
					referenced = ref.getColumn();
					strTmp.append(referenced);
					strTmp.append("\n");
				}

				List<ValueLabel> valueLabelVos = defineValueLabels(
						foreignKey.getReferencedTable(), forignColumn, strTmp);
				for (Iterator<ValueLabel> iterator = valueLabelVos.iterator(); iterator
						.hasNext();) {
					ValueLabel valueLabel = iterator.next();
					strTmp.append(valueLabel.getKey());
					strTmp.append(" ");
					strTmp.append("\"");
					strTmp.append(valueLabel.getValue());
					strTmp.append("\"\n");
				}

				valueLabels.add(strTmp.toString());
				strTmp.delete(0, strTmp.length());
			}
		}

		// write sps file
		String csvPath = exportPath + File.separator + bek1007Id.replaceAll("\\.", "-") + "-"
				+ table.getName() + ".csv";
		csvPath = csvPath.replaceAll("\\\\", "\\");
		writeOutSpssSyntax(exportPath, bek1007Id.replaceAll("\\.", "-") + "-" + table.getName()
				+ ".sps", csvPath);

		// write csv file
		tableDataUtil.writeOutToCsv(tableDataPath, csvPath);
	}

	private List<ValueLabel> defineValueLabels(String tableName,
			String forignColumn, StringBuilder sb)
			throws MalformedURLException, XMLStreamException, DDIFtpException {
		TableType table = ModelStore.getInstance().getTableByName(tableName);
		boolean isAlfaNumericKey = false;
		// key columnId
		String columnId = null;
		for (ColumnType column : table.getColumns().getColumnList()) {
			if (column.getName().equals(forignColumn)) {
				columnId = column.getColumnID();
				isAlfaNumericKey = convertSql99ToSpssDataType(column.getType())
						.startsWith("A");
				break;
			}
		}

		// table data xml path
		String tableDataPath = baseBek1007Path + File.separator + "Tables"
				+ File.separator + table.getFolder() + File.separator
				+ table.getFolder() + ".xml";

		return tableDataUtil.extractKeyValuePair(tableDataPath, columnId,
				isAlfaNumericKey);
	}

	void clearStringBuffer(StringBuffer buf) {
		buf.delete(0, buf.length());
	}

	/**
	 * Write SPSS syntax file to disk
	 * 
	 * @throws IOException
	 */
	void writeOutSpssSyntax(String path, String fileName, String csvPath)
			throws IOException {
		try {
			// init output file writer
			writer = createOutFile(path + File.separator + fileName);

			// utf-8 bom
			if (DdiEditorConfig.get(DdiEditorConfig.SPSS_IMPORT_CHARSET)
					.toLowerCase().equals("utf-8")) {
				// add utf-8 BOM
				writer.write('\ufeff');
			}

			// header
			writer.write("*.");			
			writer.newLine();
			writer.write("* "
					+ Translator.formatIso8601DateTime(System
							.currentTimeMillis()) + ".");
			writer.newLine();
			writer.write("* SPSS syntax file created by DdiEditor-Bek.1007-"
					+ Platform.getBundle("ddieditor-bek1007").getHeaders()
							.get("Bundle-Version") + ".");			
			writer.newLine();
			writer.write("*.");			
			writer.newLine();

			// GET DATA
			// TODO other encoding ?
			writer.write("SET UNICODE ON DECIMAL COMMA OLANG=ENGLISH.");
			writer.newLine();
			writer.write("GET DATA");
			writer.newLine();
			writer.write(" /TYPE=TXT");
			writer.newLine();
			writer.write(" /FILE=\"");
			writer.write(csvPath);
			writer.write("\"");
			writer.newLine();
			writer.write(" /DELCASE=LINE");
			writer.newLine();
			// TODO read setting from prefs and this instead
			writer.write(" /DELIMITERS=\",\"");
			writer.newLine();
			// SPSS: The text qualifier appears at both the beginning and end of
			// the value, enclosing the entire value.
			writer.write(" /QUALIFIER='\"'");
			writer.newLine();
			writer.write(" /ARRANGEMENT=DELIMITED");
			writer.newLine();
			writer.write(" /FIRSTCASE=1");
			writer.newLine();
			writer.write(" /IMPORTCASE=ALL");
			writer.newLine();
			writer.write(" /VARIABLES=");
			// variables
			for (String variable : variables) {
				writer.newLine();
				writer.write(" " + variable);
			}
			writer.write(".");
			writer.newLine();
			writer.write("CACHE.");
			writer.newLine();
			writer.write("EXECUTE.");
			writer.flush();

			// VARIABLE LABELS
			writeOutCommand("VARIABLE LABELS", false, variableLabels);
			writer.newLine();
			writer.write("EXECUTE.");
			writer.flush();

			// VALUE LABELS
			writeOutCommand("VALUE LABELS", true, valueLabels);
			writer.newLine();
			writer.write("EXECUTE.");

			// TODO
			// MISSING VALUES
			// writeOutCommand("MISSING VALUES", true, missingValues);
			// writer.newLine();
			// writer.write("EXECUTE.");
			// writer.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	/**
	 * Create output file
	 * 
	 * @param fileName
	 *            name of file
	 * @return buffered writer
	 * @throws IOException
	 */
	BufferedWriter createOutFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			if (!f.createNewFile()) {
				// log.debug("File '" + fileName + "' overwritten");
			}
		}
		return new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(f),
				DdiEditorConfig.get(DdiEditorConfig.SPSS_IMPORT_CHARSET)));
	}

	/**
	 * Write out a SPSS command with variable list to file on disk
	 * 
	 * @param commandName
	 *            name of SPSS command
	 * @param addForwardSlash
	 *            option -add forward slash for each variable
	 * @param varList
	 *            list of variables with parameters
	 * @throws IOException
	 */
	void writeOutCommand(String commandName, boolean addForwardSlash,
			List<String> varList) throws IOException {
		String forwardSlash = "/";

		writer.newLine();
		writer.newLine();
		writer.write(commandName);

		boolean notFirst = false;
		for (Iterator<String> iterator = varList.iterator(); iterator.hasNext();) {
			String syntaxTxt = iterator.next();
			writer.newLine();
			if (addForwardSlash && notFirst) {
				writer.write(forwardSlash);
			}
			notFirst = true;

			// trim last value
			writer.write(syntaxTxt.toString().trim());
		}
		writer.write(".");
	}
}
