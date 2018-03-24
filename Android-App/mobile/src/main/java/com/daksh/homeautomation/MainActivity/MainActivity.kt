package com.daksh.homeautomation.MainActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.daksh.homeautomation.MainActivity.Model.NodeModel
import com.daksh.homeautomation.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Listener {

    //Instantiate the controller
    private lateinit var controller: MainActivityController
    //The recyclerView adapter
    private var nodeRecyclerViewAdapter: NodeListRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Instantiate the Controller
        controller = MainActivityController(this@MainActivity)

        //Instantiate the adapter
        nodeRecyclerViewAdapter = NodeListRecyclerViewAdapter()
        //Attach the adapter to recyclerView
        nodeRecyclerView.adapter = nodeRecyclerViewAdapter
        //pass the interaction listener
        nodeRecyclerViewAdapter?.setInteractionlistener(this@MainActivity)
    }

    override fun onResume() {
        super.onResume()

        //Start refreshing the page
        swipeRefreshLayout.isRefreshing = true

        //Get all nodes from the server
        controller.getNodes()
    }

    override fun onSwitchExecuted(checked: Boolean, intPosition: Int, nodeId: String?) {
        controller.toggleSwitch(checked, nodeId)
    }

    /**
     * onReceived is executed when .getNodes() is called from the activity. The controller
     * fetches the updated controller list and returns it in this listener
     */
    override fun onReceived(nodeList: MutableList<NodeModel>) {
        Log.i(TAG, "onReceived for getAllNodes called")
        //Set the node list
        nodeRecyclerViewAdapter?.setNodeList(nodeList)
        nodeRecyclerViewAdapter?.notifyDataSetChanged()
    }

//    override fun onReceived(body: Model?) {
////        Close the refresh indicator
//        swipeRefreshLayout.isRefreshing = false
//        if(body != null)
//            for(nodeModel in nodeRecyclerViewAdapter?.getAdapterItems()!!)
//                if(nodeModel.nodeId.equals(body.nodeId, ignoreCase = false)) {
//                    nodeModel.isNodeTurnedOn = body.isLampSwitchedOn
//                    nodeRecyclerViewAdapter?.notifyDataSetChanged()
//                }
//    }

    companion object {

        private val TAG: String = MainActivity::class.java.simpleName
    }
}
