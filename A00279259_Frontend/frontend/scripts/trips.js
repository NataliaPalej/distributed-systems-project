document.addEventListener("DOMContentLoaded", function () {
    fetchTrips();
});

function fetchTrips() {
    fetch("http://localhost:8080/A00279259_Backend/rest/trips")
        .then(response => response.json())
        .then(data => displayTrips(data))
        .catch(error => console.error("Error fetching trips:", error));
}

function displayTrips(trips) {
	const upcomingTrips = document.getElementById("upcomingTrips");
    const pastTrips = document.getElementById("pastTrips");
	
    // Clear previous list
    upcomingTrips.innerHTML = "";
    pastTrips.innerHTML = "";
    
    // Get todays date
    const today = new Date();
    
    // Sort trips by startDate 
    trips.sort((a, b) => new Date(a.startDate) - new Date(b.startDate));

    trips.forEach(trip => {
        const tripCard = document.createElement("div");
        tripCard.classList.add("trip-card");

        // Convert startDate to Date object
        const tripDate = new Date(trip.startDate);

        // Check if the trip is in past
        const isPast = tripDate < today;

        tripCard.innerHTML = `
            <div class="trip-image-container ${isPast ? 'past-trip' : ''}">
                <img src="images/${trip.destination.toLowerCase().replace(/\s+/g, '-')}.jpg" alt="${trip.destination}">
                ${isPast ? '<div class="trip-overlay">COMPLETED</div>' : ''}
            </div>
            <div class="trip-info">
                <h3>${trip.destination}</h3>
                <p><strong>Start Date:</strong> ${formatDate(trip.startDate)}</p>
            </div>
        `;

        // Append to the appropriate section
        if (isPast) {
            pastTrips.appendChild(tripCard);
        } else {
            upcomingTrips.appendChild(tripCard);
        }
    });
}

// Format date to DD-MM-YYYY
function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
}
