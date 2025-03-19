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

public enum ActivitiesDAOsql {
	instance;
	
	private Connection connection;
	
	// Initialize DB connection
    private ActivitiesDAOsql() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/oneDB", "SA", "Passw0rd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get all activities
    public List<Activities> getActivities() {
        List<Activities> activities = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM activities")) {

            while (rs.next()) {
                Activities a = new Activities(
                        rs.getInt("id"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("time"),
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
                        rs.getInt("id"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("time"),
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
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM activities WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Activities(
                        rs.getInt("id"),
                        rs.getInt("tripId"),
                        rs.getString("name"),
                        rs.getDate("time"),
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
                "INSERT INTO activities (tripId, name, time, location, cost) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, activity.getTripId());
            stmt.setString(2, activity.getName());
            stmt.setDate(3, activity.getTime());
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
                "UPDATE activities SET tripId = ?, name = ?, time = ?, location = ?, cost = ? WHERE id = ?")) {

            // Check if the activity exists
            Activities existingActivity = getActivity(id);
            if (existingActivity == null) {
                System.out.println("Activity with ID " + id + " not found.");
                return null;
            }

            // Use existing values if new values are null
            int tripId = (updatedActivity.getTripId() > 0) ? updatedActivity.getTripId() : existingActivity.getTripId();
            String name = (updatedActivity.getName() != null) ? updatedActivity.getName() : existingActivity.getName();
            Date time = (updatedActivity.getTime() != null) ? updatedActivity.getTime() : existingActivity.getTime();
            String location = (updatedActivity.getLocation() != null) ? updatedActivity.getLocation() : existingActivity.getLocation();
            BigDecimal cost = (updatedActivity.getCost() != null) ? updatedActivity.getCost() : existingActivity.getCost();

            stmt.setInt(1, tripId);
            stmt.setString(2, name);
            stmt.setDate(3, time);
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

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM activities WHERE id = ?")) {
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
