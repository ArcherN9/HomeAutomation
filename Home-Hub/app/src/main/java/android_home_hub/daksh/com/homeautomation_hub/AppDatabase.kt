package android_home_hub.daksh.com.homeautomation_hub

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
         * The instance of DB returned every time it is queried
         */
        private var APPINSTANCE: AppDatabase? = null

        /**
         * Returns the DB instance
         */
        fun getInstance(context: Context): AppDatabase {
            synchronized(AppDatabase::class.java) {
                APPINSTANCE?.let {
                    return it
                }

                APPINSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "Android-Home-Hub.db").build()
                return APPINSTANCE!!
            }
        }
    }
}