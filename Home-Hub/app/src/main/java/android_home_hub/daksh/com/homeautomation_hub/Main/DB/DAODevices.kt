package com.hcl.daksh.android_poc_camp.Login.DB

import android.arch.persistence.room.*

@Dao
interface DAODevices {

    /**
     * Returns the only user on the user table
     */
    @Query("SELECT * FROM EntityDevices where _id =:deviceId")
    fun getDeviceById(deviceId: String): EntityDevices?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: EntityDevices)

    @Delete
    fun deleteDevice(device: EntityDevices)

    //Return all devices in the device list
    @Query("Select * from EntityDevices")
    fun getAllDevices(): MutableList<EntityDevices>

    //Return devices by name
    @Query("Select _id from EntityDevices where deviceIp=:deviceIpAddress")
    fun getDeviceByDeviceIp(deviceIpAddress: String): String

    @Query("update EntityDevices set isDeviceSwitchedOn=:isDeviceSwitchedOn where _id=:id")
    fun updateDevice(isDeviceSwitchedOn: Boolean, id: String)
}