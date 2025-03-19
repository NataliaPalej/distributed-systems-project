package a00279259.activities;

import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="activity")
@XmlType(propOrder = { "activityId", "tripId", "name", "time", "location", "cost" })
public class Activities {
	
	private int activityId, tripId;
    private String name, location;
    private Date time;
    private BigDecimal cost;
    
    public Activities() {}
    
    public Activities(int activityId, int tripId, String name, Date time, String location, BigDecimal cost) {
        this.activityId = activityId;
        this.tripId = tripId;
        this.name = name;
        this.time = time;
        this.location = location;
        this.cost = cost;
    }

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String toString() {
		return "Activities [activityId=" + activityId + ", tripId=" + tripId + ", name=" + name + ", location="
				+ location + ", time=" + time + ", cost=" + cost + "]";
	}
}
