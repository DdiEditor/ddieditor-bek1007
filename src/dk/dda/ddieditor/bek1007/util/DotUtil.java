package dk.dda.ddieditor.bek1007.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.util.DdiEditorConfig;

import dk.dda.ddieditor.bek1007.model.ModelStore;
import dk.sa.bek1007.siardk.ColumnType;
import dk.sa.bek1007.siardk.ForeignKeyType;
import dk.sa.bek1007.siardk.ForeignKeysType;
import dk.sa.bek1007.siardk.ReferenceType;
import dk.sa.bek1007.siardk.TableType;

public class DotUtil {
	public File createDotFile(String bek1007Id) throws IOException,
			InterruptedException {
		// out file
		File dotFile = File.createTempFile("dot", ".dot");
		BufferedWriter writer = createOutFile(dotFile.getAbsolutePath());

		// start graph
		writer.write("digraph {");
		writer.newLine();
		writer.write("rankdir = LR;");

		// define nodes
		writer.newLine();
		writer.write("node[shape=record];");
		writer.newLine();
		List<TableType> tables = ModelStore.getInstance().getSiardk(bek1007Id)
				.getTables();
		for (TableType table : tables) {
			// id[label="id"];
			writer.write(table.getName());
			writer.write("[label=\"");
			writer.write(table.getName());
			writer.write(" | ");
			for (Iterator<ColumnType> iterator = table.getColumns().getColumnList().iterator(); iterator.hasNext();) {
				ColumnType column =  iterator.next();
				writer.write("<"+column.getName()+"> ");
				writer.write(column.getName());
				if (iterator.hasNext()) {
					writer.write(" | ");
				}
//				writer.write("\\n");
			}
			writer.write("\"");
			writer.write("];");
			writer.newLine();
		}

		// define node relationships
		String column=null;
		String refColumn=null;
		for (TableType table : tables) {
			ForeignKeysType foreignKeys = ModelStore.getInstance()
					.getForeignKeysTypeByTableName(table.getName());
			if (foreignKeys != null) {
				for (ForeignKeyType foreignKey : foreignKeys
						.getForeignKeyList()) {

					 for (ReferenceType ref : foreignKey.getReferenceList()) {
					 column = ref.getColumn();
					 refColumn = ref.getReferenced();
					 }
					
					// university -- StUnDe [label="1", len=3];
					// struct1:f1 -> struct2:f0;
					writer.write(table.getName());
					writer.write(":");
					writer.write(column);
					writer.write("->");
					writer.write(foreignKey.getReferencedTable());
					writer.write(":");
					writer.write(refColumn);
					writer.write(";");
					writer.newLine();
				}
			}
		}
		writer.write("}");
		writer.close();

		// create png file
		File pngFile = File.createTempFile("png", ".png");
		pngFile.deleteOnExit();

		// run dot
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("dot -Tpng " + dotFile.getAbsolutePath()
				+ " -o " + pngFile.getAbsolutePath());
		proc.waitFor();

		// clean up
		//dotFile.delete();

		return pngFile;
	}

	BufferedWriter createOutFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			if (!f.createNewFile()) {
				// log.debug("File '" + fileName + "' overwritten");
			}
		}
		return new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(f),
				DdiEditorConfig.get(DdiEditorConfig.CHARSET_UNICODE)));
	}
}
