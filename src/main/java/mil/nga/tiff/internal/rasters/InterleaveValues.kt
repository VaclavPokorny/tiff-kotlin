package mil.nga.tiff.internal.rasters

import java.nio.ByteBuffer
import java.nio.ByteOrder

@JvmRecord
data class InterleaveValues(val values: ByteBuffer?, val metadata: RasterMetadata) {
    val isNotEmpty: Boolean
        get() = values != null

    private fun interleaveIndexLocationForPixel(x: Int, y: Int): Int {
        return (y * metadata.width + x) * metadata.pixelSize
    }

    private fun interleaveIndexLocation(x: Int, y: Int): Int {
        return (y * metadata.width * metadata.pixelSize) + (x * metadata.pixelSize)
    }


    fun setPixelSample(sample: Int, x: Int, y: Int, value: Number) {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(sample)

        val interleaveIndex = interleaveIndexLocationForPixel(x, y) + metadata.bytesPerSampleTotal(sample)
        metadata.field(sample).updateSampleInByteBuffer(values!!, interleaveIndex, sample, value)
    }

    fun getPixelSample(sample: Int, x: Int, y: Int): Number {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(sample)

        var bufferPos = interleaveIndexLocation(x, y)
        for (i in 0..<sample) {
            bufferPos += metadata.field(sample).metadata().bytesPerSample
        }

        return metadata.field(sample).getSampleFromByteBuffer(values!!, bufferPos, sample)!!
    }

    fun getSampleRow(y: Int, sample: Int, newOrder: ByteOrder): ByteArray {
        val outBuffer = ByteBuffer.allocate(metadata.width * metadata.field(sample).metadata().bytesPerSample)
        outBuffer.order(newOrder)

        var sampleOffset = 0
        for (i in 0..<sample) {
            sampleOffset += metadata.field(sample).metadata().bytesPerSample
        }

        for (i in 0..<metadata.width) {
            values!!.position((y * metadata.width + i) * metadata.pixelSize + sampleOffset)
            metadata.field(sample).transferSample(outBuffer, values)
        }

        return outBuffer.array()
    }

    fun getPixelRow(y: Int, newOrder: ByteOrder): ByteArray {
        val outBuffer = ByteBuffer.allocate(metadata.width * metadata.pixelSize)
        outBuffer.order(newOrder)

        values!!.position(y * metadata.width * metadata.pixelSize)

        for (i in 0..<metadata.width) {
            for (j in 0..<metadata.samplesPerPixel()) {
                metadata.field(j).transferSample(outBuffer, values)
            }
        }

        return outBuffer.array()
    }

    fun setPixel(x: Int, y: Int, values: Array<Number>) {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(values.size + 1)

        var interleaveIndex = interleaveIndexLocationForPixel(x, y)
        for (i in 0..<metadata.samplesPerPixel()) {
            metadata.field(i).updateSampleInByteBuffer(this.values!!, interleaveIndex, i, values[i])
            interleaveIndex += metadata.field(i).metadata().bytesPerSample
        }
    }

    fun getPixel(x: Int, y: Int): Array<Number> {
        metadata.validateCoordinates(x, y)

        // Pixel with each sample value
        val pixel = arrayOfNulls<Number>(metadata.samplesPerPixel())

        // Get the pixel values from each sample
        var interleaveIndex = interleaveIndexLocation(x, y)
        for (i in 0..<metadata.samplesPerPixel()) {
            pixel[i] = metadata.field(i).getSampleFromByteBuffer(values!!, interleaveIndex, i)
            interleaveIndex += metadata.field(i).metadata().bytesPerSample
        }

        return pixel.requireNoNulls()
    }

    /**
     * Add a value to the interleaved results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     * @since 2.0.0
     */
    fun addValue(sampleIndex: Int, coordinate: Int, value: Number) {
        val bufferPos = (coordinate * metadata.pixelSize) + metadata.bytesPerSampleTotal(sampleIndex)
        metadata.field(sampleIndex).updateSampleInByteBuffer(values!!, bufferPos, sampleIndex, value)
    }
}
