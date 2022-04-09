package com.frybits.squarechallenge.repo

import com.frybits.squarechallenge.models.EmployeeData
import com.frybits.squarechallenge.models.EmployeeType
import com.frybits.squarechallenge.repo.cache.ImageCache
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBitmapFactory
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

// Needed robolectric for bitmap usage
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], shadows = [ShadowBitmapFactory::class])
class EmployeeImageRepositoryTest {

    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var employeeApiMock: EmployeeApi

    @Mock
    lateinit var imageCacheMock: ImageCache

    @BeforeTest
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @AfterTest
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun getSmallImageTest() = runTest {
        val uuid = UUID.randomUUID()
        val employeeData = EmployeeData(
            uuid = uuid,
            full_name = "testName",
            phone_number = "",
            email_address = "test@test.com",
            biography = "",
            photo_url_small = "https://blah.com/someimage/123/small.jpg",
            photo_url_large = "https://blah.com/someimage/123/large.jpg",
            team = "test",
            employee_type = EmployeeType.FULL_TIME
        )

        val employeeImageRepository = EmployeeImageRepositoryImpl(employeeApiMock, imageCacheMock)

        val responseMock = mock<ResponseBody>()
        whenever(responseMock.source()).thenReturn(mock())

        // Plugin dummy image
        val classLoader = this::class.java.classLoader!!
        whenever(responseMock.byteStream()).thenReturn(classLoader.getResourceAsStream("images/test_image.jpg"))
        whenever(employeeApiMock.getEmployeeImage(eq(employeeData.photo_url_small))).thenReturn(responseMock)

        employeeImageRepository.getEmployeeSmallImage(employeeData)

        val expectedKey = "someimage_123_small.jpg"
        verify(imageCacheMock, times(1)).retrieveImage(eq(expectedKey))
        verify(employeeApiMock, times(1)).getEmployeeImage(eq(employeeData.photo_url_small))
        verify(imageCacheMock, times(1)).storeImage(eq(expectedKey), anyOrNull())
    }

    @Test
    fun getLargeImageTest() = runTest {
        val uuid = UUID.randomUUID()
        val employeeData = EmployeeData(
            uuid = uuid,
            full_name = "testName",
            phone_number = "",
            email_address = "test@test.com",
            biography = "",
            photo_url_small = "https://blah.com/someimage/123/small.jpg",
            photo_url_large = "https://blah.com/someimage/123/large.jpg",
            team = "test",
            employee_type = EmployeeType.FULL_TIME
        )

        val employeeImageRepository = EmployeeImageRepositoryImpl(employeeApiMock, imageCacheMock)

        val responseMock = mock<ResponseBody>()
        whenever(responseMock.source()).thenReturn(mock())

        // Plugin dummy image
        val classLoader = this::class.java.classLoader!!
        whenever(responseMock.byteStream()).thenReturn(classLoader.getResourceAsStream("images/test_image.jpg"))
        whenever(employeeApiMock.getEmployeeImage(eq(employeeData.photo_url_large))).thenReturn(responseMock)

        employeeImageRepository.getEmployeeLargeImage(employeeData)

        val expectedKey = "someimage_123_large.jpg"
        verify(imageCacheMock, times(1)).retrieveImage(eq(expectedKey))
        verify(employeeApiMock, times(1)).getEmployeeImage(eq(employeeData.photo_url_large))
        verify(imageCacheMock, times(1)).storeImage(eq(expectedKey), anyOrNull())
    }

    @Test
    fun getImageFromMemoryCache() = runTest {
        val uuid = UUID.randomUUID()
        val employeeData = EmployeeData(
            uuid = uuid,
            full_name = "testName",
            phone_number = "",
            email_address = "test@test.com",
            biography = "",
            photo_url_small = "https://blah.com/someimage/123/small.jpg",
            photo_url_large = "https://blah.com/someimage/123/large.jpg",
            team = "test",
            employee_type = EmployeeType.FULL_TIME
        )

        val employeeImageRepository = EmployeeImageRepositoryImpl(employeeApiMock, imageCacheMock)

        val expectedKey = "someimage_123_small.jpg"

        whenever(imageCacheMock.retrieveImage(eq(expectedKey))).thenReturn(mock())

        employeeImageRepository.getEmployeeSmallImage(employeeData)

        verify(imageCacheMock, times(1)).retrieveImage(eq(expectedKey))
        verify(employeeApiMock, never()).getEmployeeImage(anyOrNull())
        verify(imageCacheMock, never()).storeImage(eq(expectedKey), anyOrNull())
    }
}
