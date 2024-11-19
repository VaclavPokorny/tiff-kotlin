package mil.nga.tiff.util;

/**
 * TIFF Constants
 */
public class TiffConstants {

    /**
     * TIFF File Identifier
     */
    public static final int FILE_IDENTIFIER = 42;

    /**
     * TIFF header bytes
     */
    public static final int HEADER_BYTES = 8;

    /**
     * Image File Directory header / number of entries bytes
     */
    public static final int IFD_HEADER_BYTES = 2;

    /**
     * Image File Directory offset to the next IFD bytes
     */
    public static final int IFD_OFFSET_BYTES = 4;

    /**
     * Image File Directory entry bytes
     */
    public static final int IFD_ENTRY_BYTES = 12;

    /**
     * Default max bytes per strip when writing strips
     */
    public static final int DEFAULT_MAX_BYTES_PER_STRIP = 8000;

}
