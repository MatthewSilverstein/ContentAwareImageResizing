package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class handles loading a picture from a file and converting it to Color[][].
 */
public class ImageHandler {
	
	private BufferedImage image, displayImage;
	private Color[][] pixelsRGBOriginal;
	private byte[] pixels = null;
	private int width, height;
	
	public ImageHandler(String path){
		loadPicture(path);
		loadPixels();
		loadPixelRBG();
		setDisplayImage(image);
	}

	private void loadPicture(String path){
		image=null;
		try {
			image=ImageIO.read(new File(path));
			width=image.getWidth();
			height=image.getHeight();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void loadPixels(){
		((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	}

	private void loadPixelRBG(){
		final boolean hasAlphaChannel=image.getAlphaRaster()!=null;
		pixelsRGBOriginal=new Color[width][height];
		if (hasAlphaChannel){
			final int pixelLength=4;
			int i,j,pixel=0;
			for (j=0;j<height;j++){
				for (i=0;i<width;i++,pixel+=pixelLength){
					int rgb =(((int)pixels[pixel+0]&0xFF)<<24)| //alpha
								(((int)pixels[pixel+1] & 0xFF) << 16) | //red
								(((int)pixels[pixel+2] & 0xFF) << 8)  | //green
								(((int)pixels[pixel+3] & 0xFF) << 0); //blue
					pixelsRGBOriginal[i][j]=new Color(rgb);
				}
			}
		}else{
			final int pixelLength=3;
			int i,j,pixel=0;
			for (j=0;j<height;j++){
				for (i=0;i<width;i++,pixel+=pixelLength){
					int rgb=((255 & 0xFF) << 24) | //alpha
							(((int)pixels[pixel+2] & 0xFF) << 16) | //red
							(((int)pixels[pixel+1] & 0xFF) << 8)  | //green
							(((int)pixels[pixel+0] & 0xFF) << 0); //blue
					pixelsRGBOriginal[i][j]=new Color(rgb);
				}
			}
		}
	}

	public int getHeight(){
		return height;
	}

	public int getWidth(){
		return width;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Color[][] getPixelsRBG(){
		return pixelsRGBOriginal;
	}

	public void setDisplayImage(BufferedImage image){
		displayImage = image;
	}

	public BufferedImage getDisplayImage(){
		return displayImage;
	}
}