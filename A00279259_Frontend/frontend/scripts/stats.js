document.addEventListener("DOMContentLoaded", function() {
	populateTripDropdown();
    // Load activity stats
    fetchActivityStats()
        .then(stats => {
            renderActivitiesPerDestinationChart(stats);
            renderCostPerTripChart(stats);
        })
        .catch(error => {
            console.error("Error loading activity stats:", error);
        });

    // Load trips per month
    fetchTrips()
        .then(monthlyCounts => {
            renderTripsPerMonthChart(monthlyCounts);
        })
        .catch(error => {
            console.error("Error loading trips data:", error);
        });

    fetch("http://localhost:8080/A00279259_Backend/rest/trips", {
        headers: { "Accept": "application/json" }
    })
    .then(response => response.json())
    .then(trips => {
        const processedTrips = parseTripDates(trips);
        renderVisitedCountries(processedTrips);
        renderTripDurationChart(processedTrips);
        renderCostPerDayChart(processedTrips);
        renderTripStatusChart(processedTrips);
    })
    .catch(error => {
        console.error("Error loading trip data:", error);
    });
});

function fetchActivityStats() {
    return fetch("http://localhost:8080/A00279259_Backend/rest/trips/activity-stats", {
        headers: { "Accept": "application/json, application/xml" }
    })
    .then(response => response.json())
    .catch(error => {
        console.error("Error fetching activity stats:", error);
        return [];
    });
}

function fetchTrips() {
    return fetch("http://localhost:8080/A00279259_Backend/rest/trips", {
        headers: { "Accept": "application/json" }
    })
    .then(response => response.json())
    .then(trips => {
        const currentYear = new Date().getFullYear();
        let monthlyCount = Array(12).fill(0);

        trips.forEach(trip => {
            if (trip.startDate && trip.startDate.toLowerCase() !== "tbc") {
                // Parse date in dd/MM/yyyy format
                const dateParts = trip.startDate.split('/');
                if (dateParts.length === 3) {
                    const day = parseInt(dateParts[0], 10);
                    const month = parseInt(dateParts[1], 10) - 1; 
                    const year = parseInt(dateParts[2], 10);
                    
                    // Only count if the year matches current year
                    if (year === currentYear && month >= 0 && month <= 11) {
                        monthlyCount[month]++;
                    }
                }
            }
        });
        return monthlyCount;
    })
    .catch(error => {
        console.error("Error fetching trips:", error);
        return Array(12).fill(0);
    });
}

// Total activities per trip 
function renderActivitiesPerDestinationChart(stats) {
    if (stats.length === 0) return;

    const destinations = stats.map(stat => stat.destination);
    const totalActivities = stats.map(stat => stat.totalActivities);

    new Chart(document.getElementById('popularDestinationsChart'), {
        type: 'bar',
        data: {
            labels: destinations,
            datasets: [{
                label: 'Total Activities',
                data: totalActivities,
                backgroundColor: '#198754'
            }]
        },
        options: { 
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: Math.max(...totalActivities) + 1,
                    title: {
                        display: true,
                        text: 'Number of Activities'
                    }
                }
            }
        }
    });
}

// Total cost per trip 
function renderCostPerTripChart(stats) {
    if (stats.length === 0) return;

    const destinations = stats.map(stat => stat.destination);
    const totalCosts = stats.map(stat => parseFloat(stat.totalCost));

    new Chart(document.getElementById('costPerTripChart'), {
        type: 'bar',
        data: {
            labels: destinations,
            datasets: [{
                label: 'Activity Cost (€) Per Trip',
                data: totalCosts,
                backgroundColor: '#ffc107'
            }]
        },
        options: { 
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: Math.max(...totalCosts) + 1,
                    title: {
                        display: true,
                        text: 'Cost (€)'
                    }
                }
            }
        }
    });
}

// Total trips per month
function renderTripsPerMonthChart(monthlyCounts) {
    const monthLabels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

    new Chart(document.getElementById('tripsPerMonthChart'), {
        type: 'bar',
        data: {
            labels: monthLabels,
            datasets: [{
                label: 'Total Trips',
                data: monthlyCounts,
                backgroundColor: '#0d6efd'
            }]
        },
        options: { 
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: Math.max(...monthlyCounts) + 1,
                    ticks: {
			            stepSize: 1
			        },
                    title: {
                        display: true,
                        text: 'Number of Trips'
                    }
                }
            }
        }
    });
}

// Parse date and calculate trip duration
function parseTripDates(trips) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return trips.map(trip => {
        if (trip.startDate && trip.endDate && 
            trip.startDate.toLowerCase() !== 'tbc' && 
            trip.endDate.toLowerCase() !== 'tbc') {
            
            const startParts = trip.startDate.split('/');
            const endParts = trip.endDate.split('/');
            
            const startDate = new Date(
                parseInt(startParts[2]), 
                parseInt(startParts[1]) - 1, 
                parseInt(startParts[0])
            );
            
            const endDate = new Date(
                parseInt(endParts[2]), 
                parseInt(endParts[1]) - 1, 
                parseInt(endParts[0])
            );
            
            const durationDays = Math.round((endDate - startDate) / (1000 * 60 * 60 * 24));
            const isUpcoming = startDate > today;
            
            return {
                ...trip,
                durationDays,
                isUpcoming,
                parsedStartDate: startDate,
                parsedEndDate: endDate
            };
        }
        return null;
    }).filter(trip => trip !== null);
}

// Get country from destination
function getCountryFromDestination(destination) {
    if (!destination) return 'Unknown';
    const parts = destination.split(',').map(part => part.trim());
    return parts.length > 0 ? parts[parts.length - 1] : 'Unknown';
}

// Trip duration chart
function renderTripDurationChart(processedTrips) {
    // Group by country and calculate average duration
    const durationByCountry = {};
    
    processedTrips.forEach(trip => {
        const country = getCountryFromDestination(trip.destination);
        
        if (!durationByCountry[country]) {
            durationByCountry[country] = {
                totalDays: 0,
                count: 0,
                countryCode: getCountryCode(country)
            };
        }
        durationByCountry[country].totalDays += trip.durationDays;
        durationByCountry[country].count++;
    });
    
    const countries = Object.keys(durationByCountry);
    const avgDurations = countries.map(country => 
        Math.round(durationByCountry[country].totalDays / durationByCountry[country].count)
    );
    
    new Chart(document.getElementById('tripDurationChart'), {
        type: 'bar',
        data: {
            labels: countries,
            datasets: [{
                label: 'Average Duration (Days)',
                data: avgDurations,
                backgroundColor: '#17a2b8'
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: Math.max(...avgDurations) + 1,
                    title: {
                        display: true,
                        text: 'Days'
                    }
                },
                x: {
                    ticks: {
		            autoSkip: false,
		            maxRotation: 45,
		            minRotation: 45
                }
               }
            },
            plugins: {
                legend: {
                    display: false 
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.raw} days`;
                        }
                    }
                }
            }
        },
    });
}

// Flag mapping
function getCountryCode(countryName) {
    const countryMap = {
        'USA': 'US',
        'United States': 'US',
        'Greece': 'GR',
        'Indonesia': 'ID',
        'Poland': 'PL',
        'France': 'FR',
        'Italy': 'IT',
        'Spain': 'ES',
        'Germany': 'DE',
        'Portugal': 'PT',
        'Japan': 'JP',
        'Australia': 'AU',
        'UK': 'GB',
        'United Kingdom': 'GB',
        'England': 'GB',
        'Czech': 'CZ',
        'Czech Republic': 'CZ',
        'Hungry': 'HU',
        'Turkey': 'TR',
        'Edypt': 'EG',
        'Vietnam': 'VN',
        'Mexico': 'MX',
        'Brazil': 'BR',
        'Brasil': 'BR',
        'Peru': 'PE',
        'Iceland': 'IS',
        'Ireland': 'IE',
        'Belgium': 'BE',
        'Netherlands': 'NL',
        'Holland': 'NL',
        'Slovakia': 'SK',
        'Bulgaria': 'BG',
        'Thailand': 'TH',
		'South Africa': 'ZA'
    };
    
    // Normalize country name 
    const normalizedCountry = countryName
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();
    
    // Find matching country (case insensitive)
    for (const [name, code] of Object.entries(countryMap)) {
        if (name.toLowerCase() === normalizedCountry) {
            return code;
        }
    }
    return '';
}

// Cost per day chart 
function renderCostPerDayChart(processedTrips) {
    fetchActivityStats().then(activityStats => {
        const costPerDayData = processedTrips.map(trip => {
            const stats = activityStats.find(s => s.tripId === trip.tripId);
            const totalCost = stats ? parseFloat(stats.totalCost) : 0;
            return {
                destination: trip.destination,
                costPerDay: totalCost / trip.durationDays
            };
        });
        
        new Chart(document.getElementById('costPerDayChart'), {
            type: 'bar',
            data: {
                labels: costPerDayData.map(t => t.destination),
                datasets: [{
                    label: 'Cost Per Day (€)',
                    data: costPerDayData.map(t => t.costPerDay.toFixed(2)),
                    backgroundColor: '#6f42c1'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        suggestedMax: Math.max(...costPerDayData) + 1,
                        title: {
                            display: true,
                            text: 'Cost (€)'
                        }
                    }
                }
            }
        });
    });
}

// Future vs Past trips
function renderTripStatusChart(processedTrips) {
    const statusCounts = processedTrips.reduce((acc, trip) => {
        acc[trip.isUpcoming ? 'Upcoming' : 'Past']++;
        return acc;
    }, { 'Upcoming': 0, 'Past': 0 });
    
    new Chart(document.getElementById('tripStatusChart'), {
        type: 'pie',
        data: {
            labels: ['Upcoming Trips', 'Past Trips'],
            datasets: [{
                data: [statusCounts.Upcoming, statusCounts.Past],
                backgroundColor: ['#20c997', '#fd7e14']
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top'
                }
            }
        }
    });
}

function renderVisitedCountries(trips) {
    const visited = new Map();

    trips.forEach(trip => {
        const country = getCountryFromDestination(trip.destination);
        const code = getCountryCode(country);
        if (country !== 'Unknown' && code) {
            visited.set(country, code.toLowerCase());
        }
    });

    const container = document.getElementById("visitedCountriesSection");

    if (visited.size === 0 || !container) return;

    let html = `<h5>Visited Countries (${visited.size}):</h5>`;
    html += '<div style="display: flex; flex-wrap: wrap; gap: 15px;">';

    visited.forEach((code, country) => {
        html += `
            <div style="display: flex; gap: 8px;">
                <span class="fi fi-${code}"></span>
                <span>${country}</span>
            </div>
        `;
    });

    html += '</div>';
    container.innerHTML = html;
}