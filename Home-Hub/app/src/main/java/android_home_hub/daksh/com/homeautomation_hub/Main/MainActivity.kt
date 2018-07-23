package android_home_hub.daksh.com.homeautomation_hub.Main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android_home_hub.daksh.com.homeautomation_hub.R
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Dashboard.PresenterMain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContractMain.View {

    //This activity's presenter
    override lateinit var presenter: ContractMain.Presenter
    lateinit var deviceList: DeviceRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        presenter.loadList()
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
}