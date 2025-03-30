package a00279259.db;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/admin")
public class DatabaseResource {
	
	@DELETE
    @Path("/clear")
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public String clearDatabase() {
        DatabaseDAO.instance.clearDatabase();
        return "Database cleared successfully.";
    }

    @POST
    @Path("/populate")
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public String initializeDatabase() {
        DatabaseDAO.instance.initializeDatabase();
        return "Database populated with default data.";
    }

    @GET
    @Path("/export")
    @Produces("text/csv")
    public javax.ws.rs.core.Response exportToCSV() {
        String csvContent = DatabaseDAO.instance.exportToCSV();

        return javax.ws.rs.core.Response
            .ok(csvContent)
            .header("Content-Disposition", "attachment; filename=\"trip-data.csv\"")
            .build();
    }
}
