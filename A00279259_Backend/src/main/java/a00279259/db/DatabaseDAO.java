package a00279259.db;

import java.util.List;

import a00279259.ActivitiesDAOsql;
import a00279259.TripDAOsql;
import a00279259.activities.Activities;
import a00279259.trips.Trip;

public class DatabaseDAO {
	
	public static final DatabaseDAO instance = new DatabaseDAO();
	
	private DatabaseDAO() {}

    public void clearDatabase() {
        ActivitiesDAOsql.instance.clearAllActivities();
        TripDAOsql.instance.clearAllTrips();
        System.out.println("Database cleared: Trips and Activities.");
    }

    // Populate db (trips + activities)
    public void initializeDatabase() {
        TripDAOsql.instance.insertInitialTrips();
        System.out.println("Database populated with default data.");
    }
    
    public String exportToCSV() {
        List<Trip> trips = TripDAOsql.instance.getTrips();
        StringBuilder csvBuilder = new StringBuilder("TripID,Destination,StartDate,EndDate,Budget,Notes,ActivityID,ActivityName,ActivityDate,Location,Cost\n");

        for (Trip trip : trips) {
            List<Activities> activities = TripDAOsql.instance.getActivitiesByTrip(trip.getTripId());

            // Escape commas to properly export to CSV
            String destination = "\"" + trip.getDestination() + "\"";
            String notes = "\"" + trip.getNotes() + "\"";
            
            if (activities.isEmpty()) {
                csvBuilder.append(trip.getTripId()).append(",")
                          .append(destination).append(",")
                          .append(trip.getStartDate()).append(",")
                          .append(trip.getEndDate()).append(",")
                          .append(trip.getBudget()).append(",")
                          .append(notes).append(",")
                          .append("N/A,N/A,N/A,N/A,N/A").append("\n");
            } else {
                for (Activities activity : activities) {
                	
                	String activityName = "\"" + activity.getName() + "\"";
                    String location = "\"" + activity.getLocation() + "\"";
                	
                    csvBuilder.append(trip.getTripId()).append(",")
                              .append(destination).append(",")
                              .append(trip.getStartDate()).append(",")
                              .append(trip.getEndDate()).append(",")
                              .append(trip.getBudget()).append(",")
                              .append(notes).append(",")
                              .append(activity.getActivityId()).append(",")
                              .append(activityName).append(",")
                              .append(activity.getActivityDate()).append(",")
                              .append(location).append(",")
                              .append(activity.getCost()).append("\n");
                }
            }
        }
        return csvBuilder.toString();
    }
}
