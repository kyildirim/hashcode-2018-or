package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProblemSolver {

	Solver solver = new Solver();
	
	List<Car> solve(Problem p){
		Set<Ride> rem = new HashSet<>(p.rides);
		List<Car> cars = new ArrayList<>();
		
		for(int i = 0; i<p.numCars; i++)cars.add(new Car(i));
		
		while(!rem.isEmpty()){
			System.out.println("Remaining rides: " + rem.size());
			Set<Ride> select = rem.stream().sorted(Comparator.comparing(o -> o.window.start)).limit(cars.size()*40).collect(Collectors.toSet());
			
			Map<Ride, Car> result = solver.solve(cars, select, p, rem);
			if(result.isEmpty())break;
			
			for(Map.Entry<Ride, Car> e : result.entrySet()){
				e.getValue().addRide(e.getKey());
				rem.remove(e.getKey());
			}		
			
		}
		
		return cars;
	}
	
}
