package dk.dda.ddieditor.bek1007.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.util.DdiEditorConfig;

/**
 * <table>
 * <row> <c1>a</c1> <c2>b</c2> <c3>c</c3> <c4>d</c4> </row>
 * </table>
 */
public class TableDataUtil {
	static final String TABLE = "t";
	static final String ROW = "r";
	static final String COLUMN = "c";

	private XMLStreamReader getXmlStreamReader(String filePath)
			throws XMLStreamException, MalformedURLException {

		// read file as stream source
		InputStream xslInput = TableDataUtil.this.getClass().getClassLoader()
				.getResourceAsStream(filePath);
		StreamSource source = new StreamSource(xslInput);
		source.setSystemId(new File(filePath).toURI().toURL().toString());

		// create StaX xml stream
		XMLInputFactory factory = XMLInputFactory.newInstance();
		return factory.createXMLStreamReader(source);
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

	public void writeOutToCsv(String xml1007FilePath, String csvOutPath)
			throws XMLStreamException, IOException {
		BufferedWriter writer = createOutFile(csvOutPath);
		// utf-8 bom
		if (DdiEditorConfig.get(DdiEditorConfig.SPSS_IMPORT_CHARSET)
				.toLowerCase().equals("utf-8")) {
			// add utf-8 BOM
			writer.write('\ufeff');
		}

		XMLStreamReader reader = getXmlStreamReader(xml1007FilePath);

		String coute = "\"";
		String newLine = "\n";
		String delimiter = ",";
		String numericSperator = ".";

		StringBuilder row = new StringBuilder();
		String strValue = null;
		boolean isColumn = false;

		// iterate xml stream
		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().indexOf(COLUMN) == 0) {
					isColumn = true;
				} else if (reader.getLocalName().indexOf(ROW) == 0) {
					// start a row
					row.delete(0, row.length());
					isColumn = false;
				} else {
					isColumn = false;
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				if (isColumn) {
					// apply csv encoding rules
					strValue = reader.getText().trim();

					// recode
					try {
						Float.parseFloat(strValue);
						// TODO use index instead
						// int test = strValue.indexOf(numericSperator);
						strValue = strValue.replace(numericSperator, delimiter);
					} catch (NumberFormatException e) {
						// do nothing
					}

					if (strValue.contains(delimiter)
							|| strValue.contains(coute)
							|| strValue.contains(newLine)) {
						row.append(coute);
						row.append(strValue);
						row.append(coute);
					} else {
						row.append(strValue);
					}

					// apply csv delimiter
					row.append(delimiter);
					isColumn = false;
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (reader.getLocalName().indexOf(ROW) == 0) {
					// end row
					row.delete(row.length() - 1, row.length());
					writer.write(row.toString());
					writer.newLine();
				}
				break;

			// case XMLStreamConstants.START_DOCUMENT:
			// break;
			}
		}
		writer.close();
	}

	public List<ValueLabel> extractKeyValuePair(String filePath,
			String keyColumn, boolean isAlfaNumericKey)
			throws XMLStreamException, MalformedURLException {
		List<ValueLabel> result = new ArrayList<ValueLabel>();

		XMLStreamReader reader = getXmlStreamReader(filePath);

		// iterate xml stream
		String column = null;
		ValueLabel valueLabel = null;
		while (reader.hasNext()) {

			int event = reader.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().indexOf(COLUMN) == 0) {
					column = reader.getLocalName();
				} else if (reader.getLocalName().indexOf(ROW) == 0) {
					// start a row
					valueLabel = new ValueLabel();
					column = null;
				} else {
					column = null;
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				if (column != null) {
					// add column content
					if (column.equals(keyColumn)) {
						if (isAlfaNumericKey) {
							valueLabel.setKey("'" + reader.getText().trim()
									+ "'");
						} else {
							valueLabel.setKey(reader.getText().trim());
						}
					} else {
						valueLabel.setValue(reader.getText().trim());
					}

					column = null;
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (reader.getLocalName().indexOf(ROW) == 0) {
					// end row
					result.add(valueLabel);
				}
				break;
			}
		}

		return result;
	}

	public static void main(String[] args) {
		TableDataUtil data = new TableDataUtil();
		try {
			data.writeOutToCsv(
					"/home/ddajvj/Documents/DDA/corranhorn/test/siardk/AVID.SA.18001.1/Tables/table3/table3.xml",
					"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
