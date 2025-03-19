package a00279259.activities;

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

import a00279259.ActivitiesDAOsql;

// http://localhost:8080/A00279259_Backend/rest/activities
@Path("/activities")
public class ActivitiesResource {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Activities> getAllActivities() {
		System.out.println("getAllActivities() :: called");
		return ActivitiesDAOsql.instance.getActivities();
    }
	
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{activityId}")
    public Activities getActivity(@PathParam("activityId") String id) {
		System.out.println("getActivity(id) :: called");
		return ActivitiesDAOsql.instance.getActivity(Integer.parseInt(id));
    }
	
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("trip/{tripId}")
    public List<Activities> getActivitiesByTrip(@PathParam("tripId") String tripId) {
		System.out.println("getActivity(id) :: called");
		return ActivitiesDAOsql.instance.getActivitiesByTrip(Integer.parseInt(tripId));
    }
	
	@POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addActivity(
            @FormParam("tripId") int tripId,
            @FormParam("name") String name,
            @FormParam("activityDate") Date activityDate,
            @FormParam("location") String location,
            @FormParam("cost") BigDecimal cost,
            @Context HttpServletResponse servletResponse) throws IOException {

        // Set id to "0" as its auto-generated and handled by ActivitiesDAO
        Activities newActivity = new Activities(0, tripId, name, activityDate, location, cost);
        ActivitiesDAOsql.instance.addActivity(newActivity);

        System.out.println("New activity added successfully.");
        servletResponse.sendRedirect("../addActivity.html");
    }
	
	// @XmlType(propOrder = { "activityId", "tripId", "name", "activityDate", "location", "cost" })
	@PUT
	@Path("{activityId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Activities updateTrip(
			@PathParam("activityId") int activityId,
			@FormParam("tripId") int tripId,
			@FormParam("name") String name,
			@FormParam("activityDate") Date activityDate,
			@FormParam("location") String location,
			@FormParam("cost") BigDecimal cost) {
		
		Activities existingActivity = ActivitiesDAOsql.instance.getActivity(activityId);
		
		if (existingActivity == null) {
			System.out.println("Trip with ID " + tripId + " not found.");
			return null;
		}

		// Update only not null fields
		if (activityId > 0) existingActivity.setActivityId(activityId);
		if (tripId > 0) existingActivity.setTripId(tripId);
		if (name != null) existingActivity.setName(name);
		if (activityDate != null) existingActivity.setActivityDate(activityDate);
		if (location != null) existingActivity.setLocation(location);
		if (cost != null) existingActivity.setCost(cost);
		
		Activities updatedActivity = ActivitiesDAOsql.instance.updateActivity(activityId, existingActivity);
		System.out.println("Trip with ID " + tripId + " updated successfully.");
		return updatedActivity;
	}
	
	@DELETE
    @Path("{activityId}")
    public Activities deleteActivity(@PathParam("activityId") String id) {
        System.out.println("Activity with ID: " + id + " was deleted successfully.");
		return ActivitiesDAOsql.instance.deleteActivity(Integer.parseInt(id));
    }

}
