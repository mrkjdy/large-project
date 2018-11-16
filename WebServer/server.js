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
const schedule = require('node-schedule');


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
	console.log(process.env.DATABASE_HOST)
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

// Passport initialization
passport.use(new LocalStrategy(function(username, password, done) {
	
	if(checkInput(username, 'username') === true && password) {
		
		dbPool.getConnection(function(err, tempCont) {
			if(err) {
				console.log(err);
				return done(true, false);
			} else {
				tempCont.query("SELECT * FROM User WHERE login = ?;", [username], function(err, result) {
					if(err) {
						return done(true, false);
					} else {
						bcrypt.compare(password, result[0].password, function(err, res) {
							if(res) {
								tempCont.query("UPDATE User SET dateLastLoggedIn = NOW() WHERE user_id = ?;", [result[0].UserID], function(err, result1) {
				 					if(err) console.log(err);
				 					return done(false, result[0]);
				 				});
							} else if(err) {
								return done(true, false);
							} else {
								return done(false, false);
							}
						});
					}
				});
			}
			tempCont.release();
		});
	} else {
		return done(null, false);
	}
}));

// Uses user ID to generate session token
passport.serializeUser(function(user, done) {
	done(null, user.user_id);
});

// Converts session token to user ID, gets user info from database
passport.deserializeUser(function(id, done) {
	
	dbPool.getConnection(function(err, tempCont) {
		if(err) {
			console.log(err);
		} else {
			tempCont.query("SELECT * FROM User WHERE user_id = ?;", [id], function(err, result) {
				if(err) {
					console.log(err);
				} else {
					done(null, result[0]);
				}
			});
		}
		tempCont.release();
	});
});

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
	res.render('index', req.user);
});

app.get('/login', function (req, res) {
	res.render('login');
});

// Register function
app.post('/register', function(req, res) {
	
	// Check if correct format
	if(checkInput(req.body.firstname, "name") && checkInput(req.body.lastname, "name") && checkInput(req.body.username, "username") && checkInput(req.body.weight, "number") && checkInput(req.body.height, "number")) {
		
		// Create connection to database
		dbPool.getConnection(function(err, tempCont){
			
			// Error if connection is not established
			if(err) {
				res.status(400).send();
				
			} else {
				
				// Check if username exists
				tempCont.query("SELECT * FROM User WHERE login = ?", [req.body.username], function(err, result){
					
					// Check if query works
					if(err) {
						res.status(400).send();
					} else {
						
						// Return if username exists
						if(result != ""){
							res.status(470).send();
					
						} else {
							bcrypt.hash(req.body.password, saltRounds, function(err, hash) {
								// Add user to database
								const sqlAddUser = "INSERT INTO User (dateCreated, dateLastLoggedIn, login, password, firstName, lastName, height, weight) VALUES (";
								tempCont.query(sqlAddUser + "NOW(), NOW(), '" + req.body.username + "', '" + hash + "', '" + req.body.firstname + "', '" + req.body.lastname + "', '" + req.body.height + "', '" + req.body.weight + "')", function(err, result) {
								
									// Check if query works
									if(err) {
										console.log(err);
										res.status(400).send();
									} else {
										res.status(200).send();	
									}
								
									// End connection
									tempCont.release();
								
								});
							});
						}
					} 	
				});	
			}
		});
	
	} else {
		res.status(401).send();
	}
});

// Login function
app.post('/login', function(req, res) {
	
	passport.authenticate('local', function(err, user, info) {
		
		if(err) {
			return res.status(400).send();
		}
		if(!user) {
		
			return res.status(401).send();
		}
		
		req.logIn(user, function(err) {
			
			if(err) {
				console.log(err);
				return res.status(400).send();
			}
			
			return res.status(200).send(user);
		});
	})(req, res);
});

// Logout function
app.post('/logout', function(req, res) {
	req.logout();
	req.session.destroy(function(err) {
		if(err)	{
			console.log(err);
			res.status(400).send();
		} else {
			res.status(200).send();
		}
	});
});

// Get all user info
app.post('/getuserdata', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		switch(req.body.table) {
			case "User":
				res.status(200).send(new JSONObject('{"table":"User","value":"' + req.user.stringify() + '"}'));
				break;
			
			case "Workout":
			case "Daily Stats":
				dbPool.getConnection(function(err, tempCont) {
					if(err) {
						console.log(err);
						res.status(400).send();
					} else {
						tempCont.query("SELECT * FROM " + req.body.table + " WHERE user_id = " + req.user.user_id + ";", function(err, result) {
							if(err) {
								console.log(err);
								res.status(400).send();
							} else {
								if(result[0]) {
									res.status(200).send('{"table":"' + req.body.table + '","value":"' + result[0].stringify() + '"}');
								} else {
									res.status(400).send();
								}
							}
						});
					}
					tempCont.release();
				});
				break;
				
			default:
				res.status(400).send();
				break;
		}
	}
});

// Update user info in database
app.post('/updateuserdata', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		if(!req.body.fields || !req.body.values || !req.body.table || req.body.fields.length != req.body.values.length || (req.body.table != "User" && req.body.table != "Daily Stats" && req.body.table != "Workout" && req.body.table != "Friendship")) {
			res.status(400).send();
		} else {
			var validRequest = false;
			var updateValues = "";
			for(var i=0; i<req.body.fields.length; i++) {
				// !!! Ensure that all editable and only editable fields are present, and have correct type specified for validation
				switch(req.body.fields[i]) {
					//    field name
					case "firstName":
						//                        field value          field type
						validRequest = checkInput(req.body.values[i], "name");
						break;
					
					case "lastName":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "height":
						validRequest = checkInput(req.body.values[i], "");
						break;
						
					case "weight":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "steps":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "calories":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "distance":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "points":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "numOfPeople":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					case "coordinates":
						validRequest = checkInput(req.body.values[i], "");
						break;
					
					default:
						validRequest = false;
						break;
				}
				if(validRequest = false) {
					i = req.body.fields.length;
				} else {
					updateValues += req.body.fields[i] + "='" + req.body.values[i] + "', '";
				}
			}
			if(!validRequest) {
				res.status(400).send();
			} else {
				dbPool.getConnection(function(err, tempCont){
					if(err) {
						res.status(400).send();
					} else {
						tempCont.query("UPDATE ? SET ? WHERE user_id=?;", [table, updateValues.slice(0, -3), req.body.user_id], function(err, result) {
							if(err || !result) {
								res.status(400).send();
							} else {
								res.status(200).send();
							}
						});
					}
				});
			}
		}
	}
});

// Helper functions
// ----------------------------------------------------------------------

// Checks if input provided by user is formatted correctly for storage in database
var checkInput = function(input, type, callback) {
	
	var returnVal = null;
	
	switch(type) {
		
		case "username":
			var re = /^[a-z|\d]{1,20}$/i; // Format 5-20 characters and digit
			returnVal = re.test(input);
			break;
			
		case "name":
			var re = /^[a-z]{1,45}$/i; // Format 45 characters
			returnVal = re.test(input);
			break;
			
		case "number":
			returnVal = !isNaN(input);
			break;
		
		default:
			returnVal = null;
			break;
	}
	
	if(callback == undefined) {	
		return returnVal;
		
	} else {
		callback(returnVal);
	}
}

// Create scheduled job (node-schedule) that updates user table with daily stats
var dailyUpdateUserStats = schedule.scheduleJob('00 00 00 * * 0-6', function() {
	
	// !!! Query that adds points, distance, steps from Daily Stats to total_points, total_distance, total_steps in User
	var updateQuery = "";
	dbPool.getConnection(function(err, tempCont) {
		if(err) {
			console.log(err);
		} else {
			tempCont.query(updateQuery, function(err, result) {
				if(err) {
					console.log(err);
				}
			});
		}
		tempCont.release();
	});
});