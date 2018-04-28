package main;

public class CarStatus {

	int d;
	Intersection loc;
	
	public CarStatus(int d, Intersection loc) {
		this.d = d;
		this.loc = loc;
	}
	
	@Override
	public String toString(){
		return "[Step: " + d + " Intersection=" + loc.toString() + "]";
	}
	
}
