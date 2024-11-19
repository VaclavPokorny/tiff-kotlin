package mil.nga.tiff.field.type.enumeration

import mil.nga.tiff.compression.*
import mil.nga.tiff.util.TiffException

enum class Compression {
    NO(1, RawCompression(), RawCompression()),
    CCITT_HUFFMAN(2),
    T4(3),
    T6(4),
    LZW(5, LZWCompression(), LZWCompression()),
    JPEG_OLD(6),
    JPEG_NEW(7),
    DEFLATE(8, DeflateCompression(), DeflateCompression()),
    PKZIP_DEFLATE(32946, DeflateCompression(), DeflateCompression()),  // PKZIP-style Deflate encoding (Obsolete).
    PACKBITS(32773, PackbitsCompression(), PackbitsCompression());


    val id: Int
    private val decoder: CompressionDecoder
    private val encoder: CompressionEncoder?

    constructor(id: Int) {
        this.id = id
        this.decoder = UnsupportedCompression("Unsupported compression: $name")
        this.encoder = null
    }

    constructor(id: Int, decoder: CompressionDecoder, encoder: CompressionEncoder?) {
        this.id = id
        this.decoder = decoder
        this.encoder = encoder
    }

    companion object {
        private val DEFAULT = NO

        @JvmStatic
        fun findById(id: Int?): Compression {
            if (id == null) {
                return DEFAULT
            }

            return entries.first { it.id == id}
        }

        /**
         * Get the compression encoder
         *
         * @param compression compression ID
         * @return encoder
         */
        @JvmStatic
        fun getEncoder(compression: Int?): CompressionEncoder {
            val o = findById(compression)

            if (o.encoder == null) {
                throw TiffException("Compression not supported: $o")
            }

            return o.encoder
        }

        /**
         * Get the compression decoder
         *
         * @param compression compression ID
         * @return encoder
         */
        @JvmStatic
        fun getDecoder(compression: Int?): CompressionDecoder {
            return findById(compression).decoder
        }
    }
}
