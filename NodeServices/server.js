// server.js

// BASE SETUP
// =============================================================================

// call the packages we need
var express    				= require('express');        // call express
var app        				= express();                 // define our app using express
var bodyParser 				= require('body-parser');
var nodeSchedule			= require('node-schedule');
var momentTimezone 			= require('moment-timezone');
var moment 					= require('moment');
var firebaseAdmin 			= require("firebase-admin");
var request 				= require('request');
const DialogflowApp 		= require('actions-on-google').DialogflowApp;
var mongoClient 			= require('mongodb').MongoClient;
var ObjectID	 			= require('mongodb').ObjectID;

// Include other JS files to implement abstraction
var databaseConfigration 	= require('./databaseConf');
var miscConfiguration		= require('./misc-conf');
var serviceAccount 			= require('./serviceAccountKey.json');

//Declare variables that will be used throughout the application
var mongoDB;		//define variable DB that will be used throughout the application to communicate to the Database
const TURNOFF_INTENT		= 'light.Off';
const TURNON_INTENT			= 'light.On';
const LIST_ALL				= 'light.list.all';
const LIGHT_NAME			= 'light.name';
const USER_HELP			 	= 'user.help';

//Declare the Contexts that are passed between services
var TURNOFF_CONTEXT			= 'TurnOff';
var TURNON_CONTEXT			= 'TurnOn';

// Start up configurations
// configure app to use bodyParser()
// this will let us get the data from a POST
var urlencodedParser = bodyParser.urlencoded({ extended: true });
app.use(urlencodedParser);
app.use(bodyParser.json());
firebaseAdmin.initializeApp({
  credential: firebaseAdmin.credential.cert(serviceAccount),
  databaseURL: databaseConfigration.firebase
});

var port = process.env.PORT || 8080;        // set our port

// Make connection to the Database
// =============================================================================
// Connect to the db
mongoClient.connect("mongodb://" + databaseConfigration.mongoDB.username + ":" + databaseConfigration.mongoDB.password 
	+ "@" + databaseConfigration.mongoDB.databaseUrl + ":" + databaseConfigration.mongoDB.port + "/" + databaseConfigration.mongoDB.name, function (err, db) {
   	//Catch exceptions
   	if(err) {
   		console.log(databaseConfigration.mongoDB.username + ":" + databaseConfigration.mongoDB.password);
   		throw err;
   	} else
   	console.log(new Date() + ":" + "Connection to mLab successful.");

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
	console.log(new Date() + ":" + "All nodes requested, Querying DB for all nodes");

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
// 
// Getting the time will return in local time (Depending on where the server resides, the time will be different - Most probably, UTC)
// To fix that issue and schedule the sunSchedule according to IST, we need to convert the time to IST - To do that, we use Moment.js

//Create a new date object with the time set to the desired API execution time
var IST = moment(new Date()).tz("Asia/Kolkata");
IST.set('hour', 4);
IST.set('minute', 0);
IST.set('second', 0);
IST.set('millisecond', 0);
console.log(new Date() + ":" + "Sunrise and sunset timings will be requested by the system everyday at " + moment(IST).format('hh:mm A Z'));

//Convert said IST time to UTC
var UTCTime = IST.tz("Europe/London");
console.log(new Date() + ":" + "Relative time in UTC : " + moment(UTCTime).format('hh:mm A Z'));

// Setup a recurring rule to execute the following method everyday at 04:00 AM IST
var recurrenceRule = new nodeSchedule.RecurrenceRule();
recurrenceRule.dayOfWeek = [new nodeSchedule.Range(0, 6)];
recurrenceRule.hour = moment(UTCTime).format('k');
recurrenceRule.minute = moment(UTCTime).format('m');

//Schedule the job and define the function to be executed every morning at 04:00 AM
var sunSchedule = nodeSchedule.scheduleJob(recurrenceRule, function(){

	//Create a new object of UTC time and convert to IST to find for which date is the data being requested
	var todayUTC = moment(new Date()).tz("Europe/London");
	//convert
	var todayIST = todayUTC.tz("Asia/Kolkata");
	
	//Execute the GET API for sunset and sunrise timings for defined date
	//lat (float): Latitude in decimal degrees. Required.
	//lng (float): Longitude in decimal degrees. Required.
	//date (string): Date in YYYY-MM-DD format.
	request(miscConfiguration.host + miscConfiguration.param + "?"
		+ "lat=" + miscConfiguration.latitude + "&"
		+ "lng=" + miscConfiguration.longitude + "&"
		+ "date=" + moment(todayIST).format('YYYY-MM-DD'), function(error, response, body){
			//convert to JSON
			var jsonResponse = JSON.parse(body);
			//parse sunrise and sunset time and save to GMT
			var todaySunrise = moment(moment.tz(jsonResponse.results.sunrise, "hh:mm:ss A", "Europe/London").tz("Asia/Kolkata")).format('hh:mm A');
			var todaySunset = moment(moment.tz(jsonResponse.results.sunset, "hh:mm:ss A", "Europe/London").tz("Asia/Kolkata")).format('hh:mm A');

			console.log(new Date() + ":" + miscConfiguration.host + " responded with sunrise and sunset values - sunrise : " + todaySunrise + " | sunset : " + todaySunset);

			var daylight = {
				Sunrise 	: todaySunrise,
				sunset 		: todaySunset,
				date 		: moment(todayIST).format('YYYY-MM-DD')
			}

			//Store value in DB
			mongoDB.collection("daylight").insertOne(daylight, function(err, res){
				
				//Throw error if found
				if(err) throw err;

				//Log output to console
				console.log(new Date() + ":" + "Data stored to DB : " + JSON.stringify(res.ops));

				//Log next invocation
				console.log(new Date() + ":" + "Service will retrieve sunset and sunrise timings next on " + sunSchedule.nextInvocation());
			});
		});
});

//Log next invocation
console.log(new Date() + ":" + "Service will retrieve sunset and sunrise timings next on " + sunSchedule.nextInvocation());

// ============================================================================= //

//An API to register the FCM token with the server. Whenever a fresh token is retrieved from the library, 
//This api will be called to reigster and transfer the token to the MongoDB. This will path way for authentication
//in the future
//Params : 
//fcmid : String | The ID against which push notifications will be sent to the user
//name 	: String | The name of the device
//uid 	: String | The Secure ID of the device. This will help in the future to store refreshed tokens
router.post('/registerDevice', urlencodedParser, function(req, response) {

	//Extract the body params passed and conver it into a JSON
	var jsonResponse = req.body;

	var user = {
		//Extract the FCM ID passed by the user
		fcmid 			: jsonResponse.fcmid,

		//Extract the Device Name
		deviceName		: jsonResponse.name,

		//Extract the device unique ID
		uid 			: jsonResponse.uid,

		//Date of storage
		date 			: new Date()
	};

	//Open DB and store the FCM ID
	mongoDB.collection("users").insertOne(user, function(err, res){
				//Throw error if found
				if(err) throw err;

				//Log output to console
				console.log(new Date() + ":" + "Data stored to DB : " + JSON.stringify(res.ops));

				response.json({
					message : 'Device registered Successfully',
					success : true
				});
			});
});

// ============================================================================= //
// Setup a testing POST service to send testing push notification messages
// Params :
// fcmregistrationtoken : The FCM ID where the push notification is to be sent
// payload : The message to send
router.post('/sendNotification', urlencodedParser, function(req, res){

	//Extract the body params passed and conver it into a JSON
	var jsonResponse = req.body;
	console.log(new Date() + ":" + "Sending a dummy notification with payload : " + JSON.stringify(jsonResponse));

	// Send a message to the device corresponding to the provided registration token.
	firebaseAdmin.messaging().sendToDevice(jsonResponse.fcmregistrationtoken, jsonResponse.payload).then(function(response) {
	    // See the MessagingDevicesResponse reference documentation for
	    // the contents of response.
	    res.json({
	    	message 	: "Successfully sent message",
	    	receiver 	: jsonResponse.fcmregistrationtoken, 
	    	payload 	: jsonResponse.payload
	    });
	})
	.catch(function(error) {
		res.json({
	    	message 	: "Message failed",
	    	error 		: error
	    });
	});
});


// ============================================================================= //
//
// Setup a service to accept voice commands from the user | This API is never called directly
// Is always called from DialogFlow as a webhook to a user query posted and identified to be from
// Elsa application.
// 
// Sample body of data that will always be pushed to the service is available at : https://dialogflow.com/docs/fulfillment#webhook-example
// 
// Service response : In order to process the user command, dialogFlow needs certain response from the API to be able to serve the user
// properly. For that, the response needs to be of the following format :
// 1. speech			- 	String 	-	Response to the request.
// 2. displayText		-	string 	- 	Text displayed on the user device screen.
// 3. data 				-  	Object 	- 	Additional data required for performing the action on the client side. The data is sent to the client in the original form and is not processed by Dialogflow.
// 4. contextOut 		-	Array of context objects	- Array of context objects set after intent completion. Example: "contextOut": [{"name":"weather", "lifespan":2, "parameters":{"city":"Rome"}}]
// 5. source			-  	string 	-  	Data source.
// 6. followupEvent		- 	Object 	- 	Event name and optional parameters sent from the web service to Dialogflow.
router.post('/voiceAction', urlencodedParser, function(req, res){
	//Log the invocation
	console.log(new Date() + ":" + "voiceAction API called with data");

	//Create an instance of DialogFlow that will process the params received
	const dialogFlowApp = new DialogflowApp({request: req, response: res});

	//Create a new actions map to map intents to functions
	var actionMap = new Map();
	actionMap.set(TURNOFF_INTENT, lightsOff);
	actionMap.set(TURNON_INTENT, lightsOn);
	actionMap.set(LIST_ALL, listAll);
	actionMap.set(LIGHT_NAME, lightName);
	actionMap.set(USER_HELP, userHelp);

	//tell which function to trigger based on which intent
	dialogFlowApp.handleRequest(actionMap);
});

/**
 * Lights off method is executed when the Action is Light.Off. It is used to switch off a controllable node
 * and set the status on the DB to false.
 * @param  {[type]} assistant [description]
 * @return {[type]}           [description]
 *
 * Examples of JSON that will be received : 
 * 1. When turn off command is provided, but not the ID/Name : https://api.myjson.com/bins/o85yz
 */
 function lightsOff(assistant) {
	// Connect to the DB and the Nodes Collection
	mongoDB.collection("nodes", function(err, collection){
		collection.find({}).toArray(function(err, result){

				//throw the error if found
				if (err) throw err;

				//based on whether we have more than a single item on the list or not, we can either control the item directly,
				//or prompt the user for more information
				if(result.length == 1) {

					//Query DB and get values for lamp switched on and whether arduino is updating or not
					mongoDB.collection("status", function(err, collection) {
						//create a query for DB
						var dbQuery = {
							nodeId: ObjectID(result[result.length - 1]._id)
						};

						//Values to be updated
						var dbNewValue = {$set:{isNodeTurnedOn: false}};

						collection.updateOne(dbQuery, dbNewValue, function(err, items) {

							//Throw error if update failed
							if(err)
								throw err;

							//Console output
							console.log(new Date() + " : " + "Switch recorded in DB." + " New node status : " + false);
						});

					//There is nothing to prompt the user. There's just one item that can be controlled. Directly control it
					//and leave
					var speechText = "Okay!";
					//return response from DB 
					assistant.tell(speechText);
				}); 
				// else {
				// 	//Only one item may be controlled, repond appropriately
				// 	var speechText = "You may control the " + result[0].Description + " only.";

				// 	//ask the user
				// 	assistant.ask(speechText);
				// }
			} else {
				var parameters = {
					data: result
				};
				//There are more nodes in the list that can be controlled. Ask the user which one
				assistant.setContext(TURNOFF_CONTEXT, 5, parameters);
				assistant.ask("Which one?", ["Well?", "I think you just got occupied with something else", "We'll do this later!"]);
			}
		});
	});
}

/**
 * [lightsOn description]
 * @param  {[type]} assistant [description]
 * @return {[type]}           [description]
 */
function lightsOn(assistant) {
	// Connect to the DB and the Nodes Collection
	mongoDB.collection("nodes", function(err, collection){
		collection.find({}).toArray(function(err, result){

				//throw the error if found
				if (err) throw err;

				//based on whether we have more than a single item on the list or not, we can either control the item directly,
				//or prompt the user for more information
				if(result.length == 1) {

					//Query DB and get values for lamp switched on and whether arduino is updating or not
					mongoDB.collection("status", function(err, collection) {
						//create a query for DB
						var dbQuery = {
							nodeId: ObjectID(result[result.length - 1]._id)
						};

						//Values to be updated
						var dbNewValue = {$set:{isNodeTurnedOn: true}};

						collection.updateOne(dbQuery, dbNewValue, function(err, items) {

							//Throw error if update failed
							if(err)
								throw err;

							//Console output
							console.log(new Date() + " : " + "Switch recorded in DB." + " New node status : " + true);
						});

					//There is nothing to prompt the user. There's just one item that can be controlled. Directly control it
					//and leave
					var speechText = "Okay!";
					//return response from DB 
					assistant.tell(speechText);
				}); 
				// else {
				// 	//Only one item may be controlled, repond appropriately
				// 	var speechText = "You may control the " + result[0].Description + " only.";

				// 	//ask the user
				// 	assistant.ask(speechText);
				// }
			} else {
				var parameters = {
					data: result
				};
				//There are more nodes in the list that can be controlled. Ask the user which one
				assistant.setContext(TURNON_CONTEXT, 5, parameters);
				assistant.ask("Which one?", ["Well?", "I think you just got occupied with something else", "We'll do this later!"]);
			}
		});
	});
};

/**
 * [lightName description]
 * @param  {[type]} assistant [description]
 * @return {[type]}           [description]
 * Example of JSON that will be received by the service :
 * 1. If invoked contextually (Choose a node name on which to perform an action ): https://api.myjson.com/bins/t6ihv
 */
function lightName(assistant) {
	//Context will be received if the name was received after some commands
	const idClicked = assistant.getContextArgument('actions_intent_option', 'OPTION').value;
	var incomingContext = (assistant.getContext(TURNOFF_CONTEXT.toLowerCase()) !== undefined 
			? assistant.getContext(TURNOFF_CONTEXT.toLowerCase())
			: assistant.getContext(TURNON_CONTEXT.toLowerCase()));
	if(idClicked != undefined && incomingContext != undefined) {

		//Query DB and get values for lamp switched on and whether arduino is updating or not
		mongoDB.collection("status", function(err, collection) {
		//create a query for DB
			var dbQuery = {
				nodeId: ObjectID(idClicked)
			};

			//Values to be updated
			var dbNewValue = {$set:{isNodeTurnedOn: (incomingContext.name != TURNOFF_CONTEXT.toLowerCase())}};

			collection.updateOne(dbQuery, dbNewValue, function(err, items) {

				//Throw error if update failed
				if(err)
					throw err;

				//Console output
				console.log(new Date() + " : " + "Switch recorded in DB." + " New node status : " + incomingContext.name != TURNOFF_CONTEXT.toLowerCase());
			});
		});
		//There is nothing to prompt the user. The user has told the node that is to be controlled. Directly control it
		//and leave
		var speechText = "Okay!";
		//return response from DB 
		assistant.tell(speechText);
	}
}

/**
 * [userHelp description]
 * @param  {[type]} assistant [description]
 * @return {[type]}           [description]
 */
function userHelp(assistant) {
	assistant.tell("I'm not ready to answer your questions yet. Later, perhaps!");
}

/**
 * The method will be exeucted when the user wants to retrieve a list of all nodes that s/he can control
 * @param  {[type]} assistant [description]
 * @return {[type]}           [description]
 * Example of JSON that will be received by the service : 
 * 1. When invoked contextually (What are my options after another command) :  https://api.myjson.com/bins/d5qlv
 */
 function listAll(assistant) {

 	//Context will be received only on this case | Because if the user asked to turn on or off something, 
 	//and there was only one item, the flow would have ended on the previous command.
	//We check for any incoming contexts now
	var incomingContext = (assistant.getContext(TURNOFF_CONTEXT.toLowerCase()) !== null 
			? assistant.getContext(TURNOFF_CONTEXT.toLowerCase())
			: assistant.getContext(TURNON_CONTEXT.toLowerCase()));
	if(incomingContext !== null && incomingContext !== undefined && incomingContext.name === TURNOFF_CONTEXT.toLowerCase()) {
		//Build a bulleted list of items that can be controlled
		var speechText = "These are currently turned on";
		var optionItems = [];
		//Build the option items for the number of items on the list
		for(var index in incomingContext.parameters.data)
			optionItems.push(assistant.buildOptionItem(incomingContext.parameters.data[index]._id.toString()).setTitle(incomingContext.parameters.data[index].Description));

		assistant.setContext(incomingContext.name, 5, incomingContext.parameters);
		assistant.askWithList(speechText, assistant.buildList('Tap on an item to switch it off : ').addItems(optionItems));
		return;
	} else if(incomingContext !== null && incomingContext !== undefined && incomingContext.name === TURNON_CONTEXT.toLowerCase()) {
		//Build a bulleted list of items that can be controlled
		var speechText = "These are currently turned off";
		var optionItems = [];
		//Build the option items for the number of items on the list
		for(var index in incomingContext.parameters.data)
			optionItems.push(assistant.buildOptionItem(incomingContext.parameters.data[index]._id.toString()).setTitle(incomingContext.parameters.data[index].Description));

		assistant.setContext(incomingContext.name, 5, incomingContext.parameters);
		assistant.askWithList(speechText, assistant.buildList('Tap on an item to switch it on : ').addItems(optionItems));
		return;
	} else {

		// Connect to the DB and the Nodes Collection
		mongoDB.collection("nodes", function(err, collection){
			collection.find({}).toArray(function(err, result){

					//throw the error if found
					if (err) throw err;

					//based on whether we have more than a single item on the list or not, we can 
					//either present the user with a list or just a single item
					if(result.length > 1) {
						//Build a bulleted list of items that can be controlled
						var speechText = "You may control the following";
						var optionItems = [];
						//Build the option items for the number of items on the list
						for(var index in result)
							optionItems.push(assistant.buildOptionItem(result[index]._id.toString()).setTitle(result[index].Description));

						//return response from DB 
						// assistant.setContext(incomingContext., 1, incomingContext.parameters);
						assistant.askWithList(speechText, assistant.buildList('Tap on an item to control : ').addItems(optionItems));
					} else {
						//Only one item may be controlled, repond appropriately
						var speechText = "You may control the " + result[0].Description + " only.";

						//ask the user
						assistant.ask(speechText);
					}

					//Start creating the object to return to DialogFlow
					var dialogFlowResponse = {
						speech : speechText,
						displayText: 'You may control the following'
					};
				});
		});
	}
};

// ============================================================================= //

// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port, '0.0.0.0', function() {
    // Show confirmation message on terminal that the API has been started
    console.log(new Date() + ":" + 'Home Automation project API is running on port : ' + port);
});