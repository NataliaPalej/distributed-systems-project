package myApp;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MulticastSocket;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import trip.activities.Activities;
import trip.activities.ActivitiesDAOsql;

// http://localhost:8080/A00279259TravelPlanner/rest/trips
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
            @FormParam("time") String time,
            @FormParam("location") String location,
            @FormParam("cost") BigDecimal cost,
            @Context HttpServletResponse servletResponse) throws IOException {

        LocalTime activityTime = LocalTime.parse(time);

        // Set id to "0" as its auto-generated and handled by ActivitiesDAO
        Activities newActivity = new Activities(0, tripId, name, activityTime, location, cost);
        ActivitiesDAOsql.instance.addActivity(newActivity);

        System.out.println("New activity added successfully.");
        servletResponse.sendRedirect("../addActivity.html");
    }
	
	@DELETE
    @Path("{activityId}")
    public Activities deleteActivity(@PathParam("activityId") String id) {
        System.out.println("Activity with ID: " + id + " was deleted successfully.");
		return ActivitiesDAOsql.instance.deleteActivity(Integer.parseInt(id));
    }

}
