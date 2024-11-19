package mil.nga.tiff.internal.rasters

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.util.TiffException
import kotlin.math.max

/**
 * Raster precalculated metadata
 *
 * @param width          Width of pixels
 * @param height         Height of pixels
 * @param fields         Field type for each sample
 * @param pixelSize      Calculated pixel size in bytes
 */
@JvmRecord
data class RasterMetadata(
    @JvmField val width: Int,
    @JvmField val height: Int,
    @JvmField val fields: List<NumericFieldType<*>>,
    @JvmField val pixelSize: Int
) {
    constructor(width: Int, height: Int, fields: List<NumericFieldType<*>>) : this(
        width,
        height,
        fields,
        fields.stream()
            .map<FieldType> { obj: NumericFieldType<*> -> obj.metadata() }
            .mapToInt(FieldType::bytesPerSample).sum()
    )

    fun field(index: Int): NumericFieldType<*> {
        return fields[index]
    }

    fun fieldMetadata(index: Int): FieldType {
        return fields[index].metadata()
    }

    fun bytesPerSampleTotal(maxIndex: Int): Int {
        var ret = 0;
        for (i in 0..< maxIndex) {
            ret += field(i).metadata().bytesPerSample
        }

        return ret
    }

    /**
     * Validate the coordinates range
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    fun validateCoordinates(x: Int, y: Int) {
        if (x < 0 || x >= width || y < 0 || y > height) {
            throw TiffException("Pixel oustide of raster range. Width: $width, Height: $height, x: $x, y: $y")
        }
    }

    /**
     * Validate the sample index
     *
     * @param sample sample index
     */
    fun validateSample(sample: Int) {
        if (sample < 0 || sample >= fields.size) {
            throw TiffException("Pixel sample out of bounds. sample: " + sample + ", samples per pixel: " + fields.size)
        }
    }

    /**
     * Get the number of samples per pixel
     *
     * @return samples per pixel
     */
    fun samplesPerPixel(): Int {
        return fields.size
    }

    fun calculateRowsPerStripChunky(maxBytesPerStrip: Int): Int {
        val bytesPerRow = pixelSize * width
        return max(1.0, (maxBytesPerStrip / bytesPerRow).toDouble()).toInt()
    }

    fun calculateRowsPerStripPlanar(maxBytesPerStrip: Int): Int {
        return fields.stream()
            .map { obj: NumericFieldType<*> -> obj.metadata() }
            .mapToInt(FieldType::bytesPerSample)
            .map { bytes: Int -> bytes * width }
            .map { bytesPerRow: Int -> max(1.0, (maxBytesPerStrip / bytesPerRow).toDouble()).toInt()}
            .min()
            .orElseThrow { IllegalStateException("No fields defined.") }
    }
}
