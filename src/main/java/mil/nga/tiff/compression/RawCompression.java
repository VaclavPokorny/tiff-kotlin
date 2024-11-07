package mil.nga.tiff.compression;

import java.nio.ByteOrder;

/**
 * Raw / no compression
 *
 * @author osbornb
 */
public class RawCompression implements CompressionDecoder, CompressionEncoder {

    @Override
    public byte[] decode(byte[] bytes, ByteOrder byteOrder) {
        return bytes;
    }

    @Override
    public boolean rowEncoding() {
        return false;
    }

    @Override
    public byte[] encode(byte[] bytes, ByteOrder byteOrder) {
        return bytes;
    }

}
