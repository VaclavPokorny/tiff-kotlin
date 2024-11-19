package mil.nga.tiff.field

import mil.nga.tiff.field.type.enumeration.SampleFormat

/**
 * Field type metadata
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldType(
    val id: Int,
    val bytesPerSample: Int,
    val sampleFormat: SampleFormat = SampleFormat.UNDEFINED
)
