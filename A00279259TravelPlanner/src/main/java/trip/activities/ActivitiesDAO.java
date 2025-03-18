package trip.activities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public enum ActivitiesDAO {
	
	instance;
	
	private DataSource dataSource;
	
	// Initialize db
	
	
	// Get all activities 
	public List<Activities> getActivities() {
        List<Activities> activities = new ArrayList<>();
        String sql = "SELECT * FROM Activities";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all activities.");
            e.printStackTrace();
        }
        return activities;
    }
	
	// Get activity by trip_id
	public List<Activities> getActivitiesByTrip(int tripId) {
        List<Activities> activities = new ArrayList<>();
        String sql = "SELECT * FROM Activities WHERE trip_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching activities for trip ID: " + tripId);
            e.printStackTrace();
        }
        return activities;
    }
	
	// Get one actrivity by id
	public Activities getActivity(int id) {
        String sql = "SELECT * FROM Activities WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToActivity(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching activity with ID: " + id);
            e.printStackTrace();
        }
        return null;
    }
	
	// Add new activity
	public void addActivity(Activities activity) {
        String sql = "INSERT INTO Activities (trip_id, name, time, location, cost) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, activity.getTripId());
            pstmt.setString(2, activity.getName());
            pstmt.setTime(3, Time.valueOf(activity.getTime()));
            pstmt.setString(4, activity.getLocation());
            pstmt.setBigDecimal(5, activity.getCost());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding new activity.");
            e.printStackTrace();
        }
    }
	
	// Update activity by id 
	
	// Delete activity by id
	public void deleteActivity(int id) {
        String sql = "DELETE FROM Activities WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting activity with ID: " + id);
            e.printStackTrace();
        }
    }
	
	private Activities mapResultSetToActivity(ResultSet rs) throws SQLException {
        return new Activities(
            rs.getInt("id"),
            rs.getInt("trip_id"),
            rs.getString("name"),
            rs.getTime("time").toLocalTime(),
            rs.getString("location"),
            rs.getBigDecimal("cost")
        );
    }

}
