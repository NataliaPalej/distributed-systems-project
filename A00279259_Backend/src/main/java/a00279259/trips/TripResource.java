package a00279259.trips;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import a00279259.TripDAOsql;
import a00279259.activities.Activities;

// http://localhost:8080/A00279259_Backend/rest/trips
@Path("/trips")
public class TripResource {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<Trip> getTrips(){
		return TripDAOsql.instance.getTrips();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("{tripId}")
	public Trip getTrip(@PathParam("tripId") String tripId){
		return TripDAOsql.instance.getTrip(Integer.parseInt(tripId));
	}
	
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{tripId}/activities")
    public List<Activities> getActivitiesByTrip(@PathParam("tripId") String tripId) {
		return TripDAOsql.instance.getActivitiesByTrip(Integer.parseInt(tripId));
    }
	
	@GET
	@Path("/activity-stats")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<Map<String, Object>> getTripActivityStats() {
	    return TripDAOsql.instance.getActivityStatsPerTrip();
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Trip addTrip(Trip trip) {
	    Trip newTrip = TripDAOsql.instance.addTrip(trip);

	    if (newTrip == null) {
	        System.out.println("Error inserting trip.");
	        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	    }

	    System.out.println("New trip added with ID: " + newTrip.getTripId());
	    return newTrip;
	}

	@PUT
	@Path("{tripId}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	public Trip updateTrip(@PathParam("tripId") int tripId, Trip updatedTrip) throws SQLException {
		
		Trip existingTrip = TripDAOsql.instance.getTrip(tripId);
		
		if (existingTrip == null) {
			System.out.println("Trip with ID " + tripId + " not found.");
			return null;
		}

		// Update only not null fields
		existingTrip.setDestination(updatedTrip.getDestination() != null ? updatedTrip.getDestination() : existingTrip.getDestination());
	    existingTrip.setStartDate(updatedTrip.getStartDate() != null ? updatedTrip.getStartDate() : existingTrip.getStartDate());
	    existingTrip.setEndDate(updatedTrip.getEndDate() != null ? updatedTrip.getEndDate() : existingTrip.getEndDate());
	    existingTrip.setBudget(updatedTrip.getBudget() != null ? updatedTrip.getBudget() : existingTrip.getBudget());
	    existingTrip.setNotes(updatedTrip.getNotes() != null ? updatedTrip.getNotes() : existingTrip.getNotes());

	    Trip updatedTripObj = TripDAOsql.instance.updateTrip(tripId, existingTrip);
	    
	    return updatedTripObj;
	}
	
	@DELETE
	@Path("{tripId}")
	public Trip deleteTrip(@PathParam("tripId") String id) {
		return TripDAOsql.instance.deleteTrip(Integer.parseInt(id));
	}
}
