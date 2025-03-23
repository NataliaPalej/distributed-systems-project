document.addEventListener("DOMContentLoaded", function () {
    populateTripDropdown();
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get("type");

    if (type === "trip") {
        document.getElementById("tripForm").classList.remove("hidden");
        setupTripImageNaming();
    } else if (type === "activity") {
        document.getElementById("activityForm").classList.remove("hidden");
        setupActivityImageNaming();
    }
});

// Get trip img name and display preview
function setupTripImageNaming() {
    const tripDestinationInput = document.getElementById("tripDestination");
    const imageFormatSpan = document.getElementById("imageFormat");

    tripDestinationInput.addEventListener("input", function () {
        const imageName = cleanImageName(tripDestinationInput.value);
        imageFormatSpan.textContent = `images/${imageName}.jpg`;
    });
}

// Generate activity img name and display preview
function setupActivityImageNaming() {
    const tripIdInput = document.getElementById("tripId");
    const activityNameInput = document.getElementById("activityName");
    const imageFolderPath = document.getElementById("imageFolderPath");

    // Fetch folder path when tripID is entered
    tripIdInput.addEventListener("input", function () {
        fetchTripDetails(tripIdInput.value, function (folderName) {
            imageFolderPath.textContent = `images/${folderName}/`;
            updateActivityImageFormat();
        });
    });
    
  	// Fetch img name dynamically
  	activityNameInput.addEventListener("input", updateActivityImageFormat);

    function updateActivityImageFormat() {
        const activityImageName = getActivityImageName(activityNameInput.value);
        imageFormatText.textContent = activityImageName ? `${activityImageName}.jpg` : "activity name";
    }
}

// Fetch trip destination for img name
function fetchTripDetails(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlText, "application/xml");

        const destination = xml.querySelector("destination").textContent.trim();
        const folderName = getFolderName(destination);

        document.getElementById("imageFolderPath").textContent = `images/${folderName}/`;
    })
    .catch(error => console.error("Error fetching trip details:", error));
}

// Add a Trip
function addTrip() {
    const tripData = `
        <trip>
            <destination>${document.getElementById("tripDestination").value}</destination>
            <startDate>${document.getElementById("tripStartDate").value}</startDate>
            <endDate>${document.getElementById("tripEndDate").value}</endDate>
            <budget>${document.getElementById("tripBudget").value}</budget>
            <notes>${document.getElementById("tripNotes").value}</notes>
        </trip>`;

    fetch("http://localhost:8080/A00279259_Backend/rest/trips", {
        method: "POST",
        headers: {
            "Content-Type": "application/xml",
            "Accept": "application/xml"
        },
        body: tripData
    })
    //Read XML response
    .then(response => response.text()) 
    .then(xmlText => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlText, "application/xml");

        const tripId = xml.querySelector("tripId") ? xml.querySelector("tripId").textContent : null;
        // console.log("tripId: ", tripId);
        
        if (tripId) {
            alert(`Trip added successfully!\nNew Trip ID: ${tripId}`);
            window.location.href = `index.html?tripId=${tripId}`;
        } else {
            alert("Trip added, but Trip ID could not be retrieved.");
        }
    })
    .catch(error => {
        alert("Error adding trip.");
        console.error("Error:", error);
    });
}

// Add an Activity
function addActivity() {
    const activityData = `
        <activity>
            <tripId>${document.getElementById("tripId").value}</tripId>
            <name>${document.getElementById("activityName").value}</name>
            <activityDate>${document.getElementById("activityDate").value}</activityDate>
            <location>${document.getElementById("activityLocation").value}</location>
            <cost>${document.getElementById("activityCost").value}</cost>
        </activity>`;

    fetch("http://localhost:8080/A00279259_Backend/rest/activities", {
        method: "POST",
        headers: { "Content-Type": "application/xml" },
        body: activityData
    })
    .then(response => {
        if (response.ok) {
            alert("Activity added successfully!");
            window.location.href = `index.html`;
        } else {
            alert("Error adding activity.");
        }
    })
    .catch(error => console.error("Error adding activity:", error));
}

// Format trip img name
function cleanImageName(destination) {
    return destination.toLowerCase().replace(/,/g, '').replace(/\s+/g, '-');
}

// Format activity img name
function getActivityImageName(activityName) {
    return activityName.trim().split(" ").slice(0, 2).join("-").toLowerCase();
}

// Get folder name for activity img
function getFolderName(destination) {
    return destination.split(",")[0].toLowerCase().replace(/\s+/g, '-');
}

// Populate dropdown for trips
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

// Cancel and go back to index.html
function cancelAdd() {
    window.location.href = "index.html";
}
