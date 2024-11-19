package mil.nga.tiff.domain.container

import mil.nga.tiff.field.type.GenericFieldType

@JvmRecord
data class FieldMultiValueContainer<T>(val type: GenericFieldType<T>, val value: List<T>)
