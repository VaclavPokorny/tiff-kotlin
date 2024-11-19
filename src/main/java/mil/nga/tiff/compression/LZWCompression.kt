package mil.nga.tiff.compression

import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.util.TiffException
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow

/**
 * LZW Compression
 */
class LZWCompression : CompressionDecoder, CompressionEncoder {
    /**
     * Table entries
     */
    private val table: MutableMap<Int, Array<Int>> = HashMap()

    /**
     * Current max table code
     */
    private var maxCode = 0

    /**
     * Current byte length
     */
    private var byteLength = 0

    /**
     * Current byte compression position
     */
    private var position = 0

    override fun decode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        // Create the byte reader and decoded stream to write to

        val reader = ByteReader(bytes, byteOrder)
        val decodedStream = ByteArrayOutputStream()

        // Initialize the table, starting position, and old code
        initializeTable()
        position = 0
        var oldCode = 0

        // Read codes until end of input
        var code = getNextCode(reader)
        while (code != EOI_CODE) {
            // If a clear code

            if (code == CLEAR_CODE) {
                // Reset the table

                initializeTable()

                // Read past clear codes
                do {
                    code = getNextCode(reader)
                } while (code == CLEAR_CODE)

                if (code == EOI_CODE) {
                    break
                }
                if (code > CLEAR_CODE) {
                    throw TiffException("Corrupted code at scan line: $code")
                }

                // Write the code value
                val value: Array<Int> = table[code]!!
                writeValue(decodedStream, value)
            } else {
                // If already in the table

                val value: Array<Int>? = table[code]
                if (value != null) {
                    // Write the code value

                    writeValue(decodedStream, value)

                    // Create new value and add to table
                    val newValue = table[oldCode]!! + table[code]!![0]
                    addToTable(newValue)
                } else {
                    // Create and write new value from old value

                    val oldValue = table[oldCode]!!
                    val newValue = oldValue + oldValue[0]
                    writeValue(decodedStream, newValue)

                    // Write value to the table
                    addToTable(code, newValue)
                }
            }
            oldCode = code

            // Get the next code
            code = getNextCode(reader)
        }

        return decodedStream.toByteArray()
    }

    /**
     * Initialize the table and byte length
     */
    private fun initializeTable() {
        table.clear()
        for (i in 0..257) {
            table[i] = arrayOf(i)
        }
        maxCode = 257
        byteLength = MIN_BITS
    }

    /**
     * Check the byte length and increase if needed
     */
    private fun checkByteLength() {
        if (maxCode >= 2.0.pow(byteLength.toDouble()) - 2) {
            byteLength++
        }
    }

    /**
     * Add the value to the table
     *
     * @param value value
     */
    private fun addToTable(value: Array<Int>) {
        addToTable(maxCode + 1, value)
    }

    /**
     * Add the code and value to the table
     *
     * @param code  code
     * @param value value
     */
    private fun addToTable(code: Int, value: Array<Int>) {
        table[code] = value
        maxCode = max(maxCode.toDouble(), code.toDouble()).toInt()
        checkByteLength()
    }

    /**
     * Concatenate the two values
     *
     * @param first  first value
     * @param second second value
     * @return concatenated value
     */
    private fun concat(first: Array<Int?>, second: Int?): Array<Int?> {
        return concat(first, arrayOf(second))
    }

    /**
     * Concatenate the two values
     *
     * @param first  first value
     * @param second second value
     * @return concatenated value
     */
    private fun concat(first: Array<Int?>, second: Array<Int?>): Array<Int?> {
        val combined = arrayOfNulls<Int>(first.size + second.size)
        System.arraycopy(first, 0, combined, 0, first.size)
        System.arraycopy(second, 0, combined, first.size, second.size)
        return combined
    }

    /**
     * Write the value to the decoded stream
     *
     * @param decodedStream decoded byte stream
     * @param value         value
     */
    private fun writeValue(decodedStream: ByteArrayOutputStream, value: Array<Int>) {
        for (integer in value) {
            decodedStream.write(integer)
        }
    }

    /**
     * Get the next code
     *
     * @param reader byte reader
     * @return code
     */
    private fun getNextCode(reader: ByteReader): Int {
        val nextByte = getByte(reader)
        position += byteLength
        return nextByte
    }

    /**
     * Get the next byte
     *
     * @param reader byte reader
     * @return byte
     */
    private fun getByte(reader: ByteReader): Int {
        val d = position % 8
        val a = floor(position / 8.0).toInt()
        val de = 8 - d
        val ef = (position + byteLength) - ((a + 1) * 8)
        var fg = 8 * (a + 2) - (position + byteLength)
        val dg = (a + 2) * 8 - position
        fg = max(0.0, fg.toDouble()).toInt()
        if (a >= reader.byteLength()) {
            return EOI_CODE
        }
        var chunk1 = (reader.readUnsignedByte(a).toInt()) and ((2.0.pow((8 - d).toDouble()) - 1).toInt())
        chunk1 = chunk1 shl (byteLength - de)
        var chunks = chunk1
        if (a + 1 < reader.byteLength()) {
            var chunk2 = reader.readUnsignedByte(a + 1).toInt() ushr fg
            chunk2 = chunk2 shl max(0.0, (byteLength - dg).toDouble()).toInt()
            chunks += chunk2
        }
        if (ef > 8 && a + 2 < reader.byteLength()) {
            val hi = (a + 3) * 8 - (position + byteLength)
            val chunk3 = reader.readUnsignedByte(a + 2).toInt() ushr hi
            chunks += chunk3
        }
        return chunks
    }

    override fun rowEncoding(): Boolean {
        return false
    }

    override fun encode(bytes: ByteArray, byteOrder: ByteOrder): ByteArray {
        throw TiffException("LZW encoder is not yet implemented")
    }

    companion object {
        /**
         * Clear code
         */
        private const val CLEAR_CODE = 256

        /**
         * End of information code
         */
        private const val EOI_CODE = 257

        /**
         * Min bits
         */
        private const val MIN_BITS = 9
    }
}
