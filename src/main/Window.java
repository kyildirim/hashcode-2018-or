package main;

public class Window {

	int start;
	int end;
	
	public Window(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public String toString(){
		return "[Start: " + start + " End: " + end +"]";
	}
	
}
