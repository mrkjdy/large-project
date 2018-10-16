// Requires
// ----------------------------------------------------------------------

const express = require('express');
const mysql = require('mysql');
const pug = require('pug');
const path = require('path');



// Environment Variables
// ----------------------------------------------------------------------

const PORT = process.env.PORT || 5000;
const NODE_ENV = process.env.NODE_ENV || "development";



// Global Vars
// ----------------------------------------------------------------------

// Creates the express server
var app = express();

// Database connection info
var db = mysql.createPool({
	connectionLimit: 10,
	host     : 'us-cdbr-iron-east-01.cleardb.net',
  	user     : 'bbfac4dc0a8c9d',
  	password : 'dd4a3600',
  	database : 'heroku_52d2990a9088f84'
});



// App Config
// ----------------------------------------------------------------------

// Sets public as the public folder
app.use(express.static(path.join(__dirname, 'public')));

// Starts listening on the port provided by heroku
app.listen(PORT, function() {
	console.log("Listening on " + PORT)
});

// Redirects to HTTPS
app.use(function (req, res, next) {
	// The 'x-forwarded-proto' check is for Heroku
	if (!req.secure && req.get('x-forwarded-proto') !== 'https' && NODE_ENV !== "development") {
		return res.redirect('https://' + req.get('host') + req.url);
	}
	next();
});

// Sets pug as view engine
app.set('view engine', 'pug')
app.set('views', path.join(__dirname, 'public'));



// Post and Get functions
// ----------------------------------------------------------------------

app.get('/', function (req, res) {
  res.render('index')
})