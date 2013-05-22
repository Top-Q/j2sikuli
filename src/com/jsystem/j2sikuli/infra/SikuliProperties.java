package com.jsystem.j2sikuli.infra;



/**
 * 
 * @author itai_a
 *
 */
public enum SikuliProperties {
	SIKULI_FILE_NAME("sikuli.properties"),
	DEBUG_MODE_KEY("debugMode"),
	AGENT_PORT_KEY("port"),
	SERVER_UP_ON_INIT_KEY("serverUpOnInit"),
	IMAGES_FOLDER_KEY("images.folder");

	private String key = null;

	SikuliProperties(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
