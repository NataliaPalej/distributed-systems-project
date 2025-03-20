document.addEventListener("DOMContentLoaded", function () {
    const tripId = getTripIdFromURL();
    if (tripId) {
		fetchTripDetails(tripId);
        fetchActivities(tripId);
    } else {
        console.error("No tripId found in URL");
    }
});

// Get tripId from URL
function getTripIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("tripId");
}

// Fetch destination name 
function fetchTripDetails(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`)
        .then(response => response.json())
        .then(trip => {
            document.querySelector("h2").innerHTML = `Activities in <span class="destination-highlight">${trip.destination}</span>`;
        })
        .catch(error => console.error("Error fetching trip details:", error));
}

// Fetch activities for selected trip
function fetchActivities(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}/activities`)
        .then(response => response.json())
        .then(data => displayActivities(data))
        .catch(error => console.error("Error fetching activities:", error));
}

// Display activities
function displayActivities(activities,) {
    const activitiesList = document.getElementById("activitiesList");
    // Clear previous activity
    activitiesList.innerHTML = ""; 
    
    if (activities.length === 0) {
        activitiesList.innerHTML = "<p>No activities found for this trip.</p>";
        return;
    }
    
    // Sort activities by date 
    activities.sort((a, b) => new Date(a.activityDate.split("-").reverse().join("-")) - new Date(b.activityDate.split("-").reverse().join("-")));

    activities.forEach(activity => {
		// Get folder name based on location
        const folderName = getFolderName(activity.location); 
        // console.log("folderName: ", folderName);
        
        // Generate activity image name
        const activityImageName = getActivityImageName(activity.name); 
        // console.log("activityImageName: ", activityImageName);
        
        const activityCard = document.createElement("div");
        activityCard.classList.add("activity-card");

        activityCard.innerHTML = `
            <div class="activity-image-container">
                <img src="images/${folderName}/${activityImageName}.jpg" 
                     alt="${activity.name}" 
                     onerror="this.onerror=null; this.src='images/default.jpg';">
            </div>
            <div class="activity-info">
                <h3>${activity.name}</h3>
                <p><strong>Date:</strong> ${(activity.activityDate)}</p>
                <p><strong>Location:</strong> ${activity.location}</p>
                <p><strong>Cost:</strong> €${activity.cost ? activity.cost.toFixed(2) : "0.00"}</p>
            </div>
        `;

        activitiesList.appendChild(activityCard);
    });
}

function getActivityImageName(activityName) {
    return activityName
        .split(" ") 
        // Take the first two words
        .slice(0, 2) 
        // Join them with a hyphen
        .join("-") 
        .toLowerCase();
}

function cleanImageName(location) {
    return location
        .toLowerCase()
        .replace(/,/g, '') 
        .replace(/\s+/g, '-');
}

// Mapping for images folders 
function getFolderName(location) {
    const folderMap = {
		"Bali, Indonesia": "bali",
        "Ubud, Bali, Indonesia": "bali",
        "Tabanan Regency, Bali, Indonesia" : "bali",
        "Badung Regency, Bali, Indonesia" : "bali",
        "Chania, Crete, Greece": "crete",
        "Balos, Crete, Greece": "crete",
        "Agia Roumeli, Crete, Greece": "crete",
        "Krakow, Poland": "krakow",
        "Oswiecim, Poland" : "krakow",
        "Muszyna, Poland" : "krakow",
        "Wieliczka, Poland" : "krakow",
        "New York, USA": "new-york",
        "Jersey City, USA": "new-york",
        "Manhattan, New York, USA": "new-york",
        "45 Rockefeller Plaza, New York, USA": "new-york",
        "Santorini, Greece": "santorini",
        "Oia, Santorini, Greece": "santorini",
        "Akrotiri, Santorini, Greece": "santorini",
        "Thera, Santorini, Greece": "santorini"
    };
    
    return folderMap[location] || "default";
}