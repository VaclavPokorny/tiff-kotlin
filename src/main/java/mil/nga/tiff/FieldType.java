package mil.nga.tiff;

import mil.nga.tiff.fields.*;
import mil.nga.tiff.io.ByteReader;
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
	BYTE(new ByteField()),

	/**
	 * 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL
	 * (binary zero)
	 */
	ASCII(new ASCIIField()),

	/**
	 * 16-bit (2-byte) unsigned integer
	 */
	SHORT(new ShortField()),

	/**
	 * 32-bit (4-byte) unsigned integer
	 */
	LONG(new LongField()),

	/**
	 * Two LONGs: the first represents the numerator of a fraction; the second,
	 * the denominator
	 */
	RATIONAL(new RationalField()),

	/**
	 * An 8-bit signed (twos-complement) integer
	 */
	SBYTE(new SignedByteField()),

	/**
	 * An 8-bit byte that may contain anything, depending on the definition of
	 * the field
	 */
	UNDEFINED(new UndefinedField()),

	/**
	 * A 16-bit (2-byte) signed (twos-complement) integer
	 */
	SSHORT(new SignedShortField()),

	/**
	 * A 32-bit (4-byte) signed (twos-complement) integer
	 */
	SLONG(new SignedLongField()),

	/**
	 * Two SLONG’s: the first represents the numerator of a fraction, the second
	 * the denominator
	 */
	SRATIONAL(new SignedRationalField()),

	/**
	 * Single precision (4-byte) IEEE format
	 */
	FLOAT(new FloatField()),

	/**
	 * Double precision (8-byte) IEEE format
	 */
	DOUBLE(new DoubleField());

	private final AbstractFieldType fieldType;

    FieldType(AbstractFieldType fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * Get the field type value
	 * 
	 * @return field type value
	 */
	public int getValue() {
		return ordinal() + 1;
	}

	/**
	 * Get the number of bytes per value
	 * 
	 * @return number of bytes
	 */
	public int getBytes() {
		return fieldType.getBytes();
	}

	/**
	 * Get the number of bits per value
	 * 
	 * @return number of bits
	 * @since 2.0.0
	 */
	public int getBits() {
		return fieldType.getBits();
	}

	/**
	 * Get the field type
	 * 
	 * @param fieldType
	 *            field type number
	 * @return field type
	 */
	public static FieldType getFieldType(int fieldType) {
		return FieldType.values()[fieldType - 1];
	}

	/**
	 * Get the field type of the sample format and bits per sample
	 * 
	 * @param sampleFormat
	 *            sample format
	 * @param bitsPerSample
	 *            bits per sample
	 * @return field type
	 * @since 2.0.0
	 */
	public static FieldType getFieldType(int sampleFormat, int bitsPerSample) {
		return Arrays.stream(values())
				.filter(o -> o.fieldType.hasSampleFormat(sampleFormat))
				.filter(o -> o.fieldType.hasBitsPerSample(bitsPerSample))
				.findAny()
				.orElseThrow(() -> new TiffException("Unsupported field type for sample format: " + sampleFormat + ", bits per sample: " + bitsPerSample));
	}

	/**
	 * Get the sample format of the field type
	 * 
	 * @param fieldType
	 *            field type
	 * @return sample format
	 * @since 2.0.0
	 */
	public static int getSampleFormat(FieldType fieldType) {
		return fieldType.fieldType.getSampleFormat();
	}

	public Number readRasterValueFromReader(ByteReader reader) {
		return fieldType.readRasterValueFromReader(reader);
	}

}
