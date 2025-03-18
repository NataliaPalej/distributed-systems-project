package trip.activities;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

// http://localhost:8080/A00279259TravelPlanner/rest/trips
public class ActivitiesResource {
	
	@GET
    public Response getAllActivities() {
        return Response.ok("Activities endpoint working").build();
    }

}
