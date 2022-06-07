package com.example.parkingsystem.room.application

import android.app.Application
import com.example.parkingsystem.room.db.ParkingRoomDatabase
import com.example.parkingsystem.room.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class UsersApplication: Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { ParkingRoomDatabase.getDatabase(this, applicationScope) }
    //private val database by lazy {  ParkingRoomDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }

}