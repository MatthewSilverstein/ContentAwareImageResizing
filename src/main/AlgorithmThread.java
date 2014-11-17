package main;

/**
 * This class runs through each step of the algorithm in an independent thread
 * until it finishes or is replaced by another instance of this class running the 
 * algorithm on a more recent image resize event.
 */
class AlgorithmThread extends Thread{
	private ImageResizer pr;
	private Algorithm algo;
	private ImageHandler ih;
	private boolean running = true;

	public AlgorithmThread(ImageResizer pr, ImageHandler ih, Algorithm algo){
		this.pr = pr;
		this.ih = ih;
		this.algo = algo;
	}

	@Override
	public void run(){
		while(running){
			boolean isChanged = algo.resizeImage();
			if (!isChanged){
				break;
			}
		}
		if (running)
			ih.setDisplayImage(algo.getPicture());
		pr.repaint();
	}

	public void stopThread(){
		running = false;
	}
}