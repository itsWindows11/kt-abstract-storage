package com.itswin11.ktabstractstorage.extensions

import com.itswin11.ktabstractstorage.File
import com.itswin11.ktabstractstorage.enums.FileAccessMode
import com.itswin11.ktabstractstorage.streams.UnifiedStream

/**
 * Opens this file for read access.
 */
suspend fun File.openReadAsync(): UnifiedStream = openStreamAsync(FileAccessMode.READ)

/**
 * Opens this file for write access.
 */
suspend fun File.openWriteAsync(): UnifiedStream = openStreamAsync(FileAccessMode.WRITE)

/**
 * Opens this file for read/write access.
 */
suspend fun File.openReadWriteAsync(): UnifiedStream = openStreamAsync(FileAccessMode.READ_AND_WRITE)

