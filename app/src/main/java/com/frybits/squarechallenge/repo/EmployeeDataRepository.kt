package com.frybits.squarechallenge.repo

import android.util.Log
import com.frybits.squarechallenge.models.EmployeeDataList
import com.frybits.squarechallenge.models.toEmployeeEntities
import com.frybits.squarechallenge.models.toEmployees
import com.frybits.squarechallenge.repo.cache.EmployeeDao
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val LOG_TAG = "EmployeeDataRepository"

/**
 * Repository for getting employee data
 */
interface EmployeeDataRepository {

    /**
     * Returns an [EmployeeDataList] from cache or network
     */
    suspend fun getEmployeeDataList(forceNetwork: Boolean = false): Result<EmployeeDataList>
}

class EmployeeDataRepositoryImpl @Inject constructor(
    private val employeeApi: EmployeeApi,
    private val employeeDao: EmployeeDao
) : EmployeeDataRepository {

    // Prevents multiple coroutines from making the same network call if one is already in flight
    private val mutex = Mutex()

    override suspend fun getEmployeeDataList(forceNetwork: Boolean): Result<EmployeeDataList> {
        return runCatching {
            mutex.withLock {
                val employeeEntities = employeeDao.getAllEmployees()
                val now = System.currentTimeMillis()
                if (employeeEntities.isEmpty() || employeeEntities.any { 
                        (now - it.fetch_time) > TimeUnit.SECONDS.toMillis(30)
                }) {
                    return@withLock employeeApi.getEmployeeDataList().also {
                        Log.d(LOG_TAG, "Retrieved employee list from network")
                        employeeDao.insertAll(it.employees.toEmployeeEntities())
                    }
                } else {
                    return Result.success(EmployeeDataList(employeeEntities.toEmployees()))
                }
            }
        }
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class EmployeeModule {

    @ViewModelScoped
    @Binds
    abstract fun bindEmployeeDataRepository(employeeRepositoryImpl: EmployeeDataRepositoryImpl): EmployeeDataRepository
}
