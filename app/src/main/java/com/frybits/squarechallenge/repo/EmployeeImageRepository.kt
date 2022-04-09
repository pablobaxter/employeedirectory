package com.frybits.squarechallenge.repo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.frybits.squarechallenge.models.EmployeeData
import com.frybits.squarechallenge.repo.cache.ImageCache
import com.frybits.squarechallenge.repo.networking.EmployeeApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.net.URI
import javax.inject.Inject

private const val LOG_TAG = "EmployeeImageRepository"

/**
 * Repository for storing and retrieving images from memory, disk, or network
 */
interface EmployeeImageRepository {

    /**
     * Retrieve the small employee profile image
     */
    suspend fun getEmployeeSmallImage(employeeData: EmployeeData): Result<Bitmap>

    /**
     * Retrieve the large employee profile image
     */
    suspend fun getEmployeeLargeImage(employeeData: EmployeeData): Result<Bitmap>

    /**
     * Notifies memory cache about system low memory
     */
    fun onLowMemory()
}

class EmployeeImageRepositoryImpl @Inject constructor(
    private val employeeApi: EmployeeApi,
    private val imageCache: ImageCache
) : EmployeeImageRepository {

    override suspend fun getEmployeeSmallImage(employeeData: EmployeeData): Result<Bitmap> {
        return getImage(employeeData, ImageType.SMALL)
    }

    override suspend fun getEmployeeLargeImage(employeeData: EmployeeData): Result<Bitmap> {
        return getImage(employeeData, ImageType.LARGE)
    }

    override fun onLowMemory() {
        imageCache.onLowMemory()
    }

    // Helper function to retrieve images from memory/disk cache first, and network cache if unable to retrieve
    private suspend fun getImage(employeeData: EmployeeData, type: ImageType): Result<Bitmap> {
        return runCatching {
            val imgUrl = when (type) {
                ImageType.SMALL -> URI(employeeData.photo_url_small)
                ImageType.LARGE -> URI(employeeData.photo_url_large)
            }
            val imageId = imgUrl.path.dropWhile { it == '/' }.replace('/', '_')
            val bitmap = imageCache.retrieveImage(imageId)
            if (bitmap == null) {
                Log.d(LOG_TAG, "Image cache miss, getting image from network...")
                val response = employeeApi.getEmployeeImage(imgUrl.toString())
                val networkBitmap = BitmapFactory.decodeStream(response.byteStream())
                imageCache.storeImage(imageId, networkBitmap)
                Log.d(LOG_TAG, "Got image from network and stored in cache. Key=$imageId")
                return@runCatching networkBitmap
            }
            return@runCatching bitmap
        }
    }
}

// This is probably overkill right now, but if multiple sized images become available, should allow for expanded types
private enum class ImageType {
    SMALL, LARGE
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class EmployeeImageModule {

    @ViewModelScoped
    @Binds
    abstract fun bindEmployeeImageRepository(employeeImageRepositoryImpl: EmployeeImageRepositoryImpl): EmployeeImageRepository
}
