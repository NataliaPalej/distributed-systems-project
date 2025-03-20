package a00279259.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ActivitiesDAO {
	
	instance;
	
	private Map<Integer, Activities> activitiesMap = new HashMap<>();
	private int activityIdCounter = 1;
	
	// Constructor to initialize activities
	private ActivitiesDAO() {
		// NYC Activities 
		addActivity(new Activities(1, 1, "Visit Statue of Liberty and Ellis Island", "2025-06-10", "New York, USA", BigDecimal.valueOf(59.95)));
	    addActivity(new Activities(2, 1, "Visit Times Square", "2025-06-11", "New York, USA", BigDecimal.ZERO));
	    addActivity(new Activities(3, 1, "Visit Rockefeller Center", "2025-06-12", "New York, USA", BigDecimal.valueOf(25.00)));

		// Crete Activities
	    addActivity(new Activities(4, 2, "Visit Samaria Gorge", "2025-09-19", "Chania, Crete, Greece", BigDecimal.valueOf(45.00)));
	    addActivity(new Activities(5, 2, "Visit Sainta Limania", "2025-09-20", "Chania, Crete, Greece", BigDecimal.valueOf(15.00)));
	    addActivity(new Activities(6, 2, "Visit Balos", "2025-09-21", "Chania, Crete, Greece", BigDecimal.valueOf(30.00)));
	    addActivity(new Activities(7, 2, "Visit Elafonisi", "2025-09-22", "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
	    addActivity(new Activities(8, 2, "Visit Fallasarna", "2025-09-23", "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
		
	    // Santorini Activities 
	    addActivity(new Activities(9, 3, "Visit Oia", "2025-07-15", "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(10, 3, "Visit Thera", "2025-07-16", "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(11, 3, "Visit Red Beach", "2025-07-17", "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(12, 3, "Visit White Beach", "2025-07-18", "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(13, 3, "Do Buggie Adventure", "2025-07-19", "Santorini, Greece", BigDecimal.valueOf(160.00)));
	    
		// Bali Activities 
	    addActivity(new Activities(14, 4, "Explore Ubud Monkey Forest", "2025-08-10", "Ubud, Bali, Indonesia", BigDecimal.valueOf(10.00)));
	    addActivity(new Activities(15, 4, "Visit Tanah Lot Temple", "2025-08-11", "Bali, Indonesia", BigDecimal.valueOf(6.00)));
	    addActivity(new Activities(16, 4, "Experience Bali Swing", "2025-08-12", "Bali, Indonesia", BigDecimal.valueOf(60.00)));
	    
		// Krakow Activities 
	    addActivity(new Activities(17, 5, "Visit Main Square", "2025-12-21", "Krakow, Poland", BigDecimal.ZERO));
	    addActivity(new Activities(18, 5, "Visit Auschwitz", "2025-12-22", "Oświęcim, Poland", BigDecimal.valueOf(80.00)));
	    addActivity(new Activities(19, 5, "Visit Salt Mine", "2025-12-23", "Wieliczka, Poland", BigDecimal.valueOf(50.00)));
	    addActivity(new Activities(20, 5, "Go Shopping (Galeria Krakowska & Bonarka", "2025-12-24", "Krakow, Poland", BigDecimal.valueOf(800.00)));
	    addActivity(new Activities(21, 5, "Go Home (Muszyna)", "2025-12-25", "Muszyna, Poland", BigDecimal.valueOf(35.00)));
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
        activity.setActivityId(activityIdCounter++);
        activitiesMap.put(activity.getActivityId(), activity);
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
		if (updatedActivity.getTripId() > 0 && updatedActivity.getName() != null && updatedActivity.getActivityDate() != null && updatedActivity.getLocation() != null && updatedActivity.getCost() != null) {
			existingActivity.setTripId(updatedActivity.getTripId());
			existingActivity.setName(updatedActivity.getName());
			existingActivity.setActivityDate(updatedActivity.getActivityDate());
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
