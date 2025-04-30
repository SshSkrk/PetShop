document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const errorDiv = document.getElementById("loginError");

    try {
        const res = await fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include", // important for session
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            errorDiv.textContent = "❌ Invalid username or password.";
            errorDiv.style.display = "block";
            return;
        }

        // Fetch the current user info via session
        const userRes = await fetch('/api/current-user', {
            credentials: 'include'
        });

        if (userRes.ok) {
            const user = await userRes.json();
            localStorage.setItem("currentUser", JSON.stringify(user));
            window.location.href = "index.html";
        } else {
            errorDiv.textContent = "Login succeeded, but user info could not be retrieved.";
            errorDiv.style.display = "block";
        }
    } catch (err) {
        console.error("Login error:", err);
        errorDiv.textContent = "An error occurred. Please try again later.";
        errorDiv.style.display = "block";
    }
});

// Optional: logout function (can be reused in other JS files)
async function logout() {
    try {
        const res = await fetch("/api/logout", {
            method: "POST",
            credentials: "include"
        });

        if (res.ok) {
            localStorage.removeItem("currentUser");
            window.location.href = "login.html";
        } else {
            alert("Logout failed.");
        }
    } catch (err) {
        console.error("Logout error:", err);
        alert("An error occurred during logout.");
    }
}
