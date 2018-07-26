package com.daksh.homeautomation.MainActivity

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.daksh.homeautomation.AppDatabase
import com.daksh.homeautomation.ElsaApplication
import com.daksh.homeautomation.R
import com.google.gson.Gson
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Login.DB.DAODevices
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContractMain.View {

    //This activity's presenter
    override lateinit var presenter: ContractMain.Presenter

    //The recyclerView adapter
    private var nodeRecyclerViewAdapter: NodeListRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Instantiate the Controller
        presenter = PresenterMainActivity(this@MainActivity)
        presenter.start()

        //Instantiate the adapter
        nodeRecyclerViewAdapter = NodeListRecyclerViewAdapter()
        //Attach the adapter to recyclerView
        nodeRecyclerView.adapter = nodeRecyclerViewAdapter
        //pass the interaction listener
        nodeRecyclerViewAdapter?.setInteractionlistener(presenter = presenter)

        //Start the HandlerThread first | Scroll to bottom
        mHandlerThread = HandlerThread("Background Handler Thread")
        mHandlerThread?.start()
        mMainHandler = Handler(Looper.getMainLooper())
        mHandler = Handler(mHandlerThread?.looper, Handler.Callback {
            ElsaApplication.log("Received message on MainActivity mainHandler ${it.data}")

            //Transfer call to Presenter
            val gson = Gson()
            val model = gson.fromJson(it.data.getString("Message"), EntityDevices::class.java)

            //Refresh the recyclerView
            presenter.loadList()

            return@Callback true
        })
    }

    override fun onResume() {
        super.onResume()

        //Start refreshing the page
        swipeRefreshLayout.isRefreshing = true

        //Get all nodes from the server
        presenter.loadList()
    }

    /**
     * onReceived is executed when .getNodes() is called from the activity. The presenter
     * fetches the updated presenter list and returns it in this listener
     */
    override fun showDeviceList(nodeList: MutableList<EntityDevices>?) {
        mMainHandler?.post {
            //Set the node list
            nodeRecyclerViewAdapter?.setNodeList(nodeList)
            nodeRecyclerViewAdapter?.notifyDataSetChanged()
        }
    }

    //Returns the application to the presenter
    override fun getElsaApplication(): ElsaApplication = application as ElsaApplication

    //Returns the application context back to the presenter
    override fun getAppContext(): Context = applicationContext

    //Returns the applications resources to the caller
    override fun getAppResources(): Resources = resources

    //Returns the device DAO which can be used to interact with the devices stored in DB
    override fun getDeviceDB(): DAODevices = AppDatabase.getInstance(this@MainActivity).getDeviceDao()

    companion object {
        var mHandlerThread: HandlerThread? = null
        var mHandler: Handler? = null
        var mMainHandler: Handler? = null
    }
}
