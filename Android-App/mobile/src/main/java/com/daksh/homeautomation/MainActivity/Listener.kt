package com.daksh.homeautomation.MainActivity

import com.daksh.homeautomation.MainActivity.Model.Model

interface Listener {

    fun onReceived(body: Model?)
}