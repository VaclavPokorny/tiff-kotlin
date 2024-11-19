package mil.nga.tiff.compression

import java.nio.ByteOrder

/**
 * Compression decoder interface
 */
interface CompressionDecoder {
    /**
     * Decode the bytes
     *
     * @param bytes     bytes to decode
     * @param byteOrder byte order
     * @return decoded bytes
     */
    fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray
}
