package trip;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;

public enum TripDAO {
	
	instance;
	
	private DataSource dataSource;
	
	private TripDAO() {
        try {
            Context ctx = (Context) new InitialContext();
            dataSource = (DataSource) ((InitialContext) ctx).lookup("java:comp/env/jdbc/travelDB");
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed!", e);
        }
    }
	
	// GET all trips
	public List<Trip> getTrips() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM Trips";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }
	
	// GET one trip
	public Trip getTrip(int id) {
        String sql = "SELECT * FROM Trips WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTrip(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	// Add new trip
	public void addTrip(Trip trip) {
        String sql = "INSERT INTO Trips (destination, start_date, end_date, budget, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trip.getDestination());
            pstmt.setDate(2, Date.valueOf(trip.getStartDate()));
            pstmt.setDate(3, Date.valueOf(trip.getEndDate()));
            pstmt.setBigDecimal(4, trip.getBudget());
            pstmt.setString(5, trip.getNotes());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	// Update existing trip by ID
	public void updateTrip(Trip trip) {
        String sql = "UPDATE Trips SET destination=?, start_date=?, end_date=?, budget=?, notes=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trip.getDestination());
            pstmt.setDate(2, Date.valueOf(trip.getStartDate()));
            pstmt.setDate(3, Date.valueOf(trip.getEndDate()));
            pstmt.setBigDecimal(4, trip.getBudget());
            pstmt.setString(5, trip.getNotes());
            pstmt.setInt(6, trip.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	// Delete trip by ID
	public void deleteTrip(int id) {
        String sql = "DELETE FROM Trips WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	// Convert sql result to Trip obj
	private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        return new Trip(
            rs.getInt("id"),
            rs.getString("destination"),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getBigDecimal("budget"),
            rs.getString("notes")
        );
    }
	
}
