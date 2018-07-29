package com.hcl.daksh.android_poc_camp.Login.DB

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.Nullable
import com.google.gson.annotations.SerializedName

@Entity
class EntityDevices(@PrimaryKey() var _id: String,
                    @SerializedName("deviceName") @Nullable @ColumnInfo() var deviceName: String? = null,
                    @SerializedName("deviceIp") @Nullable @ColumnInfo() var deviceIp: String? = null,
                    @SerializedName("deviceLocation") @Nullable @ColumnInfo() var deviceLocation: String? = null,
                    @SerializedName("deviceType") @Nullable @ColumnInfo() var deviceType: Int? = null,
                    @SerializedName("isDeviceSwitchedOn") @Nullable @ColumnInfo() var isDeviceSwitchedOn: Boolean? = null) {

    override fun toString(): String = "{ _id: $_id, deviceName : $deviceName, deviceIp: $deviceIp, deviceType: $deviceType, deviceLocation: $deviceLocation, isDeviceSwitchedOn: $isDeviceSwitchedOn}"

    override fun equals(device: Any?): Boolean {
        return if(this@EntityDevices._id == (device as EntityDevices)._id)
            if(this@EntityDevices.deviceName == device.deviceName)
                if(this@EntityDevices.deviceIp == device.deviceIp)
                    if(this@EntityDevices.deviceLocation == device.deviceLocation)
                        if(this@EntityDevices.deviceType == device.deviceType)
                            this@EntityDevices.isDeviceSwitchedOn == device.isDeviceSwitchedOn
                        else
                            false
                    else
                        false
                else
                    false
            else
                false
        else
            false
    }
}