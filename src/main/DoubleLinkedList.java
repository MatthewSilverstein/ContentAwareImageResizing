package main;

import java.util.ArrayList;

//EVery row and column is a linked list
public class DoubleLinkedList {
	int width,height;
	byte[][][]list;
	int[][]offX;
	int[][]offY;
	
	public DoubleLinkedList(byte[][][] values){
		width=values.length;
		height=values[0].length;
		list=values;
		
		offX=new int[width][height];
		offY=new int[width][height];
		
		
	}
}
