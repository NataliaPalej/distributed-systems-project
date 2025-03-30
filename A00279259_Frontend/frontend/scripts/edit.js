document.addEventListener("DOMContentLoaded", function () {
    populateTripDropdown();

    const params = new URLSearchParams(window.location.search);
    const type = params.get("type");

    const editTripForm = document.getElementById("editTripForm");
    const editActivityForm = document.getElementById("editActivityForm");
    const messageBox = document.getElementById("editMessage");
    
    const activityNameInput = document.getElementById("activityName");
	const imageFormatSpan = document.getElementById("imageFormatActivity");
	const tripDestinationInput = document.getElementById("destination");
	const imageFormatTripSpan = document.getElementById("imageFormatTrip");

    // Always hide both forms initially
    editTripForm.classList.add("d-none");
    editActivityForm.classList.add("d-none");
    
    if (messageBox) messageBox.classList.add("d-none");

    if (type === "trip") {
        editTripForm.classList.remove("d-none");
        const tripId = params.get("tripId");
        if (tripId) {
            fetchTripDetails(tripId);
        } else {
            messageBox?.classList.remove("d-none");
        }
    } else if (type === "activity") {
        editActivityForm.classList.remove("d-none");
        const activityId = params.get("activityId");
        if (activityId) {
            fetchActivityDetails(activityId);
        } else {
            messageBox?.classList.remove("d-none");
        }
    } else {
        messageBox?.classList.remove("d-none");
    }
    
    if (activityNameInput && imageFormatSpan) {
        activityNameInput.addEventListener("input", function () {
            const imageName = getActivityImageName(activityNameInput.value);
            imageFormatSpan.textContent = imageName ? `${imageName}.jpg` : "";
        });
    }
    
    if (tripDestinationInput && imageFormatTripSpan) {
    tripDestinationInput.addEventListener("input", function () {
        const imageName = cleanImageName(tripDestinationInput.value);
        imageFormatTripSpan.textContent = imageName ? `${imageName}.jpg` : "";
    });
}
});

function getURLParam(param) {
    return new URLSearchParams(window.location.search).get(param);
}

// Fetch trip details for editing
function fetchTripDetails(tripId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const xml = new DOMParser().parseFromString(xmlText, "application/xml");
        const destination = xml.querySelector("destination").textContent;

        document.getElementById("tripId").value = tripId;
        document.getElementById("destination").value = destination;
        document.getElementById("startDate").value = xml.querySelector("startDate").textContent;
        document.getElementById("endDate").value = xml.querySelector("endDate").textContent;
        document.getElementById("budget").value = xml.querySelector("budget").textContent;
        document.getElementById("notes").value = xml.querySelector("notes").textContent;

        const imageName = cleanImageName(destination);
        document.getElementById("imageFormatTrip").textContent = `${imageName}.jpg`;
        const img = document.getElementById("previewTripImage");
        img.src = `images/${imageName}.jpg`;
        img.onerror = () => img.src = "images/default.jpg";
    });
}

let originalTripId = null;

// Fetch activity details for editing
function fetchActivityDetails(activityId) {
    fetch(`http://localhost:8080/A00279259_Backend/rest/activities/${activityId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xmlText => {
        const xml = new DOMParser().parseFromString(xmlText, "application/xml");

        const tripId = xml.querySelector("tripId").textContent;
        const activityName = xml.querySelector("name").textContent;
        
        originalTripId = tripId;
        const activityDate = xml.querySelector("activityDate").textContent;

        document.getElementById("activityId").value = activityId;
        document.getElementById("tripIdActivity").value = tripId;
        document.getElementById("activityName").value = activityName;
        document.getElementById("activityDate").value = normalizeDate(activityDate.replace(/[^0-9/]/g, "/"));
        document.getElementById("activityLocation").value = xml.querySelector("location").textContent;
        document.getElementById("activityCost").value = xml.querySelector("cost").textContent;

        return fetchDestination(tripId).then(destination => {
            const folder = getFolderName(destination);
            const imageName = getActivityImageName(activityName);
            document.getElementById("imageFolderPath").textContent = `images/${folder}/`;
            document.getElementById("imageFormatActivity").textContent = `${imageName}.jpg`;

            const img = document.getElementById("previewActivityImage");
            img.src = `images/${folder}/${imageName}.jpg`;
            img.onerror = () => img.src = "images/default.jpg";
        });
    });
}

// Fetch destination name by tripId
function fetchDestination(tripId) {
    return fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        headers: { "Accept": "application/xml" }
    })
    .then(response => response.text())
    .then(xml => new DOMParser().parseFromString(xml, "application/xml").querySelector("destination").textContent.trim());
}

// Update Trip
function updateTrip() {
    const tripId = document.getElementById("tripId").value;
    const startDate = normalizeDate(document.getElementById("startDate").value);
    const endDate = normalizeDate(document.getElementById("endDate").value);
    
    const tripData = `
        <trip>
            <tripId>${tripId}</tripId>
            <destination>${document.getElementById("destination").value}</destination>
            <startDate>${startDate}</startDate>
            <endDate>${endDate}</endDate>
            <budget>${document.getElementById("budget").value}</budget>
            <notes>${document.getElementById("notes").value}</notes>
        </trip>`;

    fetch(`http://localhost:8080/A00279259_Backend/rest/trips/${tripId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/xml" },
        body: tripData
    })
    .then(res => {
        if (res.ok) window.location.href = "index.html";
        else alert("Error updating trip.");
    });
}

// Update Activity
function updateActivity() {
    const activityId = document.getElementById("activityId").value;
    const tripIdInput = document.getElementById("tripIdActivity").value.trim();
    const tripIdToUse = tripIdInput !== "" ? tripIdInput : originalTripId;
    const activityDate = normalizeDate(document.getElementById("activityDate").value);
    
    const activityData = `
        <activity>
            <activityId>${activityId}</activityId>
            <tripId>${tripIdToUse}</tripId>
            <name>${document.getElementById("activityName").value}</name>
            <activityDate>${activityDate}</activityDate>
            <location>${document.getElementById("activityLocation").value}</location>
            <cost>${document.getElementById("activityCost").value}</cost>
        </activity>`;

    fetch(`http://localhost:8080/A00279259_Backend/rest/activities/${activityId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/xml" },
        body: activityData
    })
    .then(response => {
        if (response.ok) {
            window.location.href = `activities.html?tripId=${tripIdToUse}`;
        } else {
            alert("Error updating activity.");
        }
    });
}

// Cancel and redirect
function cancelEdit() {
    const type = getURLParam("type");
    if (type === "trip") {
        window.location.href = "index.html";
    } else {
        const tripId = document.getElementById("tripIdActivity").value;
        window.location.href = `activities.html?tripId=${tripId}`;
    }
}

// Clean trip img name
function cleanImageName(destination) {
    return destination.toLowerCase().replace(/,/g, '').replace(/\s+/g, '-');
}

function getActivityImageName(name) {
    return name.trim().split(" ").slice(0, 2).join("-").toLowerCase();
}

function getFolderName(destination) {
    return destination.split(",")[0].toLowerCase().replace(/\s+/g, '-');
}

// Img preview
const tripImageInput = document.getElementById("tripImage");
if (tripImageInput) {
    tripImageInput.addEventListener("change", function (e) {
        const reader = new FileReader();
        reader.onload = e => document.getElementById("previewTripImage").src = e.target.result;
        reader.readAsDataURL(e.target.files[0]);
    });
}

const activityImageInput = document.getElementById("activityImage");
if (activityImageInput) {
    activityImageInput.addEventListener("change", function (e) {
        const reader = new FileReader();
        reader.onload = e => document.getElementById("previewActivityImage").src = e.target.result;
        reader.readAsDataURL(e.target.files[0]);
    });
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
setupDateFormatting("startDate");
setupDateFormatting("endDate");