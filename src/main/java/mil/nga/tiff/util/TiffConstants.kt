package mil.nga.tiff.util

/**
 * TIFF Constants
 */
object TiffConstants {
    /**
     * TIFF File Identifier
     */
    const val FILE_IDENTIFIER: Int = 42

    /**
     * TIFF header bytes
     */
    const val HEADER_BYTES: Int = 8

    /**
     * Image File Directory header / number of entries bytes
     */
    const val IFD_HEADER_BYTES: Int = 2

    /**
     * Image File Directory offset to the next IFD bytes
     */
    const val IFD_OFFSET_BYTES: Int = 4

    /**
     * Image File Directory entry bytes
     */
    const val IFD_ENTRY_BYTES: Int = 12

    /**
     * Default max bytes per strip when writing strips
     */
    const val DEFAULT_MAX_BYTES_PER_STRIP: Int = 8000
}
