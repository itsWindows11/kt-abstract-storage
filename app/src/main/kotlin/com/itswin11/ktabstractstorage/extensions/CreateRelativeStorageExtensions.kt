package com.itswin11.ktabstractstorage.extensions

import com.itswin11.ktabstractstorage.ChildFile
import com.itswin11.ktabstractstorage.ChildFolder
import com.itswin11.ktabstractstorage.Folder
import com.itswin11.ktabstractstorage.ModifiableFolder
import com.itswin11.ktabstractstorage.Storable
import com.itswin11.ktabstractstorage.StorableChild
import com.itswin11.ktabstractstorage.enums.StorableType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Creates an item by [relativePath] starting from this folder.
 */
suspend fun Folder.createByRelativePathAsync(
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean = false,
): Storable = createByRelativePathCoreAsync(this, relativePath, targetType, overwrite)

/**
 * Creates an item by [relativePath] starting from this child file.
 */
suspend fun ChildFile.createByRelativePathAsync(
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean = false,
): Storable = createByRelativePathCoreAsync(this, relativePath, targetType, overwrite)

suspend fun Folder.createFolderByRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): ChildFolder = createByRelativePathAsync(relativePath, StorableType.FOLDER, overwrite) as ChildFolder

suspend fun ChildFile.createFolderByRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): ChildFolder = createByRelativePathAsync(relativePath, StorableType.FOLDER, overwrite) as ChildFolder

suspend fun Folder.createFileByRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): ChildFile = createByRelativePathAsync(relativePath, StorableType.FILE, overwrite) as ChildFile

suspend fun ChildFile.createFileByRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): ChildFile = createByRelativePathAsync(relativePath, StorableType.FILE, overwrite) as ChildFile

fun Folder.createFoldersAlongRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): Flow<Folder> = flow {
    createAlongRelativePathCoreAsync(this@createFoldersAlongRelativePathAsync, relativePath, StorableType.FOLDER, overwrite)
        .collect { if (it is Folder) emit(it) }
}

fun ChildFile.createFoldersAlongRelativePathAsync(
    relativePath: String,
    overwrite: Boolean = false,
): Flow<Folder> = flow {
    createAlongRelativePathCoreAsync(this@createFoldersAlongRelativePathAsync, relativePath, StorableType.FOLDER, overwrite)
        .collect { if (it is Folder) emit(it) }
}

fun Folder.createAlongRelativePathAsync(
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean = false,
): Flow<Storable> = createAlongRelativePathCoreAsync(this, relativePath, targetType, overwrite)

fun ChildFile.createAlongRelativePathAsync(
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean = false,
): Flow<Storable> = createAlongRelativePathCoreAsync(this, relativePath, targetType, overwrite)

private suspend fun createByRelativePathCoreAsync(
    from: Storable,
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean,
): Storable {
    var current = from
    val normalized = relativePath.replace('\\', '/')
    val parts = normalized.split('/').map { it.trim() }.filter { it.isNotEmpty() }

    require(targetType == StorableType.FILE || targetType == StorableType.FOLDER) {
        "Only FILE and FOLDER target types are supported."
    }

    if (targetType == StorableType.FILE) {
        require(!normalized.endsWith('/')) { "File target cannot end with '/'." }
        require(parts.isNotEmpty()) { "File target requires a non-empty path." }
    }

    val lastIndex = if (targetType == StorableType.FILE) parts.lastIndex else parts.lastIndex

    for (index in parts.indices) {
        val segment = parts[index]
        val isLast = index == lastIndex

        if (segment == ".") {
            continue
        }

        if (segment == "..") {
            val child = current as? StorableChild
                ?: throw IllegalArgumentException("A parent folder was requested, but '${current.name}' is not addressable.")
            current = child.getParentAsync()
                ?: throw IllegalArgumentException("A parent folder was requested, but '${current.name}' did not return a parent.")
            continue
        }

        val folder = current as? ModifiableFolder
            ?: throw IllegalArgumentException("'${current.name}' is not a modifiable folder and cannot contain '$segment'.")

        current = if (targetType == StorableType.FILE && isLast) {
            folder.createFileAsync(segment, overwrite)
        } else {
            folder.createFolderAsync(segment, overwrite)
        }
    }

    return current
}

private fun createAlongRelativePathCoreAsync(
    from: Storable,
    relativePath: String,
    targetType: StorableType,
    overwrite: Boolean,
): Flow<Storable> = flow {
    var current = from
    val normalized = relativePath.replace('\\', '/')
    val parts = normalized.split('/').map { it.trim() }.filter { it.isNotEmpty() }

    require(targetType == StorableType.FILE || targetType == StorableType.FOLDER) {
        "Only FILE and FOLDER target types are supported."
    }

    if (targetType == StorableType.FILE) {
        require(!normalized.endsWith('/')) { "File target cannot end with '/'." }
        require(parts.isNotEmpty()) { "File target requires a non-empty path." }
    }

    for (index in parts.indices) {
        val segment = parts[index]
        val isLast = index == parts.lastIndex

        if (segment == ".") {
            continue
        }

        if (segment == "..") {
            val child = current as? StorableChild
                ?: throw IllegalArgumentException("A parent folder was requested, but '${current.name}' is not addressable.")
            val parent = child.getParentAsync()
                ?: throw IllegalArgumentException("A parent folder was requested, but '${current.name}' did not return a parent.")
            current = parent
            emit(parent)
            continue
        }

        val folder = current as? ModifiableFolder
            ?: throw IllegalArgumentException("'${current.name}' is not a modifiable folder and cannot contain '$segment'.")

        val next = if (targetType == StorableType.FILE && isLast) {
            folder.createFileAsync(segment, overwrite)
        } else {
            folder.createFolderAsync(segment, overwrite)
        }

        current = next
        emit(next)
    }

    if (parts.isEmpty() && current is Folder) {
        emit(current)
    }
}

