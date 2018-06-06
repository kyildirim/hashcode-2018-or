package main;

public class Ride {

	//Ride variables.
	int id;
	Intersection start;
	Intersection end;
	int distance;
	int bonus;
	Window window;
	
	public Ride(int id, Intersection start, Intersection end, int bonus, Window window) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.distance = start.distance(end);
		this.bonus = bonus;
		this.window = window;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o)return true;
		if(o == null || o.getClass() != getClass())return false;
		Ride r = (Ride) o;
		return id == r.id;
	}
	
	@Override
	public String toString(){
		return "[ID=" + id +
				" Start=" + start.toString() +
				" End=" + end.toString() + 
				" Distance=" + distance +
				" Bonus=" + bonus + 
				" Window=" + window.toString() +
				"]";
	}
	
	
}
