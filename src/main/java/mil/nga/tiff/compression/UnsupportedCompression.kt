package mil.nga.tiff.compression

import mil.nga.tiff.util.TiffException
import java.nio.ByteOrder

/**
 * Unsupported compression
 */
class UnsupportedCompression(private val message: String) : CompressionDecoder, CompressionEncoder {
    override fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        throw TiffException(this.message)
    }

    override fun rowEncoding(): Boolean {
        return false
    }

    override fun encode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        throw TiffException(this.message)
    }
}
