package mil.nga.tiff.util

import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/**
 * TIFF Byte Order identifier
 */
enum class TiffByteOrder(val id: String, val byteOrder: ByteOrder) {
    LITTLE_ENDIAN("II", ByteOrder.LITTLE_ENDIAN),
    BIG_ENDIAN("MM", ByteOrder.BIG_ENDIAN);

    companion object {
        @JvmStatic
        fun findById(id: String): TiffByteOrder {
            return entries.first { it.id == id }
        }

        @JvmStatic
        fun findByByteOrder(byteOrder: ByteOrder): TiffByteOrder {
            return entries.first { it.byteOrder == byteOrder }
        }

        @JvmStatic
        fun determineFromData(bytes: ByteArray): ByteOrder {
            // Read the first 2 bytes
            if (bytes.size < 2) {
                throw TiffException("No more remaining bytes to read. Total Bytes: " + bytes.size + ", Attempted to read: 2")
            }
            val byteOrderString = String(bytes, 0, 2, StandardCharsets.US_ASCII)

            // Determine the byte order
            return findById(byteOrderString).byteOrder
        }
    }

}
