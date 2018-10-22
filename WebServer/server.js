// Requires
// ----------------------------------------------------------------------

const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const pug = require('pug');
const path = require('path');
const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const favicon = require('serve-favicon');
const bcrypt = require('bcrypt');



// Global vars
// ----------------------------------------------------------------------

// These are grabbed here because they are used multiple times
const PORT = process.env.PORT || 5000;
const NODE_ENV = process.env.NODE_ENV || 'development';

// Creates the express server
var app = express();

// Database connection pool config
var dbPool = mysql.createPool({
	// Keep in mind our Ignite database can have 10 connections max
	connectionLimit: 10,
	host     : process.env.DATABASE_HOST,
  	user     : process.env.DATABASE_USER,
  	password : process.env.DATABASE_PASSWORD,
  	database : process.env.DATABASE_NAME
});

// The cost factor for bcrypt
const saltRounds = 10;



// App config
// ----------------------------------------------------------------------

// Sets public as the public folder
app.use(express.static(path.join(__dirname, 'public')));

// Starts listening on the port provided by heroku
app.listen(PORT, function() {
	console.log("Listening on " + PORT)
	console.log("NODE_ENV is " + NODE_ENV)
});

// Redirects to HTTPS
app.use(function (req, res, next) {
	// The 'x-forwarded-proto' check is for Heroku
	// NODE_ENV is set by Heroku
	if (!req.secure && req.get('x-forwarded-proto') !== 'https' && NODE_ENV !== "development") {
		return res.redirect('https://' + req.get('host') + req.url);
	}
	next();
});

// Sets pug as view engine
app.set('view engine', 'pug')
app.set('views', path.join(__dirname, 'public'));

// Body-parser initialization
app.set('trust proxy', 1);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(session({
	secret: process.env.SESSION_SECRET,
	resave: false,
	saveUninitialized: false,
	cookie: {
		secure: true,
		maxAge: 86400000
	}
}));
app.use(passport.initialize());
app.use(passport.session());

app.use(favicon(__dirname + '/public/favicon.ico'));



// Post and get functions
// ----------------------------------------------------------------------

// Passwords need to be hashed before they are stored in the database!
// To hash a password:
// bcrypt.hash(<password>, saltRounds, function(err, hash)) {
//	// Store hash in database here
// }
//
// To check a password:
// Load hash from DB
// bcrypt.compare(<password>, <hash>, function(err, res) {
//	if(res) {
//		// password matches!
//	}
//	else {
//		// password does not match!
//	}
// });

app.get('/', function (req, res) {
  res.render('index')
})