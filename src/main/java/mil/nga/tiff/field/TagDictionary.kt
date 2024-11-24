package mil.nga.tiff.field

import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.field.type.NumericFieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat

/**
 * Dictionary for field tags and their types
 *
 * Default implementation is [DefaultTagDictionary].
 */
interface TagDictionary {

    /**
     * Find field type for given type ID
     *
     * @param id field type number
     * @return field type
     */
    fun findTypeById(id: Int): GenericFieldType<*>?

    /**
     * Find the field type for given sample format and and bits per sample
     *
     * @param sampleFormat  sample format
     * @param bitsPerSample bits per sample
     * @return field type
     */
    fun findNumericTypeBySampleParams(sampleFormat: SampleFormat, bitsPerSample: Int): NumericFieldType<*>?

    /**
     * Find a field tag by id
     *
     * @param id tag id
     * @return field tag type
     */
    fun findTagById(id: Int): FieldTagType?

}
