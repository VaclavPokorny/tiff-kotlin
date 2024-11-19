package mil.nga.tiff.field.tag

/**
 * JPEG tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class JpegTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    JPEGProc(512, false),
    JPEGInterchangeFormat(513, false),
    JPEGInterchangeFormatLength(514, false),
    JPEGRestartInterval(515, false),
    JPEGLosslessPredictors(517, true),
    JPEGPointTransforms(518, true),
    JPEGQTables(519, true),
    JPEGDCTables(520, true),
    JPEGACTables(521, true)
}
