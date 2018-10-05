const mysql = require('mysql');

// Database connection info
var db = mysql.createPool({
	connectionLimit: 10,
	host     : 'us-cdbr-iron-east-01.cleardb.net',
  	user     : 'bbfac4dc0a8c9d',
  	password : 'dd4a3600',
  	database : 'heroku_52d2990a9088f84'
});