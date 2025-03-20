package a00279259;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a00279259.activities.Activities;

// http://localhost:8080/a00279259/rest/activities
public enum ActivitiesDAOsql {
	instance;
	
	private Connection connection;
	private Map<Integer, Activities> activitiesMap = new HashMap<Integer, Activities>();
	
	// Initialize DB connection
    private ActivitiesDAOsql() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/oneDB", "SA", "Passw0rd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Initialize activities data in activities db
    void initializeActivities() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM activities")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                System.out.println("No activities found, inserting default activities...");
                insertInitialActivities();
            } else {
                System.out.println("Activities already exist in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void insertInitialActivities() {
    	// NYC Activities 
    			addActivity(new Activities(1, 1, "Visit Statue of Liberty and Ellis Island", "10-06-2025", "New York, USA", BigDecimal.valueOf(59.95)));
    		    addActivity(new Activities(2, 1, "Visit Times Square", "11-06-2025", "New York, USA", BigDecimal.ZERO));
    		    addActivity(new Activities(3, 1, "Visit Rockefeller Center", "06-12-2025", "New York, USA", BigDecimal.valueOf(25.00)));

    			// Crete Activities
    		    addActivity(new Activities(4, 2, "Visit Samaria Gorge", "19-09-2025", "Chania, Crete, Greece", BigDecimal.valueOf(45.00)));
    		    addActivity(new Activities(5, 2, "Visit Sainta Limania", "20-09-2025", "Chania, Crete, Greece", BigDecimal.valueOf(15.00)));
    		    addActivity(new Activities(6, 2, "Visit Balos", "21-09-2025", "Chania, Crete, Greece", BigDecimal.valueOf(30.00)));
    		    addActivity(new Activities(7, 2, "Visit Elafonisi", "22-09-2025", "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
    		    addActivity(new Activities(8, 2, "Visit Fallasarna", "23-09-2025", "Chania, Crete, Greece", BigDecimal.valueOf(40.00)));
    			
    		    // Santorini Activities 
    		    addActivity(new Activities(9, 3, "Visit Oia", "15-09-2025", "Santorini, Greece", BigDecimal.ZERO));
    		    addActivity(new Activities(10, 3, "Visit Thera", "16-09-2025", "Santorini, Greece", BigDecimal.ZERO));
    		    addActivity(new Activities(11, 3, "Visit Red Beach", "17-09-2025", "Santorini, Greece", BigDecimal.ZERO));
    		    addActivity(new Activities(12, 3, "Visit White Beach", "17-09-2025", "Santorini, Greece", BigDecimal.ZERO));
    		    addActivity(new Activities(13, 3, "Do Buggie Adventure", "16-09-2025", "Santorini, Greece", BigDecimal.valueOf(160.00)));
    		    
    			// Bali Activities 
    		    addActivity(new Activities(14, 4, "Explore Ubud Monkey Forest", "10-06-2025", "Ubud, Bali, Indonesia", BigDecimal.valueOf(10.00)));
    		    addActivity(new Activities(15, 4, "Visit Tanah Lot Temple", "11-06-2025", "Bali, Indonesia", BigDecimal.valueOf(6.00)));
    		    addActivity(new Activities(16, 4, "Experience Bali Swing", "12-06-2025", "Bali, Indonesia", BigDecimal.valueOf(60.00)));
    		    
    			// Krakow Activities 
    		    addActivity(new Activities(17, 5, "Visit Main Square", "21-12-2025", "Krakow, Poland", BigDecimal.ZERO));
    		    addActivity(new Activities(18, 5, "Visit Auschwitz", "22-12-2025", "Oswiecim, Poland", BigDecimal.valueOf(80.00)));
    		    addActivity(new Activities(19, 5, "Visit Salt Mine", "23-12-2025", "Wieliczka, Poland", BigDecimal.valueOf(50.00)));
    		    addActivity(new Activities(20, 5, "Go Shopping (Galeria Krakowska & Bonarka", "21-12-2025", "Krakow, Poland", BigDecimal.valueOf(800.00)));
    		    addActivity(new Activities(21, 5, "Go Home (Muszyna)", "23-12-2025", "Muszyna, Poland", BigDecimal.valueOf(35.00)));

        System.out.println("Initial activities inserted successfully.");
    }
    
    // Get all activities
    public List<Activities> getActivities() {
        List<Activities> activities = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM activities")) {

        	while (rs.next()) {
                Activities a = new Activities();
                a.setActivityId(rs.getInt("activityId"));
                a.setTripId(rs.getInt("tripId"));
                a.setName(rs.getString("name"));
                a.setActivityDate(rs.getString("activityDate"));
                a.setLocation(rs.getString("location"));
                a.setCost(rs.getBigDecimal("cost"));
                activities.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getActivities() :: called");
        return activities;
    }
    
    // Get activity by ID
    public Activities getActivity(int activityId) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM activities WHERE activityId = ?")) {
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Activities(
                        rs.getInt("activityId"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getString("activityDate"),
                        rs.getString("location"),
                        rs.getBigDecimal("cost")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getActivity("+ activityId + ") :: called");
        return null;
    }
    
    // Add a new activity
    public Activities addActivity(Activities activity) {
    	System.out.println("Adding activity for tripId: " + activity.getTripId());
    	
    	if (!tripExists(activity.getTripId())) {
            System.out.println("Error: Trip with ID " + activity.getTripId() + " does not exist. Activity not added.");
            return null;
        }
    	
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO activities (tripId, name, activityDate, location, cost) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, activity.getTripId());
            stmt.setString(2, activity.getName());
            stmt.setString(3, activity.getActivityDate());
            stmt.setString(4, activity.getLocation());
            stmt.setBigDecimal(5, activity.getCost());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating activity failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    activity.setActivityId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("New activity added successfully.\n\t" + activity);
        return activity;
    }
    
    private boolean tripExists(int tripId) {
    	try (PreparedStatement stmt = connection.prepareStatement("SELECT * from trips where tripId = ?")){
    		stmt.setInt(1,  tripId);
    		ResultSet rs = stmt.executeQuery();
    		return rs.next();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    // Update an existing activity
    public Activities updateActivity(int activityId, Activities updatedActivity) {
    	
    	String sql = "UPDATE activities SET tripId = ?, name = ?, activityDate = ?, location = ?, cost = ? WHERE activityId = ?";
		
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Check if the activity exists
            Activities existingActivity = getActivity(activityId);
            System.out.println("existingActivity: " + existingActivity);
            
            if (existingActivity == null) {
                return null;
            }

            // Use existing values if new values are null
            int tripId = (updatedActivity.getTripId() > 0) ? updatedActivity.getTripId() : existingActivity.getTripId();
            String name = (updatedActivity.getName() != null) ? updatedActivity.getName() : existingActivity.getName();
            String activityDate = (updatedActivity.getActivityDate() != null) ? updatedActivity.getActivityDate() : existingActivity.getActivityDate();
            String location = (updatedActivity.getLocation() != null) ? updatedActivity.getLocation() : existingActivity.getLocation();
            BigDecimal cost = (updatedActivity.getCost() != null) ? updatedActivity.getCost() : existingActivity.getCost();

            stmt.setInt(1, tripId);
            stmt.setString(2, name);
            stmt.setString(3, activityDate);
            stmt.setString(4, location);
            stmt.setBigDecimal(5, cost);
            stmt.setInt(6, activityId);

            int rowsUpdated = stmt.executeUpdate();
      
            if (rowsUpdated > 0) {
            	System.out.println("updatedActivity: " + updatedActivity);
        		System.out.println("\nupdateActivity(" + activityId + ") :: updated successfully.\n");
                return getActivity(activityId);
            } else {
                System.out.println("\nFailed to update activity with ID " + activityId + ".\n");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Delete an activity by ID
    public Activities deleteActivity(int activityId) {
    	try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate("DELETE FROM activities WHERE activityId = " + activityId);

            if (affectedRows > 0) {
                System.out.println("deleteActivity(" + activityId + ") :: deleted successfully.");
                return activitiesMap.remove(activityId);
            } else {
                System.out.println("Activity with ID " + activityId + " not found.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
