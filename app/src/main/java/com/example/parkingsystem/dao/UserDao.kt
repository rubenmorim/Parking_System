package com.example.parkingsystem.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parkingsystem.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Query("SELECT * FROM utilizador ORDER BY firstName DESC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM utilizador")
    fun deleteAll(): Int

    @Query("SELECT * FROM utilizador WHERE email LIKE :email")
    fun getByEmail(email: String): User?

}