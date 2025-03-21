document.addEventListener("DOMContentLoaded", function () {
    populateTripDropdown();
    const tripId = getTripIdFromURL();
    if (tripId) {
        fetchTripDetails(tripId);
    }
});

// Get tripId from URL
function getTripIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("tripId");
}

// Fetch trip details and populate the form
function fetchTripDetails(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlText, "application/xml");
		
		document.getElementById("tripId").value = xml.querySelector("tripId").textContent;
        document.getElementById("destination").value = xml.querySelector("destination").textContent;
        document.getElementById("startDate").value = xml.querySelector("startDate").textContent;
        document.getElementById("endDate").value = xml.querySelector("endDate").textContent;
        document.getElementById("budget").value = xml.querySelector("budget").textContent;
        document.getElementById("notes").value = xml.querySelector("notes").textContent;
    })
    .catch(error => console.error("Error fetching trip details:", error));
}

// Update trip details via PUT request
function updateTrip() {
    const tripId = getTripIdFromURL();
    const tripData = `
        <trip>
            <tripId>${document.getElementById("tripId").value}</tripId>
            <destination>${document.getElementById("destination").value}</destination>
            <startDate>${document.getElementById("startDate").value}</startDate>
            <endDate>${document.getElementById("endDate").value}</endDate>
            <budget>${document.getElementById("budget").value}</budget>
            <notes>${document.getElementById("notes").value}</notes>
        </trip>`;

    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/xml" },
        body: tripData
    })
    .then(response => {
        if (response.ok) {
            alert("Trip updated successfully!");
            window.location.href = "index.html";
        } else {
            alert("Error updating trip.");
        }
    })
    .catch(error => console.error("Error updating trip:", error));
}

// Cancel and go back to index.html
function cancelEdit() {
    window.location.href = "index.html";
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
