package com.frybits.squarechallenge.repo

import com.frybits.squarechallenge.repo.cache.ImageCache
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class EmployeeImageRepositoryTest {

    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var employeeApiMock: EmployeeApi

    @Mock
    lateinit var imageCache: ImageCache

    @BeforeTest
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @AfterTest
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun
}