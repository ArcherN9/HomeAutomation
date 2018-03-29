//File used to configure mongo DB configuration

var configuration = {
	firebase 	: "https://daksh-home-automation.firebaseio.com",
	mongoDB  	: {
		"databaseUrl"	: "ds251985.mlab.com",
		"port"			: 51985,
		"name"			: "homeautomation",
		"username"		: "travisCI",
		"password"		: "homeautomation"
	}
}

//Export to module to make it available globally
module.exports = configuration;