package com.jsystem.j2sikuli;

import java.io.File;

public class SikuliAgentException extends Exception {

	public SikuliAgentException(String message) {
		super(message);
	}

	public SikuliAgentException(Exception e) {
		super(e);
	}

	public SikuliAgentException(File notFoundFile) {
		super("File " + notFoundFile.getAbsolutePath() + " was not found");

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
