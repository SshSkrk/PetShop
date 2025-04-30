// auth.js
async function fetchCurrentUser() {
    try {
        const res = await fetch("/api/current-user", { credentials: "include" });
        if (!res.ok) {
            localStorage.removeItem("currentUser");
            return null;
        }
        const user = await res.json();
        user.isAdmin = user.role?.toUpperCase() === "ADMIN";
        localStorage.setItem("currentUser", JSON.stringify(user));
        return user;
    } catch (err) {
        console.error("Failed to fetch current user:", err);
        localStorage.removeItem("currentUser");
        return null;
    }
}

function getCurrentUser() {
    const user = localStorage.getItem("currentUser");
    return user ? JSON.parse(user) : null;
}

async function logout() {
    try {
        const res = await fetch("/api/logout", {
            method: "POST",
            credentials: "include"
        });

        if (res.ok) {
            localStorage.removeItem("currentUser");
            alert("Logged out successfully");
            window.location.href = "index.html";
        } else {
            alert("Logout failed");
        }
    } catch (err) {
        console.error("Logout error:", err);
        alert("An error occurred while logging out.");
    }
}
