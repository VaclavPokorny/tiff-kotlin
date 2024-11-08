package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.compression.CompressionDecoder;
import mil.nga.tiff.compression.CompressionEncoder;
import mil.nga.tiff.compression.DeflateCompression;
import mil.nga.tiff.compression.LZWCompression;
import mil.nga.tiff.compression.PackbitsCompression;
import mil.nga.tiff.compression.RawCompression;
import mil.nga.tiff.compression.UnsupportedCompression;
import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum Compression {
    NO(1, new RawCompression(), new RawCompression()),
    CCITT_HUFFMAN(2),
    T4(3),
    T6(4),
    LZW(5, new LZWCompression(), new LZWCompression()),
    JPEG_OLD(6),
    JPEG_NEW(7),
    DEFLATE(8, new DeflateCompression(), new DeflateCompression()),
    PKZIP_DEFLATE(32946, new DeflateCompression(), new DeflateCompression()), // PKZIP-style Deflate encoding (Obsolete).
    PACKBITS(32773, new PackbitsCompression(), new PackbitsCompression());


    private static final Compression DEFAULT = NO;

    private final int id;
    private final CompressionDecoder decoder;
    private final CompressionEncoder encoder;

    Compression(int id) {
        this.id = id;
        this.decoder = new UnsupportedCompression("Unsupported compression: " + name());
        this.encoder = null;
    }

    Compression(int id, CompressionDecoder decoder, CompressionEncoder encoder) {
        this.id = id;
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public int getId() {
        return id;
    }

    public static Compression findById(Integer id) {
        if (id == null) {
            return DEFAULT;
        }

        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Unknown compression ID: " + id));
    }

    /**
     * Get the compression encoder
     *
     * @param compression compression ID
     * @return encoder
     */
    public static CompressionEncoder getEncoder(Integer compression) {
        Compression o = findById(compression);

        if (o.encoder == null) {
            throw new TiffException("Compression not supported: " + o);
        }

        return o.encoder;
    }

    /**
     * Get the compression decoder
     *
     * @param compression compression ID
     * @return encoder
     */
    public static CompressionDecoder getDecoder(Integer compression) {
        Compression o = findById(compression);

        if (o.decoder == null) {
            throw new TiffException("Compression not supported: " + o);
        }

        return o.decoder;
    }

}
