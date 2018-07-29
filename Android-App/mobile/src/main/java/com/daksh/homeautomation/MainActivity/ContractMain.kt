package com.hcl.daksh.android_poc_camp.Dashboard

import com.daksh.homeautomation.ElsaApplication
import com.hcl.daksh.android_poc_camp.Login.DB.DAODevices
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import com.hcl.daksh.android_poc_rooms.BasePresenter
import com.hcl.daksh.android_poc_rooms.BaseView

interface ContractMain {

    interface View : BaseView<Presenter> {

        //Returns the Device DAO from the activity
        fun getDeviceDB(): DAODevices

        //Called from the presenter when device list has been loaded. Is used to populate the recycler
        //view
        fun showDeviceList(nodeList: MutableList<EntityDevices>?)

        //Returns the class that extends Application
        fun getElsaApplication(): ElsaApplication

        // Displays a helper on the screen
        fun showEmptyControl(show: Boolean)
    }

    interface Presenter : BasePresenter {

        //Toggle the switch
        fun toggleSwitch(changedEntity: EntityDevices)

        // loads the device list
        fun loadList()
    }
}