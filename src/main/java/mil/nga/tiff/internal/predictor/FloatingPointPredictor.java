package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
public class FloatingPointPredictor extends AbstractPredictor {

    /**
     * Decode a floating point encoded predictor row
     *
     * @param reader         byte reader
     * @param writer         byte writer
     * @param width          tile imageWidth
     * @param bytesPerSample bytes per sample
     * @param samples        number of samples
     */
    protected void decode(ByteReader reader, ByteWriter writer, int width, int bytesPerSample, int samples) {
        int samplesWidth = width * samples;
        byte[] bytes = new byte[samplesWidth * bytesPerSample];
        byte[] previous = new byte[samples];

        for (int sampleByte = 0; sampleByte < width * bytesPerSample; sampleByte++) {
            for (int sample = 0; sample < samples; sample++) {
                byte value = (byte) (reader.readByte() + previous[sample]);
                bytes[sampleByte * samples + sample] = value;
                previous[sample] = value;
            }
        }

        for (int widthSample = 0; widthSample < samplesWidth; widthSample++) {
            for (int sampleByte = 0; sampleByte < bytesPerSample; sampleByte++) {
                int index = ((bytesPerSample - sampleByte - 1) * samplesWidth) + widthSample;
                writer.writeByte(bytes[index]);
            }
        }
    }

}
