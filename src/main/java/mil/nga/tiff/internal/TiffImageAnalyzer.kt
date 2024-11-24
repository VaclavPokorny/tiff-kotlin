package mil.nga.tiff.internal

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.FieldTagType
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface TiffImageAnalyzer {
    fun describeImage(byteOrder: ByteOrder, totalSize: Long, directoriesCount: Int)
    fun commenceDirectory(size: Long, stats: DirectoryStats)
    fun describeRasters(width: Int, height: Int, fields: List<FieldType>, pixelSize: Int, samples: Array<ByteBuffer>?, interleave: ByteBuffer?)
    fun describeDirectoryEntry(fieldTag: FieldTagType?, fieldTagId: Int, type: FieldType, typeCount: Long, values: String)
}
