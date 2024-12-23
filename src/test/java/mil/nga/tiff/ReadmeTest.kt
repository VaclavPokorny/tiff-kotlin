package mil.nga.tiff

import mil.nga.tiff.TiffTestUtils.createFieldTypeArray
import mil.nga.tiff.TiffTestUtils.createSampleValues
import mil.nga.tiff.field.DefaultTagDictionary
import mil.nga.tiff.field.type.FloatField
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.field.type.enumeration.Compression
import mil.nga.tiff.field.type.enumeration.PhotometricInterpretation
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.internal.rasters.Rasters
import mil.nga.tiff.util.*
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.ByteOrder
import java.util.*

/**
 * README example tests
 */
class ReadmeTest {

    /**
     * Test read
     *
     * @throws IOException upon error
     */
    @Test
    @Throws(IOException::class)
    fun testWriteAndRead() {
        testRead(testWrite())
    }

    /**
     * Test read
     *
     * @param input input bytes
     * @throws IOException upon error
     */
    @Throws(IOException::class)
    fun testRead(input: ByteArray) {
        // File input = ...
        // InputStream input = ...
        // byte[] input = ...
        // ByteReader input = ...

        val tiffImage = Tiff
            .create()
            .read()
            .fromByteArray(input)

        val directories = tiffImage.fileDirectories
        val directory = directories[0]
        directory.readRasters()
    }

    /**
     * Test write
     *
     * @return bytes
     * @throws IOException
     * upon error
     */
    @Throws(IOException::class)
    fun testWrite(): ByteArray {
        val width = 256
        val height = 256
        val samplesPerPixel = 1
        @Suppress("UNCHECKED_CAST") val fieldType = FloatField as NumericFieldType<Number>
        val bitsPerSample = fieldType.metadata().bytesPerSample * 8

        val fieldTypes = createFieldTypeArray(samplesPerPixel, fieldType)

        val order = ByteOrder.LITTLE_ENDIAN
        val sampleValues = createSampleValues(width, height, fieldTypes, order)
        val rasters = Rasters.create(width, height, fieldTypes, sampleValues, null)

        val rowsPerStrip = rasters.calculateRowsPerStrip(
            PlanarConfiguration.CHUNKY
        )

        val directory = FileDirectory.create(emptySet(), null, false, DefaultTagDictionary, rasters)
        directory.setImageWidth(width)
        directory.imageHeight = height
        directory.setBitsPerSample(bitsPerSample)
        directory.compression = Compression.NO
        directory.photometricInterpretation = PhotometricInterpretation.BLACK_IS_ZERO
        directory.setSamplesPerPixel(samplesPerPixel)
        directory.setRowsPerStrip(rowsPerStrip)
        directory.planarConfiguration = PlanarConfiguration.CHUNKY
        directory.setSampleFormat(SampleFormat.FLOAT)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelValue = 1.0f // any pixel value
                rasters.setFirstPixelSample(x, y, pixelValue)
            }
        }

        val tiffImage = TIFFImage(listOf(directory), ByteOrder.LITTLE_ENDIAN)
        val bytes = Tiff
            .create()
            .write(tiffImage)
            .toByteArray()

        // or
        // File file = ...
        // TiffWriter.writeTiff(file, tiffImage);
        return bytes
    }
}
