package com.frybits.squarechallenge.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.frybits.squarechallenge.models.EmployeeData
import com.frybits.squarechallenge.models.EmployeeDataList
import com.frybits.squarechallenge.repo.EmployeeDataRepository
import com.frybits.squarechallenge.repo.EmployeeImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val LOG_TAG = "EmployeeDirectoryViewModel"

@HiltViewModel
class EmployeeDirectoryViewModel @Inject constructor(
    private val employeeDataRepository: EmployeeDataRepository,
    private val employeeImageRepository: EmployeeImageRepository
) : ViewModel() {

    suspend fun getEmployees(refresh: Boolean = false): Result<EmployeeDataList> {
        return employeeDataRepository.getEmployeeDataList(refresh).onFailure {
            Log.e(LOG_TAG, "Unable to retrieve employee data", it)
        }
    }

    suspend fun getEmployeeSmallImage(employeeData: EmployeeData): Bitmap? {
        return employeeImageRepository.getEmployeeSmallImage(employeeData).onFailure {
            Log.e(LOG_TAG, "Unable to retrieve employee small profile image", it)
        }.getOrNull()
    }

    suspend fun getEmployeeLargeImage(employeeData: EmployeeData): Bitmap? {
        return employeeImageRepository.getEmployeeLargeImage(employeeData).onFailure {
            Log.e(LOG_TAG, "Unable to retrieve employee large profile image", it)
        }.getOrNull()
    }

    fun onLowMemory() {
        employeeImageRepository.onLowMemory()
    }
}
