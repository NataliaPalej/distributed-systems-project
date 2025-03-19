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

import a00279259.trips.Trip;

public enum TripDAOsql {
	
	instance;
	
	private Connection connection;
	private Map<Integer, Trip> tripsMap = new HashMap<Integer, Trip>();

	// Initialize db connection
	private TripDAOsql() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/oneDB", "SA", "Passw0rd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Get all trips
	public List<Trip> getTrips() {
		List<Trip> trips = new ArrayList<Trip>();

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM trip")) {
			while (rs.next()) {
				Trip t = new Trip();
				t.setId(rs.getInt("id"));
				t.setDestination(rs.getString("destination"));
				t.setStartDate(rs.getDate("startDate"));
				t.setEndDate(rs.getDate("endDate"));
				t.setBudget(rs.getBigDecimal("budget"));
				t.setNotes(rs.getString("notes"));
				trips.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		trips.addAll(tripsMap.values());
		return trips;
	}
	
	// Get trip by id
	public Trip getTrip(int id) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM trip WHERE id = ?")) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Trip t = new Trip();
				t.setId(rs.getInt("id"));
				t.setDestination(rs.getString("destination"));
				t.setStartDate(rs.getDate("startDate"));
				t.setEndDate(rs.getDate("endDate"));
				t.setBudget(rs.getBigDecimal("budget"));
				t.setNotes(rs.getString("notes"));
				return t;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tripsMap.get(id);
	}
	
	// Add new trip
	public Trip addTrip(Trip trip) {
		int tripId = -1;

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM trip")) {
			rs.next();
			tripId = rs.getInt("total") + 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("New ID: " + tripId);
		// @XmlType(propOrder = { "tripId", "destination", "startDate", "endDate", "budget", "notes" } ) // Order in which the data will be placed
		try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO trip (tripId, destination, startDate, endDate, budget, notes) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, tripId);
			stmt.setString(2, trip.getDestination());
            stmt.setDate(3, trip.getStartDate());
            stmt.setDate(4, trip.getEndDate());
            stmt.setBigDecimal(5, trip.getBudget());
            stmt.setString(6, trip.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		trip.setId(tripId);
		return trip;
	}
	
	// Edit trip
	public Trip editTrip(int tripId, Trip updatedTrip) {
		try (PreparedStatement stmt = connection.prepareStatement(
	            "UPDATE trip SET destination = ?, startDate = ?, endDate = ?, budget = ?, notes = ? WHERE id = ?")) {

	        // Update only non-null fields
	        Trip existingTrip = getTrip(tripId);
	        if (existingTrip == null) {
	            System.out.println("Trip with ID " + tripId + " not found.");
	            return null;
	        }

	        // Use existing values if new values are null
	        String destination = updatedTrip.getDestination() != null ? updatedTrip.getDestination() : existingTrip.getDestination();
	        java.sql.Date startDate = updatedTrip.getStartDate() != null ? updatedTrip.getStartDate() : existingTrip.getStartDate();
	        java.sql.Date endDate = updatedTrip.getEndDate() != null ? updatedTrip.getEndDate() : existingTrip.getEndDate();
	        BigDecimal budget = updatedTrip.getBudget() != null ? updatedTrip.getBudget() : existingTrip.getBudget();
	        String notes = updatedTrip.getNotes() != null ? updatedTrip.getNotes() : existingTrip.getNotes();

	        // Set parameters
	        stmt.setString(1, destination);
	        stmt.setDate(2, startDate);
	        stmt.setDate(3, endDate);
	        stmt.setBigDecimal(4, budget);
	        stmt.setString(5, notes);
	        stmt.setInt(6, tripId);

	        int rowsUpdated = stmt.executeUpdate();
	        if (rowsUpdated > 0) {
	            System.out.println("Trip with ID " + tripId + " updated successfully.");
	            // Return updated trip
	            return getTrip(tripId); 
	        } else {
	            System.out.println("Failed to update trip with ID " + tripId + ".");
	            return null;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	// Delete trip
	public Trip deleteTrip(int tripId) {
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("DELETE FROM trip WHERE id =" + tripId)) {
			rs.next();
			tripId = rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tripsMap.remove(tripId);
	}
}
