package mil.nga.tiff.field

import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat

/**
 * Dictionary for field types lookup
 *
 * Default implementation is [DefaultFieldTypeDictionary].
 */
interface FieldTypeDictionary {
    /**
     * Find field type for given type ID
     *
     * @param id field type number
     * @return field type
     */
    fun findById(id: Int): GenericFieldType<*>?

    /**
     * Find the field type for given sample format and and bits per sample
     *
     * @param sampleFormat  sample format
     * @param bitsPerSample bits per sample
     * @return field type
     */
    fun findBySampleParams(sampleFormat: SampleFormat, bitsPerSample: Int): NumericFieldType<*>?
}
