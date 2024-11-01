package mil.nga.tiff

import mil.nga.tiff.util.TiffConstants
import org.junit.jupiter.api.Test
import java.io.IOException

/**
 * README example tests
 *
 * @author osbornb
 */
class ReadmeTest {
    /**
     * Test read
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testWriteAndRead() {
        testRead(testWrite())
    }

    /**
     * Test read
     *
     * @param input
     * input bytes
     * @throws IOException
     * upon error
     */
    @Throws(IOException::class)
    fun testRead(input: ByteArray?) {
        // File input = ...
        // InputStream input = ...
        // byte[] input = ...
        // ByteReader input = ...

        val tiffImage = TiffReader.readTiff(input)
        val directories = tiffImage.fileDirectories
        val directory = directories[0]
        val rasters = directory.readRasters()
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
        val fieldType = FieldType.FLOAT
        val bitsPerSample = fieldType.bits

        val rasters = Rasters(
            width, height, samplesPerPixel,
            fieldType
        )

        val rowsPerStrip = rasters.calculateRowsPerStrip(
            TiffConstants.PLANAR_CONFIGURATION_CHUNKY
        )

        val directory = FileDirectory()
        directory.setImageWidth(width)
        directory.setImageHeight(height)
        directory.setBitsPerSample(bitsPerSample)
        directory.compression = TiffConstants.COMPRESSION_NO
        directory.photometricInterpretation = TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO
        directory.samplesPerPixel = samplesPerPixel
        directory.setRowsPerStrip(rowsPerStrip)
        directory.planarConfiguration = TiffConstants.PLANAR_CONFIGURATION_CHUNKY
        directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_FLOAT)
        directory.writeRasters = rasters

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelValue = 1.0f // any pixel value
                rasters.setFirstPixelSample(x, y, pixelValue)
            }
        }

        val tiffImage = TIFFImage()
        tiffImage.add(directory)
        val bytes = TiffWriter.writeTiffToBytes(tiffImage)

        // or
        // File file = ...
        // TiffWriter.writeTiff(file, tiffImage);
        return bytes
    }
}
