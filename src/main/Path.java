package main;

import java.awt.Color;
import java.util.ArrayList;

import main.Path.PathNode;

public class Path {
	ArrayList<PathNode>nodes=new ArrayList<PathNode>();
	
	
	public void add(PathNode node) {
		nodes.add(node);
	}
	
	public void add(int index,PathNode node){
		nodes.add(index,node);
	}
	
	public int size() {
		return nodes.size();
	}

	public PathNode get(int i) {
		return nodes.get(i);
	}
	
	public static class PathNode{
		int[] index;
		Color c;

		public PathNode(Color color, int[] index) {
			this.c=color;
			this.index=index;
		}
	}

}