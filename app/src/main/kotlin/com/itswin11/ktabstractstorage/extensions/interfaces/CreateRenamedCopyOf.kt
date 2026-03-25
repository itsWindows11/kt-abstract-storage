package com.itswin11.ktabstractstorage.extensions.interfaces

import com.itswin11.ktabstractstorage.ChildFile
import com.itswin11.ktabstractstorage.File
import com.itswin11.ktabstractstorage.ModifiableFolder

/**
 * A delegate that provides a fallback for [CreateRenamedCopyOf.createCopyOfAsync].
 */
typealias CreateRenamedCopyOfDelegate = suspend (
    destination: ModifiableFolder,
    fileToCopy: File,
    overwrite: Boolean,
    newName: String,
) -> ChildFile

/**
 * Provides a fast-path for copying and renaming files into a folder.
 */
interface CreateRenamedCopyOf : CreateCopyOf {
    suspend fun createCopyOfAsync(
        fileToCopy: File,
        overwrite: Boolean,
        newName: String,
        fallback: CreateRenamedCopyOfDelegate,
    ): ChildFile
}

