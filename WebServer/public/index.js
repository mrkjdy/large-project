// Login
function login() {
	var submitMessage = document.getElementById("submitMessage");
	var username = document.getElementById("uName").value;
	var password = document.getElementById("pWord").value;
	var submitButton = document.getElementById("submitLogin");

	// Clear submit message and disable submit
	submitMessage.innerHTML = "";
	submitButton.disabled = true;

	// Check if user entered data
	if (username === "" || password === "") {
		submitMessage.innerHTML = "Missing username/password";
		submitButton.disabled = false;
	}

	// The actual POST
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/login");
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function(e) {
		// Server should send an error message or a redirect url
		var serverResponse = JSON.parse(this.responseText);
		if (this.status == 401 || this.status == 500) {
			if (serverResponse.errorMessage != null) {
				submitMessage.innerHTML = serverResponse.errorMessage;
			}
		}
		if (this.status == 200) {
			if (serverResponse.redirect != null) {
				window.location.href = serverResponse.redirect;
			}
		}
	}
	xhr.onerror = function(e) {
		submitMessage.innerHTML = e.message;
	}
	xhr.send(JSON.stringify({
		username: username,
		password: password
	}));

	// Reenable submit button once login has finished
	submitButton.disabled = false;
}

// Logout
function logout() {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/logout", true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function(e) {
		// Server should send an error message or a redirect url
		var serverResponse = JSON.parse(this.responseText);
		if (this.status == 400) {
			if (serverResponse.errorMessage != null) {
				console.log(serverResponse.errorMessage);
			}
		}
		if (this.status == 200) {
			if (serverResponse.redirect != null) {
				window.location.href = serverResponse.redirect;
			}
		}
	}
	xhr.onerror = function(e) {
		console.log(e.message);
	}
	xhr.send();

	return false;
}

// Create account
function createAccount() {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/register", true);

	xhr.onload = function(e) {
		if (this.readyState == 4 && this.status == 200) {
			// Display server response if registering is invalid
			if (this.responseText.registerMessage != null) {
				submitMessage = this.responseText.registerMessage;
				return false;
			}
		}
	}
	xhr.onerror = function(e) {
		submitMessage = e.message;
	}
	xhr.send({
		username: document.getElementById("newUName").value,
		password1: document.getElementById("newPWord1").value,
		password2: document.getElementById("newPWord2").value,
		firstname: document.getElementById("newFName").value,
		lastname: document.getElementById("newLName").value
	});

	return false;
}

// Shows create account form on login page
function showCreateAccount() {
	document.getElementById("loginForm").style.display = "none";
	document.getElementById("createAccountForm").style.display = "block";
	document.getElementById("newFName").focus();
	document.getElementById("submitMessage").innerHTML = "";
}

// Shows login form on login page
function showLogin() {
	document.getElementById("loginForm").style.display = "block";
	document.getElementById("createAccountForm").style.display = "none";
	document.getElementById("uName").focus();
	document.getElementById("submitMessage").innerHTML = "";
}
