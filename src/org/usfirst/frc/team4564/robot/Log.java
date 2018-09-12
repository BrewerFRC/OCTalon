package org.usfirst.frc.team4564.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A logging object to create CSV files of data.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Log {
	//usb are /u/
	private static final String COMMA = ",";
	private static final String NEW_LINE_SEPERATOR = "\n";
	private String Path = "/u/Log";
	private String fileName = "";
	private String header = "";
	private String input = "";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd-kk-mm",Locale.US);
	File file;
	FileWriter writer = null;
	
	/**
	 * Instantiates a logger with the file name specified and the columns listed.
	 * file is named "/u/Log" + filename + current date in the format of -year-month number-day-hours in 24 hour format-minutes + ".csv".
	 * 
	 * @param fileName of the file to be created.
	 * @param columns array of columns to be printed
	 */
	public Log(String fileName, String[] columns) {
			this.fileName = fileName+ dateFormat.format(new Date())+ ".csv";
			File file = new File(Path, this.fileName); 
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
					Common.debug("Could not create: " + fileName + " at " + Path + this.fileName);
					e.printStackTrace();
				}	
	}
	
	/**
	 * Instantiates a logger with the file name specified and the columns listed.
	 * file name is filename + current date in the format of -year-month number-day-hours in 24 hour format-minutes.csv.
	 * 
	 * @param filePath of the file to be created.
	 * @param filename of the file to be created.
	 * @param columns array of columns to be printed.
	 */
	public Log(String filePath, String fileName, String[] columns) {
			this.fileName = fileName+ dateFormat.format(new Date())+ ".csv";
			Path = filePath;
			File file = new File(Path, this.fileName); 
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
					Common.debug("Could not create: " + fileName + " at " + Path + this.fileName);
					e.printStackTrace();
				}	
	}
	
	/**
	 * logs data to the CSV file created.
	 * Commas and /n should be avoided as they will break the csv format.
	 * 
	 * @param data in a array to be printed in the same order of columns
	 */
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
