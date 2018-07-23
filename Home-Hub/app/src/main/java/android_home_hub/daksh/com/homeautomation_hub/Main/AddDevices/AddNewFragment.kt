package android_home_hub.daksh.com.homeautomation_hub.Main.AddDevices

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android_home_hub.daksh.com.homeautomation_hub.Config.Vibrator
import android_home_hub.daksh.com.homeautomation_hub.Main.MainActivity
import android_home_hub.daksh.com.homeautomation_hub.R
import com.hcl.daksh.android_poc_camp.Dashboard.ContractAddDevices
import kotlinx.android.synthetic.main.fragment_add_new_device.*

class AddNewFragment: DialogFragment(), ContractAddDevices.View {

    //The presenter associated with this dialog fragment
    override lateinit var presenter: ContractAddDevices.Presenter

    //Returns the application context
    override fun getAppContext(): Context = activity as Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Register a new presenter
        presenter = PresenterAddDevice(this@AddNewFragment)
        presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflateFragment(container)

    override fun onResume() {
        super.onResume()

        val params = dialog.window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams

        //attach tap listener
        btnSubmit.setOnClickListener { _ ->
            presenter.registerDevice(
                    ipAddress = edDeviceIp.string(),
                    deviceName = edDeviceName.string(),
                    isDeviceSwitchedOn = false,
                    deviceType = 1,
                    deviceLocation = edDeviceLocation.string()
            )
        }
    }

    //Dismisses the dialog fragment
    override fun dismissDialog() {
        //Dismiss the listener
        this@AddNewFragment.dismiss()

        //Tell activity to reload the adapter
        (activity as MainActivity).refresh()
    }

    override fun invalidateInput() {
        //Retrieve animation and start
        val shake: Animation = AnimationUtils.loadAnimation(activity, R.anim.invalidinput_shake)
        edDeviceIp.startAnimation(shake)
        edDeviceName.startAnimation(shake)

        //Change input text color to Red to highlight an invalid input attempt
        setInputTextColor(R.color.colorText_red)
        shake.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                activity?.let { Vibrator.vibrateInvalidInput(it) }
            }

            override fun onAnimationEnd(animation: Animation) {
                //Once the animation ends, change it back to it's orignal state
                setInputTextColor(R.color.colorText_primary_black)
            }

            override fun onAnimationRepeat(animation: Animation ) {
                //Empty Stub
            }
        })
    }

    /**
     * A helper method to toggle colors of input fields at the bottom of this activity.
     * @param intResid The resource ID of the color which the text fields are to be changed to
     */
    private fun setInputTextColor(intResid: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.let {
                edDeviceIp.setTextColor(resources.getColor(intResid, it.theme))
                edDeviceName.setTextColor(resources.getColor(intResid, it.theme))
            }
        } else {
            activity.let {
                edDeviceIp.setTextColor(resources.getColor(intResid))
                edDeviceName.setTextColor(resources.getColor(intResid))
            }
        }
    }

    // =========================== Extension methods =========================== //

    //Inflate the view with the mentioned resource
    private fun LayoutInflater.inflateFragment(container: ViewGroup?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return this.inflate(R.layout.fragment_add_new_device, container, false)
    }

    //Return the text value of the edit text
    private fun EditText.string(): String = this.text.toString()
}