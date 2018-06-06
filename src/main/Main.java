package main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.ortools.linearsolver.MPSolver;

public class Main {

	//List of all input files.
	static String inputs[] = {"a_example.in", "b_should_be_easy.in", "c_no_hurry.in", "d_metropolis.in", "e_high_bonus.in"};
	//Current problem to run on.
	static int currentProblem = 2;
	
	//Read input variables to read the file.
	static int rows,cols,numCars,numRides,bonus,steps;
	//List of all read rides from the file.
	static List<Ride> rides;
	
	public static void main(String args[]) throws IOException{
		//Garbage collection.
		System.gc();
		//Read the input file.
		readInput();
		
		//Create new Problem from input.
		Problem problem = new Problem(rows, cols, numCars, bonus, steps, rides);
		//Create a new instance of ProblemSolver.
		ProblemSolver solver = new ProblemSolver();
		//Get the solution.
		List<Car> solution = solver.solve(problem);
		
		//Print the solution to the console.
		for(Car c : solution)System.out.println(c.id + " " + c.rides.stream().map(o -> String.valueOf(o.id)).collect(Collectors.joining(" ")));
		
		//Print the score.
		System.out.println("Score " + solution.stream().mapToInt(o -> o.score).sum());
		
	}
	
	static void readInput() throws IOException{
		Scanner sc = new Scanner(new File(inputs[currentProblem-1]));
		rows = sc.nextInt();
		cols = sc.nextInt();
		numCars = sc.nextInt();
		numRides = sc.nextInt();
		bonus = sc.nextInt();
		steps = sc.nextInt();
		
		sc.nextLine();
		rides = new ArrayList<>();
		for(int i = 0; i<numRides; i++){
			String line = sc.nextLine();
			String split[] = line.split(" ");
			rides.add(new Ride(i, new Intersection(Integer.parseInt(split[0]), Integer.parseInt(split[1])),
					new Intersection(Integer.parseInt(split[2]), Integer.parseInt(split[3])), 
					bonus, new Window(Integer.parseInt(split[4]), Integer.parseInt(split[5]))));
		}
		sc.close();
	}
	
}
