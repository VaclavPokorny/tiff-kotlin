package mil.nga.tiff.io

import mil.nga.tiff.util.TiffException
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Read through a byte array
 *
 * @param bytes     bytes
 * @param byteOrder byte order
 * @param nextByte  next byte index to read
 */
class ByteReader @JvmOverloads constructor(private val bytes: ByteArray, @JvmField val byteOrder: ByteOrder, var nextByte: Int = 0) {

    /**
     * Set the next byte to be read
     *
     * @param nextByte next byte
     */
    fun setNextByte(nextByte: Long) {
        if (nextByte >= bytes.size) {
            throw TiffException("Byte offset out of range. Total Bytes: " + bytes.size + ", Byte offset: " + nextByte)
        }
        this.nextByte = nextByte.toInt()
    }

    /**
     * Check if there is at least one more byte left to read
     *
     * @return true more bytes left to read
     */
    fun hasByte(): Boolean {
        return hasBytes(1)
    }

    /**
     * Check if there is at least one more byte left to read
     *
     * @param offset byte offset
     * @return true more bytes left to read
     */
    fun hasByte(offset: Int): Boolean {
        return hasBytes(offset, 1)
    }

    /**
     * Check if there are the provided number of bytes left to read
     *
     * @param count number of bytes
     * @return true if has at least the number of bytes left
     */
    fun hasBytes(count: Int): Boolean {
        return hasBytes(nextByte, count)
    }

    /**
     * Check if there are the provided number of bytes left to read
     *
     * @param offset byte offset
     * @param count  number of bytes
     * @return true if has at least the number of bytes left
     */
    fun hasBytes(offset: Int, count: Int): Boolean {
        return offset + count <= bytes.size
    }

    /**
     * Read a String from the provided number of bytes
     *
     * @param num number of bytes
     * @return String
     * @throws UnsupportedEncodingException upon string encoding error
     */
    @Throws(UnsupportedEncodingException::class)
    fun readString(num: Int): String? {
        val value = readString(nextByte, num)
        nextByte += num
        return value
    }

    /**
     * Read a String from the provided number of bytes
     *
     * @param offset byte offset
     * @param num    number of bytes
     * @return String
     */
    fun readString(offset: Int, num: Int): String? {
        verifyRemainingBytes(offset, num)
        var value: String? = null
        if (num != 1 || bytes[offset].toInt() != 0) {
            value = String(bytes, offset, num, StandardCharsets.US_ASCII)
        }
        return value
    }

    /**
     * Read a byte
     *
     * @return byte
     */
    fun readByte(): Byte {
        val value = readByte(nextByte)
        nextByte++
        return value
    }

    /**
     * Read a byte
     *
     * @param offset byte offset
     * @return byte
     */
    fun readByte(offset: Int): Byte {
        verifyRemainingBytes(offset, 1)
        return bytes[offset]
    }

    /**
     * Read an unsigned byte
     *
     * @return unsigned byte as short
     */
    fun readUnsignedByte(): Short {
        val value = readUnsignedByte(nextByte)
        nextByte++
        return value
    }

    /**
     * Read an unsigned byte
     *
     * @param offset byte offset
     * @return unsigned byte as short
     */
    fun readUnsignedByte(offset: Int): Short {
        return ((readByte(offset).toInt() and 0xff).toShort())
    }

    /**
     * Read a number of bytes
     *
     * @param num number of bytes
     * @return bytes
     */
    fun readBytes(num: Int): ByteArray {
        val readBytes = readBytes(nextByte, num)
        nextByte += num
        return readBytes
    }

    /**
     * Read a number of bytes
     *
     * @param offset byte offset
     * @param num    number of bytes
     * @return bytes
     */
    fun readBytes(offset: Int, num: Int): ByteArray {
        verifyRemainingBytes(offset, num)
        return Arrays.copyOfRange(bytes, offset, offset + num)
    }

    /**
     * Read a short
     *
     * @return short
     */
    fun readShort(): Short {
        val value = readShort(nextByte)
        nextByte += 2
        return value
    }

    /**
     * Read a short
     *
     * @param offset byte offset
     * @return short
     */
    fun readShort(offset: Int): Short {
        verifyRemainingBytes(offset, 2)
        return ByteBuffer.wrap(bytes, offset, 2).order(byteOrder).getShort()
    }

    /**
     * Read an unsigned short
     *
     * @return unsigned short as int
     */
    fun readUnsignedShort(): Int {
        val value = readUnsignedShort(nextByte)
        nextByte += 2
        return value
    }

    /**
     * Read an unsigned short
     *
     * @param offset byte offset
     * @return unsigned short as int
     */
    fun readUnsignedShort(offset: Int): Int {
        return (readShort(offset).toInt() and 0xffff)
    }

    /**
     * Read an integer
     *
     * @return integer
     */
    fun readInt(): Int {
        val value = readInt(nextByte)
        nextByte += 4
        return value
    }

    /**
     * Read an integer
     *
     * @param offset byte offset
     * @return integer
     */
    fun readInt(offset: Int): Int {
        verifyRemainingBytes(offset, 4)
        return ByteBuffer.wrap(bytes, offset, 4).order(byteOrder).getInt()
    }

    /**
     * Read an unsigned int
     *
     * @return unsigned int as long
     */
    fun readUnsignedInt(): Long {
        val value = readUnsignedInt(nextByte)
        nextByte += 4
        return value
    }

    /**
     * Read an unsigned int
     *
     * @param offset byte offset
     * @return unsigned int as long
     */
    fun readUnsignedInt(offset: Int): Long {
        return (readInt(offset).toLong() and 0xffffffffL)
    }

    /**
     * Read a float
     *
     * @return float
     */
    fun readFloat(): Float {
        val value = readFloat(nextByte)
        nextByte += 4
        return value
    }

    /**
     * Read a float
     *
     * @param offset byte offset
     * @return float
     */
    fun readFloat(offset: Int): Float {
        verifyRemainingBytes(offset, 4)
        return ByteBuffer.wrap(bytes, offset, 4).order(byteOrder).getFloat()
    }

    /**
     * Read a double
     *
     * @return double
     */
    fun readDouble(): Double {
        val value = readDouble(nextByte)
        nextByte += 8
        return value
    }

    /**
     * Read a double
     *
     * @param offset byte offset
     * @return double
     */
    fun readDouble(offset: Int): Double {
        verifyRemainingBytes(offset, 8)
        return ByteBuffer.wrap(bytes, offset, 8).order(byteOrder).getDouble()
    }

    /**
     * Get the byte length
     *
     * @return byte length
     */
    fun byteLength(): Int {
        return bytes.size
    }

    /**
     * Verify with the remaining bytes that there are enough remaining to read
     * the provided amount
     *
     * @param offset      byte offset
     * @param bytesToRead number of bytes to read
     */
    private fun verifyRemainingBytes(offset: Int, bytesToRead: Int) {
        if (offset + bytesToRead > bytes.size) {
            throw TiffException("No more remaining bytes to read. Total Bytes: " + bytes.size + ", Byte offset: " + offset + ", Attempted to read: " + bytesToRead)
        }
    }
}
