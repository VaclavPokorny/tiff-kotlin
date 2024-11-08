package mil.nga.tiff.field;

import mil.nga.tiff.field.type.ASCIIField;
import mil.nga.tiff.field.type.AbstractFieldType;
import mil.nga.tiff.field.type.DoubleField;
import mil.nga.tiff.field.type.FloatField;
import mil.nga.tiff.field.type.SignedByteField;
import mil.nga.tiff.field.type.SignedLongField;
import mil.nga.tiff.field.type.SignedRationalField;
import mil.nga.tiff.field.type.SignedShortField;
import mil.nga.tiff.field.type.UndefinedField;
import mil.nga.tiff.field.type.UnsignedByteField;
import mil.nga.tiff.field.type.UnsignedLongField;
import mil.nga.tiff.field.type.UnsignedRationalField;
import mil.nga.tiff.field.type.UnsignedShortField;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

/**
 * Field Types
 *
 * @author osbornb
 */
public enum FieldType {

    /**
     * 8-bit unsigned integer
     */
    BYTE(1, new UnsignedByteField()),

    /**
     * 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL
     * (binary zero)
     */
    ASCII(2, new ASCIIField()),

    /**
     * 16-bit (2-byte) unsigned integer
     */
    SHORT(3, new UnsignedShortField()),

    /**
     * 32-bit (4-byte) unsigned integer
     */
    LONG(4, new UnsignedLongField()),

    /**
     * Two LONGs: the first represents the numerator of a fraction; the second,
     * the denominator
     */
    RATIONAL(5, new UnsignedRationalField()),

    /**
     * An 8-bit signed (twos-complement) integer
     */
    SBYTE(6, new SignedByteField()),

    /**
     * An 8-bit byte that may contain anything, depending on the definition of
     * the field
     */
    UNDEFINED(7, new UndefinedField()),

    /**
     * A 16-bit (2-byte) signed (twos-complement) integer
     */
    SSHORT(8, new SignedShortField()),

    /**
     * A 32-bit (4-byte) signed (twos-complement) integer
     */
    SLONG(9, new SignedLongField()),

    /**
     * Two SLONG’s: the first represents the numerator of a fraction, the second
     * the denominator
     */
    SRATIONAL(10, new SignedRationalField()),

    /**
     * Single precision (4-byte) IEEE format
     */
    FLOAT(11, new FloatField()),

    /**
     * Double precision (8-byte) IEEE format
     */
    DOUBLE(12, new DoubleField());


    private final int value;
    private final AbstractFieldType fieldType;

    FieldType(int value, AbstractFieldType fieldType) {
        this.value = value;
        this.fieldType = fieldType;
    }

    /**
     * Get the field type value
     *
     * @return field type value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get field definition
     *
     * @return definition
     */
    public AbstractFieldType getDefinition() {
        return fieldType;
    }

    /**
     * Get the field type
     *
     * @param id field type number
     * @return field type
     */
    public static FieldType findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.value == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Unsupported field type ID: " + id));
    }

    /**
     * Get the field type of the sample format and bits per sample
     *
     * @param sampleFormat  sample format
     * @param bitsPerSample bits per sample
     * @return field type
     * @since 2.0.0
     */
    public static FieldType findBySampleParams(SampleFormat sampleFormat, int bitsPerSample) {
        return Arrays.stream(values())
            .filter(o -> o.fieldType.hasSampleFormat(sampleFormat))
            .filter(o -> o.fieldType.hasBitsPerSample(bitsPerSample))
            .findAny()
            .orElseThrow(() -> new TiffException("Unsupported field type for sample format: " + sampleFormat + ", bits per sample: " + bitsPerSample));
    }

}
