package com.fabiobassi.famigliab.file

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipUtils {
    fun zipFolder(sourceFolder: File, zipFile: File): Boolean {
        return try {
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                zipFile(sourceFolder, sourceFolder.name, zos)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun zipFile(fileToZip: File, fileName: String, zos: ZipOutputStream) {
        if (fileToZip.isHidden) {
            return
        }
        if (fileToZip.isDirectory) {
            val entryName = if (fileName.endsWith("/")) fileName else "$fileName/"
            zos.putNextEntry(ZipEntry(entryName))
            zos.closeEntry()

            val children = fileToZip.listFiles()
            if (children != null) {
                for (childFile in children) {
                    zipFile(childFile, entryName + childFile.name, zos)
                }
            }
            return
        }
        fileToZip.inputStream().use { fis ->
            val zipEntry = ZipEntry(fileName)
            zos.putNextEntry(zipEntry)
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }
}
