document.addEventListener("DOMContentLoaded", function () {
    populateTripDropdown();
    const activityId = getActivityIdFromURL();
    if (activityId) {
        fetchActivityDetails(activityId);
    }
});

// Get activityId from URL
function getActivityIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("activityId");
}

// Fetch activity details and populate the form
function fetchActivityDetails(activityId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/activities/${activityId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlText, "application/xml");

        // Populate form fields
        document.getElementById("activityId").value = xml.querySelector("activityId").textContent;
        document.getElementById("tripId").value = xml.querySelector("tripId").textContent;
        document.getElementById("name").value = xml.querySelector("name").textContent;
        document.getElementById("activityDate").value = xml.querySelector("activityDate").textContent;
        document.getElementById("location").value = xml.querySelector("location").textContent;
        document.getElementById("cost").value = xml.querySelector("cost").textContent;
    })
    .catch(error => console.error("Error fetching activity details:", error));
}

// Update activity details via PUT request
function updateActivity() {
    const activityId = getActivityIdFromURL();
    const activityData = `
        <activity>
            <activityId>${activityId}</activityId>
            <tripId>${document.getElementById("tripId").value}</tripId>
            <name>${document.getElementById("name").value}</name>
            <activityDate>${document.getElementById("activityDate").value}</activityDate>
            <location>${document.getElementById("location").value}</location>
            <cost>${document.getElementById("cost").value}</cost>
        </activity>`;

    fetch(`http://localhost:8080/A00279259_Backend/rest/activities/${activityId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/xml" },
        body: activityData
    })
    .then(response => {
        if (response.ok) {
            alert("Activity updated successfully!");
            window.location.href = `activities.html?tripId=${document.getElementById("tripId").value}`;
        } else {
            alert("Error updating activity.");
        }
    })
    .catch(error => console.error("Error updating activity:", error));
}

// Cancel and go back to activities page
function cancelEdit() {
    window.location.href = `activities.html?tripId=${document.getElementById("tripId").value}`;
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
