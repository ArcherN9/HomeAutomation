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
// 
// The get all nodes API. It returns all nodes and their statuses to the caller
router.get('/getAllNodes', function(req, res){
	console.log("All nodes requested, Querying DB for all nodes");

	//Query the DB & get the collection which has all information w.r.t nodes
	mongoDB.collection('nodes', function(err, collection){
		
		collection.aggregate([
			{ $lookup: {
				from: 'status',
				localField: '_id',
				foreignField: 'nodeId',
				as: 'NodeDetails'
			}}	
			]).toArray(function(err, result){

				//Throw error if it doesn't work
				if(err) throw err;

				//Extract isNodeTurnedOnStatus from this response
				var nodeStatus = false;

				// Check if nodeDetails was received
				for(var index in result)
					if(result[index].NodeDetails) {
						for(var nodeIndex in result[index].NodeDetails) {
							nodeStatus = result[index].NodeDetails[nodeIndex].isNodeTurnedOn;
							//Add a new key isNodeTurnedOn to specify the node status
							result[index]['isNodeTurnedOn'] = nodeStatus;
						}

						// Delete the NodeDetails from every result object
						delete result[index].NodeDetails;
					}

				//Return the response to the caller
				return res.json(result);
			});
		});
});

// ============================================================================= //

//Toggle switch
//Query Params : 
//nodeId : xxxxxxx | The ID of the node that is being toggled
//status  : true, fase | The new value of the node
router.get('/toggleSwitch', function(req, res) {
	
	//Extract the query params
	var queryParams = req.query;

	//Do not enter if Query params are missing
	if(queryParams.nodeId !== null && queryParams.nodeId !== undefined
		&& queryParams.status !== null && queryParams.status !== undefined) {
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
					+ " Old node status : " + (queryParams.status == 'true')
					+ " New node status : " + queryParams.status);

	     		//Log the output back to the user
	     		collection.findOne(dbQuery, function(err, items) {
	     			if(err)
	     				throw err;
	     			
	     			//Replace the _id value with value from nodeId and delete that key
	     			items._id = items.nodeId;
	     			//Delete the key now
	     			delete items.nodeId;
	     			//Return Response
	     			res.json(items);
	     		});
	     	});
		});
	} else 
		//Log the output back to the user
		res.json({
			message				: 'Query params missing. Please retry.',
			success				: false
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

	//Do not enter if Query params are missing
	if(queryParams.nodeId !== null && queryParams.nodeId !== undefined) {

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

				if(items !== null && items.isNodeTurnedOn)
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
	} else {
		//Log the output back to the user
		res.json({
			message				: 'Query params missing. Please retry.',
			success				: false
		});
	}
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
// app.listen(8080, '0.0.0.0', function() {
    //Show confirmation message on terminal that the API has been started
    // console.log('Home Automation project API is running on port : ' + port);
// });
app.listen(8080, function() {
    // Show confirmation message on terminal that the API has been started
    console.log('Home Automation project API is running on port : ' + port);
});