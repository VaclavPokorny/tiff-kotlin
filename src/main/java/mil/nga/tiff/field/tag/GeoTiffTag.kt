package mil.nga.tiff.field.tag

/**
 * GEO TIFF tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class GeoTiffTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    ModelPixelScale(33550, false),
    ModelTiepoint(33922, false),
    ModelTransformation(34264, false),
    GeoKeyDirectory(34735, false),
    GeoDoubleParams(34736, false),
    GeoAsciiParams(34737, false)
}
