package trip.activities;

import java.math.BigDecimal;
import java.time.LocalTime;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="activity")
@XmlType(propOrder = { "id", "tripId", "name", "time", "location", "cost" })
public class Activities {
	
	private int id, tripId;
    private String name, location;
    private LocalTime time;
    private BigDecimal cost;
    
    public Activities() {}
    
    public Activities(int id, int tripId, String name, LocalTime time, String location, BigDecimal cost) {
        this.id = id;
        this.tripId = tripId;
        this.name = name;
        this.time = time;
        this.location = location;
        this.cost = cost;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Activities [id=" + id + ", tripId=" + tripId + ", name=" + name + ", location=" + location + ", time="
				+ time + ", cost=" + cost + "]";
	}

}
