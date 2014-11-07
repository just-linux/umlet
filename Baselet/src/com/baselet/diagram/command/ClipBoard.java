package com.baselet.diagram.command;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

import com.baselet.control.Constants;
import com.baselet.control.Constants.SystemInfo;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.io.OutputHandler;
import com.baselet.element.GridElement;
import com.baselet.gui.CurrentGui;

/** Copies and Pastes images to the system clipboard. Requires Java 2, v1.4. */
public class ClipBoard implements Transferable {

	private Clipboard clipboard;
	private DiagramHandler copiedfrom;
	private Vector<GridElement> entities;

	public static ClipBoard _instance;

	public static ClipBoard getInstance() {
		if (_instance == null) {
			_instance = new ClipBoard();
		}
		return _instance;
	}

	private ClipBoard() {
		entities = new Vector<GridElement>();
		if (Float.parseFloat(SystemInfo.JAVA_VERSION) < 1.4) {
			return;
		}
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	public DiagramHandler copiedFrom() {
		return copiedfrom;
	}

	public void copy(Vector<GridElement> entities, DiagramHandler handler) {
		copiedfrom = handler;
		this.entities = new Vector<GridElement>(entities);
		if (clipboard != null) {
			clipboard.setContents(this, null);
		}
		// AB: clipboard zooms entities to 100%
		// NOTE has to be done here because it doesn't fit with cut/copy and GenPic.getImageFromDiagram otherwise)
		DiagramHandler.zoomEntities(handler.getGridSize(), Constants.DEFAULTGRIDSIZE, this.entities);
		CurrentGui.getInstance().getGui().enablePasteMenuEntry();
	}

	public Vector<GridElement> paste() {
		return entities;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return ClipBoard.createImageForClipboard(copiedfrom, entities);
	}

	private static BufferedImage createImageForClipboard(DiagramHandler handler, Collection<GridElement> entities) {

		int oldZoom = handler.getGridSize();
		handler.setGridAndZoom(Constants.DEFAULTGRIDSIZE, false); // Zoom to the defaultGridsize before execution

		if (entities.isEmpty()) {
			entities = handler.getDrawPanel().getGridElements();
		}
		BufferedImage returnImg = OutputHandler.getImageFromDiagram(handler, entities);

		handler.setGridAndZoom(oldZoom, false); // Zoom back to the oldGridsize after execution

		return returnImg;
	}

}