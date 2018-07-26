package com.daksh.homeautomation

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.hcl.daksh.android_poc_camp.Login.DB.DAODevices
import com.hcl.daksh.android_poc_camp.Login.DB.EntityDevices

@Database(entities = [EntityDevices::class ], version = 1)
abstract class AppDatabase: RoomDatabase() {

    //The Login DAO
    abstract fun getDeviceDao(): DAODevices

    companion object {

        /**
         * The instance of DB returned everytime it is queried
         */
        private lateinit var INSTANCE: AppDatabase

        /**
         * Returns the DB instance
         */
        fun getInstance(context: Context): AppDatabase {
            synchronized(AppDatabase::class.java) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "Android-Home-Hub.db").build()
                return INSTANCE
            }
        }
    }
}