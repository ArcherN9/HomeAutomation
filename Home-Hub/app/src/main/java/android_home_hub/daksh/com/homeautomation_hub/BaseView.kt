package com.hcl.daksh.android_poc_rooms

import android.content.Context
import android.widget.EditText

interface BaseView<T> {

    /**
     * The presenter associated with this view
     */
    var presenter: T

    /**
     * Used to retrieve the application's context
     */
    fun getAppContext(): Context

    /**
     * An extension function to get values from an Edit text easily
     */
    fun EditText.value() = this.text.toString()
}