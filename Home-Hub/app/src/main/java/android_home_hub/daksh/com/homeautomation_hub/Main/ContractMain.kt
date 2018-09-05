package com.hcl.daksh.android_poc_camp.Dashboard

import android.support.v4.app.FragmentManager
import android_home_hub.daksh.com.homeautomation_hub.Main.DeviceRecyclerViewAdapter
import com.hcl.daksh.android_poc_rooms.BasePresenter
import com.hcl.daksh.android_poc_rooms.BaseView

interface ContractMain {

    interface View : BaseView<Presenter> {

        // Returns the activity's support fragment manager
        fun getActivityFragmentManager(): FragmentManager

        // Shows the device list on the main activity
        fun showList(adapter: DeviceRecyclerViewAdapter?)

        // Displays a helper on the screen
        fun showEmptyControl(show: Boolean)
    }

    interface Presenter : BasePresenter {

        // Open the dialog fragment for the user to add a new device
        fun showAddNewFragment()

        // loads the device list
        fun loadList()

        // Informs the presenter that a switch has been flipped by the user
        fun onSwitchExecuted(isChecked: Boolean, nodeId: String?)
    }
}