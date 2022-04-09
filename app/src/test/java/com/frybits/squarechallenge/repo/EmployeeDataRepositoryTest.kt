package com.frybits.squarechallenge.repo

import com.frybits.squarechallenge.models.EmployeeDataList
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import kotlinx.coroutines.test.runTest
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class EmployeeDataRepositoryTest {

    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var employeeApiMock: EmployeeApi

    @BeforeTest
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @AfterTest
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun employeeListFromNetworkTest() = runTest {
        val employeeDataRepository = EmployeeDataRepositoryImpl(employeeApiMock)
        val testEmployeeDataList = EmployeeDataList(emptyList())
        whenever(employeeApiMock.getEmployeeDataList()).thenReturn(testEmployeeDataList)

        val testResult = employeeDataRepository.getEmployeeDataList().getOrThrow()
        assertEquals(testEmployeeDataList, testResult)

        verify(employeeApiMock, times(1)).getEmployeeDataList()
    }

    @Test
    fun employeeListFromCacheTest() = runTest {
        val employeeDataRepository = EmployeeDataRepositoryImpl(employeeApiMock)
        whenever(employeeApiMock.getEmployeeDataList()).then {
            return@then EmployeeDataList(emptyList())
        }

        val testEmployeeDataList = employeeDataRepository.getEmployeeDataList().getOrThrow()

        verify(employeeApiMock, times(1)).getEmployeeDataList()

        val testResult = employeeDataRepository.getEmployeeDataList().getOrThrow()
        assertSame(testEmployeeDataList, testResult)

        verifyNoMoreInteractions(employeeApiMock)
    }

    @Test
    fun employeeListForceRefreshTest() = runTest {
        val employeeDataRepository = EmployeeDataRepositoryImpl(employeeApiMock)
        whenever(employeeApiMock.getEmployeeDataList()).then {
            return@then EmployeeDataList(emptyList())
        }

        val testEmployeeDataList = employeeDataRepository.getEmployeeDataList().getOrThrow()

        val testResult = employeeDataRepository.getEmployeeDataList(forceNetwork = true).getOrThrow()
        assertNotSame(testEmployeeDataList, testResult)

        verify(employeeApiMock, times(2)).getEmployeeDataList()
    }
}
