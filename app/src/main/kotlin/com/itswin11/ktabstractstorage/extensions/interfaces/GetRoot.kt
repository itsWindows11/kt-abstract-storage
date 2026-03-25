package com.itswin11.ktabstractstorage.extensions.interfaces

import com.itswin11.ktabstractstorage.Folder
import com.itswin11.ktabstractstorage.StorableChild

/**
 * Provides a fast-path for retrieving the root folder for a [StorableChild].
 */
interface GetRoot : StorableChild {
    /**
     * Retrieves the root folder, if available.
     */
    suspend fun getRootAsync(): Folder?
}

