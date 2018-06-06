package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class Problem {

	int rows,cols,numCars,bonus,steps;
	List<Ride> rides;
	//Map of closest rides to a ride.
	Map<Ride, Set<Ride>> closest;
	//Number of closest rides to consider.
	int num_closest=10;
	
	public Problem(int rows, int cols, int numCars, int bonus, int steps, List<Ride> rides) {
		this.rows = rows;
		this.cols = cols;
		this.numCars = numCars;
		this.bonus = bonus;
		this.steps = steps;
		this.rides = rides;
		
		//Create an 2D RTree to determine closest rides.
		RTree<Ride, Point> starts = RTree.create();
		//Add all rides to the RTree as points.
		for(Ride r : rides)starts = starts.add(r, Geometries.point(r.start.col, r.start.row));
		
		//Get the closest num_closest rides.
		//Increasing num_closest value increases the score,
		//however also increases the runtime.
		closest = new HashMap<>();
		for(Ride r : rides){
			List<Ride> closest = starts.nearest(Geometries.point(r.end.col, r.end.row), Integer.MAX_VALUE, num_closest).map(Entry::value).filter(o -> o.id != r.id).toList().toBlocking().single();
			this.closest.put(r, new HashSet<>(closest));
		}
		
	}
	
	int maximum(){
		return rides.stream().mapToInt(r -> r.distance + r.bonus).sum();
	}
	
}
