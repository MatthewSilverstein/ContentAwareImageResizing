package main;

/**
 * This package provides a tool for resizing images while preserving the content of the 
 * images using a Dynamic Programming approach.
 */

import javax.swing.JApplet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * This class listens for resizing events and then creates and starts a new AlgorithmThread 
 * and cancels any running instances of AlgorithmThread.
 */
public class ImageResizer extends JApplet{
	private String path = "samplepictures\\pic3.jpg";
	private ImageHandler ih;
	private int width=0,height=0;
	private AlgorithmThread thread;
	public void init(){
		ResizeHandler rh = new ResizeHandler();
		ih = new ImageHandler(path);
		width = ih.getWidth();
		height = ih.getHeight();
		setSize(width,height);
		thread = new AlgorithmThread(this, ih, null);
		this.addComponentListener(rh);
		setVisible(true);
		repaint();
	}

	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		if (ih != null){
			g2d.drawImage(ih.getDisplayImage(), 0,0, null);
		}
	}

	private class ResizeHandler extends ComponentAdapter{
		@Override
		public void componentResized(ComponentEvent event){
			int nWidth = event.getComponent().getWidth();
			int nHeight = event.getComponent().getHeight();
			Algorithm algo = new Algorithm(ih.getPixelsRBG(), width, height, nWidth, nHeight);
			thread.stopThread();
			thread = new AlgorithmThread(ImageResizer.this, ih, algo);
			thread.start();
		}
	}
}