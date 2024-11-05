package mil.nga.tiff.util;

/**
 * TIFF Constants
 * 
 * @author osbornb
 */
public class TiffConstants {

	/**
	 * Little Endian byte order string
	 */
	public static final String BYTE_ORDER_LITTLE_ENDIAN = "II";

	/**
	 * Big Endian byte order string
	 */
	public static final String BYTE_ORDER_BIG_ENDIAN = "MM";

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

    public static class ExtraSamples {
        public static final int UNSPECIFIED = 0;
        public static final int ASSOCIATED_ALPHA = 1;
        public static final int UNASSOCIATED_ALPHA = 2;
    }

    public static class FillOrder {
        public static final int LOWER_COLUMN_HIGHER_ORDER = 1;
        public static final int LOWER_COLUMN_LOWER_ORDER = 2;
    }

    public static class GrayResponse {
        public static final int TENTHS = 1;
        public static final int HUNDREDTHS = 2;
        public static final int THOUSANDTHS = 3;
        public static final int TEN_THOUSANDTHS = 4;
        public static final int HUNDRED_THOUSANDTHS = 5;
    }

    public static class Orientation {
        public static final int TOP_ROW_LEFT_COLUMN = 1;
        public static final int TOP_ROW_RIGHT_COLUMN = 2;
        public static final int BOTTOM_ROW_RIGHT_COLUMN = 3;
        public static final int BOTTOM_ROW_LEFT_COLUMN = 4;
        public static final int LEFT_ROW_TOP_COLUMN = 5;
        public static final int RIGHT_ROW_TOP_COLUMN = 6;
        public static final int RIGHT_ROW_BOTTOM_COLUMN = 7;
        public static final int LEFT_ROW_BOTTOM_COLUMN = 8;
    }

    public static class PhotometricInterpretation {
        public static final int WHITE_IS_ZERO = 0;
        public static final int BLACK_IS_ZERO = 1;
        public static final int RGB = 2;
        public static final int PALETTE = 3;
        public static final int TRANSPARENCY = 4;
    }

    public static class PlanarConfiguration {
        public static final int CHUNKY = 1;
        public static final int PLANAR = 2;
    }

    public static class ResolutionUnit {
        public static final int NO = 1;
        public static final int INCH = 2;
        public static final int CENTIMETER = 3;
    }

    public static class SampleFormat {
        public static final int UNSIGNED_INT = 1;
        public static final int SIGNED_INT = 2;
        public static final int FLOAT = 3;
        public static final int UNDEFINED = 4;
    }

    public static class SubfileType {
        public static final int FULL = 1;
        public static final int REDUCED = 2;
        public static final int SINGLE_PAGE_MULTI_PAGE = 3;
    }

    public static class Threshholding {
        public static final int NO = 1;
        public static final int ORDERED = 2;
        public static final int RANDOM = 3;
    }

    public static class DifferencingPredictor {
        public static final int NO = 1;
        public static final int HORIZONTAL = 2;
        public static final int FLOATINGPOINT = 3;
    }

}
