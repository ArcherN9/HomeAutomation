package com.hcl.daksh.android_poc_camp.Login.DB

import android.arch.persistence.room.*

@Dao
interface DAODevices {

    /**
     * Saves the user post login
     */
    @Insert
    fun saveUser(user: EntityDevices)

    /**
     * Returns the only user on the user table
     */
    @Query("SELECT * FROM EntityDevices where _id = :deviceId")
    fun getDeviceById(deviceId: Long?): EntityDevices?

    //Return all devices in the device list
    @Query("Select * from EntityDevices")
    fun getAllDevices(): MutableList<EntityDevices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: EntityDevices)

    //Inserts the Mutable list passed into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevices(devices: MutableList<EntityDevices>?)

    @Delete
    fun deleteDevice(device: EntityDevices)

    @Update()
    fun updateDevice(device: EntityDevices)
}