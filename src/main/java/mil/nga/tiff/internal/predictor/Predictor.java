package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;

import java.nio.ByteOrder;
import java.util.List;

/**
 * Differencing Predictor decoder
 *
 * @author osbornb
 * @since 3.0.0
 */
public interface Predictor {

    /**
     * Decode the predictor encoded bytes
     *
     * @param bytes               bytes to decode
     * @param width               tile imageWidth
     * @param height              tile imageHeight
     * @param bitsPerSample       bits per samples
     * @param planarConfiguration planar configuration
     * @param byteOrder           byte order
     * @return decoded or original bytes
     */
    byte[] decode(byte[] bytes, int width, int height, List<Integer> bitsPerSample, PlanarConfiguration planarConfiguration, ByteOrder byteOrder);

}
