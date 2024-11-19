package mil.nga.tiff.internal.rasters

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.util.TiffConstants
import mil.nga.tiff.util.TiffException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Raster image values
 *
 * @param sampleValues Values separated by sample
 * @param interleaveValues Interleaved pixel sample values
 * @param metadata Rasters metadata
 */
@JvmRecord
data class Rasters(
    val sampleValues: SampleValues,

    val interleaveValues: InterleaveValues,

    val metadata: RasterMetadata

) {
    /**
     * True if the results are stored by samples
     *
     * @return true if results exist
     */
    fun hasSampleValues(): Boolean {
        return sampleValues.isNotEmpty
    }

    /**
     * True if the results are stored interleaved
     *
     * @return true if results exist
     */
    fun hasInterleaveValues(): Boolean {
        return interleaveValues.isNotEmpty
    }

    /**
     * Add a value to the sample results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     */
    fun addToSample(sampleIndex: Int, coordinate: Int, value: Number) {
        sampleValues.addValue(sampleIndex, coordinate, value)
    }

    /**
     * Add a value to the interleaved results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     */
    fun addToInterleave(sampleIndex: Int, coordinate: Int, value: Number) {
        interleaveValues.addValue(sampleIndex, coordinate, value)
    }

    val width: Int
        /**
         * Get the imageWidth of pixels
         *
         * @return imageWidth
         */
        get() = metadata.width

    val height: Int
        /**
         * Get the imageHeight of pixels
         *
         * @return imageHeight
         */
        get() = metadata.height

    val numPixels: Int
        /**
         * Return the number of pixels
         *
         * @return number of pixels
         */
        get() = metadata.width * metadata.height

    val samplesPerPixel: Int
        /**
         * Get the number of samples per pixel
         *
         * @return samples per pixel
         */
        get() = metadata.samplesPerPixel()

    val fields: List<FieldType>
        get() = metadata.fields.stream()
            .map { obj: NumericFieldType<*> -> obj.metadata() }.toList()

    /**
     * Get the pixel sample values
     *
     * @param x x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @return pixel sample values
     */
    fun getPixel(x: Int, y: Int): Array<Number> {
        // Get the pixel values from each sample
        return if (sampleValues.isNotEmpty) {
            sampleValues.getPixel(x, y)
        } else {
            interleaveValues.getPixel(x, y)
        }
    }

    /**
     * Set the pixel sample values
     *
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @param values pixel values
     */
    fun setPixel(x: Int, y: Int, values: Array<Number>) {
        // Set the pixel values from each sample
        if (sampleValues.isNotEmpty) {
            sampleValues.setPixel(x, y, values)
        } else {
            interleaveValues.setPixel(x, y, values)
        }
    }

    /**
     * Returns byte array of pixel row.
     *
     * @param y        Row index
     * @param newOrder Desired byte order of result byte array
     * @return Byte array of pixel row
     */
    fun getPixelRow(y: Int, newOrder: ByteOrder): ByteArray {
        return if (sampleValues.isNotEmpty) {
            sampleValues.getPixelRow(y, newOrder)
        } else {
            interleaveValues.getPixelRow(y, newOrder)
        }
    }

    /**
     * Returns byte array of sample row.
     *
     * @param y        Row index
     * @param sample   Sample index
     * @param newOrder Desired byte order of resulting byte array
     * @return Byte array of sample row
     */
    fun getSampleRow(y: Int, sample: Int, newOrder: ByteOrder): ByteArray {
        return if (sampleValues.isNotEmpty) {
            sampleValues.getSampleRow(y, sample, newOrder)
        } else {
            interleaveValues.getSampleRow(y, sample, newOrder)
        }
    }

    /**
     * Get a pixel sample value
     *
     * @param sample sample index (&gt;= 0 &amp;&amp; &lt;
     * [.getSamplesPerPixel])
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @return pixel sample
     */
    fun getPixelSample(sample: Int, x: Int, y: Int): Number {
        return if (sampleValues.isNotEmpty) {
            sampleValues.getPixelSample(sample, x, y)
        } else {
            interleaveValues.getPixelSample(sample, x, y)
        }
    }

    /**
     * Set a pixel sample value
     *
     * @param sample sample index (&gt;= 0 &amp;&amp; &lt;
     * [.getSamplesPerPixel])
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @param value  pixel value
     */
    fun setPixelSample(sample: Int, x: Int, y: Int, value: Number) {
        if (sampleValues.isNotEmpty) {
            sampleValues.setPixelSample(sample, x, y, value)
        }
        if (interleaveValues.isNotEmpty) {
            interleaveValues.setPixelSample(sample, x, y, value)
        }
    }

    /**
     * Get the first pixel sample value, useful for single sample pixels
     * (grayscale)
     *
     * @param x x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @return first pixel sample
     */
    fun getFirstPixelSample(x: Int, y: Int): Number {
        return getPixelSample(0, x, y)
    }

    /**
     * Set the first pixel sample value, useful for single sample pixels
     * (grayscale)
     *
     * @param x     x coordinate (&gt;= 0 &amp;&amp; &lt; [.getWidth])
     * @param y     y coordinate (&gt;= 0 &amp;&amp; &lt; [.getHeight])
     * @param value pixel value
     */
    fun setFirstPixelSample(x: Int, y: Int, value: Number) {
        setPixelSample(0, x, y, value)
    }

    /**
     * Size in bytes of the image
     *
     * @return bytes
     */
    fun size(): Int {
        return numPixels * metadata.pixelSize
    }

    /**
     * Size in bytes of a pixel
     *
     * @return bytes
     */
    fun sizePixel(): Int {
        return metadata.pixelSize
    }

    /**
     * Calculate the rows per strip to write
     *
     * @param planarConfiguration chunky or planar
     * @param maxBytesPerStrip    attempted max bytes per strip
     * @return rows per strip
     */
    @JvmOverloads
    fun calculateRowsPerStrip(
        planarConfiguration: PlanarConfiguration,
        maxBytesPerStrip: Int = TiffConstants.DEFAULT_MAX_BYTES_PER_STRIP
    ): Int {
        return when (planarConfiguration) {
            PlanarConfiguration.CHUNKY -> metadata.calculateRowsPerStripChunky(maxBytesPerStrip)
            PlanarConfiguration.PLANAR -> metadata.calculateRowsPerStripPlanar(maxBytesPerStrip)
        }
    }

    companion object {
        /**
         * Create instance
         *
         * @param width            imageWidth of pixels
         * @param height           imageHeight of pixels
         * @param fieldTypes       Field type for each sample
         * @param sampleValues     empty sample values buffer array
         * @param interleaveValues empty interleaved values buffer
         */
        @JvmStatic
        fun create(
            width: Int,
            height: Int,
            fieldTypes: List<NumericFieldType<*>>,
            sampleValues: Array<ByteBuffer>?,
            interleaveValues: ByteBuffer?
        ): Rasters {
            val metadata = RasterMetadata(width, height, fieldTypes)

            val sampleValuesHolder = SampleValues(sampleValues, metadata)
            val interleaveValuesHolder = InterleaveValues(interleaveValues, metadata)

            if (!sampleValuesHolder.isNotEmpty && !interleaveValuesHolder.isNotEmpty) {
                throw TiffException("Results must be sample and/or interleave based")
            }

            return Rasters(sampleValuesHolder, interleaveValuesHolder, metadata)
        }
    }
}
