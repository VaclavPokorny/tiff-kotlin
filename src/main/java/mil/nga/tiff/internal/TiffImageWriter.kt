package mil.nga.tiff.internal

import mil.nga.tiff.compression.CompressionEncoder
import mil.nga.tiff.field.type.enumeration.Compression
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.io.ByteWriter
import mil.nga.tiff.util.TiffByteOrder
import mil.nga.tiff.util.TiffConstants
import mil.nga.tiff.util.TiffException
import java.io.IOException
import java.nio.ByteOrder
import java.util.*
import kotlin.math.min

class TiffImageWriter(private val writer: ByteWriter) {
    /**
     * Write a TIFF to a byte writer
     *
     * @param tiffImage TIFF image
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun write(tiffImage: TIFFImage) {
        // Write the byte order (bytes 0-1)

        val byteOrderId = TiffByteOrder.findByByteOrder(writer.byteOrder).id
        writer.writeString(byteOrderId)

        // Write the TIFF file identifier (bytes 2-3)
        writer.writeUnsignedShort(TiffConstants.FILE_IDENTIFIER)

        // Write the first IFD offset (bytes 4-7), set to start right away at
        // byte 8
        writer.writeUnsignedInt(TiffConstants.HEADER_BYTES.toLong())

        // Write each file internal
        for (i in tiffImage.fileDirectories.indices) {
            val isLast = (i + 1) == tiffImage.fileDirectories.size
            val fileDirectory = tiffImage.fileDirectories[i]
            write(fileDirectory, isLast)
        }
    }

    @Throws(IOException::class)
    private fun write(fileDirectory: FileDirectory, isLast: Boolean) {
        // Populate strip entries with placeholder values so the sizes come
        // out correctly
        populateRasterEntries(fileDirectory)

        // Track of the starting byte of this internal
        val startOfDirectory = writer.size()
        val afterDirectory = startOfDirectory + fileDirectory.size()
        val afterValues = startOfDirectory + fileDirectory.sizeWithValues()

        // Write the number of internal entries
        writer.writeUnsignedShort(fileDirectory.numEntries())

        val entryValues: MutableList<FileDirectoryEntry<*>> = ArrayList()

        // Byte to write the next values
        var nextByte = afterDirectory

        val valueBytesCheck: MutableList<Long> = ArrayList()

        // Write the raster bytes to temporary storage
        if (fileDirectory.isTiled) {
            throw TiffException("Tiled images are not supported")
        }

        // Create the raster bytes, written to the stream later
        val rastersBytes = writeRasters(writer.byteOrder, fileDirectory, afterValues)

        // Write each entry
        for (entry in fileDirectory.data.fieldTagTypeMapping.values) {
            writer.writeUnsignedShort(entry.fieldTag.id)
            writer.writeUnsignedShort(entry.fieldType.metadata().id)
            writer.writeUnsignedInt(entry.typeCount)
            val valueBytes = entry.valueBytes()
            if (valueBytes > 4) {
                // Write the value offset
                entryValues.add(entry)
                writer.writeUnsignedInt(nextByte)
                valueBytesCheck.add(nextByte)
                nextByte += entry.sizeOfValues()
            } else {
                // Write the value in the inline 4 byte space, left aligned
                val bytesWritten = entry.write(writer)
                if (bytesWritten.toLong() != valueBytes) {
                    throw TiffException("Unexpected bytes written. Expected: $valueBytes, Actual: $bytesWritten")
                }
                writer.writeFillerBytes(4 - valueBytes)
            }
        }

        if (isLast) {
            // Write 0's since there are not more file directories
            writer.writeFillerBytes(4)
        } else {
            // Write the start address of the next file internal
            val nextFileDirectory = afterValues + rastersBytes.size
            writer.writeUnsignedInt(nextFileDirectory)
        }

        // Write the external entry values
        for (entryIndex in entryValues.indices) {
            val entry = entryValues[entryIndex]
            val entryValuesByte = valueBytesCheck[entryIndex]
            if (entryValuesByte != writer.size().toLong()) {
                throw TiffException("Entry values byte does not match the write location. Entry Values Byte: " + entryValuesByte + ", Current Byte: " + writer.size())
            }
            val bytesWritten = entry.write(writer)
            val valueBytes = entry.valueBytes()
            if (bytesWritten.toLong() != valueBytes) {
                throw TiffException("Unexpected bytes written. Expected: $valueBytes, Actual: $bytesWritten")
            }
        }

        // Write the image bytes
        writer.writeBytes(rastersBytes)
    }

    /**
     * Populate the raster entry values with placeholder values for correct size
     * calculations
     *
     * @param fileDirectory file internal
     */
    private fun populateRasterEntries(fileDirectory: FileDirectory) {
        fileDirectory.writeRasters ?: throw TiffException("File Directory Writer Rasters is required to create a TIFF")

        // Populate the raster entries
        if (!fileDirectory.isTiled) {
            populateStripEntries(fileDirectory)
        } else {
            throw TiffException("Tiled images are not supported")
        }
    }

    /**
     * Populate the strip entries with placeholder values
     *
     * @param fileDirectory file internal
     */
    private fun populateStripEntries(fileDirectory: FileDirectory) {
        val rowsPerStrip = fileDirectory.rowsPerStrip.toInt()
        val imageHeight = fileDirectory.imageHeight.toInt()
        var strips = (imageHeight + rowsPerStrip - 1) / rowsPerStrip
        if (fileDirectory.planarConfiguration == PlanarConfiguration.PLANAR) {
            strips *= fileDirectory.samplesPerPixel
        }

        fileDirectory.setStripOffsetsAsLongs(ArrayList(Collections.nCopies(strips, 0L)))
        fileDirectory.setStripByteCounts(ArrayList(Collections.nCopies(strips, 0)))
    }

    /**
     * Write the rasters as bytes
     *
     * @param byteOrder     byte order
     * @param fileDirectory file internal
     * @param offset        byte offset
     * @return rasters bytes
     * @throws IOException IO exception
     */
    @Throws(IOException::class)
    private fun writeRasters(byteOrder: ByteOrder, fileDirectory: FileDirectory, offset: Long): ByteArray {
        fileDirectory.writeRasters ?: throw TiffException("File Directory Writer Rasters is required to create a TIFF")

        // Get the compression encoder
        val encoder = Compression.getEncoder(fileDirectory.compression)

        // Byte writer to write the raster
        val writer = ByteWriter(byteOrder)

        // Write the rasters
        if (!fileDirectory.isTiled) {
            writeStripRasters(writer, fileDirectory, offset, encoder)
        } else {
            throw TiffException("Tiled images are not supported")
        }

        // Return the rasters bytes
        val bytes = writer.bytes()
        writer.close()

        return bytes
    }

    /**
     * Write the rasters as bytes
     *
     * @param writer        byte writer
     * @param fileDirectory file internal
     * @param offset        byte offset
     * @param encoder       compression encoder
     * @throws IOException IO exception
     */
    @Throws(IOException::class)
    private fun writeStripRasters(
        writer: ByteWriter,
        fileDirectory: FileDirectory,
        offset: Long,
        encoder: CompressionEncoder
    ) {
        var currentOffset = offset
        val rasters = fileDirectory.writeRasters

        // Get the row and strip counts
        val rowsPerStrip = fileDirectory.rowsPerStrip.toInt()
        val maxY = fileDirectory.imageHeight.toInt()
        val stripsPerSample = (maxY + rowsPerStrip - 1) / rowsPerStrip
        var strips = stripsPerSample
        if (fileDirectory.planarConfiguration == PlanarConfiguration.PLANAR) {
            strips *= fileDirectory.samplesPerPixel
        }

        // Build the strip offsets and byte counts
        val stripOffsets: MutableList<Long> = ArrayList()
        val stripByteCounts: MutableList<Int> = ArrayList()

        // Write each strip
        for (strip in 0 until strips) {
            var startingY: Int
            var sample: Int? = null
            if (fileDirectory.planarConfiguration == PlanarConfiguration.PLANAR) {
                sample = strip / stripsPerSample
                startingY = (strip % stripsPerSample) * rowsPerStrip
            } else {
                startingY = strip * rowsPerStrip
            }

            // Write the strip of bytes
            val stripWriter = ByteWriter(writer.byteOrder)

            val endingY = min((startingY + rowsPerStrip).toDouble(), maxY.toDouble()).toInt()
            for (y in startingY until endingY) {
                // Get the row bytes and encode if needed
                var rowBytes: ByteArray?
                rowBytes = if (sample != null) {
                    rasters.getSampleRow(y, sample, writer.byteOrder)
                } else {
                    rasters.getPixelRow(y, writer.byteOrder)
                }

                if (encoder.rowEncoding()) {
                    rowBytes = encoder.encode(rowBytes, writer.byteOrder)
                }

                // Write the row
                stripWriter.writeBytes(rowBytes)
            }

            // Get the strip bytes and encode if needed
            var stripBytes = stripWriter.bytes()
            stripWriter.close()
            if (!encoder.rowEncoding()) {
                stripBytes = encoder.encode(stripBytes, writer.byteOrder)
            }

            // Write the strip bytes
            writer.writeBytes(stripBytes)

            // Add the strip byte count
            val bytesWritten = stripBytes.size
            stripByteCounts.add(bytesWritten)

            // Add the strip offset
            stripOffsets.add(currentOffset)
            currentOffset += bytesWritten.toLong()
        }

        // Set the strip offsets and byte counts
        fileDirectory.setStripOffsetsAsLongs(stripOffsets)
        fileDirectory.setStripByteCounts(stripByteCounts)
    }
}
