package mil.nga.tiff.compression

import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.util.TiffException
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder

/**
 * Packbits Compression
 */
class PackbitsCompression : CompressionDecoder, CompressionEncoder {
    override fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        val reader = ByteReader(bytes, byteOrder)

        val decodedStream = ByteArrayOutputStream()

        while (reader.hasByte()) {
            var header = reader.readByte().toInt()
            if (header != -128) {
                if (header < 0) {
                    val next = reader.readUnsignedByte().toInt()
                    header = -header
                    for (i in 0..header) {
                        decodedStream.write(next)
                    }
                } else {
                    for (i in 0..header) {
                        decodedStream.write(reader.readUnsignedByte().toInt())
                    }
                }
            }
        }

        return decodedStream.toByteArray()
    }

    override fun rowEncoding(): Boolean {
        return true
    }

    override fun encode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        throw TiffException("Packbits encoder is not yet implemented")
    }
}
