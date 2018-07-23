package com.hcl.daksh.android_poc_camp.Login.DB

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class EntityDevices(@PrimaryKey(autoGenerate = true) var _id: Long?,
                    @ColumnInfo() var deviceName: String,
                    @ColumnInfo() var deviceIp: String,
                    @ColumnInfo() var deviceLocation: String,
                    @ColumnInfo() var deviceType: Int,
                    @ColumnInfo() var isDeviceSwitchedOn: Boolean) {

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