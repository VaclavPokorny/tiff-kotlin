package mil.nga.tiff.field.type

import mil.nga.tiff.domain.UnsignedRational
import mil.nga.tiff.field.FieldType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException

/**
 * Two LONGs: the first represents the numerator of a fraction; the second,
 * the denominator
 */
@FieldType(id = 5, bytesPerSample = 8)
object UnsignedRationalField : RationalField<UnsignedRational>() {
    override fun readValue(reader: ByteReader): UnsignedRational {
        return UnsignedRational(
            reader.readUnsignedInt(),
            reader.readUnsignedInt()
        )
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: UnsignedRational): Int {
        writer.writeUnsignedInt(value.numerator)
        writer.writeUnsignedInt(value.denominator)
        return metadata().bytesPerSample
    }
}
