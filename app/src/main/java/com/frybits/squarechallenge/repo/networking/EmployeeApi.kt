package com.frybits.squarechallenge.repo.networking

import com.frybits.squarechallenge.models.EmployeeDataList
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface EmployeeApi {

    @GET("employees.json")
    suspend fun getEmployeeDataList(): EmployeeDataList

    @GET
    suspend fun getEmployeeImage(@Url url: String): ResponseBody

    // Leaving these APIs alone for now, for test purposes
    @GET("employees_malformed.json")
    suspend fun getEmployeeDataListMalformed(): EmployeeDataList

    @GET("employees_empty.json")
    suspend fun getEmployeeDataListEmpty(): EmployeeDataList
}
