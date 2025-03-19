package a00279259.activities;

import java.math.BigDecimal;
import java.sql.Date;
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
		addActivity(new Activities(1, 1, "Visit Statue of Liberty and Ellis Island", Date.valueOf("2025-06-10"), "New York, USA", BigDecimal.valueOf(59.95)));
	    addActivity(new Activities(2, 1, "Visit Times Square", Date.valueOf("2025-06-11"), "New York, USA", BigDecimal.ZERO));
	    addActivity(new Activities(3, 1, "Visit Rockefeller Center", Date.valueOf("2025-06-12"), "New York, USA", BigDecimal.valueOf(25.00)));

		// Crete Activities
	    addActivity(new Activities(1, 2, "Visit Samaria Gorge", Date.valueOf("2025-09-19"), "Chania, Crete, Greece", BigDecimal.valueOf(45.00)));
	    addActivity(new Activities(2, 2, "Visit Sainta Limania", Date.valueOf("2025-09-20"), "Chania, Crete, Greece", BigDecimal.valueOf(15.00)));
	    addActivity(new Activities(3, 2, "Visit Balos", Date.valueOf("2025-09-21"), "Chania, Crete, Greece", BigDecimal.valueOf(30.00)));
	    addActivity(new Activities(4, 2, "Visit Elafonisi", Date.valueOf("2025-09-22"), "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
	    addActivity(new Activities(5, 2, "Visit Fallasarna", Date.valueOf("2025-09-23"), "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
		
	    // Santorini Activities 
	    addActivity(new Activities(1, 3, "Visit Oia",  Date.valueOf("2025-07-15"), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(2, 3, "Visit Thera", Date.valueOf("2025-07-16"), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(3, 3, "Visit Red Beach", Date.valueOf("2025-07-17"), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(4, 3, "Visit White Beach", Date.valueOf("2025-07-18"), "Santorini, Greece", BigDecimal.ZERO));
	    addActivity(new Activities(5, 3, "Do Buggie Adventure", Date.valueOf("2025-07-19"), "Santorini, Greece", BigDecimal.valueOf(160.00)));
	    
		// Bali Activities 
	    addActivity(new Activities(1, 4, "Explore Ubud Monkey Forest", Date.valueOf("2025-08-10"), "Ubud, Bali, Indonesia", BigDecimal.valueOf(10.00)));
	    addActivity(new Activities(2, 4, "Visit Tanah Lot Temple", Date.valueOf("2025-08-11"), "Bali, Indonesia", BigDecimal.valueOf(6.00)));
	    addActivity(new Activities(3, 4, "Experience Bali Swing", Date.valueOf("2025-08-12"), "Bali, Indonesia", BigDecimal.valueOf(60.00)));
	    
		// Krakow Activities 
	    addActivity(new Activities(1, 5, "Visit Main Square", Date.valueOf("2025-12-21"), "Krakow, Poland", BigDecimal.ZERO));
	    addActivity(new Activities(2, 5, "Visit Auschwitz", Date.valueOf("2025-12-22"), "Oświęcim, Poland", BigDecimal.valueOf(80.00)));
	    addActivity(new Activities(3, 5, "Visit Salt Mine", Date.valueOf("2025-12-23"), "Wieliczka, Poland", BigDecimal.valueOf(50.00)));
	    addActivity(new Activities(4, 5, "Go Shopping (Galeria Krakowska & Bonarka)", Date.valueOf("2025-12-24"), "Krakow, Poland", BigDecimal.valueOf(800.00)));
	    addActivity(new Activities(5, 5, "Go Home (Muszyna)", Date.valueOf("2025-12-25"), "Muszyna, Poland", BigDecimal.valueOf(35.00)));
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
