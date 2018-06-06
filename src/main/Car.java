package main;
import java.util.ArrayList;
import java.util.List;

public class Car {

	final int id; //ID of the car
	List<Ride> rides; //List of rides assigned
	CarStatus status; //Current status of the car
	int score; //Score of this car
	
	Car(int id){
		this.id = id;
		rides = new ArrayList<>(); //Start with no rides assigned
		status = new CarStatus(0, new Intersection(0, 0)); //Start at 0 at time 0
	}
	
	void addRide(Ride r){
		int delta = status.loc.distance(r.start) + status.d;
		delta = Math.max(delta, r.window.start) + r.distance;
		status = new CarStatus(delta, r.end);
		if(status.d <= r.window.end)score+=r.distance; //Add distance to score if ride is possible
		if(status.d - r.distance <= r.window.start)score+=r.bonus; //If within the bonus window add bonus
		rides.add(r);
	}
	
}
