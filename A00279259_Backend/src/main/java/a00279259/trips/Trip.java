package a00279259.trips;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import a00279259.activities.Activities;

/**
 * Annotations @XmlRootElement and @XmlType are allowing for the communication with module object
 * by automatic binding. It must have default or empty constructor + getters and setters must be defined 
 */

@XmlRootElement(name="trip") // Allows JAXB to convert obj to XML/JSON
@XmlType(propOrder = { "tripId", "destination", "startDate", "endDate", "budget", "notes", "activities" } ) // Order in which the data will be placed
public class Trip {
	private int tripId;
	private String destination, notes, startDate, endDate;
	private BigDecimal budget;
	private List<Activities> activities;
	
	public Trip() {}
	
	public Trip(String destination, String startDate, String endDate, BigDecimal budget, String notes) {
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.notes = notes;
    }
	
	public Trip(int tripId, String destination, String startDate, String endDate, BigDecimal budget, String notes) {
        this.tripId = tripId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.notes = notes;
    }
	
	public List<Activities> getActivities() {
        return activities;
    }
	
	public void setActivities(List<Activities> activities) {
        this.activities = activities;
    }
	
	public int getTripId() {
		return tripId;
	}
	public void setTripId(int tripId) {
		this.tripId = tripId;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String date) {
		this.startDate = date;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getBudget() {
		return budget;
	}
	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	public String toString() {
		return "Trip [tripId=" + tripId + ", destination=" + destination + ", notes=" + notes + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", budget=" + budget + "]";
	}
}
