package com.sanryoo.shopping.feature.util

import android.content.ContentResolver
import android.net.Uri

fun getFileExtension(uri: Uri, contentResolver: ContentResolver): String? {
    return contentResolver.getType(uri)?.substringAfterLast("/")
}