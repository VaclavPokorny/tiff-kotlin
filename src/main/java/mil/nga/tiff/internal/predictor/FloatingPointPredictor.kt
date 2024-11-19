package mil.nga.tiff.internal.predictor

import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
class FloatingPointPredictor : AbstractPredictor() {
    /**
     * Decode a floating point encoded predictor row
     *
     * @param reader         byte reader
     * @param writer         byte writer
     * @param width          tile imageWidth
     * @param bytesPerSample bytes per sample
     * @param samples        number of samples
     */
    override fun decode(
        reader: ByteReader,
        writer: ByteWriter,
        width: Int,
        bytesPerSample: Int,
        samples: Int
    ) {
        val samplesWidth = width * samples
        val bytes = ByteArray(samplesWidth * bytesPerSample)
        val previous = ByteArray(samples)

        for (sampleByte in 0 until width * bytesPerSample) {
            for (sample in 0 until samples) {
                val value = (reader.readByte() + previous[sample]).toByte()
                bytes[sampleByte * samples + sample] = value
                previous[sample] = value
            }
        }

        for (widthSample in 0 until samplesWidth) {
            for (sampleByte in 0 until bytesPerSample) {
                val index = ((bytesPerSample - sampleByte - 1) * samplesWidth) + widthSample
                writer.writeByte(bytes[index])
            }
        }
    }
}
