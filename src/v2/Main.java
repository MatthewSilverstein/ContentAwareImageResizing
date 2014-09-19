package v2;

import javax.swing.JApplet;














import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Main extends JApplet implements ComponentListener{
	
	public static void main(String[] args) {
		
	}
	
	String path="C:\\Users\\Matthew\\workspace\\DynamicPictureProgramming\\src\\main\\pic1.jpg";
	BufferedImage img=null;
	Color[][]pixelsRGB;
	Color[][]pixelsRGBOriginal;
	ArrayList<ArrayList<Integer>>hGradients=null,vGradients=null;
	byte[] pixels =null;
	int width=0,height=0;
	int cWidth=0,cHeight=0;
	boolean isChanged=false,isRunning=false;
	ArrayList<ArrayList<Color>> list=null;
	public void init(){
		this.addComponentListener(this);
		setVisible(true);
		loadPicture(path);
		loadPixels();
		loadPixelRBG();
		cWidth=width;
		cHeight=height;
		setSize(width,height);
		repaint();
	}
	
	private void changePicture(int dH, int dV) {
		isChanged=false;
		isRunning=true;
		while(dH!=0||dV!=0){
			if (!isChanged){
				if(dH!=0&&dV!=0){
					dH=changeH(dH);
		//			dV=changeV(dV);
				}
				if(dH!=0){
					dH=changeH(dH);
				}
				
				dV=0;
				/*
				if(dV!=0){
					dV=changeV(dV);
				}
				*/
			}else{
				dH=0;
				dV=0;
			}
		}
		isRunning=false;
	}
	
	private int changeH(int dH){
		if (dH>0){
			dH--;
			int[][]minCost=minCostH();
			ArrayList<int[]>path=extractPathH(minCost);
			width--;
			removePath(path);
		}else{
			dH++;
		}
		
		
		return dH;
	}
	
	private int changeV(int dV){
		if (dV>0){
			dV--;
			int[][]minCost=minCostV();
			ArrayList<int[]>path=extractPathV(minCost);
			height--;
			removePath(path);
		}else{
			dV++;
		}
		
		return dV;
	}
	
	public int[][] minCostV(){
		int[][] minCost=new int[width][height];
		for (int i=0,j=0;j<height;j++){
			minCost[i][j]=vGradientsGet(i,j);
		}
		
		for (int i=1;i<width;i++){
			for (int j=0;j<height;j++){
				int min=Math.max(j-1, 0);
				int max=Math.min(j+1, height-1);
				int bestCost=Integer.MAX_VALUE;
				for (int k=min;k<=max;k++){
					if (minCost[k][j-1]<bestCost){
						bestCost=minCost[k][j-1];
					}
				}
				bestCost+=vGradientsGet(i,j);
				minCost[i][j]=bestCost;
			}
		}
		return minCost;
	}
	
	public ArrayList<int[]>extractPathV(int[][]minCost){
		ArrayList<int[]>path=new ArrayList<int[]>();
		int min=0,max=height-1;
		//Start at end work backwards
		for(int i=width-1;i>=0;i--){
			int bestIndex=min;
			for (int j=min+1;i<=max;i++){
				if (minCost[i][j]<minCost[bestIndex][j]){
					bestIndex=i;
				}
			}
			int[] index={i,bestIndex};
			path.add(0,index);
			min=Math.max(bestIndex-1, 0);
			max=Math.min(bestIndex+1, width-1);
		}
		return path;
	}
	
	public int[][] minCostH(){
		int[][] minCost=new int[width][height];
		for (int i=0,j=0;i<width;i++){
			minCost[i][j]=hGradientsGet(i,j);
		}
		
		for (int j=1;j<height;j++){
			for (int i=0;i<width;i++){
				int min=Math.max(i-1, 0);
				int max=Math.min(i+1, width-1);
				int bestCost=Integer.MAX_VALUE;
				for (int k=min;k<=max;k++){
					if (minCost[k][j-1]<bestCost){
						bestCost=minCost[k][j-1];
					}
				}
				bestCost+=hGradientsGet(i,j);
				minCost[i][j]=bestCost;
			}
		}
		return minCost;
	}
	
	public ArrayList<int[]>extractPathH(int[][]minCost){
		ArrayList<int[]>path=new ArrayList<int[]>();
		int min=0,max=width-1;
		//Start at end work backwards
		for(int j=height-1;j>=0;j--){
			int bestIndex=min;
			for (int i=min+1;i<=max;i++){
				if (minCost[i][j]<minCost[bestIndex][j]){
					bestIndex=i;
				}
			}
			int[] index={bestIndex,j};
			path.add(0,index);
			min=Math.max(bestIndex-1, 0);
			max=Math.min(bestIndex+1, width-1);
		}
		return path;
	}
	
	public void removePath(ArrayList<int[]>path){
		//Remove indices from vList and hList
		//Update gradients
		for (int i=0;i<path.size();i++){
			int[] index=path.get(i);
			removeGradient(index[0],index[1]);
		}
	}
	
	public int[][] minPathV(){
		int[][] minPath=new int[width][height];
		return minPath;
	}
	
	public void removeGradient(int x,int y){
		listRemove(x,y);
		
		hGradientsRemove(x,y);
//		vGradientsRemove(x,y);
		
		
		if (x!=width&&y!=height){
			hGradientsRemove(x,y);
			hGradients.get(y).add(x,calculateHGradient(x,y));
//			vGradientsRemove(x,y);
//			vGradients.get(x).add(y,calculateVGradient(x,y));
		}else if(x!=width){
			hGradientsRemove(x,y);
			hGradients.get(y).add(x,calculateHGradient(x,y));			
		}else if (y!=height){
//			vGradientsRemove(x,y);
//			vGradients.get(x).add(y,calculateVGradient(x,y));			
		}
		
		if (y!=height){
		}
		
		if (x!=0&&y!=0){
			hGradients.get(y).remove(x-1);
			hGradients.get(y).add(x-1,calculateHGradient(x-1,y));
//			vGradientsRemove(x,y-1);
//			vGradients.get(x).add(y-1,calculateVGradient(x,y-1));
		}else if (x!=0){
			hGradients.get(y).remove(x-1);
			hGradients.get(y).add(x-1,calculateHGradient(x-1,y));
		}else if (y!=0){
//			vGradientsRemove(x,y-1);
//			vGradients.get(x).add(y-1,calculateVGradient(x,y-1));
		}
		
		if (y!=0){
		}
		updateMinPath(x,y);
	}
	
	public void updateMinPath(int x,int y){
		
	}
	
	//TODO Gradient Calculations
	public void calculateHGradients(){
		for (int j=0;j<height;j++){
			ArrayList<Integer>list=new ArrayList<Integer>();
			for (int i=0;i<width;i++){
				list.add(calculateHGradient(i,j));
			}
			hGradients.add(list);
		}
	}
	
	public int calculateHGradient(int x,int y){
		int min=Math.max(x-1, 0);
		int max=Math.min(x+1, width-1);
		Color c1=getRGB(min,y);
		Color c2=getRGB(max,y);
		int[]pixel1={c1.getAlpha(),c1.getBlue(),c1.getRed(),c1.getGreen()};
		int[]pixel2={c2.getAlpha(),c2.getBlue(),c2.getRed(),c2.getGreen()};
		int gradient=calculateGradient(pixel1,pixel2);
		return gradient/(max-min);
	}
	
	public void calculateVGradients(){
		for (int i=0;i<width;i++){
			ArrayList<Integer>list=new ArrayList<Integer>();
			for (int j=0;j<height;j++){
				list.add(calculateVGradient(i,j));
			}
			vGradients.add(list);
		}
	}
	
	public int calculateVGradient(int x,int y){
		int min=Math.max(y-1, 0);
		int max=Math.min(y+1, height-1);
		Color c1=getRGB(x,min);
		Color c2=getRGB(x,max);
		int[]pixel1={c1.getAlpha(),c1.getBlue(),c1.getRed(),c1.getGreen()};
		int[]pixel2={c2.getAlpha(),c2.getBlue(),c2.getRed(),c2.getGreen()};
		int gradient=calculateGradient(pixel1,pixel2);
		return gradient/(max-min);
	}
	
	public int calculateGradient(int[]pixel1,int[]pixel2){
		int gradient=0;
		for (int i=0;i<pixel1.length;i++){
			gradient+=Math.pow(pixel1[i]-pixel2[i],2);
		}
		gradient=(int) Math.sqrt(gradient);
		return gradient;
	}
	
	//TODO ComponentEvents
	@Override
	public void componentResized(ComponentEvent arg0) {
		isChanged=true;
		while(isRunning){
			try {
				Thread.sleep(0, 1);;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		initProblem();
		int nWidth=getWidth();
		int nHeight=getHeight();
		int dw,dh;
		dw=width-nWidth;
		dh=height-nHeight;
		changePicture(dw,dh);
		if (dw==0&&dh==0){
			repaint();
			return;
		}
		updatePicture();
		repaint();
	}
	
	@Override
	public void componentHidden(ComponentEvent arg0) {
		
	}
	
	@Override
	public void componentMoved(ComponentEvent arg0) {
		
	}		
	
	@Override
	public void componentShown(ComponentEvent arg0) {
		
	}
	
	//TODO updatePicture,paint
	public void updatePicture(){
		try{
			int[] pixels=new int[width*height];
			int count=0;
			for(int j=0;j<height;j++){
				for(int i=0;i<width;i++){

					pixels[count]=listGet(i,j).getRGB();
					count++;
				}
			}
			img=getImageFromArray(pixels,width,height);
		}catch(Exception e){
			System.out.println(e);
		}
		/*
		for (int i=0;i<width;i++){
			for (int j=0;j<height;j++){
				img.setRGB(i, j, listGet(i,j).getRGB());
			}
		}
		*/
		System.out.println("done");
	}
	
	public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
		int[] bitMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
		SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, bitMasks);
		DataBufferInt db = new DataBufferInt(pixels, pixels.length);
		WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
		BufferedImage image = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
		
		/*
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = (WritableRaster) image.getData();
		raster.setPixels(0,0,width,height,pixels);
		*/
		return image;
	}
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;    
		g2d.drawImage(img, 0,0, null);
	}
	
	//TODO GSRs
	public Color getRGB(int x,int y){
		Color c=listGet(x,y);
		return c;
	}
	
	public Color listGet(int x,int y){
		return list.get(y).get(x);
	}
	
	public void listRemove(int x,int y){
		list.get(y).remove(x);
	}
	
	public int hGradientsGet(int x,int y){
		return hGradients.get(y).get(x);
	}
	
	public void hGradientsRemove(int x,int y){
		hGradients.get(y).remove(x);
	}
	
	public int vGradientsGet(int x,int y){
		return vGradients.get(y).get(x);
	}
	
	public void vGradientsRemove(int x,int y){
		vGradients.get(x).remove(y);
	}
	
	//TODO init
	public void initProblem(){
		initWH();
		initLists();
		initGradients();
	}
	
	public void initWH(){
		width=pixelsRGBOriginal.length;
		height=pixelsRGBOriginal[0].length;
	}
	
	public void initLists(){
		list=new ArrayList<ArrayList<Color>>();
		for (int j=0;j<height;j++){
			ArrayList<Color>list=new ArrayList<Color>();
			for (int i=0;i<width;i++){
				list.add(pixelsRGBOriginal[i][j]);
			}
			this.list.add(list);
		}
	}
	public void initGradients(){
		hGradients=new ArrayList<ArrayList<Integer>>();
		vGradients=new ArrayList<ArrayList<Integer>>();
		calculateHGradients();
		calculateVGradients();
	}
	
	//TODO load
	public void loadPicture(String path){
		img=null;
		try {
			img=ImageIO.read(new File(path));
			width=img.getWidth();
			height=img.getHeight();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void loadPixels(){
		((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
	}
	
	public void loadPixelRBG(){
		//Now simply calculate gradients and build a*
		final boolean hasAlphaChannel=img.getAlphaRaster()!=null;
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
}