package mil.nga.tiff.field

import mil.nga.tiff.field.tag.*
import mil.nga.tiff.field.type.*
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.util.TiffException
import java.util.stream.Stream

object DefaultTagDictionary : TagDictionary {

    private val ALL_TAGS: List<FieldTagType> = listOf(
        ExifTag.entries,
        GdalTag.entries,
        GeoTiffTag.entries,
        JpegTag.entries,
        MetaTag.entries,
        TiffBaselineTag.entries,
        TiffExtendedTag.entries
    ).flatten();

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

    override fun findTypeById(id: Int): GenericFieldType<*> {
        return ALL_TYPES.firstOrNull { it.metadata().id == id } ?:
            throw TiffException("Unsupported field type ID: $id")
    }

    override fun findNumericTypeBySampleParams(sampleFormat: SampleFormat, bitsPerSample: Int): NumericFieldType<*> {
        return NUMERIC_TYPES.firstOrNull() { it.metadata().sampleFormat == sampleFormat && it.metadata().bytesPerSample * 8 == bitsPerSample } ?:
            throw TiffException("Unsupported field type for sample format: $sampleFormat, bits per sample: $bitsPerSample")
    }

    override fun findTagById(id: Int): FieldTagType? {
        return ALL_TAGS.firstOrNull { it.id == id }
    }

}
