package mil.nga.tiff.field.tag

import mil.nga.tiff.field.FieldTagType

/**
 * EXIF tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class ExifTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    ApertureValue(37378, false),
    ColorSpace(40961, false),
    DateTimeDigitized(36868, false),
    DateTimeOriginal(36867, false),
    ExifIFD(34665, false),
    ExifVersion(36864, false),
    ExposureTime(33434, false),
    FileSource(41728, false),
    Flash(37385, false),
    FlashpixVersion(40960, false),
    FNumber(33437, false),
    ImageUniqueID(42016, false),
    LightSource(37384, false),
    MakerNote(37500, false),
    ShutterSpeedValue(37377, false),
    UserComment(37510, false)
}
