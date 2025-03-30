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
let allActivities = [];

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
            
        	fetchActivities(tripId);
        	
        	document.getElementById("activityDestinationHeading").textContent = tripDestination;
			document.getElementById("addActivityBtn").href = `add.html?type=activity&tripId=${tripId}`;
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
        allActivities = activities;
		displayActivities(allActivities);
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
function displayActivities(activities) {
	console.log("Activities received:", activities);
    const activitiesList = document.getElementById("activitiesList");
    // Clear previous activity
    activitiesList.innerHTML = ""; 
    
    if (activities.length === 0) {
        activitiesList.innerHTML = "<p>No activities found for this trip.</p>";
        return;
    }
    
    // Sort activities by startDate asc
    activities.sort((a, b) => toDate(a.activityDate) - toDate(b.activityDate));

    activities.forEach(activity => {
		// Get folder name based on location
        const folderName = getFolderName(tripDestination); 
        console.log("folderName: ", folderName);
        
        // Generate activity image name
        const activityImageName = getActivityImageName(activity.name); 
        // console.log("activityImageName: ", activityImageName);
    
        const activityCard = document.createElement("div");
        activityCard.className = "activity-card shadow-sm";

        activityCard.innerHTML = `
	    <div class="card shadow-sm h-100">
	        <div class="activity-image-container">
	        <img src="images/${folderName}/${activityImageName}.jpg" class="card-img-top" alt="${activity.name}" onerror="this.onerror=null; this.src='images/default.jpg';" />
	            <button class="btn-sm position-absolute top-0 end-0 m-2 delete-btn" title="Delete">&times;</button>
	        </div>
	        <div class="card-body d-flex flex-column justify-content-between">
	            <div>
	                <h5 class="card-title text-start">${activity.name}</h5>
	                <p class="card-text mb-1"><strong>Date:</strong> ${activity.activityDate}</p>
	                <p class="card-text mb-1"><strong>Location:</strong> ${activity.location}</p>
	                <p class="card-text mb-3"><strong>Cost:</strong> €${parseFloat(activity.cost).toFixed(2)}</p>
	            </div>
	            <button class="w-100 mt-auto edit-btn">Edit</button>
	        </div>
	    </div>
		`;
        
        // Create delete button
        const deleteButton = activityCard.querySelector(".delete-btn");
		deleteButton.addEventListener("click", (event) => {
		    event.stopPropagation();
		    confirmDeleteActivity(activity.activityId, activity.name);
		});

        const editButton = activityCard.querySelector(".edit-btn");
		editButton.addEventListener("click", (event) => {
		    event.stopPropagation();
		    window.location.href = `edit.html?type=activity&activityId=${activity.activityId}`;
		});

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

document.getElementById("confirmDeleteBtn").addEventListener("click", function () {
    if (selectedActivityId) {
        deleteActivity(selectedActivityId);
    }
});

// Filter by cost
document.getElementById("costFilter").addEventListener("change", function () {
    const selected = this.value;
    let filtered = allActivities;

    if (selected === "under100") {
        filtered = allActivities.filter(a => parseFloat(a.cost) < 100);
    } else if (selected === "100to1000") {
        filtered = allActivities.filter(a => parseFloat(a.cost) >= 100 && parseFloat(a.cost) <= 1000);
    } else if (selected === "1000to2000") {
        filtered = allActivities.filter(a => parseFloat(a.cost) > 1000 && parseFloat(a.cost) <= 2000);
    } else if (selected === "over2000") {
        filtered = allActivities.filter(a => parseFloat(a.cost) > 2000);
    }

    displayActivities(filtered);
});
