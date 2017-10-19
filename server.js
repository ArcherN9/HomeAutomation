// server.js

// BASE SETUP
// =============================================================================

// call the packages we need
var express    = require('express');        // call express
var app        = express();                 // define our app using express
var bodyParser = require('body-parser');

//Declare variables to be used here
//A boolean value that depicts the current state of the lamp
var isLampSwitchedOn = false;
//A boolean value that governs if Arduino is to continue it's operation or not
var isArduinoUpdating = true;

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 8080;        // set our port

// ROUTES FOR OUR API
// =============================================================================
var router = express.Router();              // get an instance of the express Router

// test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
	res.json({ message: 'Home Automation API is working normally' });   
});

// ============================================================================= //

//Toggle switch route | This is the API end point which will be accessed by the client
router.get('/toggleSwitch', function(req, res) {
	//Toggle the switch and change the boolean value
	isLampSwitchedOn = !isLampSwitchedOn;
	//Log the output back to the user
	res.json(
	{ 
		message				: 'The switch has been toggled',
		isLampSwitchedOn	: isLampSwitchedOn,
		isArduinoUpdating	: isArduinoUpdating,
		success				: true
	}
	);
});

// ============================================================================= //

//Get updated status of the lamp | This is the API end point which will be accessed by either the Arduino or
//a client that will pass on the state to the Arduino
router.get('/getStatus', function(req, res) {
	//A message to be displayed back to the user
	var strMessage = '';

	if(isLampSwitchedOn)
		strMessage = "The lamp is currently switched on.";
	else
		strMessage = "The lamp is currently switched off."


	//Log the output back to the user
	res.json({ 
		message 				: strMessage,
		isLampSwitchedOn		: isLampSwitchedOn,
		isArduinoUpdating 		: isArduinoUpdating,
		success					: true
	});
});

// ============================================================================= //

//Turn off Arduino from requesting lamp status on Google Cloud
//Query Params : 
//arduinoStatus : 1 or 0 | 0 = off | 1 = on
router.get('/arduino', function(req, res) {
	//Extract the query params
	var queryParams = req.query;
	var strMessage = '';

	//Update the continue Service variable with the one passed by user
	isArduinoUpdating = queryParams.isArduinoUpdating === 'true';

	if(!isArduinoUpdating)
		strMessage = "Service requests to Google Console have been turned off for Arduino."; 
	else
		strMessage = "Service requests to Google Console have been turned on for Arduino."; 

	//Send response
	res.json({
			message				: strMessage,
			isLampSwitchedOn	: isLampSwitchedOn,
			isArduinoUpdating 	: isArduinoUpdating,
			success				: true
		});
});

// ============================================================================= //

// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port);
//Show confirmation message on terminal that the API has been started
console.log('Home Automation project API is running on port : ' + port);