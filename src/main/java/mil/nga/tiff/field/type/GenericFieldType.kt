package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.tag.FieldTagType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException

interface GenericFieldType<T> {
    /**
     * Get the internal entry values
     *
     * @param reader    byte reader
     * @param typeCount type count
     * @return values
     */
    fun readDirectoryEntryValues(reader: ByteReader, typeCount: Long): List<T>

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @param fieldTag entry tag
     * @param typeCount count of values
     * @param values actual values to write
     * @return bytes written
     * @throws IOException IO exception
     */
    @Throws(IOException::class)
    fun writeDirectoryEntryValues(writer: ByteWriter, fieldTag: FieldTagType?, typeCount: Long, values: List<T>): Int


    /**
     * Gathers field type metadata and returns a record with them
     *
     * @return metadata record
     */
    fun metadata(): FieldType {
        return javaClass.getAnnotation(FieldType::class.java)
    }

}
