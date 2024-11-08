package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;

import java.util.List;

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
public class NullPredictor implements Predictor {

    /**
     * Decode the predictor encoded bytes
     *
     * @param bytes               bytes to decode
     * @param width               tile width
     * @param height              tile height
     * @param bitsPerSample       bits per samples
     * @param planarConfiguration planar configuration
     * @return decoded or original bytes
     */
    @Override
    public byte[] decode(byte[] bytes, int width, int height, List<Integer> bitsPerSample, PlanarConfiguration planarConfiguration) {
        return bytes;
    }

}
