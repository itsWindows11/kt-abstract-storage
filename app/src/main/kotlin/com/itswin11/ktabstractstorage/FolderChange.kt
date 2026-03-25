package com.itswin11.ktabstractstorage

import com.itswin11.ktabstractstorage.enums.FolderChangeType

/**
 * Represents a single change observed by a [FolderWatcher].
 *
 * @param type The type of change that occurred.
 * @param item The item that was added, removed, or updated.
 */
data class FolderChange(
    val type: FolderChangeType,
    val item: Storable,
)
