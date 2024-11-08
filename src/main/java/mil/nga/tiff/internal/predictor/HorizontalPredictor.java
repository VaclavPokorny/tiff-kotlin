package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
public class HorizontalPredictor extends AbstractPredictor {

    /**
     * Decode a horizontal encoded predictor row
     *
     * @param reader         byte reader
     * @param writer         byte writer
     * @param width          tile width
     * @param bytesPerSample bytes per sample
     * @param samples        number of samples
     */
    protected void decode(ByteReader reader, ByteWriter writer, int width, int bytesPerSample, int samples) {
        int[] previous = new int[samples];

        for (int pixel = 0; pixel < width; pixel++) {
            for (int sample = 0; sample < samples; sample++) {
                int value = readValue(reader, bytesPerSample) + previous[sample];
                writeValue(writer, bytesPerSample, value);
                previous[sample] = value;
            }
        }
    }

}
