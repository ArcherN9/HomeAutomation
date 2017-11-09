// server.js

// BASE SETUP
// =============================================================================

// call the packages we need
var express    				= require('express');        // call express
var app        				= express();                 // define our app using express
var bodyParser 				= require('body-parser');
var mongoClient 			= require('mongodb').MongoClient;
var ObjectID	 			= require('mongodb').ObjectID;

// Include other JS files to implement abstraction
var databaseConfigration 	= require('./databaseConf');

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 8080;        // set our port

//define variable DB that will be used throughout the application to communicate to the Database
var mongoDB;

// Make connection to the Database
// =============================================================================
// Connect to the db
mongoClient.connect("mongodb://" + databaseConfigration.username + ":" + databaseConfigration.password 
	+ "@" + databaseConfigration.databaseUrl + ":" + databaseConfigration.port + "/" + databaseConfigration.name, function (err, db) {
   	//Catch exceptions
   	if(err) {
   		console.log(credentials.username + ":" + credentials.password);
   		throw err;
   	} else
   	console.log("Connection to mLab successful.");

     //Assign varaible db to mongoDB global variable | This will be used throughout the app to communicate to DB
     mongoDB = db;
 });
// =============================================================================

// ROUTES FOR OUR API
// =============================================================================
var router = express.Router();              // get an instance of the express Router

// test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
	res.json({ message: 'Home Automation API is working normally' });

	//Console output
	console.log(new Date() + " : " + "Home automation API responded connections working normally.");
});

// ============================================================================= //

//Toggle swit
//Query Params : 
//nodeId : xxxxxxx | The ID of the node that is being toggled
//status  : true, fase | The new value of the node
router.get('/toggleSwitch', function(req, res) {
	
	//Extract the query params
	var queryParams = req.query;

	//Console output
	console.log(new Date() + " : " + "Switch toggle requested. Quering DB for updated values");

	//Query DB and get values for lamp switched on and whether arduino is updating or not
	mongoDB.collection("status", function(err, collection) {

		//create a query for DB
		var dbQuery = {
			nodeId: ObjectID(queryParams.nodeId)
		};

		//Values to be updated
		var dbNewValue = {$set:{isNodeTurnedOn: (queryParams.status == 'true')}};

		collection.updateOne(dbQuery, dbNewValue, function(err, items) {

			//Throw error if update failed
			if(err)
				throw err;

			//Console output
     		console.log(new Date() + " : " + "Switch recorded in DB."
     			+ " Old node status : " + !queryParams.status
     			+ " New node status : " + queryParams.status);

     		//Log the output back to the user
     		res.json({ 
     			message				: 'The switch has been toggled',
     			isLampSwitchedOn	: queryParams.status,
     			isArduinoUpdating	: queryParams.status,
     			success				: true
     		});
     	});
	});
});

// ============================================================================= //

//Get updated status of the lamp | This is the API end point which will be accessed by either the Arduino or
//a client that will pass on the state to the Arduino
//Query Params : 
//nodeId : xxxxxxx | The ID of the node that is being toggled
router.get('/getStatus', function(req, res) {

	//Extract the query params
	var queryParams = req.query;

	//Query DB and get values for lamp switched on and whether arduino is updating or not
	mongoDB.collection("status", function(err, collection) {

		//create a query for DB
		var dbQuery = {
			nodeId: ObjectID(queryParams.nodeId)
		};

		collection.findOne(dbQuery, function(err, items) {

			//Throw error if update failed
			if(err)
				throw err;
			else
				console.log(new Date() + " : " + "Lamp status requested. Current Lamp status : " + JSON.stringify(items));

			//A message to be displayed back to the user
			var strMessage = '';

			if(items.isNodeTurnedOn)
				strMessage = "The lamp is currently switched on.";
			else
				strMessage = "The lamp is currently switched off."

     		//Log the output back to the user
     		res.json({
     			message				: 'The switch has been toggled',
     			isLampSwitchedOn	: items.isNodeTurnedOn,
     			isArduinoUpdating	: true,
     			success				: true
     		});
     	});
	});
});

// ============================================================================= //

//Turn off Arduino from requesting lamp status on Google Cloud
//Query Params : 
//arduinoStatus : 1 or 0 | 0 = off | 1 = on
// router.get('/arduino', function(req, res) {

// 	//Console output
// 	console.log(new Date() + " : " + "Arduino Toggle requested."
// 		+ " Old status : " + isArduinoUpdating
// 		+ "| New Status : " + !isArduinoUpdating);

// 	//Extract the query params
// 	var queryParams = req.query;
// 	var strMessage = '';

// 	//Update the continue Service variable with the one passed by user
// 	isArduinoUpdating = queryParams.isArduinoUpdating === 'true';

// 	if(!isArduinoUpdating)
// 		strMessage = "Service requests to Google Console have been turned off for Arduino."; 
// 	else
// 		strMessage = "Service requests to Google Console have been turned on for Arduino."; 

// 	//Send response
// 	res.json({
// 		message				: strMessage,
// 		isLampSwitchedOn	: isLampSwitchedOn,
// 		isArduinoUpdating 	: isArduinoUpdating,
// 		success				: true
// 	});
// });

// ============================================================================= //

// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
//app.listen(port);
app.listen(8080, '0.0.0.0', function() {
    //Show confirmation message on terminal that the API has been started
    console.log('Home Automation project API is running on port : ' + port);
});