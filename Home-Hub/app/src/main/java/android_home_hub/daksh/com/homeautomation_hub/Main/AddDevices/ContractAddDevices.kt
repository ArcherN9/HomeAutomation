package com.hcl.daksh.android_poc_camp.Dashboard

import com.hcl.daksh.android_poc_rooms.BasePresenter
import com.hcl.daksh.android_poc_rooms.BaseView

interface ContractAddDevices {

    interface View : BaseView<Presenter> {
        //Dismisses the dialog fragment
        fun dismissDialog()

        //Provide visual feedback to user of incorrect entry
        fun invalidateInput()
    }

    interface Presenter : BasePresenter {

        //Register a new device in DB
        fun registerDevice(id: String, ipAddress: String, deviceName: String, deviceLocation: String, deviceType: Int, isDeviceSwitchedOn: Boolean)
    }
}