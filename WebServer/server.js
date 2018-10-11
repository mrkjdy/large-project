const express = require('express');
const mysql = require('mysql');
const PORT = process.env.PORT || 5000;

var app = express();
var path = require('path');

// Sets public as the public folder
app.use(express.static(path.join(__dirname, 'public')));

// Creates the server
app.listen(PORT, function() {
	console.log("Listening on " + PORT)
});

// Database connection info
var db = mysql.createPool({
	connectionLimit: 10,
	host     : 'us-cdbr-iron-east-01.cleardb.net',
  	user     : 'bbfac4dc0a8c9d',
  	password : 'dd4a3600',
  	database : 'heroku_52d2990a9088f84'
});