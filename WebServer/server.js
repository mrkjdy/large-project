////////////////////////////////////
// Requires
////////////////////////////////////

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
const cdnurl = "";


////////////////////////////////////
// Global vars
////////////////////////////////////

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

// Top 100 Users, periodically updated to save the database
var topRankedUsers;
var sessionID;



////////////////////////////////////
// App config
////////////////////////////////////

// Sets public as the public folder
app.use(express.static(path.join(__dirname, 'public')));

// Starts listening on the port provided by heroku
app.listen(PORT, function() {
	console.log("Listening on " + PORT);
	console.log("NODE_ENV is " + NODE_ENV);
	console.log(process.env.DATABASE_HOST);
	getNewTopUsers();
	getSessionID();
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
app.set('views', path.join(__dirname, 'pug'));

// Body-parser initialization
app.set('trust proxy', 1);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false}));
app.use(session({
	secret: process.env.SESSION_SECRET,
	resave: false,
	saveUninitialized: false,
	cookie: {
		secure: true,
		maxAge: 86400000,
		secure: (NODE_ENV === 'development' ? false : true)
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
				tempCont.query("SELECT password, user_id FROM User WHERE login = ?;", [username], function(err, result) {
					if(err) {
						console.log(err);
						return done(true, false);
					} else if(!result[0]) {
						return done(false, false);
					} else {
						bcrypt.compare(password, result[0].password, function(err, res) {
							if(res) {
								tempCont.query("UPDATE User SET dateLastLoggedIn = NOW() WHERE user_id = ?;", [result[0].UserID], function(err, result1) {
				 					if(err) console.log(err);
				 					return done(false, result[0]);
				 				});
							} else if(err) {
								console.log(err);
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
			// UPDATE to not include password, any other sensitive info
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



////////////////////////////////////
// API functions
////////////////////////////////////

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
	res.render('index', {
        user: req.user,
        top: topRankedUsers
    });
});

app.get('/login', function (req, res) {
	res.render('login', {
        user: req.user,
        showLogin: true
    });
});

app.get('/create-account', function (req, res) {
	res.render('login', {
        user: req.user,
        showLogin: false
    });
});

app.get('/user/:username', function (req, res) {

	var userinfo = null, own = false, globalrank = 0, friendrank = 0, friendtable = topRankedUsers;

	console.log("test");
	if (req.user && req.user.login === req.params.username) {
		userinfo = req.user;
		own = true;
	}
	else
		userinfo = getUserPageData(req.params.username, req.user);

	// if not found display 404
	if (userinfo != null) {
		res.render('profile', {
			user: req.user,
			userinfo: userinfo,
			globalrank: globalrank,
			friendrank: friendrank,
			friendtable: friendtable,
			own: own
		});
	}
	else {
		res.status(404).redirect('/404');
	}
});

app.get('/myprofile', function (req, res) {
	res.redirect('/user/' + req.user.login);
});

app.get('/404', function (req, res) {
	res.render('404', {
        user: req.user
    });
});

app.get('/search/:searchstring', function (req, res) {
	// get table from database
	var searchString = decodeURI(req.params.searchstring);
	console.log(searchString);
	var searchResults = search(searchString);

	// render table
	res.render('search', {
		user: req.user,
		searchResults: searchResults,
		searchString: searchString
	});
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
		// Database error
		if(err) {
			return res.status(500).send(JSON.stringify({errorMessage: err.message, loginSuccess: false}));
		}
		// Credentials invalid
		if(!user) {
			return res.status(401).send(JSON.stringify({errorMessage: "Username/Password incorrect", loginSuccess: false}));
		}
		req.logIn(user, function(err) {
			if(err) {
				return res.status(500).send(JSON.stringify({errorMessage: err.message, loginSuccess: false}));
      		}
      		return res.status(200).send(JSON.stringify({redirect: "/", loginSuccess: true}));
      	});
	})(req, res);
});

// Logout function
app.post('/logout', function(req, res) {
	req.logout();
	req.session.destroy(function(err) {
		if(err)	{
			res.status(400).send(JSON.stringify({errorMessage: err.message}));
		} else {
			res.status(200).send(JSON.stringify({redirect: "/"}));
		}
	});
});

// Get all user info
app.post('/getuserdata', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		switch(req.body.table) {
			// TODO: Update this so that password doesn't get sent to user
			case "User":
			case "Workout":
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
									res.status(200).send(JSON.stringify(
										{
											table: req.body.table,
											value: result[0]
										}
									));
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
		if(!req.body["fields[0]"] || !req.body["values[0]"] || req.body.table != "User") {
			console.log("input error");
			res.status(400).send();
		} else {
			var i = 0;
			while(req.body["fields[" + (i + 1) + "]"] != undefined) {
				if(req.body["values[" + (i + 1) + "]"] == undefined) {
					console.log("fields and values not equal");
					res.status(400).send();
				} else {
					i++;
				}
			}
			var updateValues = "";
			var validRequest = false;
			for(var j = 0; j <= i; j++) {
				switch(req.body["fields[" + j + "]"]) {
					case "latitude":
						validRequest = checkInput(req.body["values[" + j + "]"], "number");
						break;

					case "longitude":
						validRequest = checkInput(req.body["values[" + j + "]"], "number");
						break;

					case "isPrivate":
						validRequest = checkInput(req.body["values[" + j + "]"], "boolean");
						break;
					
					case "firstName":
						validRequest = checkInput(req.body["values[" + j + "]"], "username");
						break;
					
					case "lastName":
						validRequest = checkInput(req.body["values[" + j + "]"], "username");
						break;
					
					case "height":
						validRequest = checkInput(req.body["values[" + j + "]"], "number");
						break;
					
					case "weight":
						validRequest = checkInput(req.body["values[" + j + "]"], "number");
						break;
					
					case "total_points":
						validRequest = checkInput(req.body["values[" + j + "]"], "number");
						break;
					
					
					
					
					default:
						validRequest = false;
						break;
				}
				if(!validRequest) {
					j = i + 1;
				} else {
					updateValues += req.body["fields[" + j + "]"] + "=" + req.body["values[" + j + "]"] + ", ";
				}
			}
			if(!validRequest) {
				console.log("field not validated");
				res.status(400).send();
			} else {
				dbPool.getConnection(function(err, tempCont){
					if(err) {
						res.status(400).send();
					} else {
						tempCont.query("UPDATE " + req.body.table + " SET " + updateValues.slice(0, -2) + " WHERE user_id=" + req.user.user_id + ";", function(err, result) {
							if(err) {
								console.log("Query failed");
								res.status(400).send();
							} else {
								res.status(200).send();
							}
						});
					}
					tempCont.release();
				});
			}
		}
	}
});

// Gets top 100 users, must specify if global or friends
app.post('/gettopusers', function(req, res) {
	if(!req.body.group) {
		res.status(400).send();
	} else {
		if(req.body.group === "global") {
			if(topRankedUsers) {
				res.status(200).send(topRankedUsers);
			} else {
				res.status(400).send();
			}
		} else if(req.body.group === "friends") {
			if(!req.user) {
				res.status(401).send();
			} else {
				dbPool.getConnection(function(err, tempCont){
					if(err) {
						res.status(400).send();
					} else {
						tempCont.query("SELECT login, total_points FROM User INNER JOIN Friendship ON (User.user_id = Friendship.user_one_id AND Friendship.user_two_id = ?) OR (Friendship.user_one_id = ? AND User.user_id = Friendship.user_two_id) ORDER BY total_points LIMIT 100;", [req.user.user_id, req.user.user_id], function(err, result) {
							if(err || !result) {
								res.status(400).send();
							} else {
								res.status(200).send(result);
							}
						});
					}
					tempCont.release();
				});
			}
		} else {
			res.status(400).send();
		}
	}
});

// Add a friend
app.post('/addfriend', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		if(req.body.username) {
			dbPool.getConnection(function(err, tempCont) {
				if(err) {
					res.status(400).send();
				} else {
					tempCont.query("SELECT * FROM Friendship WHERE (user_one_id = ? AND user_two_id = (SELECT user_id FROM User WHERE login = ?)) OR (user_one_id = (SELECT user_id FROM User WHERE login = ?) AND user_two_id = ?);", [req.user.user_id, req.body.username, req.body.username, req.user.user_id], function(err, result) {
						if(err) {
							res.status(400).send();
						} else if(result[0]) {
							res.status(200).send();
						} else {
							tempCont.query("INSERT INTO Friendship (user_one_id, user_two_id) VALUES (?, (SELECT user_id FROM User WHERE login = ?));", [req.user.user_id, req.body.username], function(err, result1) {
								if(err || !result) {
									res.status(400).send();
								} else {
									res.status(200).send();
								}
							});
						}
					});
				}
				tempCont.release();
			});
		} else {
			res.status(400).send();
		}
	}
});

// Remove a friend
app.post('/removefriend', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		if(req.body.username) {
			dbPool.getConnection(function(err, tempCont) {
				if(err) {
					res.status(400).send();
				} else {
					tempCont.query("DELETE FROM Friendship WHERE (user_one_id = ? AND user_two_id = (SELECT user_id FROM User WHERE login = ?)) OR (user_one_id = (SELECT user_id FROM User WHERE login = ?) AND user_two_id = ?);", [req.user.user_id, req.body.username, req.body.username, req.user.user_id], function(err, result) {
						if(err) {
							res.status(400).send();
						} else {
							res.status(200).send();
						}
					});
				}
				tempCont.release();
			});
		} else {
			res.status(400).send();
		}
	}
});

// Search for user info
app.post('/searchuserinfo', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		if(req.body.username) {
			dbPool.getConnection(function(err, tempCont) {
				if(err) {
					console.log(err);
					res.status(400).send();
				} else {
					tempCont.query("SELECT DISTINCT login, total_points, CASE WHEN EXISTS (SELECT user_id FROM Friendship WHERE (user_one_id = ? AND user_two_id = User.user_id) OR (user_one_id = User.user_id AND user_two_id = ?)) THEN 'TRUE' ELSE 'FALSE' END AS isFriend FROM User INNER JOIN Friendship ON (User.user_id = Friendship.user_one_id AND Friendship.user_two_id = ?) OR (Friendship.user_one_id = ? AND User.user_id = Friendship.user_two_id) OR (User.isPrivate = false) WHERE login LIKE '%" + req.body.username + "%' AND user_id != ?;", [req.user.user_id, req.user.user_id, req.user.user_id, req.user.user_id, req.user.user_id], function(err, result) {
						if(err) {
							console.log(err);
							res.status(400).send();
						} else if(!result) {
							res.status(200).send(null);
						} else {
							res.status(200).send(result);
						}
					});
				}
				tempCont.release();
			});
		} else {
			res.status(400).send();
		}
	}
});

//User joins a nearby session of a friend, otherwise creates a new session
app.post('/joinsession', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		dbPool.getConnection(function(err, tempCont) {
			if(err) {
				console.log(err);
				res.status(400).send();
			} else {
				tempCont.query("SELECT latitude, longitude, session_id FROM User INNER JOIN Friendship ON (User.user_id = Friendship.user_one_id AND Friendship.user_two_id = ?) OR (Friendship.user_one_id = ? AND User.user_id = Friendship.user_two_id);", [req.user.user_id, req.user.user_id], function(err, result) {
					if(err) {
						console.log(err);
						res.status(400).send();
					} else if(!result) {
						// Create new session
						tempCont.query("UPDATE User SET session_id = ? WHERE user_id = ?;", [sessionID, req.user.user_id], function(err, result1) {
							if(err) {
								console.log(err);
								res.status(400).send();
							} else {
								res.status(200).send(sessionID);
								sessionID++;
							}
						});
					} else {
						// Join session
						for(var i = 0; i < result.length; i++) {
							if(withinRange(result[i].latitude, result[i].longitude, req.user.latitude, req.user.longitude)) {
								tempCont.query("UPDATE User SET session_id = ? WHERE user_id = ?;", [result[i].session_id], function(err, result1) {
									if(err) {
										console.log(err);
										res.status(400).send();
										i = result.length;
									} else {
										res.status(200).send(result[i].session_id);
										i = result.length;
									}
								});
							}
						}
						if(!res.headerSent) {
							// Create new session if no friends in range
							tempCont.query("UPDATE User SET session_id = ? WHERE user_id = ?;", [sessionID, req.user.user_id], function(err, result1) {
								if(err) {
									console.log(err);
									res.status(400).send();
								} else {
									//res.status(200).send(sessionID);
									res.sendStatus(200);
									sessionID++;
								}
							});
						}
					}
				});
			}
			tempCont.release();
		});
	}
});

app.post('/leavesession', function(req, res) {
	if(!req.user) {
		res.status(401).send();
	} else {
		dbPool.getConnection(function(err, tempCont) {
			if(err) {
				console.log(err);
				res.status(400).send();
			} else {
				tempCont.query("UPDATE User SET session_id = null WHERE user_id = ?;", [req.user.user_id], function(err, result) {
					if(err) {
						console.log(err);
						res.status(400).send();
					} else {
						res.status(200).send();
					}
				});
			}
			tempCont.release();
		});
	}
});

app.post('/getsession', function(req, res) {
	if(!req.user) {
		res.status(400).send();
	} else {
		if(req.user.session_id === null) {
			res.status(400).send();
		} else {
			dbPool.getConnection(function(err, tempCont) {
				if(err) {
					console.log(err);
					res.status(400).send();
				} else {
					tempCont.query("SELECT COUNT(*) AS value FROM User WHERE session_id = ?;", [req.user.session_id], function(err, result) {
						if(err) {
							console.log(err);
							res.status(400).send();
						} else {
							//res.status(200).send(result[0].value);
							res.status(200).send(result[0].value.toString());
						}
					});
				}
				tempCont.release();
			});
		}
	}
});

// Get URL for a profile pic
// app.post('/getUserPicURL', function(req, res) {
	// if(!req.user) {
		// res.status(401).send();
	// } else {
		// if(req.body.username) {
			// dbPool.getConnection(function(err, tempCont) {
				// if(err) {
					// console.log(err);
					// res.status(400).send();
				// } else {
					// tempCont.query("SELECT user_id, photo_type FROM User WHERE login = ?;", [req.body.username], function(err, result) {
						// if(err) {
							// console.log(err);
							// res.status(400).send();
						// } else if(!result[0]) {
							// res.status(400).send();
						// } else {
							// let url = cdnurl + result[0].user_id + "." + result[0].photo_type;
							// res.status(200).send(url);
						// }
					// });
				// }
				// tempCont.release();
			// });
		// } else {
			// res.status(400).send();
		// }
	// }
// });

// // Upload a profile pic
// app.post('/addUserPic', function(req, res) {
	// if(!req.user) {
		// res.status(401).send();
	// } else if(!req.files.photo) {
		// res.status(400).send();
	// } else {
		// let photo = req.files.photo;
		// if(photo.mimetype.localeCompare('image/png') === 0 || photo.mimetype.localeCompare('image/jpeg') === 0) {
			
			// //Do something
			
		// } else {
			// res.status(400).send();
		// }
	// }
// });

// Display 404 for 
// MUST BE AT BOTTOM OF THIS SECTION!
app.use(function(req, res, next) {
	res.status(404).redirect('/404');
});



////////////////////////////////////
// Helper functions
////////////////////////////////////

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
			
		case "boolean":
			returnVal = input == "true" || input == "false";
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

// Updates the top users variable
var getNewTopUsers = function() {
	dbPool.getConnection(function(err, tempCont){
		if(err) {
			topRankedUsers = false;
			console.log(err);
		} else {
			tempCont.query("SELECT login, total_points FROM User ORDER BY total_points DESC LIMIT 100;", function(err, result) {
				if(err || !result) {
					topRankedUsers = false;
					console.log(err);
				} else {
					topRankedUsers = result;
				}
			});
			tempCont.release();
		}
	});
}

// Scheduled job to update top users
var updateTopUsers = schedule.scheduleJob('*/5 * * * *', function() {
	getNewTopUsers();
});

var getUserPageData = function(usernameToSearch, user) {
	dbPool.getConnection(function(err, tempCont) {
		if(err) {
			console.log(err);
			return null;
		} else {
			if(!user) {
				tempCont.query("SELECT * FROM User WHERE login = ? AND isPrivate = false;", [usernameToSearch], function(err, result) {
					if(err) {
						console.log(err);
						return null;
					} else {
						if(result[0]) {
							return result[0];
						} else {
							return null;
						}
					}
				});
			} else {
				tempCont.query("SELECT DISTINCT * FROM User INNER JOIN Friendship ON (Friendship.user_one_id = (SELECT user_id FROM User WHERE login = ?) AND Friendship.user_two_id = ?) OR (Friendship.user_one_id = ? AND Friendship.user_two_id = (SELECT user_id FROM User WHERE login = ?)) OR (User.isPrivate = false) WHERE login = ?;", [usernameToSearch, user.user_id, user.user_id, usernameToSearch, usernameToSearch], function(err, result) {
					if(err) {
						console.log(err);
						return null;
					} else {
						if(result[0]) {
							return result[0];
						} else {
							return null;
						}
					}
				});
			}
			
			
		}
		tempCont.release();
	});
}

var search = function(searchString) {
	return "123";
}

var getUserIndex = function(username) {
	dbPool.getConnection(function(err, tempCont) {
		if(err) {
			console.log(err);
			return null;
		} else {
			tempCont.query("SELECT COUNT(*) + 1 AS value FROM User WHERE total_points > (SELECT total_points FROM User WHERE login = ?);", [username], function(err, result) {
				if(err) {
					console.log(err);
					return null;
				} else {
					if(result[0]) {
						return result[0].value;
					} else {
						return null;
					}
				}
			});
		}
		tempCont.release();
	});
}

var withinRange = function(latA, longA, latB, longB) {
	if(latA === null || longA === null || latB === null || longB === null) return false;
	var radius = Math.sqrt(Math.Pow(latA-latB,2) + Math.Pow(longA-longB,2));

	// Wiki:
	// one latitudinal degree is 110.6 kilometres
	// one longitudinal degree is 96.5 km

	// 0.5 km radius ~= sqrt (2 x (0.05 degrees)^2) ~= 0.0707
	return (radius < 0.0707);
}

var getSessionID = function() {
	dbPool.getConnection(function(err, tempCont) {
		if(err) {
			console.log(err);
			sessionID = 10000;
			console.log("SessionID index is " + sessionID);
		} else {
			tempCont.query("SELECT session_id FROM User ORDER BY session_id DESC LIMIT 1;", function(err, result) {
				if(err) {
					console.log(err);
					sessionID = 10000;
				} else {
					if(result[0].session_id === null) {
						sessionID = 0;
					} else {
						sessionID = result[0].session_id + 1;
					}
				}
				console.log("SessionID index is " + sessionID);
			});
		}
		tempCont.release();
	});
}