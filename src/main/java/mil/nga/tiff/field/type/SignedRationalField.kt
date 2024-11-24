package mil.nga.tiff.field.type

import mil.nga.tiff.domain.SignedRational
import mil.nga.tiff.field.FieldType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second the denominator
 */
@FieldType(name = "SRATIONAL", id = 10, bytesPerSample = 8)
object SignedRationalField : RationalField<SignedRational>() {
    override fun readValue(reader: ByteReader): SignedRational {
        return SignedRational(
            reader.readInt(),
            reader.readInt()
        )
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: SignedRational): Int {
        writer.writeInt(value.numerator)
        writer.writeInt(value.denominator)
        return metadata().bytesPerSample
    }
}
