// Login
function login() {
	var submitMessage = document.getElementById("submitmessage");
	var username = document.getElementById("username").value;
	var password = document.getElementById("password").value;
	var submitButton = document.getElementById("submitlogin");

	// Clear submit message and disable submit
	submitMessage.innerHTML = "";
	submitButton.disabled = true;

	// Check if user entered data
	if (username === "" || password === "") {
		submitMessage.innerHTML = "Missing username/password";
		submitButton.disabled = false;
		return false;
	}

	// The actual POST
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/login");
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function(e) {
		// Server should send an error message or a redirect url
		var serverResponse = JSON.parse(this.responseText);
		if (serverResponse.errorMessage != null) {
			submitMessage.innerHTML = serverResponse.errorMessage;
			return false;
		}
		if (serverResponse.redirect != null) {
			window.location.href = serverResponse.redirect;
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
		if (serverResponse.errorMessage != null) {
			console.log(serverResponse.errorMessage);
			return false;
		}
		if (serverResponse.redirect != null) {
			window.location.href = serverResponse.redirect;
		}
	}
	xhr.onerror = function(e) {
		console.log(e.message);
	}
	xhr.send();
}

// Create account
function createAccount() {
	var submitMessage = document.getElementById("submitmessage");
	var username = document.getElementById("newusername").value;
	var password1 = document.getElementById("password1").value;
	var password2 = document.getElementById("password2").value;
	var firstname = document.getElementById("newfirstname").value;
	var lastname = document.getElementById("newlastname").value;
	var weight = document.getElementById("weight").value;
	var height = document.getElementById("height").value;
	var submitButton = document.getElementById("submitregister");

	// Clear submit message and disable submit
	submitMessage.innerHTML = "";
	submitButton.disabled = true;

	// Check if user has entered data
	if (username === "" || password1 === "" || password2 === "" || firstname === "" || lastname === "") {
		submitMessage.innerHTML = "Missing info";
		submitButton.disabled = false;
		return false;
	}

	// Check if user has entered valid data
	if (!/^[a-z|\d]{1,20}$/i.test(username)) {
		submitMessage.innerHTML = "Invalid username";
		submitButton.disabled = false;
		return false;
	}
	if (password1 != password2) {
		submitMessage.innerHTML = "Passwords don't match";
		submitButton.disabled = false;
		return false;
	}
	if (/^[a-z]\d{1,45}$/i.test(password1)) {
		submitMessage.innerHTML = "Invalid password";
		submitButton.disabled = false;
		return false;
	}

	// The actual POST
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/register");
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function(e) {
		// Server should send an error message or a redirect url
		var serverResponse = JSON.parse(this.responseText);
		if (serverResponse.errorMessage != null) {
			// Account creation was successful
			if (serverResponse.errorMessage == "") {
				document.getElementById("username").value = username;
				document.getElementById("password").value = password1;
				login();
			}
			// There was an error
			else {
				submitMessage.innerHTML = serverResponse.errorMessage;
				return false;
			}
		}
	}
	xhr.onerror = function(e) {
		submitMessage.innerHTML = e.message;
	}
	xhr.send(JSON.stringify({
		firstname: firstname,
		lastname: lastname,
		weight: weight,
		height: height,
		username: username,
		password: password1
	}));

	submitButton.disabled = false;
}

// Shows create account form on login page
function showCreateAccount() {
	document.getElementById("loginForm").style.display = "none";
	document.getElementById("createAccountForm").style.display = "block";
	document.getElementById("newfirstname").focus();
	document.getElementById("submitmessage").innerHTML = "";
}

// Shows login form on login page
function showLogin() {
	document.getElementById("loginForm").style.display = "block";
	document.getElementById("createAccountForm").style.display = "none";
	document.getElementById("username").focus();
	document.getElementById("submitmessage").innerHTML = "";
}
