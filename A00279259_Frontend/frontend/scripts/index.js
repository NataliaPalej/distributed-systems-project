document.addEventListener("DOMContentLoaded", function () {
	console.log("DOMContentLoaded index.js")
	closeModal();
	
    fetchActivityStats().then(() => {
        fetchTrips();
        populateTripDropdown();
    });
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
        let tripId = trip.getElementsByTagName("tripId")[0]?.textContent;
        let destination = trip.getElementsByTagName("destination")[0]?.textContent || "TBC";
        let startDate = trip.getElementsByTagName("startDate")[0]?.textContent?.trim() !== "" 
            ? trip.getElementsByTagName("startDate")[0].textContent : "TBC";
        let endDate = trip.getElementsByTagName("endDate")[0]?.textContent?.trim() !== "" 
            ? trip.getElementsByTagName("endDate")[0].textContent : "TBC";
        let budget = trip.getElementsByTagName("budget")[0]?.textContent?.trim() !== "" 
            ? trip.getElementsByTagName("budget")[0].textContent : "TBC";
        let notes = trip.getElementsByTagName("notes")[0]?.textContent?.trim() !== "" 
            ? trip.getElementsByTagName("notes")[0].textContent : "TBC";

        trips.push({ tripId, destination, startDate, endDate, budget, notes });
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
    trips.sort((a, b) => {
        const aDate = toDate(a.startDate);
        const bDate = toDate(b.startDate);
        return aDate - bDate;
    });
	
    trips.forEach(trip => {
		const start = trip.startDate;
        const end = trip.endDate;
        
        const startDate = toDate(start);
        const endDate = toDate(end);

        const isOngoing = startDate <= today && endDate >= today;
        const isPast = endDate < today;
		
        const imageName = cleanImageName(trip.destination);
        
        const tripCard = document.createElement("div");
        tripCard.classList.add("trip-card");

        tripCard.innerHTML = `
		    <div class="trip-image-container ${isPast ? 'past-trip' : ''}">
		        <img src="images/${imageName}.jpg" alt="${trip.destination}" onerror="this.onerror=null; this.src='images/default.jpg';">
		        ${isPast ? '<div class="trip-overlay">COMPLETED</div>' : isOngoing
		        ? '<div class="trip-overlay">ONGOING</div>' : ''}
		        <div class="delete-container"></div>
		    </div>
		    <div class="trip-info">
		        <div class="trip-details">
		            <h3>${trip.destination}</h3>
		            <p><strong>Start Date:</strong> ${start}</p>
		            <p><strong>End Date:</strong> ${end}</p>
		            <p><strong>Budget:</strong> \u20AC${trip.budget}</p>
		            <p class="notes-section"><strong>Notes:</strong> ${trip.notes}</p>  
		        </div><br/>
		        <p><strong>Total Activities:</strong> ${tripStatsMap[trip.tripId]?.totalActivities || 0}</p>
		        <div class="trip-actions">
		            <button class="edit-btn">Edit</button>
		        </div>
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
        
        const editButton = tripCard.querySelector(".edit-btn");
        editButton.addEventListener("click", (event) => {
			// Prevents triggering the card click event
            event.stopPropagation(); 
            window.location.href = `edit.html?type=trip&tripId=${trip.tripId}`;
        });

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

let tripStatsMap = {}

function fetchActivityStats(){
	return fetch("http://localhost:8080/A00279259_Backend/rest/trips/activity-stats", {
        headers: { "Accept": "application/json" }
    })
    .then(response => response.json())
    .then(data => {
		// Convert list to map
        data.forEach(stat => {
            tripStatsMap[stat.tripId] = stat;
        });
    })
    .catch(error => console.error("Error fetching activity stats:", error));
}