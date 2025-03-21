document.addEventListener("DOMContentLoaded", function () {
	console.log("DOMContentLoaded index.js")
	closeModal();
    fetchTrips();
    populateTripDropdown();
});

function fetchTrips() {
    fetch("http://localhost:8080/A00279259_Backend/rest/trips", {
        headers: { "Accept": "application/xml" }})
        // Get XML response as text
        .then(response => response.text()) 
        // Call Parse XML
        .then(xmlString => parseTripsXML(xmlString)) 
        // Display trips
        .then(trips => displayTrips(trips)) 
        .catch(error => console.error("Error fetching trips:", error));
}

function parseTripsXML(xmlString) {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlString, "application/xml");
    const tripElements = xml.getElementsByTagName("trip");

    let trips = [];
    for (let trip of tripElements) {
        let tripId = trip.getElementsByTagName("tripId")[0].textContent;
        let destination = trip.getElementsByTagName("destination")[0].textContent;
        let startDate = trip.getElementsByTagName("startDate")[0].textContent;
        let endDate = trip.getElementsByTagName("endDate")[0].textContent;
        let budget = trip.getElementsByTagName("budget")[0].textContent;
        let notes = trip.getElementsByTagName("notes")[0].textContent;

        trips.push({ tripId, destination, startDate, endDate, budget, notes});
    }

    return trips;
}

function displayTrips(trips) {
	const upcomingTrips = document.getElementById("upcomingTrips");
    const pastTrips = document.getElementById("pastTrips");
	
    // Clear previous list
    upcomingTrips.innerHTML = "";
    pastTrips.innerHTML = "";
    
    // Get todays date
    const today = new Date();
    
    // Sort trips by startDate asc
    trips.sort((a, b) => new Date(a.startDate.split("-").reverse().join("-")) - new Date(b.startDate.split("-").reverse().join("-")));
    
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
                <div class="delete-container"></div>
            </div>
            <div class="trip-info">
                <h3>${trip.destination}</h3>
                <p><strong>Start Date:</strong> ${trip.startDate}</p>
                <p><strong>End Date:</strong> ${trip.endDate}</p>
                <p><strong>Budget:</strong> \u20AC${trip.budget}</p>
                <br>
                <p><strong>Notes:</strong> ${trip.notes}</p>
            </div>
        `;
        
        // Create delete button
        const deleteButton = document.createElement("button");
        deleteButton.classList.add("delete-btn");
        // "×" symbol
        deleteButton.innerHTML = "&times;";  
        
        // Add event listener to delete button
        deleteButton.addEventListener("click", (event) => {
			// Prevent card click from opening activities.html
            event.stopPropagation(); 
            confirmDeleteTrip(trip.tripId, trip.destination);
        });
        
        const deleteContainer = tripCard.querySelector(".delete-container");
        // Append delete btn
        deleteContainer.appendChild(deleteButton); 

        // Add on click event to open activities.html with specific tripId
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

/* Delete Function */
let selectedTripId = null;

function confirmDeleteTrip(tripId, destination) {
    selectedTripId = tripId; // Store tripId for deletion

    // Update modal text with trip name
    document.getElementById("modalText").innerHTML = `Are you sure you want to delete trip to 
        <span id="modalTripName">${destination}</span>?<br><br>`;

    // Show modal
    document.getElementById("deleteModal").style.display = "flex";
}

function deleteTrip(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        method: "DELETE",
    })
    .then(response => {
        if (response.ok) {
            closeModal();
            fetchTrips(); 
        } else {
            alert("Error deleting trip.");
        }
    })
    .catch(error => console.error("Error deleting trip:", error));
}

document.getElementById("confirmDeleteBtn").addEventListener("click", function() {
    if (selectedTripId) {
        deleteTrip(selectedTripId);
    }
});

document.getElementById("cancelDeleteBtn").addEventListener("click", closeModal);

function closeModal() {
    document.getElementById("deleteModal").style.display = "none";
}

/* Nav Bar Dropdown */
function populateTripDropdown() {
    fetch("http://localhost:8080/A00279259_Backend/rest/trips", {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlString => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlString, "application/xml");
        const tripElements = xml.getElementsByTagName("trip");

        const dropdown = document.getElementById("tripDropdown");

        if (tripElements.length === 0) {
            const noTrips = document.createElement("li");
            noTrips.textContent = "No trips available";
            dropdown.appendChild(noTrips);
            return;
        }

        // Populate dropdown with trips
        for (let trip of tripElements) {
            let tripId = trip.getElementsByTagName("tripId")[0].textContent;
            let destination = trip.getElementsByTagName("destination")[0].textContent;

            const tripItem = document.createElement("a");
            tripItem.href = `activities.html?tripId=${tripId}`;
            tripItem.textContent = destination;
            dropdown.appendChild(tripItem);
        }
    })
    .catch(error => console.error("Error fetching trips for dropdown:", error));
}