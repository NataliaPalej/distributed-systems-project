document.addEventListener("DOMContentLoaded", async function () {
    let tripsList = document.getElementById("trips-list");

    let trips = await fetchTrips();
    trips.forEach(trip => {
        let li = document.createElement("li");
        li.textContent = `${trip.destination} - ${trip.startDate} to ${trip.endDate}`;
        tripsList.appendChild(li);
    });
});

document.getElementById("trip-form").addEventListener("submit", async function (event) {
    event.preventDefault();

    let newTrip = {
        destination: document.getElementById("destination").value,
        startDate: document.getElementById("startDate").value,
        endDate: document.getElementById("endDate").value,
        budget: document.getElementById("budget").value,
        notes: document.getElementById("notes").value
    };

    try {
        let response = await fetch(`${API_URL}/trips`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams(newTrip)
        });

        if (response.ok) {
            alert("Trip added successfully!");
            window.location.reload();
        } else {
            alert("Error adding trip.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
});
