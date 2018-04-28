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
	Map<Ride, Set<Ride>> closest;
	
	public Problem(int rows, int cols, int numCars, int bonus, int steps, List<Ride> rides) {
		this.rows = rows;
		this.cols = cols;
		this.numCars = numCars;
		this.bonus = bonus;
		this.steps = steps;
		this.rides = rides;
		
		RTree<Ride, Point> starts = RTree.create();
		for(Ride r : rides)starts = starts.add(r, Geometries.point(r.start.col, r.start.row));
		
		closest = new HashMap<>();
		for(Ride r : rides){
			List<Ride> closest = starts.nearest(Geometries.point(r.end.col, r.end.row), Integer.MAX_VALUE, 25).map(Entry::value).filter(o -> o.id != r.id).toList().toBlocking().single();
			this.closest.put(r, new HashSet<>(closest));
		}
		
	}
	
	int maximum(){
		return rides.stream().mapToInt(r -> r.distance + r.bonus).sum();
	}
	
}
