package mil.nga.tiff.field;

/**
 * GEO TIFF tags
 */
public enum GeoTiffTag implements FieldTagType {
    ModelPixelScale(33550, false),
    ModelTiepoint(33922, false),
    ModelTransformation(34264, false),
    GeoKeyDirectory(34735, false),
    GeoDoubleParams(34736, false),
    GeoAsciiParams(34737, false);

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
    GeoTiffTag(int id, boolean array) {
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
