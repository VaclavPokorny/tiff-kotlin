package mil.nga.tiff.internal

import mil.nga.tiff.field.TagDictionary
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.internal.rasters.Rasters
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.util.TiffException
import java.nio.ByteBuffer
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

class FileDirectoryRasterReader(
    private val stats: DirectoryStats,
    private val tileOrStripProcessor: TileOrStripProcessor,
    private val typeDictionary: TagDictionary,
    private val reader: ByteReader?
) {
    fun readRasters(
        window: ImageWindow,
        samples: IntArray?,
        sampleValues: Boolean,
        interleaveValues: Boolean,
        tiled: Boolean
    ): Rasters {

        reader!!

        // Validate the image window

        var actualSamples = samples
        window.validate()
        window.validateFitsInImage(stats.imageWidth!!, stats.imageHeight!!)

        val numPixels = window.numPixels()

        // Set or validate the samples
        val samplesPerPixel = stats.samplesPerPixel!!
        if (actualSamples == null) {
            actualSamples = IntArray(samplesPerPixel)
            for (i in actualSamples.indices) {
                actualSamples[i] = i
            }
        } else {
            for (sample in actualSamples) {
                if (sample >= samplesPerPixel) {
                    throw TiffException("Invalid sample index: $sample")
                }
            }
        }

        // Create the interleaved result buffer
        val bitsPerSample = stats.bitsPerSample!!
        var bytesPerPixel = 0
        for (i in 0..<samplesPerPixel) {
            bytesPerPixel += bitsPerSample[i] / 8
        }
        var interleave: ByteBuffer? = null
        if (interleaveValues) {
            interleave = ByteBuffer.allocateDirect(numPixels * bytesPerPixel)
            interleave.order(reader.byteOrder)
        }

        // Create the sample indexed result buffer array
        var sample: Array<ByteBuffer?>? = null
        if (sampleValues) {
            sample = arrayOfNulls(samplesPerPixel)
            for (i in sample.indices) {
                val numberOfBytes = numPixels.toDouble() * bitsPerSample[i] / 8

                if (numberOfBytes > Int.MAX_VALUE) {
                    throw TiffException("Number of sample value bytes is above max byte buffer capacity: $numberOfBytes")
                }

                sample[i] = ByteBuffer.allocateDirect(numberOfBytes.toInt())
                sample[i]!!.order(reader.byteOrder)
            }
        }

        val fieldTypes = IntStream.range(0, actualSamples.size)
            .mapToObj { sampleIndex: Int ->
                this.getFieldTypeForSample(
                    sampleIndex
                )
            }
            .toList()

        // Create the rasters results
        @Suppress("UNCHECKED_CAST") val rasters =
            Rasters.create(window.width(), window.height(), fieldTypes as List<NumericFieldType<*>>, sample?.requireNoNulls(), interleave)

        // Read the rasters
        readRaster(window, actualSamples, rasters, reader, tiled)

        return rasters
    }

    /**
     * Read and populate the rasters
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @param rasters rasters to populate
     */
    private fun readRaster(
        window: ImageWindow,
        samples: IntArray,
        rasters: Rasters,
        reader: ByteReader,
        tiled: Boolean
    ) {
        val tileWidth = stats.tileWidth!!
        val tileHeight = stats.tileHeight!!

        val minXTile = window.minX / tileWidth
        val maxXTile = (window.maxX + tileWidth - 1) / tileWidth
        val minYTile = window.minY / tileHeight
        val maxYTile = (window.maxY + tileHeight - 1) / tileHeight

        var bytesPerPixel = bytesPerPixel

        val srcSampleOffsets = IntArray(samples.size)
        val sampleFieldTypesInit: Array<NumericFieldType<*>?> = arrayOfNulls(samples.size)
        for (i in samples.indices) {
            var sampleOffset = 0
            if (stats.planarConfiguration == PlanarConfiguration.CHUNKY) {
                sampleOffset = stats.bitsPerSample!!
                    .subList(0, samples[i])
                    .stream()
                    .mapToInt { obj: Int -> obj }
                    .sum() / 8
            }
            srcSampleOffsets[i] = sampleOffset
            sampleFieldTypesInit[i] = getFieldTypeForSample(samples[i])!!
        }

        val sampleFieldTypes = sampleFieldTypesInit.requireNoNulls()

        for (yTile in minYTile..<maxYTile) {
            for (xTile in minXTile..<maxXTile) {
                val firstLine = yTile * tileHeight
                val firstCol = xTile * tileWidth
                val lastLine = (yTile + 1) * tileHeight
                val lastCol = (xTile + 1) * tileWidth

                for (sampleIndex in samples.indices) {
                    val sample = samples[sampleIndex]
                    if (stats.planarConfiguration == PlanarConfiguration.PLANAR) {
                        bytesPerPixel = getSampleByteSize(sample)
                    }

                    val block = tileOrStripProcessor.run(xTile, yTile, sample, reader, tiled, reader.byteOrder)
                    val blockReader = ByteReader(block, reader.byteOrder)

                    val minY = max(0.0, (window.minY - firstLine).toDouble()).toInt()
                    val maxY = min(tileHeight.toDouble(), (tileHeight - (lastLine - window.maxY)).toDouble()).toInt()
                    for (y in minY..<maxY) {
                        val minX = max(0.0, (window.minX - firstCol).toDouble()).toInt()
                        val maxX = min(tileWidth.toDouble(), (tileWidth - (lastCol - window.maxX)).toDouble()).toInt()
                        for (x in minX..<maxX) {
                            val pixelOffset = (y * tileWidth + x) * bytesPerPixel
                            val valueOffset = pixelOffset + srcSampleOffsets[sampleIndex]
                            blockReader.nextByte = valueOffset

                            // Read the value
                            val value = sampleFieldTypes[sampleIndex].readValue(blockReader) as Number

                            if (rasters.hasInterleaveValues()) {
                                val windowCoordinate =
                                    (y + firstLine - window.minY) * window.width() + (x + firstCol - window.minX)
                                rasters.addToInterleave(sampleIndex, windowCoordinate, value)
                            }

                            if (rasters.hasSampleValues()) {
                                val windowCoordinate =
                                    (y + firstLine - window.minY) * window.width() + x + firstCol - window.minX
                                rasters.addToSample(sampleIndex, windowCoordinate, value)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the sample byte size
     *
     * @param sampleIndex sample index
     * @return byte size
     */
    private fun getSampleByteSize(sampleIndex: Int): Int {
        val bitsPerSample = stats.bitsPerSample!!
        if (sampleIndex >= bitsPerSample.size) {
            throw TiffException("Sample index $sampleIndex is out of range")
        }
        val bits = bitsPerSample[sampleIndex]
        if ((bits % 8) != 0) {
            throw TiffException("Sample bit-imageWidth of $bits is not supported")
        }
        return (bits / 8)
    }

    private val bytesPerPixel: Int
        /**
         * Calculates the number of bytes for each pixel across all samples. Only
         * full bytes are supported, an exception is thrown when this is not the
         * case.
         *
         * @return the bytes per pixel
         */
        get() {
            var bitsPerSample = 0
            val bitsPerSamples = stats.bitsPerSample!!
            for (i in bitsPerSamples.indices) {
                val bits = bitsPerSamples[i]
                if ((bits % 8) != 0) {
                    throw TiffException("Sample bit-imageWidth of $bits is not supported")
                } else if (bits != bitsPerSamples.first()) {
                    throw TiffException("Differing size of samples in a pixel are not supported. sample 0 = " + bitsPerSamples.first() + ", sample " + i + " = " + bits)
                }
                bitsPerSample += bits
            }
            return bitsPerSample / 8
        }

    /**
     * Get the field type for the sample
     *
     * @param sampleIndex sample index
     * @return field type
     */
    fun getFieldTypeForSample(sampleIndex: Int): NumericFieldType<*>? {
        val sampleFormat: SampleFormat
        val sampleFormatList = stats.sampleFormatList!!
        if (sampleFormatList.isEmpty()) {
            sampleFormat = SampleFormat.UNSIGNED_INT
        } else {
            val listId = if (sampleIndex < sampleFormatList.size) sampleIndex else 0
            sampleFormat = sampleFormatList[listId]
        }
        val bitsPerSample = stats.bitsPerSample!![sampleIndex]
        return typeDictionary.findNumericTypeBySampleParams(sampleFormat, bitsPerSample)
    }
}
