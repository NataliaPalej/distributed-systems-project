const API_URL = "http://localhost:8080/a00279259/rest";

async function fetchTrips() {
    try {
        let response = await fetch(`${API_URL}/trips`);
        let trips = await response.json();
        return trips;
    } catch (error) {
        console.error("fetchTrips() :: Error fetching trips:", error);
    }
}
