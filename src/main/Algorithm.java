package main;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This class uses a dynamic programming algorithm to find a minimal path through the image:
 * 	1) The algorithm converts the Color[][] to a linked list where every node has a link to
 * 		the left, right, up, and down.
 * 	2) The algorithm calculates the change in colour across each pixel.
 * 		(vertically and horizontally)
 * 	3) The algorithm uses a D.P. approach to find minimal paths through the pixels and
 * 		removes that path from the image.
 */
public class Algorithm {
	private int width, height, nWidth, nHeight;
	private Color[][] pixels;
	private Node head; /* link to the top left node of the linked list. */
	private int dH, dV; /* the change in width, height still remaining */

	public Algorithm(Color[][] pixels, int width, int height, int nWidth, int nHeight){
		this.width = width;
		this.height = height;
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.pixels = pixels;
		initNodes(pixels);
		initGradients();
		dH = width - nWidth;
		dV = height - nHeight;
	}

	/**
	 * Creates the linked list where every node is connected 
	 * to the left, right, up, down.
	 */
	private void initNodes(Color[][] pixels){
		Node[][] nodesInit = new Node[width][height];
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				nodesInit[i][j] = new Node(pixels[i][j]);
				nodesInit[i][j].x = i;
				nodesInit[i][j].y = j;
			}
		}
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				Node node = nodesInit[i][j];
				if (i != 0){
					node.l = nodesInit[i - 1][j];
				}
				if (i != width - 1){
					node.r = nodesInit[i + 1][j];
				}
				if (j != 0){
					node.u = nodesInit[i][j - 1];
				}
				if (j != height - 1){
					node.d = nodesInit[i][j + 1];
				}
			}
		}
		head = nodesInit[0][0];
	}

	/**
	 * Initializes the vertical and horizontal gradients for each node.
	 */
	private void initGradients(){
		Node currentRow = head;
		while(currentRow != null){
			Node current = currentRow;
			while(current != null){
				calculateHGradient(current);
				calculateVGradient(current);
				current = current.r;
			}
			currentRow = currentRow.d;
		}
	}

	/** 
	 * Resizes the image, one step at a time. This allows for the algorithm to be stopped
	 * throughout the process.
	 */
	public boolean resizeImage(){
		boolean changed = false;
		if (dH != 0){
			changeHorizontally();
			width--;
			changed = true;
		}
		if (dV != 0){
			changeVertically();
			height--;
			changed = true;
		}
		return changed;
	}

	/**
	 * Handles changing the image horizontally.
	 */
	private void changeHorizontally(){
		if (dH>0){
			dH--;
			Node minPathV = calculateMinPathV();
			extractMinPathV(minPathV);
		}else{
			dH++;
		}
	}

	/**
	 * Calculates a minimal vertical path through the picture using D.P.
	 * 
	 * @return The bottom node in the minimal path.
	 */
	private Node calculateMinPathV(){
		head.gradientH = 100000;
		Node currentRow;
		Node current;
		Node minPath = null;
		currentRow = head;
		current = currentRow;
		while(current != null){
			current.minV = current.gradientH;
			current = current.r;
		}
		currentRow = currentRow.d;
		while (currentRow != null){
			current = currentRow;
			while(current != null){
				Node u = current.u, ur = u.r, ul = u.l;
				int min = u.minV;
				if (ul != null && ul.minV < min)
					min = ul.minV;
				if (ur != null && ur.minV < min)
					min = ur.minV;
				current.minV = min + current.gradientH;
				current = current.r;
			}
			if (currentRow.d == null){
				current = currentRow;
				minPath = current;
				while (current != null){
					if (current.minV < minPath.minV)
						minPath = current;
					current = current.r;
				}
			}
			currentRow = currentRow.d;
		}
		return minPath;
	}

	/**
	 * Uses the bottom node of a minimal vertical path to go up through the path and 
	 * remove each node in the path.
	 * 
	 * @param minPathV the bottom node of a minimal vertical path.
	 */
	private void extractMinPathV(Node minPathV){
		Node current = minPathV;
		Node min = current.u, u = current.u, ul = min.l, ur = min.r;
		while (current != null && current.u != null){
			min = u = current.u; 
			ul = min.l; 
			ur = min.r;
			int minType = 0;
			if (ul != null && ul.minV < min.minV ){
				min = ul;
				minType = 1;
			}
			if (ur != null && ur.minV < min.minV){
				min = ur;
				minType = 2;
			}
			if (minType == 1){
				current.l.u = u;
				u.d = current.l;
			}else if (minType == 2){
				current.r.u = u;
				u.d = current.r;
			}
			if (current.l != null)
				current.l.r = current.r;
			if (current.r != null)
				current.r.l = current.l;
			current = min;
		}
		if (current.l != null)
			current.l.r = current.r;
		if (current.r != null)
			current.r.l = current.l;
	}

	/**
	 * Handles changing the picture vertically.
	 */
	private void changeVertically(){
		if (dV>0){
			dV--;
			Node minPathH = calculateMinPathH();
			extractMinPathH(minPathH);
		}else{
			dV++;
		}
	}

	/**
	 * Calculates a minimal horizontal path through the picture using D.P.
	 * 
	 * @return	the rightmost node in a minimal horizontal path.
	 */
	private Node calculateMinPathH(){
		head.gradientV = 100000;
		Node currentCol;
		Node current;
		Node minPath = null;
		currentCol = head;
		current = currentCol;
		while(current != null){
			current.minH = current.gradientV;
			current = current.d;
		}
		currentCol = currentCol.r;
		while (currentCol != null){
			current = currentCol;
			while(current != null){
				Node l = current.l, lu = l.u, ld = l.d;
				int min = l.minH;
				if (lu != null && lu.minH < min)
					min = lu.minH;
				if (ld != null && ld.minH < min)
					min = ld.minH;
				current.minH = min + current.gradientV;
				current = current.d;
			}
			if (currentCol.r == null){
				current = currentCol;
				minPath = current;
				while (current != null){
					if (current.minH < minPath.minH)
						minPath = current;
					current = current.d;
				}
			}
			currentCol = currentCol.r;
		}
		return minPath;
	}

	/**
	 * Uses the rightmost node of a minimal horizontal path to go left through the path 
	 * and remove each node in the path.
	 * 
	 * @param minPathH the rightmost node of a minimal horizontal path.
	 */
	private void extractMinPathH(Node minPathH){
		Node current = minPathH;
		Node min = current.l, l = current.l, lu = min.u, ld = min.d;
		while (current != null && current.l != null){
			min = l = current.l; 
			lu = l.u; 
			ld = l.d;
			int minType = 0;
			if (lu != null && lu.minH < min.minH ){
				min = lu;
				minType = 1;
			}
			if (ld != null && ld.minH < min.minH){
				min = ld;
				minType = 2;
			}
			if (minType == 1){
				current.u.l = l;
				l.r = current.u;
			}else if (minType == 2){
				current.d.l = l;
				l.r = current.d;
			}
			if (current.u != null)
				current.u.d = current.d;
			if (current.d != null)
				current.d.u = current.u;
			current = min;
		}
		if (current.u != null)
			current.u.d = current.d;
		if (current.d != null)
			current.d.u = current.u;
	}

	/**
	 * Converts the linked list back into an image and returns it.
	 * 
	 * @return the resized image
	 */
	public BufferedImage getPicture(){
		BufferedImage image = new BufferedImage(nWidth + dH, nHeight + dV, BufferedImage.TYPE_4BYTE_ABGR);
		Node currentRow = head;
		int j = 0;
		while (currentRow != null){
			Node current = currentRow;
			int i = 0;
			while(current != null){
				image.setRGB(i, j, current.color.getRGB());
				current = current.r;
				i++;
			}
			currentRow = currentRow.d;
			j++;
		}
		return image;
	}

	private static class Node{
		private Node l, r, u, d;
		int[] colorARGB;
		Color color;
		int gradientH, gradientV;
		int minV, minH;
		int x, y;
		public Node(Color color){
			this.colorARGB = new int[4];
			this.colorARGB[0] = color.getAlpha();
			this.colorARGB[1] = color.getRed();
			this.colorARGB[2] = color.getGreen();
			this.colorARGB[3] = color.getBlue();
			this.color = color;
		}
	}

	private void calculateHGradient(Node node){
		if (node == null)
			return;
		Node l = node.l, r = node.r;
		if (l == null){
			l = node;
		}
		if (r == null){
			r = node;
		}
		node.gradientH = calculateGradient(l, r);
	}

	private void calculateVGradient(Node node){
		if (node == null)
			return;
		Node d = node.d, u = node.u;
		if (d == null){
			d = node;
		}
		if (u == null){
			u = node;
		}
		node.gradientV = calculateGradient(d, u);
	}

	private int calculateGradient(Node n1,Node n2){
		int gradient=0;
		for (int i=0;i<4;i++){
			gradient+=Math.pow(n1.colorARGB[i] - n2.colorARGB[i],2);
		}
		gradient=(int) Math.sqrt(gradient);
		return gradient;
	}
}