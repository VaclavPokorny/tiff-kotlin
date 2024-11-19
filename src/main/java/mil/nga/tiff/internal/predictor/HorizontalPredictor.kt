package mil.nga.tiff.internal.predictor

import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
class HorizontalPredictor : AbstractPredictor() {
    /**
     * Decode a horizontal encoded predictor row
     *
     * @param reader         byte reader
     * @param writer         byte writer
     * @param width          tile imageWidth
     * @param bytesPerSample bytes per sample
     * @param samples        number of samples
     */
    override fun decode(reader: ByteReader, writer: ByteWriter, width: Int, bytesPerSample: Int, samples: Int) {
        val previous = IntArray(samples)

        for (pixel in 0 until width) {
            for (sample in 0 until samples) {
                val value = readValue(reader, bytesPerSample) + previous[sample]
                writeValue(writer, bytesPerSample, value)
                previous[sample] = value
            }
        }
    }
}
