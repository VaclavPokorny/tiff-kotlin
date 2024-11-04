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

    public static class Compression {
        public static final int COMPRESSION_NO = 1;
        public static final int COMPRESSION_CCITT_HUFFMAN = 2;
        public static final int COMPRESSION_T4 = 3;
        public static final int COMPRESSION_T6 = 4;
        public static final int COMPRESSION_LZW = 5;
        public static final int COMPRESSION_JPEG_OLD = 6;
        public static final int COMPRESSION_JPEG_NEW = 7;
        public static final int COMPRESSION_DEFLATE = 8;
        @Deprecated
        public static final int COMPRESSION_PKZIP_DEFLATE = 32946; // PKZIP-style Deflate encoding (Obsolete).
        public static final int COMPRESSION_PACKBITS = 32773;
    }

    public static class ExtraSamples {
        public static final int EXTRA_SAMPLES_UNSPECIFIED = 0;
        public static final int EXTRA_SAMPLES_ASSOCIATED_ALPHA = 1;
        public static final int EXTRA_SAMPLES_UNASSOCIATED_ALPHA = 2;
    }

    public static class FillOrder {
        public static final int FILL_ORDER_LOWER_COLUMN_HIGHER_ORDER = 1;
        public static final int FILL_ORDER_LOWER_COLUMN_LOWER_ORDER = 2;
    }

    public static class GrayResponse {
        public static final int GRAY_RESPONSE_TENTHS = 1;
        public static final int GRAY_RESPONSE_HUNDREDTHS = 2;
        public static final int GRAY_RESPONSE_THOUSANDTHS = 3;
        public static final int GRAY_RESPONSE_TEN_THOUSANDTHS = 4;
        public static final int GRAY_RESPONSE_HUNDRED_THOUSANDTHS = 5;
    }

    public static class Orientation {
        public static final int ORIENTATION_TOP_ROW_LEFT_COLUMN = 1;
        public static final int ORIENTATION_TOP_ROW_RIGHT_COLUMN = 2;
        public static final int ORIENTATION_BOTTOM_ROW_RIGHT_COLUMN = 3;
        public static final int ORIENTATION_BOTTOM_ROW_LEFT_COLUMN = 4;
        public static final int ORIENTATION_LEFT_ROW_TOP_COLUMN = 5;
        public static final int ORIENTATION_RIGHT_ROW_TOP_COLUMN = 6;
        public static final int ORIENTATION_RIGHT_ROW_BOTTOM_COLUMN = 7;
        public static final int ORIENTATION_LEFT_ROW_BOTTOM_COLUMN = 8;
    }

    public static class PhotometricInterpretation {
        public static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;
        public static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;
        public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
        public static final int PHOTOMETRIC_INTERPRETATION_PALETTE = 3;
        public static final int PHOTOMETRIC_INTERPRETATION_TRANSPARENCY = 4;
    }

    public static class PlanarConfiguration {
        public static final int PLANAR_CONFIGURATION_CHUNKY = 1;
        public static final int PLANAR_CONFIGURATION_PLANAR = 2;
    }

    public static class ResolutionUnit {
        public static final int RESOLUTION_UNIT_NO = 1;
        public static final int RESOLUTION_UNIT_INCH = 2;
        public static final int RESOLUTION_UNIT_CENTIMETER = 3;
    }

    public static class SampleFormat {
        public static final int SAMPLE_FORMAT_UNSIGNED_INT = 1;
        public static final int SAMPLE_FORMAT_SIGNED_INT = 2;
        public static final int SAMPLE_FORMAT_FLOAT = 3;
        public static final int SAMPLE_FORMAT_UNDEFINED = 4;
    }

    public static class SubfileType {
        public static final int SUBFILE_TYPE_FULL = 1;
        public static final int SUBFILE_TYPE_REDUCED = 2;
        public static final int SUBFILE_TYPE_SINGLE_PAGE_MULTI_PAGE = 3;
    }

    public static class Threshholding {
        public static final int THRESHHOLDING_NO = 1;
        public static final int THRESHHOLDING_ORDERED = 2;
        public static final int THRESHHOLDING_RANDOM = 3;
    }

    public static class DifferencingPredictor {
        public static final int PREDICTOR_NO = 1;
        public static final int PREDICTOR_HORIZONTAL = 2;
        public static final int PREDICTOR_FLOATINGPOINT = 3;
    }

}
