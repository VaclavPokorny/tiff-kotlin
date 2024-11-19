package mil.nga.tiff.compression

import java.nio.ByteOrder

/**
 * Raw / no compression
 */
class RawCompression : CompressionDecoder, CompressionEncoder {
    override fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        return bytes
    }

    override fun rowEncoding(): Boolean {
        return false
    }

    override fun encode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        return bytes
    }
}
