package mil.nga.tiff

import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.ImageWindow
import mil.nga.tiff.internal.Rasters
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.util.TiffException
import org.junit.jupiter.api.Assertions
import java.io.File
import java.net.URISyntaxException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * TIFF Test Utility methods
 *
 * @author osbornb
 */
object TiffTestUtils {
    /**
     * Compare two TIFF Images
     *
     * @param tiffImage1
     * tiff image 1
     * @param tiffImage2
     * tiff image 2
     * @param exactType
     * true if matching the exact data type
     * @param sameBitsPerSample
     * true if should have the same bits per sample
     */
    /**
     * Compare two TIFF Images
     *
     * @param tiffImage1
     * tiff image 1
     * @param tiffImage2
     * tiff image 2
     */
    @JvmStatic
    @JvmOverloads
    fun compareTIFFImages(
        tiffImage1: TIFFImage,
        tiffImage2: TIFFImage, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        Assertions.assertNotNull(tiffImage1)
        Assertions.assertNotNull(tiffImage2)
        Assertions.assertEquals(
            tiffImage1.fileDirectories.size,
            tiffImage2.fileDirectories.size
        )
        for (i in tiffImage1.fileDirectories.indices) {
            val fileDirectory1 = tiffImage1.getFileDirectory(i)
            val fileDirectory2 = tiffImage2.getFileDirectory(i)

            val sampleRasters1 = fileDirectory1.readRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory1,
                sampleRasters1
            )
            val sampleRasters2 = fileDirectory2.readRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory2,
                sampleRasters2
            )
            compareRastersSampleValues(
                sampleRasters1, sampleRasters2,
                exactType, sameBitsPerSample
            )

            val interleaveRasters1 = fileDirectory1
                .readInterleavedRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory1,
                interleaveRasters1
            )
            val interleaveRasters2 = fileDirectory2
                .readInterleavedRasters()
            compareFileDirectoryAndRastersMetadata(
                fileDirectory2,
                interleaveRasters2
            )
            compareRastersInterleaveValues(
                interleaveRasters1,
                interleaveRasters2, exactType, sameBitsPerSample
            )

            compareRasters(
                fileDirectory1, sampleRasters1, fileDirectory2,
                interleaveRasters2, exactType, sameBitsPerSample
            )
            compareRasters(
                fileDirectory1, interleaveRasters1, fileDirectory2,
                sampleRasters2, exactType, sameBitsPerSample
            )
        }
    }

    /**
     * Compare the metadata between a file internal and rasters
     *
     * @param fileDirectory
     * file internal
     * @param rasters
     * rasters
     */
    private fun compareFileDirectoryAndRastersMetadata(
        fileDirectory: FileDirectory, rasters: Rasters
    ) {
        Assertions.assertEquals(fileDirectory.imageWidth, rasters.width)
        Assertions.assertEquals(
            fileDirectory.imageHeight,
            rasters.height
        )
        Assertions.assertEquals(
            fileDirectory.samplesPerPixel,
            rasters.samplesPerPixel
        )
        Assertions.assertEquals(
            fileDirectory.bitsPerSample.size, rasters
                .bitsPerSample.size
        )
        for (i in fileDirectory.bitsPerSample.indices) {
            Assertions.assertEquals(
                fileDirectory.bitsPerSample[i],
                rasters.bitsPerSample[i]
            )
        }
    }

    /**
     * Compare rasters sample values
     *
     * @param rasters1
     * rasters 1
     * @param rasters2
     * rasters 2
     * @param exactType
     * true if matching the exact data type
     * @param sameBitsPerSample
     * true if should have the same bits per sample
     */
    /**
     * Compare rasters sample values
     *
     * @param rasters1
     * rasters 1
     * @param rasters2
     * rasters 2
     */
    @JvmOverloads
    fun compareRastersSampleValues(
        rasters1: Rasters,
        rasters2: Rasters, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        compareRastersMetadata(rasters1, rasters2, sameBitsPerSample)

        Assertions.assertNotNull(rasters1.sampleValues)
        Assertions.assertNotNull(rasters2.sampleValues)
        Assertions.assertEquals(
            rasters1.sampleValues.size,
            rasters2.sampleValues.size
        )

        for (i in rasters1.sampleValues.indices) {
            Assertions.assertEquals(
                rasters1.sampleValues[i].capacity()
                        / rasters1.fieldTypes[i].bytes,
                rasters2.sampleValues[i].capacity()
                        / rasters2.fieldTypes[i].bytes
            )

            for (x in 0 until rasters1.width) {
                for (y in 0 until rasters1.height) {
                    compareNumbers(
                        rasters1.getPixelSample(i, x, y),
                        rasters2.getPixelSample(i, x, y), exactType
                    )
                }
            }
        }
    }

    /**
     * Compare rasters interleave values
     *
     * @param rasters1
     * rasters 1
     * @param rasters2
     * rasters 2
     * @param exactType
     * true if matching the exact data type
     * @param sameBitsPerSample
     * true if should have the same bits per sample
     */
    /**
     * Compare rasters interleave values
     *
     * @param rasters1
     * rasters 1
     * @param rasters2
     * rasters 2
     */
    @JvmOverloads
    fun compareRastersInterleaveValues(
        rasters1: Rasters,
        rasters2: Rasters, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        compareRastersMetadata(rasters1, rasters2, sameBitsPerSample)

        Assertions.assertNotNull(rasters1.interleaveValues)
        Assertions.assertNotNull(rasters2.interleaveValues)
        Assertions.assertEquals(
            rasters1.interleaveValues.capacity()
                    / rasters1.sizePixel(), rasters2.interleaveValues
                .capacity() / rasters2.sizePixel()
        )

        for (i in 0 until rasters1.samplesPerPixel) {
            for (x in 0 until rasters1.width) {
                for (y in 0 until rasters1.height) {
                    compareNumbers(
                        rasters1.getPixelSample(i, x, y),
                        rasters2.getPixelSample(i, x, y), exactType
                    )
                }
            }
        }
    }

    /**
     * Compare rasters pixel values
     *
     * @param fileDirectory1
     * file internal 1
     * @param rasters1
     * rasters 1
     * @param fileDirectory2
     * file internal 2
     * @param rasters2
     * rasters 2
     * @param exactType
     * true if matching the exact data type
     * @param sameBitsPerSample
     * true if should have the same bits per sample
     */
    fun compareRasters(
        fileDirectory1: FileDirectory,
        rasters1: Rasters, fileDirectory2: FileDirectory, rasters2: Rasters,
        exactType: Boolean, sameBitsPerSample: Boolean
    ) {
        compareRastersMetadata(rasters1, rasters2, sameBitsPerSample)

        val randomX = (Math.random() * rasters1.width).toInt()
        val randomY = (Math.random() * rasters1.height).toInt()

        for (x in 0 until rasters1.width) {
            for (y in 0 until rasters1.height) {
                val pixel1 = rasters1.getPixel(x, y)
                val pixel2 = rasters2.getPixel(x, y)

                var rasters3: Rasters? = null
                var rasters4: Rasters? = null
                if ((x == 0 && y == 0)
                    || (x == rasters1.width - 1 && y == rasters1
                        .height - 1)
                    || (x == randomX && y == randomY)
                ) {
                    val window = ImageWindow(x, y)
                    rasters3 = fileDirectory1.readRasters(window)
                    Assertions.assertEquals(1, rasters3.numPixels)
                    rasters4 = fileDirectory2.readInterleavedRasters(window)
                    Assertions.assertEquals(1, rasters4.numPixels)
                }

                for (sample in 0 until rasters1.samplesPerPixel) {
                    val sample1 = rasters1.getPixelSample(sample, x, y)
                    val sample2 = rasters2.getPixelSample(sample, x, y)
                    compareNumbers(sample1, sample2, exactType)
                    compareNumbers(pixel1[sample], sample1, exactType)
                    compareNumbers(pixel1[sample], pixel2[sample], exactType)

                    if (rasters3 != null) {
                        val sample3 = rasters3.getPixelSample(sample, 0, 0)
                        val sample4 = rasters4!!.getPixelSample(sample, 0, 0)
                        compareNumbers(pixel1[sample], sample3, exactType)
                        compareNumbers(pixel1[sample], sample4, exactType)
                    }
                }
            }
        }
    }

    /**
     * Compare the rasters metadata
     *
     * @param rasters1
     * rasters 1
     * @param rasters2
     * rasters 2
     * @param sameBitsPerSample
     * true if should have the same bits per sample
     */
    private fun compareRastersMetadata(
        rasters1: Rasters,
        rasters2: Rasters, sameBitsPerSample: Boolean
    ) {
        Assertions.assertNotNull(rasters1)
        Assertions.assertNotNull(rasters2)
        Assertions.assertEquals(rasters1.width, rasters2.width)
        Assertions.assertEquals(rasters1.height, rasters2.height)
        Assertions.assertEquals(rasters1.numPixels, rasters2.numPixels)
        Assertions.assertEquals(
            rasters1.bitsPerSample.size, rasters2
                .bitsPerSample.size
        )
        if (sameBitsPerSample) {
            for (i in rasters1.bitsPerSample.indices) {
                Assertions.assertEquals(
                    rasters1.bitsPerSample[i],
                    rasters2.bitsPerSample[i]
                )
            }
        }
    }

    /**
     * Compare the two numbers, either exactly or as double values
     *
     * @param number1
     * number 1
     * @param number2
     * number 2
     * @param exactType
     * true if matching the exact data type
     */
    private fun compareNumbers(
        number1: Number, number2: Number,
        exactType: Boolean
    ) {
        if (exactType) {
            Assertions.assertEquals(number1, number2)
        } else {
            Assertions.assertEquals(number1.toDouble(), number2.toDouble())
        }
    }

    /**
     * Get the file
     *
     * @param fileName
     * file name
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
}
