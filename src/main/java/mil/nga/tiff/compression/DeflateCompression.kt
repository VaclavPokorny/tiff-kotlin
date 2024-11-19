package mil.nga.tiff.compression

import mil.nga.tiff.io.IOUtils
import mil.nga.tiff.util.TiffException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteOrder
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * Deflate Compression
 */
class DeflateCompression : CompressionDecoder, CompressionEncoder {
    override fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        try {
            val inflater = Inflater()
            inflater.setInput(bytes)
            val outputStream = ByteArrayOutputStream(bytes.size)
            val buffer = ByteArray(IOUtils.COPY_BUFFER_SIZE)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                outputStream.write(buffer, 0, count)
            }
            outputStream.close()

            return outputStream.toByteArray()
        } catch (e: IOException) {
            throw TiffException("Failed close decoded byte stream", e)
        } catch (e: DataFormatException) {
            throw TiffException("Data format error while decoding stream", e)
        }
    }

    override fun rowEncoding(): Boolean {
        return false
    }

    override fun encode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        try {
            val deflater = Deflater()
            deflater.setInput(bytes)
            val outputStream = ByteArrayOutputStream(bytes.size)
            deflater.finish()
            val buffer = ByteArray(IOUtils.COPY_BUFFER_SIZE)
            while (!deflater.finished()) {
                val count = deflater.deflate(buffer) // returns the generated code... index
                outputStream.write(buffer, 0, count)
            }

            outputStream.close()
            return outputStream.toByteArray()
        } catch (e: IOException) {
            throw TiffException("Failed close encoded stream", e)
        }
    }
}
