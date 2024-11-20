package mil.nga.tiff.util

/**
 * TIFF exception
 */
class TiffException : RuntimeException {
    /**
     * Constructor
     */
    constructor() : super()

    /**
     * Constructor
     *
     * @param message error message
     */
    constructor(message: String?) : super(message)

    /**
     * Constructor
     *
     * @param message   error message
     * @param throwable cause
     */
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)

    /**
     * Constructor
     *
     * @param throwable cause
     */
    constructor(throwable: Throwable?) : super(throwable)

}
