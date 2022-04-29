package com.frybits.squarechallenge.repo.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.frybits.squarechallenge.models.EmployeeEntity

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(employeeEntities: List<EmployeeEntity>)

    @Query("SELECT * FROM employeeentity")
    suspend fun getAllEmployees(): List<EmployeeEntity>
}
