package com.example.images.data

import android.graphics.Bitmap
import android.os.Parcelable
import android.util.Log
import com.example.images.cache.Cache
import kotlinx.android.parcel.Parcelize

interface Image : Parcelable {
    val url: String
    val description: String?
    val bitmap: Bitmap
}

@Parcelize
data class ImageInfo(
    val smallUrl: String,
    val bigUrl: String,
    val description: String?
) : Parcelable

@Parcelize
class SmallImage(
    override val url: String,
    val bigVersionUrl: String,
    override val description: String?,
    override val bitmap: Bitmap
) : Image {
    constructor(imageInfo: ImageInfo, bitmap: Bitmap) : this(
        url = imageInfo.smallUrl,
        bigVersionUrl = imageInfo.bigUrl,
        description = imageInfo.description,
        bitmap = bitmap
    )

    fun getImageInfo(): ImageInfo =
        ImageInfo(
            smallUrl = url,
            bigUrl = bigVersionUrl,
            description = description
        )
}

@Parcelize
class BigImage(
    override val url: String,
    override val description: String?,
    override val bitmap: Bitmap
) : Image {
    constructor(smallVersion: SmallImage, bitmap: Bitmap) : this(
        url = smallVersion.bigVersionUrl,
        description = smallVersion.description,
        bitmap = bitmap
    )
}

fun List<ImageInfo>.convertToSmallImageList(logKey: String = "Image") =
    mapNotNull { info ->
        val bitmap: Bitmap? = Cache.simpleCathe[info.smallUrl]
        if (bitmap == null) {
            Log.e(logKey, "Image with url `${info.smallUrl}` not in cache")
            null
        } else {
            SmallImage(info, bitmap)
        }
    }