package com.example.parkingsystem.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.parkingsystem.room.dao.UserDao
import com.example.parkingsystem.room.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities  = [User::class], version = 10, exportSchema = false)
abstract class ParkingRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    private class UserDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {


        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val userDao = database.userDao()

                    // Delete all content here.
                    userDao.deleteAll()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ParkingRoomDatabase? = null

        //fun getDatabase(context: Context): ParkingRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
         //   if (INSTANCE == null) {
         //       synchronized(this) {
         //           // Pass the database to the INSTANCE
         //           INSTANCE = buildDatabase(context)
         //       }
        //    }
            // Return database.
        //    return INSTANCE!!
        //}

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ParkingRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                   ParkingRoomDatabase::class.java,
                    "parking_database"
                )
                    .addCallback(UserDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
                //return instance

            }
        }

        private fun buildDatabase(context: Context): ParkingRoomDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ParkingRoomDatabase::class.java,
                "parking_database"
            )
                .build()
        }
    }
}
