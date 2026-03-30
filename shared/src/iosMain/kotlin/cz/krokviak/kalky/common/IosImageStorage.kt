@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package cz.krokviak.kalky.common

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUUID
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.posix.memcpy

class IosImageStorage : ImageStorage {
    override suspend fun storeImageFile(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val fileManager = NSFileManager.defaultManager
        val documentsDir = fileManager.URLsForDirectory(
            NSDocumentDirectory, NSUserDomainMask
        ).first() as NSURL
        val photosDir = documentsDir.URLByAppendingPathComponent("photos")!!
        val photosDirPath = photosDir.path!!

        if (!fileManager.fileExistsAtPath(photosDirPath)) {
            fileManager.createDirectoryAtPath(photosDirPath, withIntermediateDirectories = true, attributes = null, error = null)
        }

        val fileName = "photo_${NSUUID().UUIDString}.jpg"
        val filePath = "$photosDirPath/$fileName"
        val nsData = imageBytes.toNSData()
        nsData.writeToFile(filePath, atomically = true)
        filePath
    }

    override suspend fun getImageBytes(imagePath: String): ByteArray = withContext(Dispatchers.IO) {
        val nsData = NSData.create(contentsOfFile = imagePath)
            ?: return@withContext ByteArray(0)
        nsData.toByteArray()
    }
}

private fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
}

private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    if (size == 0) return ByteArray(0)
    val bytes = ByteArray(size)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
    return bytes
}
