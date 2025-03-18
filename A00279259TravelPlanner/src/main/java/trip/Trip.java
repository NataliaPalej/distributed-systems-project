package trip;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Annotations @XmlRootElement and @XmlType are allowing for the communication with module object
 * by automatic binding. It must have default or empty constructor + getters and setters must be defined 
 */

@XmlRootElement(name="trip") // Allows JAXB to convert obj to XML/JSON
@XmlType(propOrder = { "id", "destination", "startDate", "endDate", "budget", "notes" } ) // Order in which the data will be placed
public class Trip {
	private int id;
	private String destination, notes;
	private LocalDate startDate, endDate;
	private BigDecimal budget;
	
	public Trip() {}
	
	public Trip(int id, String destination, LocalDate startDate, LocalDate endDate, BigDecimal budget, String notes) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.notes = notes;
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getBudget() {
		return budget;
	}
	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}
	@Override
	public String toString() {
		return "Trip [id=" + id + ", destination=" + destination + ", notes=" + notes + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", budget=" + budget + "]";
	}
}
