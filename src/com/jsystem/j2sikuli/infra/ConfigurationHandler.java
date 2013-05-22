package com.jsystem.j2sikuli.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigurationHandler {
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private final File configFile;
	private Properties prop;

	public ConfigurationHandler(File configFile) {
		super();
		this.configFile = configFile;
		readConfigutation();

	}

	private void readConfigutation() {
		if (!configFile.exists()) {
			log.info("Configuration file " + configFile.getName() + " is not exist");
			return;
		}
		FileInputStream fis = null;
		prop = new Properties();
		try {
			fis = new FileInputStream(configFile);
			prop.load(fis);
		} catch (IOException e) {
			log.warning("Failed to read configuration file");
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				log.warning("Failed to close file input stream");
			}
		}
	}
	
	public boolean exists(String key){
		if (null == prop){
			return false;
		}
		return (null != prop.getProperty(key));
	}

	public String getString(String key) {
		if (null == prop) {
			return null;
		}
		return prop.getProperty(key);
	}

	public int getInt(String key) {
		if (null == prop) {
			return 0;
		}
		return Integer.parseInt(prop.getProperty(key));
	}

	public boolean getBoolean(String key) {
		if (null == prop) {
			return false;
		}
		return Boolean.parseBoolean(prop.getProperty(key));

	}
}
