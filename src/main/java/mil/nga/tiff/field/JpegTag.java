package mil.nga.tiff.field;

/**
 * JPEG tags
 */
public enum JpegTag implements FieldTagType {
    JPEGProc(512, false),
    JPEGInterchangeFormat(513, false),
    JPEGInterchangeFormatLength(514, false),
    JPEGRestartInterval(515, false),
    JPEGLosslessPredictors(517, true),
    JPEGPointTransforms(518, true),
    JPEGQTables(519, true),
    JPEGDCTables(520, true),
    JPEGACTables(521, true);

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
    JpegTag(int id, boolean array) {
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
