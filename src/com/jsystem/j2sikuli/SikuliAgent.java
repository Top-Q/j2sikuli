package com.jsystem.j2sikuli;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jsystem.utils.FileUtils;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import com.jsystem.j2sikuli.infra.ConfigurationHandler;
import com.jsystem.j2sikuli.infra.LoggerHandler;
import com.jsystem.j2sikuli.infra.SikuliProperties;

/**
 * The agent allows remote operation on Sikuli. In order for the agent to work,
 * make sure all Sikuli dll's required are in the machine.
 * 
 * @author Agmon
 * 
 */
public class SikuliAgent implements SikuliI {

	private static Logger log = Logger.getLogger(SikuliAgent.class.getSimpleName());

	private static int webServicePort = 8889;

	private static boolean serverState;

	private static String imagesFolder = "images";

	private static Screen screen;

	private static boolean serverOnInit = true;

	private static File configFile = new File("sikuli.properties");

	private static WebServer webServer;

	public static void main(String[] args) {
		LoggerHandler.initLogger();
		readConfiguration();

		if (serverOnInit) {
			startServer();
			if (!serverState) {
				log.log(Level.SEVERE, "Failed to start server");
				return;

			}
		}
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		} catch (Exception e) {
			// TODO: handle exception
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		// Schedule a job for the event-dispatching thread:
		// adding TrayIcon.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}

		});
		try {
			screen = new Screen();
		} catch (Throwable t) {
			log.log(Level.SEVERE,
					"Failed to create Sikuli object. Please copy all DLL's from the agent lib folder to windows/System32 and try again",
					t);
			JOptionPane.showConfirmDialog(null,
					"Failed to create Sikuli object. Please check that the Sikuli package is installed",
					"Sikuli Agent Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			stopServer();
			System.exit(1);
		}

	}

	private static void readConfiguration() {
		ConfigurationHandler config = new ConfigurationHandler(configFile);
		if (config.exists(SikuliProperties.IMAGES_FOLDER_KEY.getKey())) {
			imagesFolder = config.getString(SikuliProperties.IMAGES_FOLDER_KEY.getKey());
		}
		if (config.exists(SikuliProperties.AGENT_PORT_KEY.getKey())) {
			webServicePort = config.getInt(SikuliProperties.AGENT_PORT_KEY.getKey());
		}
		if (config.exists(SikuliProperties.SERVER_UP_ON_INIT_KEY.getKey())) {
			serverOnInit = config.getBoolean(SikuliProperties.SERVER_UP_ON_INIT_KEY.getKey());
		}
		log.info("Image folder was set to " + imagesFolder);
		log.info("Web server port was set to " + webServicePort);
		log.info("Server on init was set to " + serverOnInit);

	}

	private static void createAndShowGUI() {
		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			log.severe("SystemTray is not supported\n");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(createImage("images/jsystem_ico.gif", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		final MenuItem aboutItem = new MenuItem("About");
		final MenuItem startAgentItem = new MenuItem("Start Sikuli Agent");
		final MenuItem stopAgentItem = new MenuItem("Stop Sikuli Agent");
		final MenuItem exitItem = new MenuItem("Exit");
		final MenuItem changePortItem = new MenuItem("Setting Sikuli Port");
		final MenuItem changeImagesFolder = new MenuItem("Sikuli Images Folder");
		final MenuItem displayLogFile = new MenuItem("Display Log File");
		// Add components to popup menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(changePortItem);
		popup.add(changeImagesFolder);
		popup.addSeparator();
		popup.add(startAgentItem);
		popup.add(stopAgentItem);
		popup.addSeparator();
		popup.add(displayLogFile);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setToolTip("Sikuli Agent");
		trayIcon.setImageAutoSize(true);
		trayIcon.setPopupMenu(popup);
		final DisplayLogFile displayLog = new DisplayLogFile();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			log.severe("TrayIcon could not be added.\n");
			return;
		}

		trayIcon.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				startAgentItem.setEnabled(!serverState);
				stopAgentItem.setEnabled(serverState);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		startAgentItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				startServer();
			}
		});

		stopAgentItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				stopServer();
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				JOptionPane.showMessageDialog(null, "Sikuli Agent By JSystem And Top-Q");
			}
		});
		changeImagesFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File currentImagesFolder = new File(imagesFolder);
				String newImagesFolder = JOptionPane.showInputDialog("Please Insert Images Folder",
						currentImagesFolder.getAbsolutePath());
				if (null == newImagesFolder) {
					return;
				}
				final File tempFile = new File(newImagesFolder);
				if (tempFile.getAbsolutePath().equals(currentImagesFolder.getAbsolutePath())) {
					return;
				}
				if (!tempFile.exists() || !tempFile.isDirectory()) {
					// TODO: report problem
					return;
				}
				newImagesFolder = imagesFolder;
			}
		});

		displayLogFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					displayLog.reBuild(FileUtils.read(LoggerHandler.LOG_FILE_NAME + ".0"));
					displayLog.actionPerformed(e);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		changePortItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String portAsString = JOptionPane.showInputDialog("Please Insert New Port",
						String.valueOf(webServicePort));
				if (portAsString != null) {
					try {
						int temp = Integer.parseInt(portAsString);
						if (temp > 1000) {
							if (temp != webServicePort) {
								webServicePort = temp;
								stopServer();
								startServer();
							}
						} else {
							log.warning("Port number should be greater then 1000\n");
						}
					} catch (Exception exception) {
						log.severe("Illegal port number\n");
						return;
					}
				}
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				tray.remove(trayIcon);
				stopServer();
				System.exit(0);
			}
		});
	}

	// Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = SikuliAgent.class.getResource(path);

		if (imageURL == null) {
			log.severe("Resource not found: " + path + "\r");
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	private static void startServer() {
		log.info("Starting server");
		serverState = false;
		webServer = new WebServer(webServicePort);
		XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		try {
			phm.addHandler(com.jsystem.j2sikuli.SikuliI.class.getName(), com.jsystem.j2sikuli.SikuliAgent.class);
			xmlRpcServer.setHandlerMapping(phm);
			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setEnabledForExtensions(true);
			serverConfig.setContentLengthOptional(false);
			webServer.start();
			serverState = true;

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void stopServer() {
		log.info("Stopping server");
		if (null != webServer) {
			webServer.shutdown();
		}
		webServer = null;
		serverState = false;
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean click(String imageName) throws SikuliAgentException {
		log.info("Clicking on image " + imageName);
		final File imgFile = assertFile(imageName);
		try {
			screen.click(imgFile.getAbsolutePath(), 0);
		} catch (FindFailed e) {
			return false;
		}
		return true;

	}

	private File assertFile(String imageName) throws SikuliAgentException {
		final File imgFile = new File(imagesFolder + File.separator + imageName);
		if (!imgFile.exists()) {
			log.severe("Image file " + imageName + " was not found");
			throw new SikuliAgentException(imgFile);
		}
		return imgFile;
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean click(int x, int y) throws SikuliAgentException {
		log.info("Clicking on coordinates " + x + "," + y);
		try {
			Robot robot = new Robot();
			robot.mouseMove(x, y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			throw new SikuliAgentException("Failed to click using Java robot");
		}
		return true;

	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean doubleClick(int x, int y) throws SikuliAgentException {
		log.info("Double clicking on coordinates " + x + "," + y);
		try {
			Robot robot = new Robot();
			robot.mouseMove(x, y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			throw new SikuliAgentException("Failed to click using Java robot");
		}
		return true;
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean exist(String imageName) throws SikuliAgentException {
		log.info("Checking if image file " + imageName + " exists");
		final File imgFile = assertFile(imageName);
		Match match = screen.exists(imgFile.getAbsolutePath());
		if (null == match) {
			return false;
		}
		return match.toString().contains("Match");
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public boolean addImage(String imageFileName, byte[] content) {
		File destFolder = new File(imagesFolder);
		log.info("Adding image " + imageFileName + " to image folder " + destFolder.getAbsolutePath());
		if (!destFolder.exists()) {
			log.warning("Image folder " + destFolder.getAbsolutePath() + " is not exist. Creating ");
			FileUtils.mkdirs(destFolder.getAbsolutePath());
		}
		final File imgFile = new File(destFolder, imageFileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgFile);
			fos.write(content);
		} catch (Exception e) {
			log.warning("Failed to write image file " + imgFile.getName());
			return false;
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * @see com.jsystem.j2sikuli.SikuliI
	 */
	@Override
	public byte[] captureScreenshot() throws SikuliAgentException {
		log.info("Capturing screenshot");
		byte[] content = null;
		try {
			final String fileName = screen.capture().getFilename();
			content = FileUtils.readBytes(new File(fileName));
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to capture screen shot", e);
			throw new SikuliAgentException("Failed to capture screen shot");
		}
		return content;
	}

	@Override
	public boolean type(String imageFileName, String text) throws SikuliAgentException {
		log.info("Typing text " + text);
		final File imgFile = assertFile(imageFileName);
		try {
			screen.type(imgFile.getAbsolutePath(), text);
		} catch (FindFailed e) {
			return false;
		} catch (Exception e) {
			throw new SikuliAgentException(e);
		}
		return true;
	}

	static class DisplayLogFile extends JFrame implements ActionListener {
		private static final long serialVersionUID = 2092378207577576792L;
		JTextArea _resultArea = new JTextArea(25, 40);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		private int x = (int) (double) dim.getWidth() * 2 / 3;
		private int y = (int) (double) dim.getHeight() * 5 / 11;

		public DisplayLogFile() {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			reBuild("");
		}

		public void reBuild(String text) {
			reBuild("Sikuli Log Buffer", text);
		}

		public void reBuild(String title, String text) {
			_resultArea.setText(text);
			_resultArea.setEditable(false);
			JScrollPane scrollingArea = new JScrollPane(_resultArea);
			JPanel content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(scrollingArea, BorderLayout.CENTER);
			this.setTitle(title);
			this.setLocation(x, y);
			this.setContentPane(content);
			this.pack();
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			setVisible(true);
		}
	}

}
