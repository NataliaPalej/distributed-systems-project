package a00279259.activities;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
    public Activities getActivity(@PathParam("activityId") String activityId) {
		return ActivitiesDAOsql.instance.getActivity(Integer.parseInt(activityId));
    }
	
	@POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void addActivity(Activities activity, @Context HttpServletResponse servletResponse) throws IOException {

		// Check if tripID exists before insert 
		if (activity.getTripId() <= 0) {
	        System.out.println("Error: invalid tripId " + activity.getTripId() + ".");
	        servletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: tripId is invalid or missing.");
	        return;
	    }
		
		Activities newActivity = ActivitiesDAOsql.instance.addActivity(activity);
		
		if (newActivity == null) {
	        System.out.println("Error inserting activity.");
	        servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inserting activity.");
	        return;
	    }
    }
	
	// @XmlType(propOrder = { "activityId", "tripId", "name", "activityDate", "location", "cost" })
	@PUT
	@Path("{activityId}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	public Activities updateActivity(@PathParam("activityId") int activityId, Activities updatedActivity) {
		
		Activities existingActivity = ActivitiesDAOsql.instance.getActivity(activityId);
		
		if (existingActivity == null) {
			System.out.println("\nActivity with ID " + activityId + " not found.\n");
			return null;
		}
		
		// Update only not null fields
		existingActivity.setTripId(updatedActivity.getTripId() > 0 ? updatedActivity.getTripId() : existingActivity.getTripId());
		existingActivity.setName(updatedActivity.getName() != null ? updatedActivity.getName() : existingActivity.getName());
		existingActivity.setActivityDate(updatedActivity.getActivityDate() != null ? updatedActivity.getActivityDate() : existingActivity.getActivityDate());
		existingActivity.setLocation(updatedActivity.getLocation() != null ? updatedActivity.getLocation() : existingActivity.getLocation());
		existingActivity.setCost(updatedActivity.getCost() != null ? updatedActivity.getCost() : existingActivity.getCost());
		
		Activities updatedActivityObj = ActivitiesDAOsql.instance.updateActivity(activityId, existingActivity);
		return updatedActivityObj;
	}
	
	@DELETE
    @Path("{activityId}")
    public Activities deleteActivity(@PathParam("activityId") String id) {
		return ActivitiesDAOsql.instance.deleteActivity(Integer.parseInt(id));
    }

}
