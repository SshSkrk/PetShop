/***** register.js: Registration page logic *****/

document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const errorDiv = document.getElementById("regError");

    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("regUsername").value.trim();
        const password = document.getElementById("regPassword").value;
        const password2 = document.getElementById("regPassword2").value;
        errorDiv.style.display = "none";  // hide previous errors

        if (password !== password2) {
            errorDiv.textContent = "Passwords do not match.";
            errorDiv.style.display = "block";
            return;
        }
        if (username === "" || password === "") {
            errorDiv.textContent = "Username and password are required.";
            errorDiv.style.display = "block";
            return;
        }

        const newUserData = { username: username, password: password };
        try {
            const res = await fetch("/api/registrationOfNewCustomer", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newUserData)
            });
            if (!res.ok) {
                // If server provided error message, display it; otherwise generic
                let msg = "Registration failed. Please try again.";
                try {
                    const errData = await res.json();
                    if (errData.message) msg = errData.message;
                } catch (_) { /* ignore */ }
                errorDiv.textContent = msg;
                errorDiv.style.display = "block";
                return;
            }
            // Registration successful
            alert("Registration successful! Please log in with your new account.");
            window.location.href = "login.html";
        } catch (err) {
            console.error("Registration error:", err);
            errorDiv.textContent = "An error occurred. Please try again.";
            errorDiv.style.display = "block";
        }
    });
});