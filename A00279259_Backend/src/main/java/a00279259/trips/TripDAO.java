package a00279259.trips;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public enum TripDAO {
	
	instance;
	
	private Map<Integer, Trip> tripsMap = new HashMap<Integer, Trip>();

	// Constructor to initialize trips
	private TripDAO() {
		Trip t1 = new Trip();
		t1.setTripId(1);
	    t1.setDestination("New York, USA");
	    t1.setStartDate("2025-01-14");
	    t1.setEndDate("2025-01-20");
	    t1.setBudget(BigDecimal.valueOf(2500.00));
	    t1.setNotes("Exploring NYC, food tour and Timesquare");
	    
	    tripsMap.put(1, t1);
	    
	    Trip t2 = new Trip();
	    t2.setTripId(2);
	    t2.setDestination("Chania, Crete, Greece");
	    t2.setStartDate("2025-09-18");
	    t2.setEndDate("2025-09-28");
	    t2.setBudget(BigDecimal.valueOf(2000.00));
	    t2.setNotes("Chilling on the beach, visit Samaria Gorge.");

	    tripsMap.put(2, t2);
	    
	    Trip t3 = new Trip();
	    t3.setTripId(3);
	    t3.setDestination("Santorini, Greece");
	    t3.setStartDate("2025-09-15");
	    t3.setEndDate("2025-09-18");
	    t3.setBudget(BigDecimal.valueOf(1500.00));
	    t3.setNotes("Sunset views, volcano tour and buggie adventure.");

	    tripsMap.put(3, t3);
	    
	    Trip t4 = new Trip();
	    t4.setTripId(4);
	    t4.setDestination("Bali, Indonesia");
	    t4.setStartDate("2025-06-06");
	    t4.setEndDate("2025-06-17");
	    t4.setBudget(BigDecimal.valueOf(2700.00));
	    t4.setNotes("Temple visits, rice terraces and scuba diving.");

	    tripsMap.put(4, t4);
	    
	    Trip t5 = new Trip();
	    t5.setTripId(5);
	    t5.setDestination("Krakow, Poland");
	    t5.setStartDate("2025-12-20");
	    t5.setEndDate("2026-01-02");
	    t5.setBudget(BigDecimal.valueOf(1500.00));
	    t5.setNotes("Exploring old town, Auschwitz tour and visit home");
	
	    tripsMap.put(5, t5);
	}
	
	// GET all trips
	public List<Trip> getTrips() {
		System.out.println("getTrips() :: called");
		List<Trip> trips = new ArrayList<Trip>();
		trips.addAll(tripsMap.values());
		return trips;
	}
  
	// GET one trip
	public Trip getTrip(int id) {
		System.out.println("getTrip(id) :: called");
		return tripsMap.get(id);
    }
	
	// Add new trip
	public Trip addTrip(Trip trip) {
        int id = tripsMap.size() + 1;
        trip.setTripId(id);
        tripsMap.put(id, trip);
        
        System.out.println("New trip was added successfully.\n" + trip.toString());
        return trip;
    }
	
	// Update existing trip by ID
	public Trip updateTrip(int id, Trip updatedTrip) {
		// Check if trip exists
        if (!tripsMap.containsKey(id)) {
        	System.out.println("Trip with id: " + id + " does not exist.");
        	return null;
        }
        
        // Find trip to edit
        Trip existingTrip = tripsMap.get(id);
        
		// Update trip details 
        if (updatedTrip.getDestination() != null) {
        	existingTrip.setDestination(updatedTrip.getDestination());
        }
        if (updatedTrip.getStartDate() != null) {
        	existingTrip.setStartDate(updatedTrip.getStartDate());
        }
        if (updatedTrip.getEndDate() != null) {
        	existingTrip.setEndDate(updatedTrip.getEndDate());
        }
        if (updatedTrip.getBudget() != null) {
        	existingTrip.setBudget(updatedTrip.getBudget());
        }
        if (updatedTrip.getNotes() != null) {
        	existingTrip.setNotes(updatedTrip.getNotes());
        }
        
        tripsMap.put(id,  existingTrip);
        System.out.println("Trip with ID: " + id + " was updated successfully.");
        return existingTrip;
    }

	// Delete trip by ID
	public Trip deleteTrip(int id) {
		System.out.println("Trip with ID: " + id + " was deleted successfully.");
		return tripsMap.remove(id); 
    }
}
