const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const PORT = process.env.PORT || 5000;
const session = require('express-session');
const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const favicon = require('serve-favicon');

var app = express();
var path = require('path');

// Body-parser initialization
app.set('trust proxy', 1);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(session({
	secret: 'T9LqJYlgFNQi46lBBGge',
	resave: false,
	saveUninitialized: false,
	cookie: {
		secure: true,
		maxAge: 86400000
	}
}));
app.use(passport.initialize());
app.use(passport.session());

// Sets public as the public folder
app.use(express.static(path.join(__dirname, 'public')));
app.use(favicon(__dirname + '/public/favicon.ico'));

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