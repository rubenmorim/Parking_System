package com.example.parkingsystem.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parkingsystem.room.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("DELETE FROM utilizador_table")
    suspend fun deleteAll(): Int

    //@Query("SELECT * FROM utilizador WHERE email LIKE :email")
    //fun getByEmail(email: String): User?

    @Query("SELECT * FROM utilizador_table ORDER BY firstName DESC")
    fun getAllUsers(): Flow<List<User>>

}