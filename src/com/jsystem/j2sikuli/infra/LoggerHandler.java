package com.jsystem.j2sikuli.infra;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class LoggerHandler {
	private static final String LOG_CONFIG_NAME = "log.config";

	public static final String LOG_FILE_NAME = "sikuliAgent.log";

	public static void initLogger() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(LOG_CONFIG_NAME);
			LogManager.getLogManager().readConfiguration(fis);
		} catch (Exception e) {
			String config = "handlers=java.util.logging.FileHandler \n" + ".level=INFO\n"
					+ "java.util.logging.FileHandler.limit=10000000\n" + "java.util.logging.FileHandler.count=4\n"
					+ "java.util.logging.FileHandler.append=true\n"
					+ "java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter\n"
					+ "java.util.logging.FileHandler.pattern=" + LOG_FILE_NAME + "\n"
					+ "java.util.logging.FileHandler.level=ALL\n" + "java.util.logging.ConsoleHandler.level=ALL\n";

			try {
				LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(config.getBytes()));
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}

		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
