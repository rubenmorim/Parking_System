package com.example.parkingsystem.repository

import com.example.parkingsystem.dao.UserDao
import com.example.parkingsystem.entity.User

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class UserRepository(private val userDao: UserDao) {

    val allUsers = userDao.getAllUsers()

    fun insert(user: User) {
        return userDao.insert(user)
    }

    fun getByEmail(email: String):User?{
        return userDao.getByEmail(email)
    }
}
