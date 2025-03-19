package a00279259;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import a00279259.activities.Activities;

// http://localhost:8080/a00279259/rest/activities
public enum ActivitiesDAOsql {
	instance;
	
	private Connection connection;
	
	// Initialize DB connection
    private ActivitiesDAOsql() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/oneDB", "SA", "Passw0rd");
            initializeActivities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Initialize activities data in activities db
    private void initializeActivities() {
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
        String sql = "INSERT INTO activities (tripId, name, activityDate, location, cost) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // NYC Activities
            addActivity(stmt, 1, "Visit Statue of Liberty and Ellis Island", "2025-06-10", "New York, USA", 59.95);
            addActivity(stmt, 1, "Visit Times Square", "2025-06-11", "New York, USA", 0);
            addActivity(stmt, 1, "Visit Rockefeller Center", "2025-06-12", "New York, USA", 25.00);

            // Crete Activities
            addActivity(stmt, 2, "Visit Samaria Gorge", "2025-09-19", "Chania, Crete, Greece", 45.00);
            addActivity(stmt, 2, "Visit Sainta Limania", "2025-09-20", "Chania, Crete, Greece", 15.00);
            addActivity(stmt, 2, "Visit Balos", "2025-09-21", "Chania, Crete, Greece", 30.00);

            // Santorini Activities
            addActivity(stmt, 3, "Visit Oia", "2025-07-15", "Santorini, Greece", 0);
            addActivity(stmt, 3, "Visit Thera", "2025-07-16", "Santorini, Greece", 0);
            addActivity(stmt, 3, "Visit Red Beach", "2025-07-17", "Santorini, Greece", 0);

            // Bali Activities
            addActivity(stmt, 4, "Explore Ubud Monkey Forest", "2025-08-10", "Ubud, Bali, Indonesia", 10.00);
            addActivity(stmt, 4, "Visit Tanah Lot Temple", "2025-08-11", "Bali, Indonesia", 6.00);
            addActivity(stmt, 4, "Experience Bali Swing", "2025-08-12", "Bali, Indonesia", 60.00);

            // Krakow Activities
            addActivity(stmt, 5, "Visit Main Square", "2025-12-21", "Krakow, Poland", 0);
            addActivity(stmt, 5, "Visit Auschwitz", "2025-12-22", "Oświęcim, Poland", 80.00);
            addActivity(stmt, 5, "Visit Salt Mine", "2025-12-23", "Wieliczka, Poland", 50.00);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addActivity(PreparedStatement stmt, int tripId, String name, String date, String location, double cost) throws SQLException {
        stmt.setInt(1, tripId);
        stmt.setString(2, name);
        stmt.setDate(3, Date.valueOf(date));
        stmt.setString(4, location);
        stmt.setBigDecimal(5, BigDecimal.valueOf(cost));
        stmt.executeUpdate();
    }
    
    // Get all activities
    public List<Activities> getActivities() {
        List<Activities> activities = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM activities")) {

            while (rs.next()) {
                Activities a = new Activities(
                        rs.getInt("activityId"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("activityDate"),
                        rs.getString("location"),
                        rs.getBigDecimal("cost")
                );
                activities.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activities;
    }
    
    // Get activities for specific trip
    public List<Activities> getActivitiesByTrip(int tripId) {
        List<Activities> activities = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM activities WHERE tripId = ?")) {
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Activities a = new Activities(
                        rs.getInt("activityId"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("activityDate"),
                        rs.getString("location"),
                        rs.getBigDecimal("cost")
                );
                activities.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activities;
    }
    
    // Get activity by ID
    public Activities getActivity(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM activities WHERE activityId = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Activities(
                        rs.getInt("activityId"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("activityDate"),
                        rs.getString("location"),
                        rs.getBigDecimal("cost")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Add a new activity
    public Activities addActivity(Activities activity) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO activities (tripId, name, activityDate, location, cost) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, activity.getTripId());
            stmt.setString(2, activity.getName());
            stmt.setDate(3, activity.getActivityDate());
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

        return activity;
    }
    
    // Update an existing activity
    public Activities updateActivity(int id, Activities updatedActivity) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE activities SET tripId = ?, name = ?, activityDate = ?, location = ?, cost = ? WHERE activityId = ?")) {

            // Check if the activity exists
            Activities existingActivity = getActivity(id);
            if (existingActivity == null) {
                System.out.println("Activity with ID " + id + " not found.");
                return null;
            }

            // Use existing values if new values are null
            int tripId = (updatedActivity.getTripId() > 0) ? updatedActivity.getTripId() : existingActivity.getTripId();
            String name = (updatedActivity.getName() != null) ? updatedActivity.getName() : existingActivity.getName();
            Date activityDate = (updatedActivity.getActivityDate() != null) ? updatedActivity.getActivityDate() : existingActivity.getActivityDate();
            String location = (updatedActivity.getLocation() != null) ? updatedActivity.getLocation() : existingActivity.getLocation();
            BigDecimal cost = (updatedActivity.getCost() != null) ? updatedActivity.getCost() : existingActivity.getCost();

            stmt.setInt(1, tripId);
            stmt.setString(2, name);
            stmt.setDate(3, activityDate);
            stmt.setString(4, location);
            stmt.setBigDecimal(5, cost);
            stmt.setInt(6, id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Activity with ID " + id + " updated successfully.");
                return getActivity(id);
            } else {
                System.out.println("Failed to update activity with ID " + id + ".");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Delete an activity by ID
    public Activities deleteActivity(int id) {
        Activities activityToDelete = getActivity(id);

        if (activityToDelete == null) {
            System.out.println("Activity with ID " + id + " not found.");
            return null;
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM activities WHERE activityId = ?")) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Activity with ID " + id + " deleted successfully.");
                return activityToDelete;
            } else {
                System.out.println("Failed to delete activity with ID " + id + ".");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}
