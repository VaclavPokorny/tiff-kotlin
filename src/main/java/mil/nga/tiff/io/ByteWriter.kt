package mil.nga.tiff.io

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Write a byte array
 *
 * @param byteOrder byte order
 * @param outputStream output stream
 *
 * @author osbornb
 */
class ByteWriter @JvmOverloads constructor(val byteOrder: ByteOrder, private val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()) : AutoCloseable {

    /**
     * Close the byte writer
     */
    @Throws(IOException::class)
    override fun close() {
        outputStream.close()
    }

    fun bytes(): ByteArray {
        return outputStream.toByteArray()
    }

    /**
     * Get the current size in bytes written
     *
     * @return bytes written
     */
    fun size(): Int {
        return outputStream.size()
    }

    /**
     * Write a String
     *
     * @param value string value
     * @return bytes written
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeString(value: String): Int {
        val valueBytes = value.toByteArray()
        outputStream.write(valueBytes)
        return valueBytes.size
    }

    /**
     * Write a byte
     *
     * @param value byte
     */
    fun writeByte(value: Byte) {
        outputStream.write(value.toInt())
    }

    /**
     * Write an unsigned byte
     *
     * @param value unsigned byte as a short
     */
    fun writeUnsignedByte(value: Short) {
        outputStream.write((value.toInt() and 0xff).toByte().toInt())
    }

    /**
     * Write the bytes
     *
     * @param value bytes
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeBytes(value: ByteArray) {
        outputStream.write(value)
    }

    /**
     * Write a short
     *
     * @param value short
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeShort(value: Short) {
        val valueBytes = ByteArray(2)
        val byteBuffer = ByteBuffer.allocate(2).order(byteOrder).putShort(value)
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write an unsigned short
     *
     * @param value unsigned short as an int
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeUnsignedShort(value: Int) {
        val valueBytes = ByteArray(2)
        val byteBuffer = ByteBuffer.allocate(2).order(byteOrder).putShort((value and 0xffff).toShort())
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write an integer
     *
     * @param value int
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeInt(value: Int) {
        val valueBytes = ByteArray(4)
        val byteBuffer = ByteBuffer.allocate(4).order(byteOrder).putInt(value)
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write an unsigned int
     *
     * @param value unsigned int as long
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeUnsignedInt(value: Long) {
        val valueBytes = ByteArray(4)
        val byteBuffer = ByteBuffer.allocate(4).order(byteOrder).putInt((value and 0xffffffffL).toInt())
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write a float
     *
     * @param value float
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeFloat(value: Float) {
        val valueBytes = ByteArray(4)
        val byteBuffer = ByteBuffer.allocate(4).order(byteOrder).putFloat(value)
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write a double
     *
     * @param value double
     * @throws IOException upon failure to write
     */
    @Throws(IOException::class)
    fun writeDouble(value: Double) {
        val valueBytes = ByteArray(8)
        val byteBuffer = ByteBuffer.allocate(8).order(byteOrder).putDouble(value)
        byteBuffer.flip()
        byteBuffer[valueBytes]
        outputStream.write(valueBytes)
    }

    /**
     * Write filler 0 bytes
     *
     * @param count number of 0 bytes to write
     */
    fun writeFillerBytes(count: Long) {
        for (i in 0..<count) {
            writeUnsignedByte(0.toShort())
        }
    }
}
