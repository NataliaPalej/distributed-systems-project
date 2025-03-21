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

// Get tripId from URL
function getTripIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("tripId");
}

let destination = "";

// Fetch destination name 
function fetchDestination(tripId) {
    return fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlText, "application/xml");
        return xml.querySelector("destination").textContent.trim(); // Return destination name
    })
    .catch(error => console.error("Error fetching trip details:", error));
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

        // Extract activity details
        const activityId = xml.querySelector("activityId").textContent;
        const tripId = xml.querySelector("tripId").textContent;
        const activityName = xml.querySelector("name").textContent;
        const activityDate = xml.querySelector("activityDate").textContent;
        const location = xml.querySelector("location").textContent;
        const cost = xml.querySelector("cost").textContent;

        // Populate form fields
        document.getElementById("activityId").value = activityId;
        document.getElementById("tripId").value = tripId;
        document.getElementById("name").value = activityName;
        document.getElementById("activityDate").value = activityDate;
        document.getElementById("location").value = location;
        document.getElementById("cost").value = cost;
        
        return fetchDestination(tripId).then(tripDestination => {
            destination = tripDestination;
        
        // Generate expected folder and image name
        const folderName = getFolderName(destination);
        const expectedImageName = getActivityImageName(activityName);

        // Display expected file naming format
        document.getElementById("imageFolderPath").textContent = `images/${folderName}/`
        document.getElementById("imageFormat").textContent = `${expectedImageName}.jpg`;

        // Set preview image
        document.getElementById("previewImage").src = `images/${folderName}/${expectedImageName}.jpg`;
        document.getElementById("previewImage").onerror = function() {
            this.src = 'images/default.jpg';
        };
         });
    })
    .catch(error => console.error("Error fetching activity details:", error));
}

document.getElementById("activityImage").addEventListener("change", function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById("previewImage").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

function getActivityImageName(activityName) {
    return activityName.split(" ").slice(0, 2).join("-").toLowerCase();
}

// Get folder name for images 
function getFolderName(destination) {
    return destination.split(",")[0].toLowerCase().replace(/\s+/g, '-');
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
