package com.daksh.homeautomation.MainActivity.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Model {

    @Expose
    var nodeId: String? = null

    @Expose
    var message: String? = null

    @Expose
    var isLampSwitchedOn : Boolean? = null

    @Expose
    var isArduinoUpdating: Boolean? = null

    @Expose
    var success: Boolean? = null
}