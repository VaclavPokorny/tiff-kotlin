package mil.nga.tiff.io

import java.io.*

/**
 * Input / Output utility methods
 *
 * @author osbornb
 */
object IOUtils {
    /**
     * Copy stream buffer chunk size in bytes
     *
     * @since 2.0.3
     */
    @JvmField
    var COPY_BUFFER_SIZE: Int = 8192

    /**
     * Copy a file to a file location
     *
     * @param copyFrom file to copy
     * @param copyTo   file to copy to
     * @throws IOException upon failure to copy file
     */
    @Throws(IOException::class)
    fun copyFile(copyFrom: File, copyTo: File) {
        val from: InputStream = FileInputStream(copyFrom)
        val to: OutputStream = FileOutputStream(copyTo)

        copyStream(from, to)
    }

    /**
     * Copy an input stream to a file location
     *
     * @param copyFrom stream to copy
     * @param copyTo   file to copy to
     * @throws IOException upon failure to copy the stream
     */
    @Throws(IOException::class)
    fun copyStream(copyFrom: InputStream, copyTo: File) {
        val to: OutputStream = FileOutputStream(copyTo)

        copyStream(copyFrom, to)
    }

    /**
     * Get the file bytes
     *
     * @param file file
     * @return bytes
     * @throws IOException upon failure to read the file
     */
    @Throws(IOException::class)
    fun fileBytes(file: File): ByteArray {
        val fis = FileInputStream(file)

        return streamBytes(fis)
    }

    /**
     * Get the stream bytes
     *
     * @param stream input stream
     * @return bytes
     * @throws IOException upon failure to read stream bytes
     */
    @Throws(IOException::class)
    fun streamBytes(stream: InputStream): ByteArray {
        val bytes = ByteArrayOutputStream()

        copyStream(stream, bytes)

        return bytes.toByteArray()
    }

    /**
     * Copy an input stream to an output stream
     *
     * @param copyFrom stream to copy
     * @param copyTo   stream to copy to
     * @throws IOException upon failure to copy stream
     */
    @Throws(IOException::class)
    fun copyStream(copyFrom: InputStream, copyTo: OutputStream) {
        val buffer = ByteArray(COPY_BUFFER_SIZE)
        var length: Int
        while ((copyFrom.read(buffer).also { length = it }) > 0) {
            copyTo.write(buffer, 0, length)
        }

        copyTo.flush()
        copyTo.close()
        copyFrom.close()
    }
}
