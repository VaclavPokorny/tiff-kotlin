package mil.nga.tiff.internal.rasters

import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.ImageWindow
import org.junit.jupiter.api.Assertions

object RasterTestUtils {

    /**
     * Compare rasters sample values
     *
     * @param rasters1 rasters 1
     * @param rasters2 rasters 2
     * @param exactType true if matching the exact data type
     * @param sameBitsPerSample true if should have the same bits per sample
     */
    @JvmOverloads
    fun compareRastersSampleValues(
        rasters1: Rasters, rasters2: Rasters, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        compareRastersMetadata(rasters1, rasters2, sameBitsPerSample)

        Assertions.assertNotNull(rasters1.sampleValues)
        Assertions.assertNotNull(rasters2.sampleValues)
        Assertions.assertEquals(
            rasters1.sampleValues.values!!.size, rasters2.sampleValues.values!!.size
        )

        for (i in rasters1.sampleValues.values!!.indices) {
            val capacity1 = rasters1.sampleValues.values!![i].capacity()
            val bytesPerSample1 = rasters1.metadata.fields[i].metadata().bytesPerSample
            val capacity2 = rasters2.sampleValues.values!![i].capacity()
            val bytesPerSample2 = rasters2.metadata.fields[i].metadata().bytesPerSample
            Assertions.assertEquals(capacity1 / bytesPerSample1, capacity2 / bytesPerSample2)

            for (x in 0 until rasters1.width) {
                for (y in 0 until rasters1.height) {
                    compareNumbers(
                        rasters1.getPixelSample(i, x, y), rasters2.getPixelSample(i, x, y), exactType
                    )
                }
            }
        }
    }

    /**
     * Compare rasters interleave values
     *
     * @param rasters1 rasters 1
     * @param rasters2 rasters 2
     * @param exactType true if matching the exact data type
     * @param sameBitsPerSample true if should have the same bits per sample
     */
    @JvmOverloads
    fun compareRastersInterleaveValues(
        rasters1: Rasters, rasters2: Rasters, exactType: Boolean = true, sameBitsPerSample: Boolean = true
    ) {
        compareRastersMetadata(rasters1, rasters2, sameBitsPerSample)

        Assertions.assertNotNull(rasters1.interleaveValues)
        Assertions.assertNotNull(rasters2.interleaveValues)
        Assertions.assertEquals(
            rasters1.interleaveValues.values!!.capacity() / rasters1.sizePixel(), rasters2.interleaveValues.values!!.capacity() / rasters2.sizePixel()
        )

        for (i in 0 until rasters1.samplesPerPixel) {
            for (x in 0 until rasters1.width) {
                for (y in 0 until rasters1.height) {
                    compareNumbers(
                        rasters1.getPixelSample(i, x, y), rasters2.getPixelSample(i, x, y), exactType
                    )
                }
            }
        }
    }

    /**
     * Compare rasters pixel values
     *
     * @param fileDirectory1 file internal 1
     * @param rasters1 rasters 1
     * @param fileDirectory2 file internal 2
     * @param rasters2 rasters 2
     * @param exactType true if matching the exact data type
     * @param sameBitsPerSample true if should have the same bits per sample
     */
    public fun compareRasters(
        fileDirectory1: FileDirectory, rasters1: Rasters, fileDirectory2: FileDirectory, rasters2: Rasters, exactType: Boolean, sameBitsPerSample: Boolean
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
                if ((x == 0 && y == 0) || (x == rasters1.width - 1 && y == rasters1.height - 1) || (x == randomX && y == randomY)) {
                    val window = ImageWindow.singlePixel(x, y)
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
     * @param rasters1 rasters 1
     * @param rasters2 rasters 2
     * @param sameBitsPerSample true if should have the same bits per sample
     */
    private fun compareRastersMetadata(
        rasters1: Rasters, rasters2: Rasters, sameBitsPerSample: Boolean
    ) {
        Assertions.assertNotNull(rasters1)
        Assertions.assertNotNull(rasters2)
        Assertions.assertEquals(rasters1.width, rasters2.width)
        Assertions.assertEquals(rasters1.height, rasters2.height)
        Assertions.assertEquals(rasters1.numPixels, rasters2.numPixels)
        Assertions.assertEquals(
            rasters1.fields.size, rasters2.fields.size
        )
        if (sameBitsPerSample) {
            for (i in rasters1.fields.indices) {
                Assertions.assertEquals(
                    rasters1.fields[i].bytesPerSample, rasters2.fields[i].bytesPerSample
                )
            }
        }
    }

    /**
     * Compare the two numbers, either exactly or as double values
     *
     * @param number1 number 1
     * @param number2 number 2
     * @param exactType true if matching the exact data type
     */
    private fun compareNumbers(
        number1: Number, number2: Number, exactType: Boolean
    ) {
        if (exactType) {
            Assertions.assertEquals(number1, number2)
        } else {
            Assertions.assertEquals(number1.toDouble(), number2.toDouble())
        }
    }

    fun testRastersMetadata(inpWidth: Int, inpHeight: Int, inpPixVals: IntArray, rasters: Rasters) {
        val fieldTypes = rasters.metadata.fields
        Assertions.assertEquals(1, fieldTypes.size)
        Assertions.assertEquals(2, fieldTypes[0].metadata().bytesPerSample)

        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                Assertions.assertEquals(
                    inpPixVals[y * inpWidth + x], rasters.getPixelSample(0, x, y)
                )
            }
        }
    }

}
