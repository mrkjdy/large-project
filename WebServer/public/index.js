// Global vars
// ----------------------------------------------------------------------

const APIRoot = 'https://large-project.herokuapp.com/'; 
const fileExtension = '.js'; 
const indexURL = '/';

var userID = 0;
var firstName = '';
var lastName = '';

var JSONtextID = '';

////////////////////////////////////
// New functions
////////////////////////////////////

function logout() {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/logout", true);

	xhr.onload = function(e) {
		if (this.readyState == 4 && this.status == 200) {
			document.location.href = indexURL;
		}
	}
	xhr.onerror = function(e) {
		alert("Logout unsuccessful!");
	}
	xhr.send(null);
}

// Hashing password on client is not necessary with HTTPS!
// It will be hashed by server before being stored in DB!

// Checks username and password with server and then redirects to scores
function login() {
	var user = document.getElementById("uName").value;
	var pass = document.getElementById("pWord").value;

	// Create JSON package 
	var jsonPayload = '{"username" : "' + user + '", "password" : "' + pass + '"}';

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/login", true);

	try {
		xhr.onreadystatechange = function() {
    		if (this.readyState == 4 && this.status == 200) {

        		var JS = this.responseText;
        		var JSLength = JS.length;
				JS = JS.substr(1, (JSLength - 2));

				var jsonObject = JSON.parse(JS);

				//check wether login was succesfull
				if (!jsonObject.loginSuccess) {
					document.getElementById("submitMessage").innerHTML = "Username or Password incorrect";
					document.getElementById("submitMessage").style.display = "inherit";
					return false;
				}

				// redirection code
				document.location.href = indexURL;
			}
		};

		// send package to API
		xhr.send(jsonPayload);
	}
	catch(err) {
		document.getElementById("submitMessage").innerHTML = err.message;
	}

	return false;
}

// Registers account info with server and then login()s
function createAccount()
{
	var fName = document.getElementById("newFName").value;
	var lName = document.getElementById("newLName").value;
	var user = document.getElementById("newUName").value;
	var newPWord1 = document.getElementById("newPWord1").value;
	var newPWord2 = document.getElementById("newPword2").value;

	if (user == "")
	{
		document.getElementById("submitMessage").innerHTML = "Enter a user name";
		return;
	}

	// Check if passwords match
	if (newPWord1 !== newPWord2)
	{
		document.getElementById("submitMessage").innerHTML = "Passwords don't match";
		return;
	}

	// Check if username available
	var jsonPayload = '{"username" : "' + user + '", "password" : "' + newPWord1 
						+ '", "firstname" : "' + fName + '", "lastname" : "'
						+ lName + '"}';
	var url = '/register';

	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

	try
	{

		xhr.onreadystatechange = function() 
		{
			if (this.readyState == 4 && this.status == 200) 
			{
				document.getElementById("uName").value = user;
				document.getElementById("pWord").value = newPWord2;
				login();
			}
			else if (this.status == 400)
			{
				document.getElementById("submitMessage").innerHTML = "Username already used";
			}
		};
		xhr.send(jsonPayload);
	}
	catch(err)
	{
		document.getElementById("submitMessage").innerHTML = err.message;
	}
}

function showCreateAccount()
{
	document.getElementById("loginForm").style.display = "none";
	document.getElementById("createAccountForm").style.display = "block";
	document.getElementById("newFName").focus();
	document.getElementById("submitMessage").innerHTML = "";
}

function showLogin()
{
	document.getElementById("loginForm").style.display = "block";
	document.getElementById("createAccountForm").style.display = "none";
	document.getElementById("uName").focus();
	document.getElementById("submitMessage").innerHTML = "";
}



////////////////////////////////////
// Old functions
////////////////////////////////////


// Scores.html functions
// ----------------------------------------------------------------------

function getContacts()
{
	if(window.location.pathname.localeCompare('/contacts') === 0) {
		var url = '/getallcontact';
		
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");
		
		try
		{
			xhr.onreadystatechange = function() 
			{
				if (this.readyState == 4 && this.status == 200) 
				{
					var table = document.getElementById("cTable");

					// clear the table
					while (table.rows.length > 1)
					{
						table.deleteRow(table.rows.length - 1);
					}
					//console.log(table.rows.length);

					//console.log(this.responseText);

					var jt = this.responseText;
					//var JSLength = jt.length;
					//jt = jt.substr(1, (JSLength - 2));

					//console.log(jt);

					var jsonObject = JSON.parse(jt);

					// create the fields in the table 
					for(var i = 0; i < jsonObject.length; i++)
					{
						// create a new row
						var newRow = table.insertRow(table.rows.length);

						// create a new cell
						var cell = newRow.insertCell(0);
						// add value to the cell
						cell.innerHTML = jsonObject[i].FirstName;

						cell = newRow.insertCell(1);
						cell.innerHTML = jsonObject[i].LastName;
						cell = newRow.insertCell(2);
						cell.innerHTML = jsonObject[i].Email;
						cell = newRow.insertCell(3);
						cell.innerHTML = jsonObject[i].PhoneNumber;
						cell = newRow.insertCell(4);

						// Creates the X button to delete the contact TODO: revise
						cell.innerHTML = '<span id="pointer" onclick="deleteContact('+ (i + 1) + ', ' + jsonObject[i].ContactID +')" class="w3-button w3-display-right">&times;</span>';
					}
				}
			}
			xhr.send();
		}
		catch(err)
		{
			
		}
	}
}

function addContact()
{
	document.getElementById("contactAddResult").innerHTML = "";

	var fName = document.getElementById("newFirstName").value;
	var lName = document.getElementById("newLastName").value;
	var eMail = document.getElementById("newEmail").value;
	var pNum = document.getElementById("newPhone").value;

	document.getElementById("newFirstName").value = '';
	document.getElementById("newLastName").value = '';
	document.getElementById("newEmail").value = '';
	document.getElementById("newPhone").value = '';

	// // Get the user id
	// var JS = localStorage.getItem(JSONtextID);

	// //console.log(JS);

	// var uJsonObject = JSON.parse(JS);

	// //console.log(uJsonObject.UserId);

	// userID = uJsonObject.UserId;
	// // End

	// document.getElementById("contactAddResult").innerHTML = "";

	//console.log(userID);

	// Create JSON pacage and send it to API
	// var jsonPayload = '{"firstname" : "' + fName + '", "lastname" : "'
	// 					+ lName + '", "email" : "' + eMail 
	// 					+ '", "phone" : "' + pNum + '", "userid" : ' 
	// 					+ userID + '}';

	var jsonPayload = '{"firstname" : "' + fName + '", "lastname" : "'
						+ lName + '", "email" : "' + eMail 
						+ '", "phone" : "' + pNum + '"}';

	//console.log(jsonPayload);

	var url = '/addcontact';

	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

	try
	{
		xhr.onreadystatechange = function()
		{
			if (this.readyState == 4 && this.status == 200)
			{
				document.getElementById("contactAddResult").innerHTML = "Contact successfully added";
				
				var table = document.getElementById("cTable");
		
				// create a new row
				var newRow = table.insertRow(table.rows.length);

				// create a new cell
				var cell = newRow.insertCell(0);
				// add value to the cell
				cell.innerHTML = fName;
				cell = newRow.insertCell(1);
				cell.innerHTML = lName;
				cell = newRow.insertCell(2);
				cell.innerHTML = eMail;
				cell = newRow.insertCell(3);
				cell.innerHTML = pNum;
				cell = newRow.insertCell(4);
				
				var jsonObject = JSON.parse(this.responseText);

				// Creates the X button to delete the contact TODO: revise
				cell.innerHTML = '<span id="pointer" onclick="deleteContact('+ (table.rows.length - 1) + ', ' + jsonObject.insertId +')" class="w3-button w3-display-right">&times;</span>';
			}
		};
		xhr.send(jsonPayload);
		//console.log("payload sent");
	}
	catch(err)
	{
		document.getElementById("contactAddResult").innerHTML = "Contact not added successfully.";
	}
	// End

	// test the function is running: alert("addContact()");
}

function searchContact()
{
	document.getElementById("contactAddResult").innerHTML = "";
	
	var sString = document.getElementById("searchString").value;
	var sValue = document.getElementById("sBox").value;

	// // Get the user id
	// var JS = localStorage.getItem(JSONtextID);

	// //console.log(JS);

	// var uJsonObject = JSON.parse(JS);

	// //console.log(uJsonObject.UserId);

	// userID = uJsonObject.UserId;
	// // End

	// document.getElementById("colorSearchResult").innerHTML = "";

	var contactTable = document.getElementById("cTable");
	// Clear the table

	// Create JSON pacage and send it to API
	// var jsonPayload = '{"value" : "' + sString + '", "type" : "' + sValue + '", "userid" : ' 
	// 					+ userID + '}';
	var jsonPayload = '{"value" : "' + sString + '", "type" : "' + sValue + '"}';

	var url = '/searchcontact';

	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

	try
	{
		xhr.onreadystatechange = function() 
		{
			if (this.readyState == 4 && this.status == 200) 
			{
				var table = document.getElementById("cTable");

				// clear the table
				while (table.rows.length > 1)
				{
					table.deleteRow(table.rows.length - 1);
				}
				//console.log(table.rows.length);

				//console.log(this.responseText);

				var jt = this.responseText;
        		//var JSLength = jt.length;
				//jt = jt.substr(1, (JSLength - 2));

				//console.log(jt);

				var jsonObject = JSON.parse(jt);

				// create the fields in the table 
				for(var i = 0; i < jsonObject.length; i++)
				{
					// create a new row
					var newRow = table.insertRow(table.rows.length);

					// create a new cell
					var cell = newRow.insertCell(0);
					// add value to the cell
					cell.innerHTML = jsonObject[i].FirstName;

					cell = newRow.insertCell(1);
					cell.innerHTML = jsonObject[i].LastName;
					cell = newRow.insertCell(2);
					cell.innerHTML = jsonObject[i].Email;
					cell = newRow.insertCell(3);
					cell.innerHTML = jsonObject[i].PhoneNumber;
					cell = newRow.insertCell(4);

					// Creates the X button to delete the contact TODO: revise
					cell.innerHTML = '<span onclick="deleteContact('+ (i + 1) + ', ' + jsonObject[i].ContactID +')" class="w3-button w3-display-right">&times;</span>';
				}
			}
		};
		xhr.send(jsonPayload);
	}
	catch(err)
	{
		
	}
	// End

	// test the function is running: alert("searchContact()");
}

function deleteContact(index, id)
{
	// // Get the user id
	// var JS = localStorage.getItem(JSONtextID);

	// //console.log(JS);

	// var uJsonObject = JSON.parse(JS);

	// //console.log(uJsonObject.UserId);

	// userID = uJsonObject.UserId;
	// // End

	var table = document.getElementById("cTable");

	//var jsonPayload = '{"userid" : ' + userID + ', "contactid" : ' + id + '}';
	var jsonPayload = '{"contactid" : ' + id + '}';

	//console.log(jsonPayload);

	var url = '/deletecontact';

	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

	try
	{
		xhr.send(jsonPayload);
		//console.log("payload sent");
		document.getElementById("contactAddResult").innerHTML = "Contact successfully deleted";
	}
	catch(err)
	{
		document.getElementById("contactAddResult").innerHTML = "Contact not deleted successfully";

	}
	// End

	// Update the table on the website
	table.deleteRow(index);
	// End

	// test the function is running: alert("deleteContact()");
}
