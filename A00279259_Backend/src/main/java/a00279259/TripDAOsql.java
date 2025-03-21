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
			initializeTrips();
			// Initialize Activities after Trips
			ActivitiesDAOsql.instance.initializeActivities();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Initialize data in trips db
	private void initializeTrips() {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM trips")) {
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			if (count == 0) {
				System.out.println("No trips found, inserting default trips...");
				insertInitialTrips();
				connection.commit();
			} else {
				System.out.println("Trips already exist in the database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertInitialTrips() {
		addTrip(new Trip("New York, USA", "14-01-2025", "20-01-2025", BigDecimal.valueOf(2500.00), "Exploring NYC, food tour and Times Square"));
	    addTrip(new Trip("Chania, Crete, Greece", "18-09-2025", "28-09-2025", BigDecimal.valueOf(2000.00), "Chilling on the beach, visit Samaria Gorge."));
	    addTrip(new Trip("Santorini, Greece", "15-09-2025", "18-09-2025", BigDecimal.valueOf(1500.00), "Sunset views, volcano tour, and buggy adventure."));
	    addTrip(new Trip("Bali, Indonesia", "06-06-2025", "17-06-2025", BigDecimal.valueOf(2700.00), "Temple visits, rice terraces, and scuba diving."));
	    addTrip(new Trip("Krakow, Poland", "20-12-2025", "02-01-2026", BigDecimal.valueOf(1500.00), "Exploring old town, Auschwitz tour, and visit home."));
	    
	    System.out.println("Initial trips inserted successfully.");
	}
	
	// Get all trips
	public List<Trip> getTrips() {
		List<Trip> trips = new ArrayList<Trip>();

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM trips")) {
			while (rs.next()) {
				Trip t = new Trip();
				t.setTripId(rs.getInt("tripId"));
	            t.setDestination(rs.getString("destination") != null ? rs.getString("destination") : "TBC");
	            t.setStartDate(rs.getString("startDate") != null ? rs.getString("startDate") : "TBC");
	            t.setEndDate(rs.getString("endDate") != null ? rs.getString("endDate") : "TBC");
	            t.setBudget(rs.getBigDecimal("budget") != null ? rs.getBigDecimal("budget") : BigDecimal.ZERO);
	            t.setNotes(rs.getString("notes") != null ? rs.getString("notes") : "TBC");
	            trips.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("getTrips() :: called");
		trips.addAll(tripsMap.values());
		return trips;
	}
	
	// Get trip by id
	public Trip getTrip(int tripId) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM trips WHERE tripId = ?")) {
			stmt.setInt(1, tripId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Trip t = new Trip();
				t.setTripId(rs.getInt("tripId"));
				t.setDestination(rs.getString("destination") != null ? rs.getString("destination") : "TBC");
	            t.setStartDate(rs.getString("startDate") != null ? rs.getString("startDate") : "TBC");
	            t.setEndDate(rs.getString("endDate") != null ? rs.getString("endDate") : "TBC");
	            t.setBudget(rs.getBigDecimal("budget") != null ? rs.getBigDecimal("budget") : BigDecimal.ZERO);
	            t.setNotes(rs.getString("notes") != null ? rs.getString("notes") : "TBC");
				
				// Fetch activities for the trip id
				List<Activities> activities = getActivitiesByTrip(tripId);
	            t.setActivities(activities);
				
				System.out.println("getTrip(" + tripId + ") :: called");
				return t;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tripsMap.get(tripId);
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
                    rs.getString("name") != null ? rs.getString("name") : "TBC",
                    rs.getString("activityDate") != null ? rs.getString("activityDate") : "TBC",
                    rs.getString("location") != null ? rs.getString("location") : "TBC",
                    rs.getBigDecimal("cost") != null ? rs.getBigDecimal("cost") : BigDecimal.ZERO
                );
                activities.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("getActivitiesByTrip("+ tripId + ") :: called");
        return activities;
    }
	
	// Add new trip
	public Trip addTrip(Trip trip) {
		String sql = "INSERT INTO trips (destination, startDate, endDate, budget, notes) VALUES (?, ?, ?, ?, ?)";
	
		// @XmlType(propOrder = { "tripId", "destination", "startDate", "endDate", "budget", "notes" } ) // Order in which the data will be placed
		try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, trip.getDestination());
            stmt.setString(2, trip.getStartDate());
            stmt.setString(3, trip.getEndDate());
            stmt.setBigDecimal(4, trip.getBudget());
            stmt.setString(5, trip.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating trip failed, no rows affected.\n");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setTripId(generatedKeys.getInt(1));
                    System.out.println("New ID: " + trip.getTripId());
                } else {
                    throw new SQLException("Creating trip failed, no ID obtained.\n");
                }
            }
        } catch (SQLException e) {
        	System.out.println("addTrip() :: couldnt add new trip.\n");
            e.printStackTrace();
        }
		System.out.println("Trip added successfully.\n\t" + trip);
		return trip;
	}
	
	// Edit trip
	public Trip updateTrip(int tripId, Trip updatedTrip) throws SQLException {
		
		String sql = "UPDATE trips SET destination = ?, startDate = ?, endDate = ?, budget = ?, notes = ? WHERE tripId = ?";
		
		try (PreparedStatement stmt = connection.prepareStatement(sql)){
			
			// Check if trip exists
			Trip existingTrip = getTrip(tripId);
			System.out.println("existingTrip: " + existingTrip);
			
	        if (existingTrip == null) {
	            return null;
	        }
	        
	        // Use existing values if new values are null
	        String destination = updatedTrip.getDestination() != null ? updatedTrip.getDestination() : existingTrip.getDestination();
	        String startDate = updatedTrip.getStartDate() != null ? updatedTrip.getStartDate() : existingTrip.getStartDate();
	        String endDate = updatedTrip.getEndDate() != null ? updatedTrip.getEndDate() : existingTrip.getEndDate();
	        BigDecimal budget = updatedTrip.getBudget() != null ? updatedTrip.getBudget() : existingTrip.getBudget();
	        String notes = updatedTrip.getNotes() != null ? updatedTrip.getNotes() : existingTrip.getNotes();

	        stmt.setString(1, destination);
	        stmt.setString(2, startDate);
	        stmt.setString(3, endDate);
	        stmt.setBigDecimal(4, budget);
	        stmt.setString(5, notes);
	        stmt.setInt(6, tripId);
	        
	        int rowsUpdated = stmt.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            System.out.println("updatedTrip: " + updatedTrip);
	            System.out.println("\nupdateTrip(" + tripId + ") :: Updated successfully.");
	            return getTrip(tripId); 
	        } else {
	            System.out.println("\nFailed to update trip with ID " + tripId + ".\n");
	            return null;
	        }
		} catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }	
	}
	
	// Delete trip
	public Trip deleteTrip(int tripId) {
		try (Statement stmt = connection.createStatement()) {
			int affectedRows = stmt.executeUpdate("DELETE FROM trips WHERE tripId = " + tripId);

            if (affectedRows > 0) {
            	System.out.println("deleteTrip(" + tripId + ") :: deleted successfully.");
                return tripsMap.remove(tripId);
            } else {
            	System.out.println("deleteTrip(" + tripId + ") :: No trip found with that ID.");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
