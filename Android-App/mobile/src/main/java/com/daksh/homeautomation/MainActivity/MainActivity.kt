package com.daksh.homeautomation.MainActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import com.daksh.homeautomation.MainActivity.Model.Model
import com.daksh.homeautomation.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Listener {

    //UI elements used on the page
    private var txLivingRoomLamp:TextView? = null
    private var swLivingRoomSwitch: Switch? = null

    //Instantiate the controller
    private val controller: MainActivityController = MainActivityController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        //Assign variables
        txLivingRoomLamp = livingRoomLamp as TextView
        swLivingRoomSwitch = livingRoomLampToggle as Switch
    }

    override fun onResume() {
        super.onResume()

        //Update status and reflect on the switch
        controller.updateStatus(this@MainActivity)

        //Apply tap listener on TextView
        txLivingRoomLamp?.setOnClickListener { _ ->

            //Toggle the switch first
            swLivingRoomSwitch?.isChecked = !swLivingRoomSwitch?.isChecked!!

            controller.toggleSwitch(object: Listener {

                override fun onReceived(body: Model?) {
                    swLivingRoomSwitch?.isChecked = body?.isLampSwitchedOn!!
                }
            })
        }
    }

    override fun onReceived(body: Model?) {
        if(body != null)
            swLivingRoomSwitch?.isChecked = body.isLampSwitchedOn!!
    }
}
