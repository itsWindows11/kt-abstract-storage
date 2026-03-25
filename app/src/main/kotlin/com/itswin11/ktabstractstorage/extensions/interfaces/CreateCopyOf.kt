package com.itswin11.ktabstractstorage.extensions.interfaces

import com.itswin11.ktabstractstorage.ChildFile
import com.itswin11.ktabstractstorage.File
import com.itswin11.ktabstractstorage.ModifiableFolder

/**
 * A delegate that provides a fallback for [CreateCopyOf.createCopyOfAsync].
 */
typealias CreateCopyOfDelegate = suspend (
    destination: ModifiableFolder,
    fileToCopy: File,
    overwrite: Boolean,
) -> ChildFile

/**
 * Provides a fast-path for copying files into a folder.
 */
interface CreateCopyOf : ModifiableFolder {
    suspend fun createCopyOfAsync(
        fileToCopy: File,
        overwrite: Boolean,
        fallback: CreateCopyOfDelegate,
    ): ChildFile
}

