package mil.nga.tiff.internal.predictor

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import mil.nga.tiff.util.TiffException
import java.io.IOException
import java.nio.ByteOrder

abstract class AbstractPredictor : Predictor {
    override fun decode(
        bytes: ByteArray,
        width: Int,
        height: Int,
        bitsPerSample: List<Int>,
        planarConfiguration: PlanarConfiguration,
        byteOrder: ByteOrder
    ): ByteArray {
        val bytesPerSample = getBytesPerSample(bitsPerSample)
        val samples = if (planarConfiguration == PlanarConfiguration.PLANAR) 1 else bitsPerSample.size

        val reader = ByteReader(bytes, byteOrder)
        try {
            ByteWriter(byteOrder).use { writer ->
                for (row in 0 until height) {
                    // Last strip will be truncated if imageHeight % stripHeight != 0
                    if (row * samples * width * bytesPerSample >= bytes.size) {
                        break
                    }

                    decode(reader, writer, width, bytesPerSample, samples)
                }
                return writer.bytes()
            }
        } catch (e: IOException) {
            throw TiffException(e)
        }
    }

    protected abstract fun decode(
        reader: ByteReader,
        writer: ByteWriter,
        width: Int,
        bytesPerSample: Int,
        samples: Int
    )

    protected fun getBytesPerSample(bitsPerSample: List<Int>): Int {
        val numBitsPerSample: Int = bitsPerSample.first()
        if (numBitsPerSample % 8 != 0) {
            throw TiffException("When decoding with predictor, only multiple of 8 bits are supported")
        }

        for (i in 1 until bitsPerSample.size) {
            if (bitsPerSample[i] != numBitsPerSample) {
                throw TiffException("When decoding with predictor, all samples must have the same size")
            }
        }

        return numBitsPerSample / 8
    }

    /**
     * Read a sample value
     *
     * @param reader         byte reader
     * @param bytesPerSample bytes per sample
     * @return sample value
     */
    protected fun readValue(reader: ByteReader, bytesPerSample: Int): Int {
        return when (bytesPerSample) {
            1 -> reader.readByte().toInt()
            2 -> reader.readShort().toInt()
            4 -> reader.readInt()
            else -> throw TiffException("Predictor not supported with $bytesPerSample bytes per sample")
        }
    }

    /**
     * Write a sample value
     *
     * @param writer         byte writer
     * @param bytesPerSample bytes per sample
     * @param value          sample value
     */
    protected fun writeValue(writer: ByteWriter, bytesPerSample: Int, value: Int) {
        try {
            when (bytesPerSample) {
                1 -> writer.writeByte(value.toByte())
                2 -> writer.writeShort(value.toShort())
                4 -> writer.writeInt(value)
                else -> throw TiffException("Predictor not supported with $bytesPerSample bytes per sample")
            }
        } catch (e: IOException) {
            throw TiffException("Failed to write value: $value, bytes: $bytesPerSample", e)
        }
    }
}
