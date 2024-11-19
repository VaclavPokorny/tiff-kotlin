package mil.nga.tiff.field.tag

/**
 * GDAL tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class GdalTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    GDAL_METADATA(42112, false),
    GDAL_NODATA(42113, false)
}
