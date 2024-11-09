package mil.nga.tiff.field;

/**
 * Meta tags containg embedded custom data
 */
public enum MetaTag implements FieldTagType {
    IPTC(33723, false),
    ICCProfile(34675, false),
    XMP(700, false),
    Photoshop(34377, false);

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
    MetaTag(int id, boolean array) {
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
