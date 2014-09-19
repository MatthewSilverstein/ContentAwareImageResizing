package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

import main.Path.PathNode;

public class Picture extends JApplet implements ComponentListener{
	public static void main(String[] args) {
		Picture p=new Picture();
	}
	
	String path="C:\\Users\\Matthew\\workspace\\DynamicPictureProgramming\\src\\main\\pic2.jpg";
	BufferedImage img=null;
	Color[][]pixelsRBG;
	byte[] pixels =null;
	int width=0,height=0;
	int cWidth=0,cHeight=0;
	Stack<Path> SIS,DESS,GIS,DESG;
	public void init(){
		this.addComponentListener(this);
		UpdateThread ut=new UpdateThread(this);
		setVisible(true);
		loadPicture(path);
		loadPixels();
		loadPixelRBG();
		//loadPixelListRBG();
		DESS=new Stack<Path>();
		SIS=new Stack<Path>();
		DESG=new Stack<Path>();
		GIS=new Stack<Path>();
		cWidth=width;
		cHeight=height;
		setSize(width,height);
//		ut.start();
		repaint();
	}
	
	public void changePicture(int dh,int dv){
		if (dh>0){
			while(dh>0){
				shrinkPictureH();
				dh--;
			}
		}else if (dh<0){
			while(dh<0){
				growPictureH();
				dh++;
			}
		}
		updatePicture();
		repaint();
	}
	
	public void growPictureH(){
		Path p=null;
		if (SIS.isEmpty()){
			//Means picture is full create new positions
			return;
		}else{
			p=SIS.pop();
		}
		addPathH(p);
		DESS.push(p);
	}
	
	public void shrinkPictureH(){
		Path p=null;
		if (!DESG.isEmpty()){
			p=DESG.pop();
		}else if (DESS.isEmpty()){
			p=getMinPathH();
		}else{
			p=DESS.pop();
		}
		removePathH(p);
		SIS.push(p);
	}
	
	public void addPathH(Path p){
		for (int i=0;i<p.size();i++){
			PathNode node=p.get(i);
			addIndexH(node.index,node.c);
		}
		width++;
	}
	
	public void removePathH(Path p){
		for (int i=0;i<p.size();i++){
			int[] index=p.get(i).index;
			removeIndexH(index);
		}
		width--;
	}
	
	public void addIndexH(int[]index,Color c){
		for (int i=width-1,j=index[1];i>index[0];i--){
			pixelsRBG[i][j]=pixelsRBG[i-1][j];
		}
		pixelsRBG[index[0]][index[1]]=c;
	}
	
	public void removeIndexH(int[]index){
		for (int i=index[0],j=index[1];i<width-1;i++){
			pixelsRBG[i][j]=pixelsRBG[i+1][j];
		}
		pixelsRBG[width-1][index[1]]=Color.black;
	}
	
	public Path getMinPathH(){
		Path p=null;
		int[][] hGradients=new int[width][height];
		hGradients=calculateHGradients();
		p=dpSolveH2(hGradients);
		return p;
	}
	
	public Path dpSolveH2(int[][] hGradients){
		Path p=null;
		int[][] minCost=new int[width][height];
		//Base case (top row)
		for (int i=0,j=0;i<width;i++){
			minCost[i][j]=hGradients[i][j];
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
				bestCost+=hGradients[i][j];
				minCost[i][j]=bestCost;
			}
		}
		p=extractHPath(minCost,hGradients);
		return p;
	}
	
	public Path extractHPath(int[][] minCost,int[][] hGradients){
		Path p=new Path();
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
			PathNode node=new PathNode(pixelsRBG[index[0]][index[1]],index);
			p.add(0,node);
			min=Math.max(bestIndex-1, 0);
			max=Math.min(bestIndex+1, width-1);
		}
		
		return p;
	}
	
	public void updatePicture(){
		for (int i=0;i<width;i++){
			for (int j=0;j<height;j++){
				img.setRGB(i, j, pixelsRBG[i][j].getRGB());
			}
		}
	}
	
	public ArrayList<int[]> dpExtractPathV(int[][] minPath,int[][] vGradients){
		ArrayList<int[]>path=new ArrayList<int[]>();
		//Start at end work backwards
		int min=0,max=width-1;
		for(int j=height-1;j>=0;j--){
			int bestIndex=min;
			for (int i=min+1;i<=max;i++){
				if (minPath[i][j]<minPath[bestIndex][j]){
					bestIndex=i;
				}
			}
			int[] index={bestIndex,j};
			path.add(0, index);
			min=Math.max(bestIndex-1, 0);
			max=Math.min(bestIndex+1, width-1);
		}
		return path;
	}
	
	public int[][] calculateHGradients(){
		int[][] gradients=new int[width][height];
		for (int i=0;i<width;i++){
			for (int j=0;j<height;j++){
				gradients[i][j]=calculateHGradient(i,j);
			}
		}
		return gradients;
	}
	
	public int calculateHGradient(int x,int y){
		int min=Math.max(x-1, 0);
		int max=Math.min(x+1, width-1);
//		byte[]pixel1=getPixelsRBG(min,y);
//		byte[]pixel2=getPixelsRBG(max,y);
		Color c1=pixelsRBG[min][y];
		Color c2=pixelsRBG[max][y];
		int[]pixel1={c1.getAlpha(),c1.getBlue(),c1.getRed(),c1.getGreen()};
		int[]pixel2={c2.getAlpha(),c2.getBlue(),c2.getRed(),c2.getGreen()};
		int gradient=calculateGradient(pixel1,pixel2);
		return gradient/(max-min);
	}
	
	public int calculateGradient(int[]pixel1,int[]pixel2){
		int gradient=0;
		int[] dGradient=new int[pixel1.length];
		for (int i=0;i<pixel1.length;i++){
			gradient+=Math.pow(pixel1[i]-pixel2[i],2);
		}
		gradient=(int) Math.sqrt(gradient);
		return gradient;
	}
	
	public int calculateGradient(byte[]pixel1,byte[] pixel2){
		int gradient=0;
		int[] dGradient=new int[pixel1.length];
		for (int i=0;i<pixel1.length;i++){
			gradient+=Math.pow((int)pixel1[i]-(int)pixel2[i],2);
		}
		gradient=(int) Math.sqrt(gradient);
		return gradient;
	}	
	
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
		pixelsRBG=new Color[width][height];
		if (hasAlphaChannel){
			final int pixelLength=4;
			int i,j,pixel=0;
			for (j=0;j<height;j++){
				for (i=0;i<width;i++,pixel+=pixelLength){
					int rgb =(((int)pixels[pixel+0]&0xFF)<<24)| //alpha
								(((int)pixels[pixel+1] & 0xFF) << 16) | //red
								(((int)pixels[pixel+2] & 0xFF) << 8)  | //green
								(((int)pixels[pixel+3] & 0xFF) << 0); //blue
					pixelsRBG[i][j]=new Color(rgb);
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
					pixelsRBG[i][j]=new Color(rgb);
				}
			}
		}
	}
		
	public void update(){
//		this.dpSolve(0, 0);
		this.shrinkPictureH();
		repaint();
	}
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;    
		g2d.drawImage(img, 0,0, null);
	}
	
	@Override
	public void componentResized(ComponentEvent arg0) {
		int nWidth=getWidth();
		int nHeight=getHeight();
		if (nHeight!=cHeight){
			setSize(nWidth,cHeight);
			repaint();
			return;
		}
		if (nWidth>pixelsRBG.length){
			setSize(pixelsRBG.length,cHeight);
			repaint();
			return;
//			pixelsRBG=doublePixelsRBG();
		}
		int dw,dh;
		dw=cWidth-nWidth;
		dh=cHeight-nHeight;
		cWidth=nWidth;
		cHeight=nHeight;
		changePicture(dw,dh);
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
	
	public Color[][] doublePixelsRBG(){
		Color[][] newArray=new Color[pixelsRBG.length*2][pixelsRBG[0].length];
		int i=0;
		for (;i<pixelsRBG.length;i++){
			for (int j=0;j<pixelsRBG[i].length;j++){
				newArray[i][j]=pixelsRBG[i][j];
			}
		}
		return newArray;
	}
}