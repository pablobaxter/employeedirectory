package com.frybits.squarechallenge.repo.cache

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBitmapFactory
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// Needed robolectric for bitmap usage
@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], shadows = [ShadowBitmapFactory::class])
class ImageCacheTest {

    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var contextMock: Context

    @Mock
    lateinit var bitmapMock: Bitmap

    @BeforeTest
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        closeable.close()
        Dispatchers.resetMain()
    }

    @Test
    fun storeAndRetrieveBitmapTest() = runTest {
        val testPath = createTempDirectory("test")
        whenever(contextMock.cacheDir).thenReturn(testPath.toFile())
        val imageCache = ImageCacheImpl(contextMock)
        val randomKey = "Key-${Random.nextInt()}"
        imageCache.storeImage(randomKey, bitmapMock)

        verify(bitmapMock, times(1)).compress(any(), anyInt(), any())

        // Ensure file was created
        assertTrue(testPath.toFile().walkTopDown().any { it.name == randomKey })

        val result = imageCache.retrieveImage(randomKey)
        assertSame(bitmapMock, result)
    }

    @Test
    fun missImageCacheTest() = runTest {
        val testPath = createTempDirectory("test")
        whenever(contextMock.cacheDir).thenReturn(testPath.toFile())
        val imageCache = ImageCacheImpl(contextMock)
        val randomKey = "Key-${Random.nextInt()}"
        imageCache.storeImage(randomKey, bitmapMock)

        verify(bitmapMock, times(1)).compress(any(), anyInt(), any())

        val result = imageCache.retrieveImage("Key-${Random.nextInt()}")
        assertNull(result)
    }

    @Test
    fun ensureEvictAllTest() = runTest {
        val testPath = createTempDirectory("test")
        whenever(contextMock.cacheDir).thenReturn(testPath.toFile())
        val imageCache = ImageCacheImpl(contextMock)
        val randomKey = "Key-${Random.nextInt()}"
        imageCache.storeImage(randomKey, bitmapMock)

        verify(bitmapMock, times(1)).compress(any(), anyInt(), any())

        // Clear the disk cache
        testPath.toFile().deleteRecursively()

        // Ensure the image is still in memory
        val result = imageCache.retrieveImage(randomKey)
        assertSame(bitmapMock, result)

        imageCache.onLowMemory()

        // Ensure that image is no longer in memory or disk cache
        val test = imageCache.retrieveImage(randomKey)
        assertNull(test)
    }

    @Test
    fun retrieveFromDiskTest() = runTest {
        val testPath = createTempDirectory("test")
        whenever(contextMock.cacheDir).thenReturn(testPath.toFile())
        val imageCache = ImageCacheImpl(contextMock)
        val randomKey = "Key-${Random.nextInt()}"

        // Check for empty memory cache
        val emptyResult = imageCache.retrieveImage(randomKey)
        assertNull(emptyResult)

        // Place dummy file
        val imagesDir = File(testPath.toFile(), "images")
        val imageFile = File(imagesDir, randomKey)
        val classLoader = this::class.java.classLoader!!
        classLoader.getResourceAsStream("images/test_image.jpg").buffered().use { inputStream ->
            imageFile.outputStream().buffered().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Get dummy image
        val testResult = imageCache.retrieveImage(randomKey)
        assertNotNull(testResult)
    }
}
