package mil.nga.tiff.compression;

import mil.nga.tiff.util.TiffException;

import java.nio.ByteOrder;

/**
 * Unsupported compression
 *
 * @author michaelknigge
 */
public class UnsupportedCompression implements CompressionDecoder, CompressionEncoder {

    private final String message;

    /**
     * Constructor
     *
     * @param message message of the TiffException
     * @since 2.0.1
     */
    public UnsupportedCompression(final String message) {
        this.message = message;
    }

    @Override
    public byte[] decode(byte[] bytes, ByteOrder byteOrder) {
        throw new TiffException(this.message);
    }

    @Override
    public boolean rowEncoding() {
        return false;
    }

    @Override
    public byte[] encode(byte[] bytes, ByteOrder byteOrder) {
        throw new TiffException(this.message);
    }
}
