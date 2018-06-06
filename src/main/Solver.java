package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;

public class Solver {

	// Load OR Tools
	static {
		System.loadLibrary("jniortools");
	}
	
	//Create a new MPSolver and a result variable.
	//Note: When not done statically calls made to C may cause
	//garbage collector to collect in use objects.
	static MPSolver mpsolver;
	static MPSolver.ResultStatus res;
	
	//Check if the assignment of ride r to car c is feasible.
	boolean isFeasible(Car c, Ride r){
		int delta = c.status.loc.distance(r.start) + c.status.d;
		delta = Math.max(delta, r.window.start) + r.distance;
		return delta <= r.window.end;
	}
	
	//Calculate the future waste caused.
	int wasteCaused(int d, Ride r, Problem p, Set<Ride> rem){
		int w = p.closest.get(r).stream().filter(rem::contains).filter(o -> o.window.end >= d+o.start.distance(r.end)+o.distance)
				.mapToInt(o -> r.end.distance(o.start) + Math.max(o.window.start-d-r.end.distance(o.start), 0)).min().orElse(p.rows+p.cols);
		return Math.min(w, p.steps-d);
	}
	
	//Base value, waste and waste caused coefficient.
	//All variables are arrays to allow for selecting
	//with the value of the current problem.
	int base[] = {100, 1500, 1000000, 1000000, 1000000};
	double wCoeff[] = {0.1, 0.3, 0.1, 0.1, 0.1};
	double wCCoeff[] = {0.01, 0.1, 0.01, 0.01, 0.01};
	
	//Calculate a coefficient based on the waste by taking a ride
	//and the future waste caused by taking the same ride.
	double calcCoeff(Car c, Ride r, Problem p, Set<Ride> rem){
		int i = Main.currentProblem-1;
		int delta = c.status.loc.distance(r.start) + c.status.d;
		delta = Math.max(delta, r.window.start) + r.distance;
		int waste = delta - c.status.d - r.distance;
		int wasteCaused = wasteCaused(delta, r, p, rem);
		return base[i] - wCoeff[i] * waste - wCCoeff[i] * wasteCaused;
	}
	
	Map<Ride, Car> solve(List<Car> cars, Set<Ride> rides, Problem problem, Set<Ride> ridesLeft){
		
		//Initialize the MPSolver wtih problem type GLOP
		//CBC mode did not work, probably due to some problem with C bindings.
		mpsolver = new MPSolver("RideSolver",MPSolver.OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
		mpsolver.enableOutput();
		
		//Create a table to set constraints.
		Table<Ride, Car, MPVariable> mpvars = HashBasedTable.create();
		
		//For all possible assignments of rides to each car, create a mpvar.
		for(Car c : cars)for(Ride r : rides)if(isFeasible(c, r))mpvars.put(r, c, mpsolver.makeIntVar(0.0, 1.0, "Assign Ride: " + r.id + " to Car: " + c.id ));
		//Create a new objective.
		MPObjective objective = mpsolver.objective();
		//Add all mpvars with calculated coefficients to the objective function.
		for(Ride r : rides)for(Car c : cars)objective.setCoefficient(mpvars.get(r, c), calcCoeff(c, r, problem, ridesLeft));
		//Set objective type to maximization.
		objective.setMaximization();
		//Create constraints based on both row maps and column maps.
		for(Map.Entry<Ride, Map<Car, MPVariable>> e : mpvars.rowMap().entrySet()){
			MPConstraint cons = mpsolver.makeConstraint(-MPSolver.infinity(), 1.0);
			for(Map.Entry<Car, MPVariable> ie : e.getValue().entrySet()){
				cons.setCoefficient(ie.getValue(), 1.0);
			}
		}
		for(Map.Entry<Car, Map<Ride, MPVariable>> e : mpvars.columnMap().entrySet()){
			MPConstraint cons = mpsolver.makeConstraint(-MPSolver.infinity(), 1.0);
			for(Map.Entry<Ride, MPVariable> ie : e.getValue().entrySet()){
				cons.setCoefficient(ie.getValue(), 1.0);
			}
		}
		
		//Print the Linear Program to the console.
		//System.out.println(mpsolver.exportModelAsLpFormat(true));
		//Run the solver.
		res = mpsolver.solve();
		System.out.println("Current solution status: " + res);
		
		if(res == MPSolver.ResultStatus.FEASIBLE || res == MPSolver.ResultStatus.OPTIMAL || res == MPSolver.ResultStatus.UNBOUNDED){
			Map<Ride, Car> results = new HashMap<>();
			//Add all ride car assignments to the results.
			for(Ride r : rides)for(Car c : cars)if(mpvars.get(r, c)!=null&&mpvars.get(r, c).solutionValue() == 1.0)results.put(r, c);
			return results;
		}else{
			System.err.println("Illegal status: " + res.toString());
		}
		
		return null;
	}

}
