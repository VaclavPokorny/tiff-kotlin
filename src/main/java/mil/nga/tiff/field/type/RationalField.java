package mil.nga.tiff.field.type;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
abstract sealed class RationalField<T> extends SingleValueFieldType<T> permits UnsignedRationalField, SignedRationalField {

}
