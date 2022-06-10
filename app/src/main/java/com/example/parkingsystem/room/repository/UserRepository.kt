package com.example.parkingsystem.room.repository

import androidx.annotation.WorkerThread
import com.example.parkingsystem.room.dao.UserDao
import com.example.parkingsystem.room.entity.User
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        return userDao.insert(user)
    }

    suspend fun deleteAll() {
        return userDao.deleteAll()
    }

    //fun getIdUser():List<Long>{
    //   return userDao.getIdUser()
   //}
}
