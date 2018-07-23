package android_home_hub.daksh.com.homeautomation_hub.Main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import android_home_hub.daksh.com.homeautomation_hub.R
import com.hcl.daksh.android_poc_camp.Dashboard.ContractMain
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices
import kotlinx.android.synthetic.main.layout_device_list.view.*

class DeviceRecyclerViewAdapter(private var presenter: ContractMain.Presenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //The node list
    private var nodeList: MutableList<EntityDevices>? = null

    /**
     * Accepts the node list items from the activity
     */
    internal fun setNodeList(nodeList: MutableList<EntityDevices>?) {
        this@DeviceRecyclerViewAdapter.nodeList = nodeList
    }

    /**
     * Returns the adapter items
     */
    internal fun getAdapterItems(): MutableList<EntityDevices>? = nodeList

    /**
     * Returns an adapter element by ID
     */
    internal fun getAdapterItemById(itemId: Long): EntityDevices? {
        nodeList?.let {
            for(listItem in it)
                if(listItem._id == itemId)
                    return listItem
        }

        return null
    }

    /**
     * Returns the position of the element
     */
    internal fun getAdapterPositionById(itemId: Long): Int {
        nodeList?.let {
            for(index in 0..it.size)
                if(itemId == it[index]._id)
                    return index
        }

        return -1
    }

    override fun getItemCount(): Int {
        nodeList?.let {
            return if(it.size > 0)
                it.size
            else
                0
        }

        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
            = (holder as DeviceViewHolder).bind(position, nodeList, presenter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = DeviceViewHolder(parent.inflateView())

    class DeviceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(intPosition: Int, nodeList: MutableList<EntityDevices>?, presenter: ContractMain.Presenter){
            //Set the name of the node

            itemView.deviceName.text = nodeList?.get(intPosition)?.deviceName

            //Set the description of the node
            itemView.deviceLocation.text = nodeList?.get(intPosition)?.deviceLocation

            //Identify the type of node and setup appropriately
            if(nodeList?.get(intPosition)?.deviceType == 1) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.VISIBLE
                //Set the status of the node
                itemView.nodeToggle.isChecked = nodeList[intPosition].isDeviceSwitchedOn!!
            } else if(nodeList?.get(intPosition)?.deviceType == 2) {

                //Hide the switch, its not required
                itemView.nodeToggle.visibility = View.GONE
            }
            //Set the tap listener
            itemView.nodeContainer.setOnClickListener { _ ->

                HomeApplication.log("Switch for device ${nodeList?.get(intPosition)?.deviceName} with IP ${nodeList?.get(intPosition)?.deviceIp} was flipped.")
                itemView.nodeToggle.isChecked = !itemView.nodeToggle.isChecked

                //Inform the activity to pass on request to the controller to fetch updated data on the switch
                presenter.onSwitchExecuted(itemView.nodeToggle.isChecked, intPosition, nodeList?.get(intPosition)?._id)
            }
        }
    }

    // =========================== Extension methods =========================== //

    private fun ViewGroup.inflateView(): View =
            LayoutInflater.from(context).inflate(R.layout.layout_device_list, this, false)
}