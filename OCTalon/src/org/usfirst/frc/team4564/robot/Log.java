package org.usfirst.frc.team4564.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	
	private static final String COMMA = ",";
	private static final String NEW_LINE_SEPERATOR = "\n";
	private static final String PATH = "/var/datadump/";
	private String filename = "";
	private String header = "";
	private String input = "";
	File file;
	FileWriter writer = null;
	
	public Log(String filename, String[] columns) {
			this.filename = filename;
			File file = new File(PATH, filename); 
				try {
					if (!file.exists() || !file.canWrite()) {
						file.createNewFile();
					}
					writer = new FileWriter(file);
					if (file.length() == 0) {
						for (int i = 0 ; i <  columns.length-1; i++) {
							header = header+columns[i]+COMMA;
						}
						header = header+columns[columns.length-1]+NEW_LINE_SEPERATOR;
						writer.append(header);
					}
					
				} catch(IOException e) {
					Common.debug("Could not create: " + filename + "at " + PATH + filename);
					e.printStackTrace();
				}	
	}
	
	public void log(String[] data) {
		try {
			for (int i = 0; i < data.length-1; i++) {
				input = input + data[i] + COMMA;
			}
			input = input+data[data.length - 1]+NEW_LINE_SEPERATOR;
			writer.append(input);
		}
		catch(IOException e) {
			Common.debug("Could not add " + input + "to:" + file);
			e.printStackTrace();
		}
	}
	
}
