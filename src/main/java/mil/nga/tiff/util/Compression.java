package mil.nga.tiff.util;

import mil.nga.tiff.compression.CompressionDecoder;
import mil.nga.tiff.compression.CompressionEncoder;
import mil.nga.tiff.compression.DeflateCompression;
import mil.nga.tiff.compression.LZWCompression;
import mil.nga.tiff.compression.PackbitsCompression;
import mil.nga.tiff.compression.RawCompression;
import mil.nga.tiff.compression.UnsupportedCompression;

public class Compression {
    public static final int NO = 1;
    public static final int CCITT_HUFFMAN = 2;
    public static final int T4 = 3;
    public static final int T6 = 4;
    public static final int LZW = 5;
    public static final int JPEG_OLD = 6;
    public static final int JPEG_NEW = 7;
    public static final int DEFLATE = 8;
    public static final int PKZIP_DEFLATE = 32946; // PKZIP-style Deflate encoding (Obsolete).
    public static final int PACKBITS = 32773;


    /**
     * Get the compression encoder
     *
     * @param compression compression ID
     * @return encoder
     */
    public static CompressionEncoder getEncoder(Integer compression) {
        return switch (compression) {
            case null -> new RawCompression();
            case Compression.NO -> new RawCompression();
            case Compression.CCITT_HUFFMAN -> throw new TiffException("CCITT Huffman compression not supported: " + compression);
            case Compression.T4 -> throw new TiffException("T4-encoding compression not supported: " + compression);
            case Compression.T6 -> throw new TiffException("T6-encoding compression not supported: " + compression);
            case Compression.LZW -> new LZWCompression();
            case Compression.JPEG_OLD, Compression.JPEG_NEW -> throw new TiffException("JPEG compression not supported: " + compression);
            case Compression.DEFLATE, Compression.PKZIP_DEFLATE -> new DeflateCompression();
            case Compression.PACKBITS -> new PackbitsCompression();
            default -> throw new TiffException("Unknown compression method identifier: " + compression);
        };
    }

    /**
     * Get the compression decoder
     *
     * @param compression compression ID
     * @return encoder
     */
    public static CompressionDecoder getDecoder(Integer compression) {
        return switch (compression) {
            case null -> new RawCompression();
            case Compression.NO -> new RawCompression();
            case Compression.CCITT_HUFFMAN -> new UnsupportedCompression("CCITT Huffman compression not supported: " + compression);
            case Compression.T4 -> new UnsupportedCompression("T4-encoding compression not supported: " + compression);
            case Compression.T6 -> new UnsupportedCompression("T6-encoding compression not supported: " + compression);
            case Compression.LZW -> new LZWCompression();
            case Compression.JPEG_OLD, Compression.JPEG_NEW -> new UnsupportedCompression("JPEG compression not supported: " + compression);
            case Compression.DEFLATE, Compression.PKZIP_DEFLATE -> new DeflateCompression();
            case Compression.PACKBITS -> new PackbitsCompression();
            default -> new UnsupportedCompression("Unknown compression method identifier: " + compression);
        };
    }

}
