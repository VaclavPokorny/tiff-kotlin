package mil.nga.tiff.internal.rasters

import java.nio.ByteBuffer
import java.nio.ByteOrder

@JvmRecord
data class SampleValues(val values: Array<ByteBuffer>?, val metadata: RasterMetadata) {
    val isNotEmpty: Boolean
        get() = values != null

    private fun sampleIndexLocation(x: Int, y: Int): Int {
        return y * metadata.width + x
    }

    fun setPixelSample(sample: Int, x: Int, y: Int, value: Number) {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(sample)

        addValueInternal(sampleIndexLocation(x, y), sample, value)
    }

    fun getPixelSample(sample: Int, x: Int, y: Int): Number {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(sample)

        val bufferPos = sampleIndexLocation(x, y) * metadata.field(sample).metadata().bytesPerSample
        return metadata.field(sample).getSampleFromByteBuffer(values!![sample], bufferPos, sample)!!
    }

    fun getSampleRow(y: Int, sample: Int, newOrder: ByteOrder): ByteArray {
        val outBuffer = ByteBuffer.allocate(metadata.width * metadata.field(sample).metadata().bytesPerSample)
        outBuffer.order(newOrder)

        values!![sample].position(y * metadata.width * metadata.field(sample).metadata().bytesPerSample)
        for (x in 0..<metadata.width) {
            metadata.field(sample).transferSample(outBuffer, values[sample])
        }

        return outBuffer.array()
    }

    fun getPixelRow(y: Int, newOrder: ByteOrder): ByteArray {
        val outBuffer = ByteBuffer.allocate(metadata.width * metadata.pixelSize)
        outBuffer.order(newOrder)

        for (i in 0..<metadata.samplesPerPixel()) {
            values!![i].position(y * metadata.width * metadata.field(i).metadata().bytesPerSample)
        }
        for (i in 0..<metadata.width) {
            for (j in 0..<metadata.samplesPerPixel()) {
                metadata.field(j).transferSample(outBuffer, values!![j])
            }
        }

        return outBuffer.array()
    }

    fun setPixel(x: Int, y: Int, values: Array<Number>) {
        metadata.validateCoordinates(x, y)
        metadata.validateSample(values.size + 1)

        // Set the pixel values from each sample
        for (i in 0..<metadata.samplesPerPixel()) {
            addValueInternal(sampleIndexLocation(x, y), i, values[i])
        }
    }

    fun getPixel(x: Int, y: Int): Array<Number> {
        metadata.validateCoordinates(x, y)

        // Pixel with each sample value
        val pixel = arrayOfNulls<Number>(metadata.samplesPerPixel())

        // Get the pixel values from each sample
        val sampleIndex = sampleIndexLocation(x, y)
        for (i in 0..<metadata.samplesPerPixel()) {
            val bufferIndex = sampleIndex * metadata.field(i).metadata().bytesPerSample
            pixel[i] = metadata.field(i).getSampleFromByteBuffer(values!![i], bufferIndex, i)
        }

        return pixel.requireNoNulls()
    }

    fun addValue(sampleIndex: Int, coordinate: Int, value: Number) {
        addValueInternal(coordinate, sampleIndex, value)
    }

    private fun addValueInternal(coordinate: Int, sampleIndex: Int, value: Number) {
        val bufferIndex = coordinate * metadata.field(sampleIndex).metadata().bytesPerSample
        metadata.field(sampleIndex).updateSampleInByteBuffer(values!![sampleIndex], bufferIndex, sampleIndex, value)
    }

}
