//package com.hcl.daksh.android_poc_camp.Login.Models
//
//import com.google.gson.annotations.Expose
//import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
//
//class ModelLogin {
//
//    @Expose
//
//    var success: Boolean?   = null
//
//    @Expose
//    var token: String?      = null
//
//    @Expose
//    var users: ModelUsers?  = null
//
//    class ModelUsers {
//
//        @Expose
//        var _id: String?    = null
//
//        @Expose
//        var mailid: String? = null
//
//        @Expose
//        var role: String?   = null
//
//        @Expose
//        var password:String?= null
//
//        @Expose
//        var username:String?= null
//    }
//
//    /**
//     * Creates an entity out of received values
//     */
//    fun createEntity():EntityDevices = EntityDevices().apply {
//        _id        = this@ModelLogin.users?._id
//        token      = this@ModelLogin.token
//        mailid     = this@ModelLogin.users?.mailid
//        username   = this@ModelLogin.users?.username
//    }
//}