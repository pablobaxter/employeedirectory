package com.frybits.squarechallenge.repo

import android.util.Log
import com.frybits.squarechallenge.models.EmployeeDataList
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val employeeApi: EmployeeApi
) : EmployeeDataRepository {

    @Volatile // Using volatile to ensure we are storing and reading from main memory
    private var inMemoryEmployeeDataList: EmployeeDataList? = null // Store the network result in-memory

    // Prevents multiple coroutines from making the same network call if one is already in flight
    private val mutex = Mutex()

    override suspend fun getEmployeeDataList(forceNetwork: Boolean): Result<EmployeeDataList> {
        return runCatching {
            mutex.withLock {
                if (forceNetwork) {
                    inMemoryEmployeeDataList = null // If we are forcing the network, clear in-memory cache
                }
                return@withLock inMemoryEmployeeDataList ?: employeeApi.getEmployeeDataList().also {
                    Log.d(LOG_TAG, "Retrieved employee list from network")
                    inMemoryEmployeeDataList = it
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
