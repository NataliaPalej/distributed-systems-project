document.getElementById("cancelDeleteBtn")?.addEventListener("click", closeModal);

function closeModal() {
    const modal = document.getElementById("deleteModal");
    if (modal) modal.style.display = "none";
}

// Clean and parse date format 
function normalizeDate(input) {
    if (!input || input.trim() === "") return "TBC";

    const cleaned = input.replace(/[-.]/g, "/");
    const [dd, mm, yyyy] = cleaned.split("/");

    if (!dd || !mm || !yyyy) return "TBC";

    return `${dd.padStart(2, "0")}/${mm.padStart(2, "0")}/${yyyy}`;
}

function toDate(dateString) {
    const [dd, mm, yyyy] = normalizeDate(dateString).split("/");
    return new Date(`${yyyy}-${mm}-${dd}`);
}

// Populate Dropdown
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
        if (!dropdown) return;

        dropdown.innerHTML = "";

        if (tripElements.length === 0) {
            const noTrips = document.createElement("li");
            noTrips.className = "dropdown-item text-muted";
            noTrips.textContent = "No trips available";
            dropdown.appendChild(noTrips);
            return;
        }

        for (let trip of tripElements) {
            let tripId = trip.getElementsByTagName("tripId")[0].textContent;
            let destination = trip.getElementsByTagName("destination")[0].textContent;

            const tripItem = document.createElement("a");
            tripItem.className = "dropdown-item";
            tripItem.href = `activities.html?tripId=${tripId}`;
            tripItem.textContent = destination;
            dropdown.appendChild(tripItem);
        }
    })
    .catch(error => console.error("Error fetching trips for dropdown:", error));
}

// DB Optiohns
function populateDatabase() {
    fetch("http://localhost:8080/A00279259_Backend/rest/admin/populate", {
        method: "POST"
    })
    .then(response => response.text())
    .then(message => {
        showToast(message);
        if (typeof fetchTrips === "function") fetchTrips();
    })
    .catch(error => {
        console.error("Error initializing database:", error);
        showToast("Error initializing database", "danger");
    });
}

function clearDatabase() {
    fetch("http://localhost:8080/A00279259_Backend/rest/admin/clear", {
        method: "DELETE"
    })
    .then(response => response.text())
    .then(message => {
        showToast(message);
        if (typeof fetchTrips === "function") fetchTrips();
    })
    .catch(error => console.error("Error clearing database:", error));
}

function exportDatabase() {
    fetch("http://localhost:8080/A00279259_Backend/rest/admin/export")
    .then(response => response.text())
    .then(csvData => {
        const blob = new Blob([csvData], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "trip-data.csv";
        a.click();
        showToast("Trip data exported successfully.");
    })
    .catch(error => console.error("Error exporting trips:", error));
}

// Toast Notification
function showToast(message, type = "success") {
    const toastEl = document.getElementById("liveToast");
    const toastBody = document.getElementById("toastMessage");

    if (!toastEl || !toastBody) return;

    toastBody.innerText = message;

    toastEl.classList.remove("bg-success", "bg-danger", "bg-info");
    toastEl.classList.add(
        type === "danger" ? "bg-danger" :
        type === "info" ? "bg-info" :
        "bg-success"
    );

    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}
