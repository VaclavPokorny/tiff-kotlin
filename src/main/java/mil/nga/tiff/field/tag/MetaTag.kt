package mil.nga.tiff.field.tag

import mil.nga.tiff.field.FieldTagType

/**
 * Meta tags containg embedded custom data
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class MetaTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    IPTC(33723, false),
    ICCProfile(34675, false),
    XMP(700, false),
    Photoshop(34377, false)
}
