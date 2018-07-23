package android_home_hub.daksh.com.homeautomation_hub.Config

import android.annotation.SuppressLint
import android.content.Context
import android.os.Vibrator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android_home_hub.daksh.com.homeautomation_hub.R

object Vibrator {

    //An object of the Android OS Vibrator service
    private lateinit var androidVibrator: Vibrator

    /**
     * A utility method to check if the android device has a vibrator or not
     * @return returns whether android device supports vibration or not
     */
    private fun hasVibrator(): Boolean = androidVibrator.hasVibrator()

    @SuppressLint("MissingPermission")
            /**
             * A utility method to vibrate the phone for as long as the animation runs on the two input boxes
             * on the rink activity. This method is only used to inform the user that the entry inserted
             * is not valid.
             */
    fun vibrateInvalidInput(context: Context) {
        if(!::androidVibrator.isInitialized)
            androidVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if(hasVibrator()) {
            val shake: Animation = AnimationUtils.loadAnimation(context, R.anim.invalidinput_shake)
            androidVibrator.vibrate(shake.duration)
        }
    }
}