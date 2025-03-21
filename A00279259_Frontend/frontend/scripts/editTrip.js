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

        const destination = xml.querySelector("destination").textContent;
        document.getElementById("tripId").value = xml.querySelector("tripId").textContent;
        document.getElementById("destination").value = destination;
        document.getElementById("startDate").value = xml.querySelector("startDate").textContent;
        document.getElementById("endDate").value = xml.querySelector("endDate").textContent;
        document.getElementById("budget").value = xml.querySelector("budget").textContent;
        document.getElementById("notes").value = xml.querySelector("notes").textContent;

        // Generate expected img filename
        const expectedImageName = cleanImageName(destination);
        document.getElementById("imageFormat").textContent = `${expectedImageName}.jpg`;

        // Set preview img if exists
        document.getElementById("previewImage").src = `images/${expectedImageName}.jpg`;
        document.getElementById("previewImage").onerror = function() {
            this.src = 'images/default.jpg';
        };
    })
    .catch(error => console.error("Error fetching trip details:", error));
}

document.getElementById("tripImage").addEventListener("change", function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById("previewImage").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

function cleanImageName(destination) {
    return destination.toLowerCase().replace(/,/g, '').replace(/\s+/g, '-'); 
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
