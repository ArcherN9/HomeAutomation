//File used to configure mongo DB configuration

var configuration = {
	firebase 	: "https://daksh-home-automation.firebaseio.com",
	mongoDB  	: {

		// mLab Database | Production
		"databaseUrl"	: "ds251985.mlab.com",
		"port"			: 51985,
		
		//Local DB
		// "databaseUrl"	: "localhost",
		// "port"			: 27017,


		"name"			: "homeautomation",
		"username"		: "travisCI",
		"password"		: "homeautomation"
		
		// "username"		: "dakshsrivastava",
		// "password"		: "$May@2018#"
	}
}

//Export to module to make it available globally
module.exports = configuration;