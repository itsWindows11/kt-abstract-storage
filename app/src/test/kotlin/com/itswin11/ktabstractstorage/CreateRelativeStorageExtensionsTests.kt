package com.itswin11.ktabstractstorage

import com.itswin11.ktabstractstorage.enums.StorableType
import com.itswin11.ktabstractstorage.extensions.createAlongRelativePathAsync
import com.itswin11.ktabstractstorage.extensions.createFileByRelativePathAsync
import com.itswin11.ktabstractstorage.extensions.createFolderByRelativePathAsync
import com.itswin11.ktabstractstorage.extensions.createFoldersAlongRelativePathAsync
import com.itswin11.ktabstractstorage.extensions.getFirstByNameAsync
import com.itswin11.ktabstractstorage.memory.MemoryFolder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreateRelativeStorageExtensionsTests {
    private suspend fun createRoot(): MemoryFolder {
        val root = MemoryFolder("root")
        val folderA = root.createFolderAsync("folderA") as ModifiableFolder
        folderA.createFolderAsync("subA")
        folderA.createFileAsync("fileA.txt")
        root.createFileAsync("fileRoot.txt")
        return root
    }

    @Test
    fun create_relative_folder_from_folder() = runTest {
        val root = createRoot()

        val final = root.createFolderByRelativePathAsync("folderA/subB")

        assertEquals("subB", final.name)
        val parent = final.getParentAsync()
        assertNotNull(parent)
        assertEquals("folderA", parent.name)
    }

    @Test
    fun create_relative_folders_yields_in_order_ignores_file_like_tail() = runTest {
        val root = createRoot()

        val yielded = root.createFoldersAlongRelativePathAsync("folderA/subC/new.txt").toList().map { it.name }

        assertContentEquals(listOf("folderA", "subC"), yielded)
    }

    @Test
    fun create_relative_folder_from_file_with_parent_traversal() = runTest {
        val root = createRoot()
        val file = root.getFirstByNameAsync("fileRoot.txt") as ChildFile

        val final = file.createFolderByRelativePathAsync("../created/chain")

        assertEquals("chain", final.name)
        val parent = final.getParentAsync()
        assertNotNull(parent)
        assertEquals("created", parent.name)
    }

    @Test
    fun create_by_relative_path_creates_file_and_parents() = runTest {
        val root = createRoot()

        val file = root.createFileByRelativePathAsync("nested/path/newfile.txt")

        assertEquals("newfile.txt", file.name)
        val parent = file.getParentAsync()
        assertNotNull(parent)
        assertEquals("path", parent.name)
    }

    @Test
    fun create_file_by_relative_path_rejects_trailing_slash() = runTest {
        val root = createRoot()

        assertFailsWith<IllegalArgumentException> {
            root.createFileByRelativePathAsync("a/b/c/")
        }
    }

    @Test
    fun create_along_relative_path_yields_parents_then_file() = runTest {
        val root = createRoot()

        val yielded = root.createAlongRelativePathAsync("p/q/r.txt", StorableType.FILE).toList().map { it.name }

        assertContentEquals(listOf("p", "q", "r.txt"), yielded)
    }
}

