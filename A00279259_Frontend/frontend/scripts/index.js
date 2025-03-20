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
    trips.sort((a, b) => new Date(b.startDate.split("-").reverse().join("-")) - new Date(a.startDate.split("-").reverse().join("-")))

    trips.forEach(trip => {
        const tripCard = document.createElement("div");
        tripCard.classList.add("trip-card");

        // Extract day, month, and year from trip.startDate
        const [tripDay, tripMonth, tripYear] = trip.startDate.split("-").map(Number);
        const tripDate = new Date(tripYear, tripMonth - 1, tripDay);

        // Check if the trip is in the past
        const isPast = tripDate < today;

        const imageName = cleanImageName(trip.destination);

        tripCard.innerHTML = `
            <div class="trip-image-container ${isPast ? 'past-trip' : ''}">
                <img src="images/${imageName}.jpg" alt="${trip.destination}" onerror="this.onerror=null; this.src='images/default.jpg';">
                ${isPast ? '<div class="trip-overlay">COMPLETED</div>' : ''}
            </div>
            <div class="trip-info">
                <h3>${trip.destination}</h3>
                <p><strong>Start Date:</strong> ${trip.startDate}</p>
            </div>
        `;

        // Add event to open activities.html with tripId
        tripCard.addEventListener("click", () => {
            window.location.href = `activities.html?tripId=${trip.tripId}`;
        });

        // Append to the appropriate section
        if (isPast) {
            pastTrips.appendChild(tripCard);
        } else {
            upcomingTrips.appendChild(tripCard);
        }
    });
}


// Fix generated destination image name 
function cleanImageName(destination) {
    return destination
        .toLowerCase()
        // Remove commas
        .replace(/,/g, '')  
        // Replace spaces with hyphens
        .replace(/\s+/g, '-'); 
}
