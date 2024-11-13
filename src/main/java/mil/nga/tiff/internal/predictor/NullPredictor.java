package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;

import java.nio.ByteOrder;
import java.util.List;

/**
 * Null differencing predictor decoder
 */
public class NullPredictor implements Predictor {

    @Override
    public byte[] decode(byte[] bytes, int width, int height, List<Integer> bitsPerSample, PlanarConfiguration planarConfiguration, ByteOrder byteOrder) {
        return bytes;
    }

}
