// base url of your backend
const BASE_URL = "http://localhost:8080";

// your API key
const API_KEY = "myApiKey123";

// store JWT token after login
let token = "";

// store all logs for filtering
let allLogs = [];

// login function
async function login() {
    const username = document
        .getElementById("username").value;
    const password = document
        .getElementById("password").value;

    try {
        const response = await fetch(
            BASE_URL + "/auth/login", {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (response.ok) {
            const data = await response.json();
            token = data.token;

            // hide login
            document.getElementById(
                "loginContainer")
                .style.display = "none";

            // show dashboard
            document.getElementById(
                "dashboard")
                .style.display = "block";

            // load data immediately
            loadStats();
            loadLogs();

            // auto refresh every 30 seconds
            setInterval(() => {
                loadStats();
                loadLogs();
            }, 30000);

        } else {
            document.getElementById(
                "loginError").textContent
                = "Invalid username or password";
        }

    } catch (error) {
        console.log("Login error: " + error);
        document.getElementById(
            "loginError").textContent
            = "Cannot connect to server. " +
              "Make sure backend is running.";
    }
}

// logout function
function logout() {
    token = "";
    allLogs = [];

    document.getElementById(
        "loginContainer")
        .style.display = "flex";

    document.getElementById(
        "dashboard")
        .style.display = "none";

    document.getElementById(
        "logsBody").innerHTML = "";
}

// get headers for every request
function getHeaders() {
    return {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "X-API-KEY": API_KEY,
        "Authorization": "Bearer " + token
    };
}

// load attack statistics
async function loadStats() {
    try {
        const response = await fetch(
            BASE_URL + "/threat/stats", {
            method: "GET",
            mode: "cors",
            headers: getHeaders()
        });

        if (response.ok) {
            const stats = await response.json();

            // update total attacks
            document.getElementById(
                "totalAttacks").textContent
                = stats.totalAttacks || 0;

            // update by type
            const types =
                stats.attacksByType || {};

            document.getElementById(
                "sqlCount").textContent
                = types["SQL_INJECTION"] || 0;

            document.getElementById(
                "xssCount").textContent
                = types["XSS_ATTACK"] || 0;

            // calculate other attacks
            const sql =
                types["SQL_INJECTION"] || 0;
            const xss =
                types["XSS_ATTACK"] || 0;
            const total =
                stats.totalAttacks || 0;
            const other = total - sql - xss;

            document.getElementById(
                "otherCount").textContent
                = other > 0 ? other : 0;

        } else {
            console.log("Stats failed: "
                + response.status);
        }

    } catch (error) {
        console.log("Stats error: " + error);
    }
}

// load all attack logs
async function loadLogs() {
    try {
        const response = await fetch(
            BASE_URL + "/threat/logs", {
            method: "GET",
            mode: "cors",
            headers: getHeaders()
        });

        if (response.ok) {
            allLogs = await response.json();
            renderLogs(allLogs);
        } else {
            console.log("Logs failed: "
                + response.status);
        }

    } catch (error) {
        console.log("Logs error: " + error);
    }
}

// render logs into table
function renderLogs(logs) {
    const tbody = document
        .getElementById("logsBody");
    const noLogs = document
        .getElementById("noLogs");

    tbody.innerHTML = "";

    if (logs.length === 0) {
        noLogs.style.display = "block";
        return;
    }

    noLogs.style.display = "none";

    // show latest first
    const sorted = [...logs].reverse();

    sorted.forEach(log => {
        const row = document
            .createElement("tr");

        // format timestamp
        const time = log.timestamp
            ? new Date(log.timestamp)
                .toLocaleString()
            : "unknown";

        // get badge class
        let badgeClass = "other";
        if (log.attackType === "SQL_INJECTION") {
            badgeClass = "sql";
        } else if (
                log.attackType === "XSS_ATTACK") {
            badgeClass = "xss";
        }

        // format attack type for display
        const attackDisplay = log.attackType
            ? log.attackType
                .replace(/_/g, " ")
            : "UNKNOWN";

        row.innerHTML = `
            <td>${log.id}</td>
            <td>${log.ipAddress || "unknown"}</td>
            <td>${log.endpoint || "/"}</td>
            <td>
                <span class="badge ${badgeClass}">
                    ${attackDisplay}
                </span>
            </td>
            <td>${time}</td>
            <td>
                <button
                    class="delete-btn"
                    onclick="deleteLog(${log.id})">
                    Delete
                </button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

// filter logs by attack type
function filterLogs() {
    const type = document
        .getElementById("filterType").value;

    if (type === "all") {
        renderLogs(allLogs);
    } else {
        const filtered = allLogs.filter(
            log => log.attackType === type);
        renderLogs(filtered);
    }
}

// delete a single log
async function deleteLog(id) {
    if (!confirm(
            "Are you sure you want to " +
            "delete this log?")) return;

    try {
        const response = await fetch(
            BASE_URL + "/threat/logs/" + id, {
            method: "DELETE",
            mode: "cors",
            headers: getHeaders()
        });

        if (response.ok) {
            // reload after delete
            loadLogs();
            loadStats();
        } else {
            console.log("Delete failed: "
                + response.status);
        }

    } catch (error) {
        console.log("Delete error: " + error);
    }
}