package com.itswin11.ktabstractstorage

import com.itswin11.ktabstractstorage.enums.StorableType
import com.itswin11.ktabstractstorage.extensions.getFirstByNameAsync
import com.itswin11.ktabstractstorage.extensions.readTextAsync
import com.itswin11.ktabstractstorage.extensions.writeTextAsync
import com.itswin11.ktabstractstorage.streams.MemoryStream
import com.itswin11.ktabstractstorage.system.SystemFile
import com.itswin11.ktabstractstorage.ziparchive.ZipArchiveFolder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ZipArchiveFolderTests {
    @Test
    fun in_memory_archive_supports_nested_items_and_file_content() = runTest {
        val stream = MemoryStream()
        val root = ZipArchiveFolder(stream, id = "zip:test", name = "test.zip")

        val subA = root.createFolderAsync("subA") as ModifiableFolder
        val fileA = subA.createFileAsync("fileA.txt")
        fileA.writeTextAsync("hello zip")

        val subB = root.createFolderAsync("subB")
        assertIs<ChildFolder>(subB)

        val rootItems = root.getItemsAsync(StorableType.ALL).toList().map { it.name }
        assertEquals(listOf("subA", "subB"), rootItems)

        val fileFromFolder = (root.getFirstByNameAsync("subA") as Folder)
            .getFirstByNameAsync("fileA.txt") as File

        assertEquals("hello zip", fileFromFolder.readTextAsync())
    }

    @Test
    fun on_disk_archive_persists_updates() = runTest {
        val tempZip = Files.createTempFile("kt-abstract-storage", ".zip")
        try {
            val file = SystemFile(tempZip)
            val archive = ZipArchiveFolder(file, id = "zip:disk", name = tempZip.fileName.toString())
            val entry = archive.createFileAsync("persisted.txt")
            entry.writeTextAsync("persist me")

            val reopened = ZipArchiveFolder(SystemFile(tempZip), id = "zip:disk", name = tempZip.fileName.toString())
            val reopenedEntry = reopened.getFirstByNameAsync("persisted.txt") as File

            assertEquals("persist me", reopenedEntry.readTextAsync())
        } finally {
            tempZip.deleteIfExists()
        }
    }
}

