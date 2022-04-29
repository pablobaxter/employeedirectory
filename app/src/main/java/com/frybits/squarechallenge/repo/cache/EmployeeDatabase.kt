package com.frybits.squarechallenge.repo.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.frybits.squarechallenge.models.EmployeeEntity
import com.frybits.squarechallenge.utils.UUIDTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [EmployeeEntity::class], version = 2)
@TypeConverters(UUIDTypeConverter::class)
abstract class EmployeeDatabase: RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
}

@Module
@InstallIn(SingletonComponent::class)
class EmployeeDatabaseModule {

    @Provides
    @Singleton
    fun provideEmployeeDatabase(@ApplicationContext context: Context): EmployeeDatabase {
        return Room.databaseBuilder(
            context,
            EmployeeDatabase::class.java,
            "employeeDatabase"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEmployeeDao(employeeDatabase: EmployeeDatabase): EmployeeDao {
        return employeeDatabase.employeeDao()
    }
}
