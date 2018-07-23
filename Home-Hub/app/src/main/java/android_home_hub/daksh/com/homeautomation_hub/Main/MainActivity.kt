package android_home_hub.daksh.com.homeautomation_hub.Main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import android_home_hub.daksh.com.homeautomation_hub.Main.Model.ModelDevice
import android_home_hub.daksh.com.homeautomation_hub.R
import com.google.gson.Gson
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Dashboard.PresenterMain
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Type
import java.util.*

class MainActivity : AppCompatActivity(), ContractMain.View {

    //This activity's presenter
    override lateinit var presenter: ContractMain.Presenter
    lateinit var deviceList: DeviceRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Start the HandlerThread first | Scroll to bottom
        mHandlerThread = HandlerThread("Background Handler Thread")
        mHandlerThread?.start()
        mainHandler = Handler(mHandlerThread?.looper, Handler.Callback {
            HomeApplication.log("Received message on MainActivity mainHandler ${it.data}")

            //Transfer call to Presenter
            val gson = Gson()
            val model = gson.fromJson(it.data.getString("Message"), ModelDevice::class.java)
            presenter.onSwitchExecuted(model.status, model.position, model._id)
            return@Callback true
        })

        //Setup the presenter
        presenter = PresenterMain(this@MainActivity)
        //Star the presenter execution
        presenter.start()

        //setup a support action bar
        setSupportActionBar(toolbar)

        //Instantiate the node list
        deviceList = DeviceRecyclerViewAdapter(presenter)
    }

    override fun onResume() {
        super.onResume()
        //Tell the presenter to load the list
        presenter.loadList()

        //Send a dummmy message on pubnub
        txEmptyMessage.setOnClickListener { _ ->
            (application as HomeApplication).getPubNub()?.publish()
                    ?.message(Arrays.asList("hello", "there"))
                    ?.channel(resources.getString(R.string.Home))
                    ?.async(object: PNCallback<PNPublishResult>() {
                        override fun onResponse(result: PNPublishResult?, status: PNStatus?) {
                            HomeApplication.log(status.toString())
                            HomeApplication.log(result.toString())
                        }
                    })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Get the menu inflater to inflate the menu
        val menuInflater = MenuInflater(baseContext)

        //Inflate the menu
        menuInflater.inflate(R.menu.menu, menu)

        //return true to set the action has been consumed
        return true
    }

    /**
     * The method displays the list on the screen
     */
    override fun showList(adapter: DeviceRecyclerViewAdapter?) {
        //Add adpater to the RecyclerView
        deviceRecyclerView.adapter = adapter
    }

    //Refreshes the list with new addition
    fun refresh() = presenter.loadList()

    //Returns the application context
    override fun getAppContext(): Context = applicationContext

    //Returns the support fragment activity manager
    override fun getActivityFragmentManager(): FragmentManager = supportFragmentManager

    /**
     * Displays the empty control on the screen
     */
    override fun showEmptyControl(show: Boolean) {
        if(show)
            txEmptyMessage.visibility = View.VISIBLE
        else
            txEmptyMessage.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addNewDevice ->
                presenter.showAddNewFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        var mHandlerThread: HandlerThread? = null
        var mainHandler: Handler? = null
    }
}