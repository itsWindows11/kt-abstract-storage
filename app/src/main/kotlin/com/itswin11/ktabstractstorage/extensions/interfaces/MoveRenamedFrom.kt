package com.itswin11.ktabstractstorage.extensions.interfaces

import com.itswin11.ktabstractstorage.ChildFile
import com.itswin11.ktabstractstorage.ModifiableFolder

/**
 * A delegate that provides a fallback for [MoveRenamedFrom.moveFromAsync].
 */
typealias MoveRenamedFromDelegate = suspend (
    destination: ModifiableFolder,
    file: ChildFile,
    source: ModifiableFolder,
    overwrite: Boolean,
    newName: String,
) -> ChildFile

/**
 * Provides a fast-path for moving and renaming files between folders.
 */
interface MoveRenamedFrom : MoveFrom {
    suspend fun moveFromAsync(
        fileToMove: ChildFile,
        source: ModifiableFolder,
        overwrite: Boolean,
        newName: String,
        fallback: MoveRenamedFromDelegate,
    ): ChildFile
}

