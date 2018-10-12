package org.usfirst.frc.team4564.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class to log all the data values on a robot through devices.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class DeviceLog {
	private static final String COMMA = ",";
	private static final String NEW_LINE_SEPERATOR = "\n";
	private String Path = "/u/Log";
	private String fileName = "";
	private String header = "";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd-kk-mm",Locale.US);
	private Map<String, Supplier<Double>> devices =  new HashMap<String, Supplier<Double>>();
	private boolean firstRun = true;
	File file;
	FileWriter writer = null;
	
	/**
	 * Instantiates a device logger with the file name specified and the columns listed.
	 * file is named "/u/Log" + filename + current date in the format of -year-month number-day-hours in 24 hour format-minutes + ".csv".
	 * @param fileName, name of file to be created.
	 */
	public DeviceLog(String fileName) {
		this.fileName = fileName+ dateFormat.format(new Date())+ ".csv";
		File file = new File(Path, this.fileName); 
			try {
				if (!file.exists() || !file.canWrite()) {
					file.createNewFile();
				}
				writer = new FileWriter(file);
			} catch(IOException e) {
				Common.debug("Could not create: " + fileName + " at " + Path + this.fileName);
				e.printStackTrace();
			}	
	}
	
	/**
	 * Instantiates a device logger with the file name specified and the columns listed.
	 * file name is filename + current date in the format of -year-month number-day-hours in 24 hour format-minutes.csv.
	 * 
	 * @param filePath of the file to be created.
	 * @param filename of the file to be created.
	 */
	public DeviceLog(String filePath, String fileName) {
			this.fileName = fileName+ dateFormat.format(new Date())+ ".csv";
			Path = filePath;
			File file = new File(Path, this.fileName); 
				try {
					if (!file.exists() || !file.canWrite()) {
						file.createNewFile();
					}
					writer = new FileWriter(file);
					
				} catch(IOException e) {
					Common.debug("Could not create: " + fileName + " at " + Path + this.fileName);
					e.printStackTrace();
				}	
	}
	
	/**
	 * Adds an device to supply data to the log.
	 * 
	 * @param name of the device to be created. 
	 * @param data, the function to be logged.
	 */
	public void addDevice(String name, Supplier<Double> data) {
		devices.put(name, data);
	}
	
	/**
	 * Updates the log, should be run at most once a cycle.
	 * 
	 */
	public void update() {
		Object[] keys  = devices.keySet().toArray();
		String input = new String();
		if (firstRun = true) { 
			try {
				for (int i = 0; i < keys.length - 1; i++) {
					header = header + keys[i - 1] + COMMA;
				}
				header = header + keys[keys.length - 1] + NEW_LINE_SEPERATOR;
				writer.append(header);
				firstRun = false;
			} catch(IOException e) {
				Common.debug("Could not create header of " + fileName);
				e.printStackTrace();
			}
		}
		try {
			//int i = 0; i < devices.size() - 1; i++
			for (String length:devices.keySet()) {
				input += devices.get(length) + COMMA;
			}
			input = input.substring(0, input.length()-1)+NEW_LINE_SEPERATOR;
			writer.append(input);
		} catch(IOException e) {
			Common.debug("Could not create new line: "+ input + " in: " + fileName);
			e.printStackTrace();
		}
	}
	
	
}
