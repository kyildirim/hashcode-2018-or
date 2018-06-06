package main;

public class Intersection {

	int col; //Column
	int row; //Row
	
	public Intersection(int col, int row) {
		this.col = col;
		this.row = row;
	}
	
	//Distance between this and intersection i
	public int distance(Intersection i){
		return Math.abs(i.row-this.row)+Math.abs(i.col-this.col);
	}
	
	@Override
	public String toString(){
		return "[row: "+ row + " col: " + col +"]";
	}
	
}
