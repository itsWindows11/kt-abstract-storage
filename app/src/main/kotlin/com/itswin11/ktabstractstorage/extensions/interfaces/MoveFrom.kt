package com.itswin11.ktabstractstorage.extensions.interfaces

import com.itswin11.ktabstractstorage.ChildFile
import com.itswin11.ktabstractstorage.ModifiableFolder

/**
 * A delegate that provides a fallback for [MoveFrom.moveFromAsync].
 */
typealias MoveFromDelegate = suspend (
    destination: ModifiableFolder,
    file: ChildFile,
    source: ModifiableFolder,
    overwrite: Boolean,
) -> ChildFile

/**
 * Provides a fast-path for moving files between folders.
 */
interface MoveFrom : ModifiableFolder {
    suspend fun moveFromAsync(
        fileToMove: ChildFile,
        source: ModifiableFolder,
        overwrite: Boolean,
        fallback: MoveFromDelegate,
    ): ChildFile
}

