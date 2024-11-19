package mil.nga.tiff.field

import mil.nga.tiff.field.type.GenericFieldType

class FieldValue(private val typeId: Int, private val type: GenericFieldType<*>, private val value: Any)
