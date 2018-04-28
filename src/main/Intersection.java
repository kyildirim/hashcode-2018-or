package main;

public class Intersection {

	int col;
	int row;
	
	public Intersection(int col, int row) {
		this.col = col;
		this.row = row;
	}
	
	public int distance(Intersection i){
		return Math.abs(i.row-this.row)+Math.abs(i.col-this.col);
	}
	
	@Override
	public String toString(){
		return "[row: "+ row + " col: " + col +"]";
	}
	
}
