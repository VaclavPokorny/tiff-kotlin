package mil.nga.tiff

import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.internal.rasters.RasterTestUtils
import mil.nga.tiff.internal.rasters.Rasters
import mil.nga.tiff.util.TiffException
import org.junit.jupiter.api.Assertions
import java.io.File
import java.net.URISyntaxException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * TIFF Test Utility methods
 */
object TiffTestUtils {

    /**
     * Compare two TIFF Images
     *
     * @param tiffImage1 tiff image 1
     * @param tiffImage2 tiff image 2
     * @param exactType true if matching the exact data type
     * @param sameBitsPerSample true if should have the same bits per sample
     */
    @JvmStatic
    @JvmOverloads
    fun compareTIFFImages(
        tiffImage1: TIFFImage, tiffImage2: TIFFImage, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        Assertions.assertNotNull(tiffImage1)
        Assertions.assertNotNull(tiffImage2)
        Assertions.assertEquals(
            tiffImage1.fileDirectories.size, tiffImage2.fileDirectories.size
        )
        for (i in tiffImage1.fileDirectories.indices) {
            val fileDirectory1 = tiffImage1.fileDirectories[i]
            val fileDirectory2 = tiffImage2.fileDirectories[i]

            val sampleRasters1 = fileDirectory1.readRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory1, sampleRasters1
            )
            val sampleRasters2 = fileDirectory2.readRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory2, sampleRasters2
            )
            RasterTestUtils.compareRastersSampleValues(
                sampleRasters1, sampleRasters2, exactType, sameBitsPerSample
            )

            val interleaveRasters1 = fileDirectory1.readInterleavedRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory1, interleaveRasters1
            )
            val interleaveRasters2 = fileDirectory2.readInterleavedRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory2, interleaveRasters2
            )
            RasterTestUtils.compareRastersInterleaveValues(
                interleaveRasters1, interleaveRasters2, exactType, sameBitsPerSample
            )

            RasterTestUtils.compareRasters(
                fileDirectory1, sampleRasters1, fileDirectory2, interleaveRasters2, exactType, sameBitsPerSample
            )
            RasterTestUtils.compareRasters(
                fileDirectory1, interleaveRasters1, fileDirectory2, sampleRasters2, exactType, sameBitsPerSample
            )
        }
    }

    /**
     * Compare the metadata between a file internal and rasters
     *
     * @param fileDirectory file internal
     * @param rasters rasters
     */
    private fun compareFileDirectoryAndRastersMetadata(fileDirectory: FileDirectory, rasters: Rasters) {
        Assertions.assertEquals(fileDirectory.stats.imageWidth, rasters.width)
        Assertions.assertEquals(
            fileDirectory.imageHeight, rasters.height
        )
        Assertions.assertEquals(
            fileDirectory.samplesPerPixel, rasters.samplesPerPixel
        )
        Assertions.assertEquals(
            fileDirectory.stats.bitsPerSample.size, rasters.fields.size
        )
        for (i in fileDirectory.stats.bitsPerSample.indices) {
            Assertions.assertEquals(
                fileDirectory.stats.bitsPerSample[i], rasters.fields[i].bytesPerSample * 8
            )
        }
    }

    /**
     * Get the file
     *
     * @param fileName file name
     * @return file
     */
    @JvmStatic
    fun getTestFile(fileName: String): File {
        val resourceUrl = TiffTestUtils::class.java.getResource("/$fileName")
        val resourcePath: Path
        try {
            resourcePath = Paths.get(resourceUrl!!.toURI())
        } catch (e: URISyntaxException) {
            throw TiffException("Failed to get test file path", e)
        }
        val file = resourcePath.toFile()
        return file
    }

    /**
     * Create [NumericFieldType] filled array for samples per pixel size
     *
     * @param samplesPerPixel number of samples per pixel
     * @param fieldType       type of field for each sample
     * @return field type array
     */
    @JvmStatic
    fun createFieldTypeArray(samplesPerPixel: Int, fieldType: NumericFieldType<Number>): List<NumericFieldType<Number>> {
        val result = arrayOfNulls<NumericFieldType<Number>>(samplesPerPixel)
        Arrays.fill(result, fieldType)
        return result.requireNoNulls().toList()
    }

    @JvmStatic
    fun createSampleValues(width: Int, height: Int, fieldTypes: List<NumericFieldType<Number>>, order: ByteOrder): Array<ByteBuffer> {
        val sampleValues = arrayOfNulls<ByteBuffer>(fieldTypes.size)
        for (i in sampleValues.indices) {
            sampleValues[i] = ByteBuffer.allocateDirect(width * height * fieldTypes[i].metadata().bytesPerSample).order(order)
        }
        return sampleValues.requireNoNulls()
    }

}
