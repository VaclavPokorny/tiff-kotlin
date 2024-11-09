package mil.nga.tiff.field;

/**
 * GDAL tags
 */
public enum GdalTag implements FieldTagType {
    GDAL_METADATA(42112, false),
    GDAL_NODATA(42113, false);

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
    GdalTag(int id, boolean array) {
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
