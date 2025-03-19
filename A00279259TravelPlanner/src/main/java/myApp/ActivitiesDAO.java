package myApp;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trip.activities.Activities;

public enum ActivitiesDAO {
	
	instance;
	
	private Map<Integer, Activities> activitiesMap = new HashMap<>();
	private int activityIdCounter = 1;
	
	// Constructor to initialize activities
	private ActivitiesDAO() {
		// NYC Activities 
		addActivity(new Activities(1, 1, "Visit Statue of Liberty and Ellis Island", LocalTime.of(10, 0), "New York, USA", BigDecimal.valueOf(59.95)));
	    addActivity(new Activities(2, 1, "Visit Times Square", LocalTime.of(20, 0), "New York, USA", BigDecimal.ZERO));
	    addActivity(new Activities(3, 1, "Visit Rockefeller Center", LocalTime.of(15, 0), "New York, USA", BigDecimal.valueOf(25.00)));

		// Crete Activities
	    addActivity(new Activities(1, 2, "Visit Samaria Gorge", LocalTime.of(9, 0), "Chania, Crete, Greece", BigDecimal.valueOf(45.00)));
	    addActivity(new Activities(2, 2, "Visit Sainta Limania", LocalTime.of(11, 30), "Chania, Crete, Greece", BigDecimal.valueOf(15.00)));
	    addActivity(new Activities(3, 2, "Visit Balos", LocalTime.of(13, 0), "Chania, Crete, Greece", BigDecimal.valueOf(30.00)));
	    addActivity(new Activities(4, 2, "Visit Elafonisi", LocalTime.of(14, 30), "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
	    addActivity(new Activities(5, 2, "Visit Fallasarna", LocalTime.of(16, 0), "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
		
	    // Santorini Activities 
	    addActivity(new Activities(1, 3, "Visit Oia", LocalTime.of(18, 0), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(2, 3, "Visit Thera", LocalTime.of(12, 0), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(3, 3, "Visit Red Beach", LocalTime.of(10, 0), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(4, 3, "Visit White Beach", LocalTime.of(14, 0), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(5, 3, "Do Buggie Adventure", LocalTime.of(16, 0), "Santorini, Greece", BigDecimal.valueOf(160.00)));
	    
		// Bali Activities 
	    addActivity(new Activities(1, 4, "Explore Ubud Monkey Forest", LocalTime.of(10, 0), "Ubud, Bali, Indonesia", BigDecimal.valueOf(10.00)));
	    addActivity(new Activities(2, 4, "Visit Tanah Lot Temple", LocalTime.of(17, 0), "Bali, Indonesia", BigDecimal.valueOf(6.00)));
	    addActivity(new Activities(3, 4, "Experience Bali Swing", LocalTime.of(13, 0), "Bali, Indonesia", BigDecimal.valueOf(60.00)));
	    
		// Krakow Activities 
	    addActivity(new Activities(1, 5, "Visit Main Square", LocalTime.of(11, 0), "Krakow, Poland", BigDecimal.ZERO));
	    addActivity(new Activities(2, 5, "Visit Auschwitz", LocalTime.of(8, 0), "Oświęcim, Poland", BigDecimal.valueOf(80.00)));
	    addActivity(new Activities(3, 5, "Visit Salt Mine", LocalTime.of(10, 0), "Wieliczka, Poland", BigDecimal.valueOf(50.00)));
	    addActivity(new Activities(4, 5, "Go Shopping (Galeria Krakowska & Bonarka)", LocalTime.of(14, 0), "Krakow, Poland", BigDecimal.valueOf(800.00)));
	    addActivity(new Activities(5, 5, "Go Home (Muszyna)", LocalTime.of(9, 0), "Muszyna, Poland", BigDecimal.valueOf(35.00)));
	}
	
	// Get all activities 
	public List<Activities> getActivities() {
		System.out.println("getActivities() :: called");
		return new ArrayList<>(activitiesMap.values());
	}
	
	// Get activity by trip_id
	public List<Activities> getActivitiesByTrip(int tripId) {
		System.out.println("getActivitiesByTrip(tripID) :: called");
		List<Activities> activitiesList = new ArrayList<>();
		for (Activities activity : activitiesMap.values()) {
			if(activity.getTripId() == tripId) {
				activitiesList.add(activity);
			}
		}
		return activitiesList;
    }
	
	// Get activity by id
	public Activities getActivity(int id) {
		System.out.println("getActivity(id) :: called");
		return activitiesMap.get(id);
    }
	
	// Add new activity
	public void addActivity(Activities activity) {
        activity.setId(activityIdCounter++);
        activitiesMap.put(activity.getId(), activity);
        System.out.println("Activity added successfully.\n" + activity.toString());
    }
	
	// Update activity by id 
	public Activities updateActivity(int id, Activities updatedActivity) {
		if (!activitiesMap.containsKey(id)) {
			System.out.println("Activity with ID: " + id + " does not exist.");
			return null;
		}
		
		Activities existingActivity = activitiesMap.get(id);
		
		// Update activity 
		if (updatedActivity.getTripId() > 0 && updatedActivity.getName() != null && updatedActivity.getTime() != null && updatedActivity.getLocation() != null && updatedActivity.getCost() != null) {
			existingActivity.setTripId(updatedActivity.getTripId());
			existingActivity.setName(updatedActivity.getName());
			existingActivity.setTime(updatedActivity.getTime());
			existingActivity.setLocation(updatedActivity.getLocation());
			existingActivity.setCost(updatedActivity.getCost());
		}
		
		activitiesMap.put(id, existingActivity);
		System.out.println("Activity ID: " + id + " updated successfully.");
		return existingActivity;
	}
	
	// Delete activity by id
	public Activities deleteActivity(int id) {
		System.out.println("Activity with ID: " + id + " was deleted successfully.");
		return activitiesMap.remove(id);
    }
}
