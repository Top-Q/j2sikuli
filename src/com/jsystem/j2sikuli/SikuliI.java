package com.jsystem.j2sikuli;

public interface SikuliI {

	/**
	 * Click on the specified image.
	 * 
	 * @param imageName
	 *            The name of the image that located in the remote machine. Use
	 *            addImage to add image files.
	 * @return true if and only if the image was found and clicked
	 * @throws SikuliAgentException
	 *             If image file was not found
	 */
	boolean click(final String imageName) throws SikuliAgentException;

	/**
	 * Click on the specified coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return true if coordinates was clicked.
	 * @throws SikuliAgentException
	 *             If failed to create Java Robot
	 */
	boolean click(int x, int y) throws SikuliAgentException;

	/**
	 * Double click on the specified coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return true if coordinates was clicked.
	 * @throws SikuliAgentException
	 *             If failed to create Java Robot
	 */
	boolean doubleClick(int x, int y) throws SikuliAgentException;

	/**
	 * Check that image exists on screen
	 * 
	 * @param imageName
	 *            The name of the image that located in the remote machine. Use
	 *            addImage to add image files.
	 * @return true if and only if image exists on screen
	 * @throws SikuliAgentException
	 *             If image file was not found
	 */
	boolean exist(final String imageName) throws SikuliAgentException;

	/**
	 * Add image to the images folder. If image file with the same name exists,
	 * it will be replaced
	 * 
	 * @param imageFileName
	 * @param content
	 *            The content of the image file
	 * @return true if image was added
	 */
	boolean addImage(final String imageFileName, final byte[] content);

	/**
	 * Captures the current remote machine screen.
	 * 
	 * @return content of screen shot png image or null if failed to capture.
	 */
	byte[] captureScreenshot() throws SikuliAgentException;

	/**
	 * Type the specified text to the given image.
	 * 
	 * @param imageFileName
	 *            The name of the image that located in the remote machine. Use
	 *            addImage to add image files.
	 * @param text
	 *            test to type
	 * @return
	 */
	boolean type(final String imageFileName, final String text) throws SikuliAgentException;

}
