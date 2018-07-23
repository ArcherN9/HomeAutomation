package com.hcl.daksh.android_poc_rooms

import android.os.Handler
import android.os.HandlerThread

interface BasePresenter {

    //A handler every presenter needs to implement
    var mHandler: Handler
    //A handlerThread every presenter needs to implement
    var mHandlerThread: HandlerThread

    //The first method to be executed on the presenter | used this for all inits
    fun start()
}