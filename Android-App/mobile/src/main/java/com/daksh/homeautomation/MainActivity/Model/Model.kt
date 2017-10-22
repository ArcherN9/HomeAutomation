package com.daksh.homeautomation.MainActivity.Model

import com.google.gson.annotations.Expose

class Model {

    @Expose
    var message: String? = null

    @Expose
    var isLampSwitchedOn : Boolean? = null

    @Expose
    var isArduinoUpdating: Boolean? = null

    @Expose
    var success: Boolean? = null
}