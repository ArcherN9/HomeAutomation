// server.js

// BASE SETUP
// =============================================================================

// call the packages we need
var express    = require('express');        // call express
var app        = express();                 // define our app using express
var bodyParser = require('body-parser');

//Declare variables to be used here
var isLampSwitchedOn = false;

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

//Toggle switch route | This is the API end point which will be accessed by the client
router.get('/toggleSwitch', function(req, res) {
	//Toggle the switch and change the boolean value
	isLampSwitchedOn = !isLampSwitchedOn;
	//Log the output back to the user
	res.json({ message: 'Is lamp turned on now? : ' + isLampSwitchedOn });
});

//Get updated status of the lamp | This is the API end point which will be accessed by either the Arduino or
//a client that will pass on the state to the Arduino
router.get('/getStatus', function(req, res) {
	//Log the output back to the user
	res.json({ status: isLampSwitchedOn });
});

// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port);
//Show confirmation message on terminal that the API has been started
console.log('Home Automation project API is running on port : ' + port);