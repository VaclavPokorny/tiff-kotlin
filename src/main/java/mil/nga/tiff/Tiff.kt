package mil.nga.tiff

import mil.nga.tiff.field.DefaultFieldTypeDictionary
import mil.nga.tiff.field.FieldTypeDictionary
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.internal.TiffImageReader
import mil.nga.tiff.internal.TiffImageWriter
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import mil.nga.tiff.io.IOUtils
import mil.nga.tiff.util.TiffByteOrder
import mil.nga.tiff.util.TiffException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * TIFF Fluent API
 */
class Tiff {
    /**
     * Create a TIFF image read query
     */
    fun read(): Reader {
        return Reader(true, DefaultFieldTypeDictionary())
    }

    /**
     * Create a TIFF image read query
     *
     * @param useCache       true to use caching for reading operations
     * @param typeDictionary field type dictionary
     */
    fun read(useCache: Boolean, typeDictionary: FieldTypeDictionary): Reader {
        return Reader(useCache, typeDictionary)
    }

    /**
     * Create a TIFF image write query
     *
     * @param image TIFF image
     */
    fun write(image: TIFFImage): Writer {
        return Writer(image)
    }

    /**
     * TIFF reader
     *
     * @author osbornb
     */
    class Reader(private val useCache: Boolean, private val typeDictionary: FieldTypeDictionary) {
        /**
         * Read a TIFF from a file
         *
         * @param file TIFF file
         * @return TIFF image
         * @throws IOException upon failure to read
         */
        @Throws(IOException::class)
        fun fromFile(file: File): TIFFImage {
            val bytes = IOUtils.fileBytes(file)
            return fromByteArray(bytes)
        }

        /**
         * Read a TIFF from an input stream
         *
         * @param stream TIFF input stream
         * @return TIFF image
         * @throws IOException upon failure to read
         */
        @Throws(IOException::class)
        fun fromInputStream(stream: InputStream): TIFFImage {
            val bytes = IOUtils.streamBytes(stream)
            return fromByteArray(bytes)
        }

        /**
         * Read a TIFF from the bytes
         *
         * @param bytes TIFF bytes
         * @return TIFF image
         */
        fun fromByteArray(bytes: ByteArray): TIFFImage {
            val byteOrder = TiffByteOrder.determineFromData(bytes)
            val reader = ByteReader(bytes, byteOrder)
            val imageReader = TiffImageReader(reader, typeDictionary)
            return imageReader.readTiff(useCache)
        }
    }

    /**
     * TIFF Writer.
     *
     *
     * For a striped TIFF, the [FileDirectory.setStripOffsets] and
     * [FileDirectory.setStripByteCounts] methods are automatically set
     * or adjusted based upon attributes including:
     * [FileDirectory.getRowsPerStrip],
     * [FileDirectory.getImageHeight],
     * [FileDirectory.getPlanarConfiguration], and
     * [FileDirectory.getSamplesPerPixel].
     *
     *
     * The [Rasters.calculateRowsPerStrip] and
     * [Rasters.calculateRowsPerStrip] methods provide a mechanism
     * for determining a [FileDirectory.getRowsPerStrip] setting.
     */
    class Writer internal constructor(private val image: TIFFImage) {
        /**
         * Write a TIFF to a file
         *
         * @param file file to create
         * @throws TiffException upon failure to write
         */
        fun toFile(file: File) {
            try {
                ByteWriter(image.byteOrder).use { writer ->
                    val imageWriter = TiffImageWriter(writer)
                    imageWriter.write(image)
                    val bytes = writer.bytes()
                    val inputStream = ByteArrayInputStream(bytes)
                    IOUtils.copyStream(inputStream, file)
                }
            } catch (e: IOException) {
                throw TiffException(e)
            }
        }

        /**
         * Write a TIFF to bytes
         *
         * @return tiff bytes
         * @throws TiffException upon failure to write
         */
        fun toByteArray(): ByteArray {
            try {
                ByteWriter(image.byteOrder).use { writer ->
                    val imageWriter = TiffImageWriter(writer)
                    imageWriter.write(image)
                    return writer.bytes()
                }
            } catch (e: IOException) {
                throw TiffException(e)
            }
        }
    }

    companion object {
        fun create(): Tiff {
            return Tiff()
        }
    }
}
