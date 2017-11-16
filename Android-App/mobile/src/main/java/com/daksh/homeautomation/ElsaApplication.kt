package com.daksh.homeautomation

import android.app.Application
import com.daksh.homeautomation.MainActivity.Model.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.DebugFlags

class ElsaApplication: Application() {

    //The box store. This is used application wide to read / write from the DB
    internal lateinit var objectBox: BoxStore

    override fun onCreate() {
        super.onCreate()

        //Create the objectBox instance
        objectBox = MyObjectBox
                .builder()
                .androidContext(this@ElsaApplication)
                .debugFlags(DebugFlags.LOG_QUERIES)
                .build()
    }
}