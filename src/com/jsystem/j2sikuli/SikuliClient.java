package com.jsystem.j2sikuli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.util.ClientFactory;

/**
 * Client for the Sikuli Agent. Enables to perform sikuli and Java robot
 * commands on remote machine. Make sure that the Sikuli agent and Sikuli X are
 * installed on the remote machine.
 * 
 * @author Agmon
 * 
 */
public class SikuliClient extends SystemObjectImpl implements SikuliI {

	private SikuliI sikuli;

	private String host = "127.0.0.1";

	private int port = 8889;

	@Override
	public void init() throws Exception {
		super.init();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(String.format("http://%s:%d/XmlRpcServlet", host, port)));

		XmlRpcClient client = new XmlRpcClient();
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(config);

		ClientFactory factory = new ClientFactory(client);
		sikuli = (SikuliI) factory.newInstance(SikuliI.class);
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean click(String imageName) throws SikuliAgentException {
		return sikuli.click(imageName);
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean doubleClick(int x, int y) throws SikuliAgentException {
		return sikuli.doubleClick(x, y);
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean click(int x, int y) throws SikuliAgentException {
		return sikuli.click(x, y);
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean exist(String imageName) throws SikuliAgentException {
		return sikuli.exist(imageName);
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean addImage(String imageFileName, byte[] content) {
		return sikuli.addImage(imageFileName, content);
	}

	/**
	 * Add image to the agent image folder. This image can be addressed in later
	 * calls.
	 * 
	 * @param imageFile
	 *            PNG image file
	 * @throws SikuliAgentException
	 *             If the file to add is not exist or failed to read the file or
	 *             failed to add file to agent
	 */
	public void addImage(final File imageFile) throws SikuliAgentException {
		if (imageFile == null || !imageFile.exists()) {
			throw new SikuliAgentException(imageFile);
		}
		byte[] content = null;
		try {
			content = FileUtils.readBytes(imageFile);
		} catch (Exception e) {
			report.report("Failed to read file " + imageFile.getName(), false);
			throw new SikuliAgentException("Failed to read file " + imageFile.getName());
		}
		if (!addImage(imageFile.getName(), content)) {
			throw new SikuliAgentException("Failed to add file to agent");
		}

	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public byte[] captureScreenshot() throws SikuliAgentException {
		return sikuli.captureScreenshot();
	}

	/**
	 * @throws SikuliAgentException
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean type(String imageFileName, String text) throws SikuliAgentException {
		return sikuli.type(imageFileName, text);
	}

	/**
	 * Capture the current remote machine to file
	 * 
	 * @return screen shot png file
	 * @throws SikuliAgentException
	 */
	public File captureScreenshotFile() throws SikuliAgentException {
		byte[] content = captureScreenshot();
		File screenShotFile;
		try {
			screenShotFile = File.createTempFile("SikuliAgentScreenShot", ".png");
		} catch (IOException e1) {
			throw new SikuliAgentException("Failed to create sikuli screen shot file");
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(screenShotFile);
			fos.write(content);
		} catch (Exception e) {
			report.report("Failed to write screen shot to file", e);
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return screenShotFile;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
