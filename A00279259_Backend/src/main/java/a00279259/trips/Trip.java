package a00279259.trips;

import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Annotations @XmlRootElement and @XmlType are allowing for the communication with module object
 * by automatic binding. It must have default or empty constructor + getters and setters must be defined 
 */

@XmlRootElement(name="trip") // Allows JAXB to convert obj to XML/JSON
@XmlType(propOrder = { "tripId", "destination", "startDate", "endDate", "budget", "notes" } ) // Order in which the data will be placed
public class Trip {
	private int tripId;
	private String destination, notes;
	private Date startDate;
	private Date endDate;
	private BigDecimal budget;
	
	public Trip() {}
	
	public Trip(int tripId, String destination, Date startDate, Date endDate, BigDecimal budget, String notes) {
        this.tripId = tripId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.notes = notes;
    }
	
	public int getId() {
		return tripId;
	}
	public void setId(int tripId) {
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date date) {
		this.startDate = date;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
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
