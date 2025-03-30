document.addEventListener("DOMContentLoaded", function () {
    populateTripDropdown();
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get("type");

    if (type === "trip") {
    document.getElementById("tripForm").classList.remove("d-none");
    setupTripImageNaming();
	} else if (type === "activity") {
	    document.getElementById("activityForm").classList.remove("d-none");
	    setupActivityImageNaming();
	}
    
    // Format date input
    const startDateInput = document.getElementById("tripStartDate");
    const endDateInput = document.getElementById("tripEndDate");

    if (startDateInput) {
        startDateInput.addEventListener("blur", (e) => {
            e.target.value = normalizeDate(e.target.value);
        });
    }

    if (endDateInput) {
        endDateInput.addEventListener("blur", (e) => {
            e.target.value = normalizeDate(e.target.value);
        });
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
    
    const urlParams = new URLSearchParams(window.location.search);
    const prefillTripId = urlParams.get("tripId");
    
    // Pre-fill tripId if in URL
    if (prefillTripId) {
        tripIdInput.value = prefillTripId;
        tripIdInput.setAttribute("disabled", true);
        fetchTripDetails(prefillTripId, function (folderName) {
            imageFolderPath.textContent = `images/${folderName}/`;
        });
    }

    // Update folder name dynamically
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
	const startDate = normalizeDate(document.getElementById("tripStartDate").value);
	const endDate = normalizeDate(document.getElementById("tripEndDate").value);
	
    const tripData = `
        <trip>
            <destination>${document.getElementById("tripDestination").value}</destination>
            <startDate>${startDate}</startDate>
            <endDate>${endDate}</endDate>
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
            window.location.href = `activities.html?tripId=${tripId}`;
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
	const tripId = document.getElementById("tripId").value;
	
    const activityData = `
        <activity>
            <tripId>${tripId}</tripId>
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
            window.location.href = `activities.html?tripId=${tripId}`;
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

// Cancel and go back to index.html
function cancelAdd() {
    window.location.href = "index.html";
}

// Dynamically format date input
function setupDateFormatting(id) {
    const input = document.getElementById(id);
    if (!input) return;

    input.addEventListener("input", function (e) {
        let value = e.target.value.replace(/\D/g, "");
        if (value.length > 8) value = value.slice(0, 8);

        let formatted = value;
        if (value.length >= 5) {
            formatted = `${value.slice(0, 2)}/${value.slice(2, 4)}/${value.slice(4)}`;
        } else if (value.length >= 3) {
            formatted = `${value.slice(0, 2)}/${value.slice(2)}`;
        }

        e.target.value = formatted;
    });
}

setupDateFormatting("activityDate");
setupDateFormatting("tripStartDate");
setupDateFormatting("tripEndDate");
