package mil.nga.tiff.field.tag;

/**
 * EXIF tags
 */
public enum ExifTag implements FieldTagType {
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
    UserComment(37510, false);

    /**
     * Tag id
     */
    private final int id;

    /**
     * True if an array type
     */
    private final boolean array;

    /**
     * Constructor
     *
     * @param id    tag id
     * @param array true if an array type
     */
    ExifTag(int id, boolean array) {
        this.id = id;
        this.array = array;
    }

    /**
     * Is this field an array type
     *
     * @return true if array type
     */
    @Override
    public boolean isArray() {
        return array;
    }

    /**
     * Get the tag id
     *
     * @return tag id
     */
    @Override
    public int getId() {
        return id;
    }

}
