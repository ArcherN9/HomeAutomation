package com.daksh.homeautomation.MainActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.daksh.homeautomation.MainActivity.Model.Model
import com.daksh.homeautomation.MainActivity.Model.NodeModel
import com.daksh.homeautomation.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Listener {

    //UI elements used on the page
    //Not all UI elements are list | Only the ones which need a change of name are listed here
//    private var txLivingRoomLamp:TextView? = null
//    private var swLivingRoomSwitch: Switch? = null

    //Instantiate the controller
    private val controller: MainActivityController = MainActivityController()
    //The recyclerView adapter
    private var nodeRecyclerViewAdapter: NodeListRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        //Assign variables
//        txLivingRoomLamp = livingRoomLamp as TextView
//        swLivingRoomSwitch = livingRoomLampToggle as Switch
    }

    override fun onResume() {
        super.onResume()

        //Start refreshing the page
        swipeRefreshLayout.isRefreshing = true

        //Get all nodes from the server
        controller.getNodes(this@MainActivity)

        //Update status and reflect on the switch
//        controller.updateStatus(this@MainActivity)

        //Apply tap listener on TextView
//        txLivingRoomLamp?.setOnClickListener { _ ->
//
//            //Toggle the switch first
//            swLivingRoomSwitch?.isChecked = !swLivingRoomSwitch?.isChecked!!
//
//            controller.toggleSwitch(swLivingRoomSwitch?.isChecked!!, object: Listener {
//
//                override fun onReceived(body: Model?) {
//                    swLivingRoomSwitch?.isChecked = body?.isLampSwitchedOn!!
//                }
//            })
//        }
    }

    override fun onSwitchExecuted(checked: Boolean, intPosition: Int, nodeId: String?) {
        controller.toggleSwitch(checked, nodeId, this@MainActivity)
    }

    /**
     * onReceived is executed when .getNodes() is called from the activity. The controller
     * fetches the updated controller list and returns it in this listener
     */
    override fun onReceived(nodeList: MutableList<NodeModel>) {
        //Instantiate the adapter
        nodeRecyclerViewAdapter = NodeListRecyclerViewAdapter()
        //pass the interaction listener
        nodeRecyclerViewAdapter?.setInteractionlistener(this@MainActivity)
        //Set the node list
        nodeRecyclerViewAdapter?.setNodeList(nodeList)

        //Attach the adapter to recyclerView
        nodeRecyclerView.adapter = nodeRecyclerViewAdapter
    }

    override fun onReceived(body: Model?) {
//        Close the refresh indicator
        swipeRefreshLayout.isRefreshing = false
        if(body != null)
            for(nodeModel in nodeRecyclerViewAdapter?.getAdapterItems()!!)
                if(nodeModel.nodeId.equals(body.nodeId, ignoreCase = false)) {
                    nodeModel.isNodeTurnedOn = body.isLampSwitchedOn
                    nodeRecyclerViewAdapter?.notifyDataSetChanged()
                }
    }
}
