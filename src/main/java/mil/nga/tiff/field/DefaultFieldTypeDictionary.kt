package mil.nga.tiff.field

import mil.nga.tiff.field.type.*
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.util.TiffException
import java.util.stream.Stream

class DefaultFieldTypeDictionary : FieldTypeDictionary {
    override fun findById(id: Int): GenericFieldType<*> {
        return ALL_TYPES.firstOrNull { it.metadata().id == id } ?:
            throw TiffException("Unsupported field type ID: $id")
    }

    override fun findBySampleParams(sampleFormat: SampleFormat, bitsPerSample: Int): NumericFieldType<*> {
        return NUMERIC_TYPES.firstOrNull() { it.metadata().sampleFormat == sampleFormat && it.metadata().bytesPerSample * 8 == bitsPerSample } ?:
            throw TiffException("Unsupported field type for sample format: $sampleFormat, bits per sample: $bitsPerSample")
    }

    companion object {
        private val NUMERIC_TYPES: List<NumericFieldType<*>> = listOf<NumericFieldType<*>>(
            UnsignedByteField,
            UnsignedShortField,
            UnsignedLongField,
            SignedByteField,
            SignedShortField,
            SignedLongField,
            FloatField,
            DoubleField
        )

        private val ALL_TYPES: List<GenericFieldType<*>> = Stream.concat(
            Stream.of(
                UndefinedField,
                ASCIIField,
                UnsignedRationalField,
                SignedRationalField
            ),
            NUMERIC_TYPES.stream()
        ).toList()
    }
}
