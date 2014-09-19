package main;

public class UpdateThread extends Thread{
	Picture p;
	public UpdateThread(Picture p){
		this.p=p;
	}
	public void run(){
		System.out.println("HEY");
		while(true){
			p.update();
		}
	}
}
