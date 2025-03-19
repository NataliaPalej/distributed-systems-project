package a00279259.trips;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import a00279259.TripDAOsql;

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
	public Trip getTrip(@PathParam("tripId") String id){
		return TripDAOsql.instance.getTrip(Integer.parseInt(id));
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addTrip(
			@FormParam("destination") String destination,
			@FormParam("startDate") Date startDate,
			@FormParam("endDate") Date endDate,
			@FormParam("budget") BigDecimal budget,
			@FormParam("notes") String notes,
			@Context HttpServletResponse servletResponse) throws IOException {
		Trip newTrip = new Trip();
		newTrip.setDestination(destination);
		newTrip.setStartDate(startDate);
		newTrip.setEndDate(endDate);
		newTrip.setBudget(budget);
		newTrip.setNotes(notes);
		
		TripDAOsql.instance.addTrip(newTrip);
		
		System.out.println("New Book added successfully.");
		
		servletResponse.sendRedirect("../addTrip.html");
	}
	
	@PUT
	@Path("{tripId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Trip updateTrip(
			@PathParam("tripId") String id,
			@FormParam("destination") String destination,
			@FormParam("startDate") Date startDate,
			@FormParam("endDate") Date endDate,
			@FormParam("budget") BigDecimal budget,
			@FormParam("notes") String notes) {
		
		int tripId = Integer.parseInt(id);
		Trip existingTrip = TripDAOsql.instance.getTrip(tripId);
		
		if (existingTrip == null) {
			System.out.println("Trip with ID " + tripId + " not found.");
			return null;
		}

		// Update only not null fields
		if (destination != null) existingTrip.setDestination(destination);
		if (startDate != null) existingTrip.setStartDate(startDate);
		if (endDate != null) existingTrip.setEndDate(endDate);
		if (budget != null) existingTrip.setBudget(budget);
		if (notes != null) existingTrip.setNotes(notes);
		
		Trip updatedTrip = TripDAOsql.instance.editTrip(tripId, existingTrip);
		System.out.println("Trip with ID " + tripId + " updated successfully.");
		return updatedTrip;
	}
	
	@DELETE
	@Path("{tripId}")
	public Trip deleteTrip(@PathParam("tripId") String id) {
		return TripDAOsql.instance.deleteTrip(Integer.parseInt(id));
	}

}
