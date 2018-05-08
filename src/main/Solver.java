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

	static {
		System.loadLibrary("jniortools");
	}
	static MPSolver mpsolver;
	static MPSolver.ResultStatus res;
	
	boolean isFeasible(Car c, Ride r){
		int delta = c.status.loc.distance(r.start) + c.status.d;
		delta = Math.max(delta, r.window.start) + r.distance;
		return delta <= r.window.end;
	}
	
	int wasteCaused(int d, Ride r, Problem p, Set<Ride> rem){
		int w = p.closest.get(r).stream().filter(rem::contains).filter(o -> o.window.end >= d+o.start.distance(r.end)+o.distance)
				.mapToInt(o -> r.end.distance(o.start) + Math.max(o.window.start-d-r.end.distance(o.start), 0)).min().orElse(p.rows+p.cols);
		return Math.min(w, p.steps-d);
	}
	
	int base[] = {100, 1500, 1000000, 1000000, 1000000};
	double wCoeff[] = {0.1, 0.3, 0.1, 0.1, 0.1};
	double wCCoeff[] = {0.01, 0.1, 0.01, 0.01, 0.01};
	
	double calcCoeff(Car c, Ride r, Problem p, Set<Ride> rem){
		int i = Main.currentProblem-1;
		int delta = c.status.loc.distance(r.start) + c.status.d;
		delta = Math.max(delta, r.window.start) + r.distance;
		int waste = delta - c.status.d - r.distance;
		int wasteCaused = wasteCaused(delta, r, p, rem);
		return base[i] - wCoeff[i] * waste - wCCoeff[i] * wasteCaused;
	}
	
	Map<Ride, Car> solve(List<Car> cars, Set<Ride> rides, Problem problem, Set<Ride> ridesLeft){
		
		mpsolver = new MPSolver("RideSolver",MPSolver.OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
		mpsolver.enableOutput();
		
		Table<Ride, Car, MPVariable> mpvars = HashBasedTable.create();
		
		for(Car c : cars)for(Ride r : rides)if(isFeasible(c, r))mpvars.put(r, c, mpsolver.makeIntVar(0.0, 1.0, "Assign Ride: " + r.id + " to Car: " + c.id ));
		MPObjective objective = mpsolver.objective();
		for(Ride r : rides)for(Car c : cars)objective.setCoefficient(mpvars.get(r, c), calcCoeff(c, r, problem, ridesLeft));
		objective.setMaximization();
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
		
		//System.out.println(mpsolver.exportModelAsLpFormat(true));
		res = mpsolver.solve();
		System.out.println(res);
		
		if(res == MPSolver.ResultStatus.FEASIBLE || res == MPSolver.ResultStatus.OPTIMAL || res == MPSolver.ResultStatus.UNBOUNDED){
			Map<Ride, Car> results = new HashMap<>();
			for(Ride r : rides)for(Car c : cars)if(mpvars.get(r, c)!=null&&mpvars.get(r, c).solutionValue() == 1.0)results.put(r, c);
			return results;
		}else{
			System.err.println("Illegal status: " + res.toString());
		}
		
		return null;
	}

}
