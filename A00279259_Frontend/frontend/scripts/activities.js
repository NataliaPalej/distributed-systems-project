document.addEventListener("DOMContentLoaded", function () {
    const tripId = getTripIdFromURL();
    closeModal();
    populateTripDropdown();
    if (tripId) {
        fetchTripDetails(tripId);
    } else {
        console.error("No tripId found in URL");
    }
});

// Get tripId from URL
function getTripIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("tripId");
}

let tripDestination = "";

// Fetch destination name 
function fetchTripDetails(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
		// Request XML
        headers: { "Accept": "application/xml" } 
    })
        .then(response => response.text())
        .then(xmlText => {
            const parser = new DOMParser();
            const xml = parser.parseFromString(xmlText, "application/xml");
            tripDestination = xml.querySelector("destination").textContent.trim();
            
            // Update header with destination name
            document.querySelector("h2").innerHTML = `Activities in <span class="destination-highlight">${tripDestination}</span>`;
        	fetchActivities(tripId);
        })
        .catch(error => console.error("Error fetching trip details:", error));
}

// Fetch activities for selected trip
function fetchActivities(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}/activities`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        // console.log("API Response: ", xmlText);
        const activities = parseXMLActivities(xmlText);
        displayActivities(activities);
    })
    .catch(error => console.error("Error fetching activities:", error));
}

// Parse XML response for activities
function parseXMLActivities(xmlText) {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlText, "application/xml");

    const activities = xml.getElementsByTagName("activity");
    if (activities.length === 0) {
        console.warn("No activities found in XML response.");
        return [];
    }

    let activityList = [];
    for (let activity of activities) {
        const activityId = activity.querySelector("activityId")?.textContent || "TBC";
        const tripId = activity.querySelector("tripId")?.textContent || "TBC";
        const name = activity.querySelector("name")?.textContent || "TBC";
        const activityDate = activity.querySelector("activityDate")?.textContent || "TBC";
        const location = activity.querySelector("location")?.textContent || "TBC";
        const cost = activity.querySelector("cost")?.textContent || "0.00";

        activityList.push({ activityId, tripId, name, activityDate, location, cost });
    }
    return activityList;
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
        const folderName = getFolderName(tripDestination); 
        console.log("folderName: ", folderName);
        
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
                <p><strong>Cost:</strong> \u20AC${parseFloat(activity.cost).toFixed(2)}</p>
                <br>
                <div class="activity-actions">
                	<button class="edit-btn">Edit</button>
            	</div>
           </div>
        `;
        
        // Create delete button
        const deleteButton = document.createElement("button");
        deleteButton.classList.add("delete-btn");
        deleteButton.innerHTML = "&times;";
        deleteButton.addEventListener("click", (event) => {
			// Prevent unexpected modal triggers
            event.stopPropagation(); 
            confirmDeleteActivity(activity.activityId, activity.name);
        });

        // Create delete container and attach delete btn
        const deleteContainer = document.createElement("div");
        deleteContainer.classList.add("delete-container");
        deleteContainer.appendChild(deleteButton);
        
        const editButton = activityCard.querySelector(".edit-btn");
        editButton.addEventListener("click", (event) => {
            event.stopPropagation();
            window.location.href = `editActivity.html?activityId=${activity.activityId}`;
        });

        // Append delete btn inside activity img container
        const imageContainer = activityCard.querySelector(".activity-image-container");
        imageContainer.appendChild(deleteContainer);

        activitiesList.appendChild(activityCard);
    });
}

function getActivityImageName(activityName) {
    return activityName.split(" ").slice(0, 2).join("-").toLowerCase();
}

// Get folder name for images 
function getFolderName(destination) {
    return destination.split(",")[0].toLowerCase().replace(/\s+/g, '-');
}

// Delete function 
let selectedActivityId = null;

function confirmDeleteActivity(activityId, name) {
    selectedActivityId = activityId;

    // Update modal text
    document.getElementById("modalText").innerHTML = `Are you sure you want to delete activity: 
    <span id="modalActivityName">${name}</span>?<br><br>`;

    // Show modal
    document.getElementById("deleteModal").style.display = "flex";
}

function deleteActivity(activityId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/activities/${activityId}`, {
        method: "DELETE",
    })
    .then(response => {
        if (response.ok) {
			closeModal();
            // alert("Activity deleted successfully!");
            // Refresh Activities
            fetchActivities(getTripIdFromURL());
        } else {
            alert("Error deleting activity.");
        }
    })
    .catch(error => console.error("Error deleting activity:", error));
}

/* On click event for modal buttons */
document.getElementById("confirmDeleteBtn").addEventListener("click", function() {
    if (selectedActivityId) {
        deleteActivity(selectedActivityId);
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