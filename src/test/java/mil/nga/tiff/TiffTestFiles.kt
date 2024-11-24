package mil.nga.tiff

import mil.nga.tiff.util.TiffException
import java.io.File
import java.net.URISyntaxException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * TIFF test files list
 */
enum class TiffTestFiles(val value: String) {
    /**
     * Stripped TIFF test file
     */
    FILE_STRIPPED("stripped.tiff"),

    /**
     * Packbits compressed TIFF test file
     */
    FILE_PACKBITS("packbits.tiff"),

    /**
     * LZW compressed TIFF test file
     */
    FILE_LZW("lzw.tiff"),

    /**
     * Tiled TIFF test file
     */
    FILE_TILED("tiled.tiff"),

    /**
     * Float 32 TIFF test file
     */
    FILE_FLOAT32("float32.tiff"),

    /**
     * Float 64 TIFF test file
     */
    FILE_FLOAT64("float64.tiff"),

    /**
     * Int 32 TIFF test file
     */
    FILE_INT32("int32.tiff"),

    /**
     * Unsigned Int 32 TIFF test file
     */
    FILE_UINT32("uint32.tiff"),

    /**
     * Interleave TIFF test file
     */
    FILE_INTERLEAVE("interleave.tiff"),

    /**
     * Tiled Planar TIFF test file
     */
    FILE_TILED_PLANAR("tiledplanar.tiff"),

    /**
     * JPEG TIFF test file
     */
    FILE_JPEG("quad-jpeg.tif"),

    /**
     * Deflate Predictor TIFF test file
     */
    FILE_DEFLATE_PREDICTOR("deflate_predictor.tiff"),

    /**
     * Deflate Predictor Big Strips TIFF test file
     */
    FILE_DEFLATE_PREDICTOR_BIG_STRIPS("deflate_predictor_big_strips.tiff"),

    /**
     * Deflate Predictor Tiled TIFF test file
     */
    FILE_DEFLATE_PREDICTOR_TILED("deflate_predictor_tiled.tiff"),

    /**
     * LZW Predictor TIFF test file
     */
    FILE_LZW_PREDICTOR("lzw_predictor.tiff"),

    /**
     * Tiled Planar LZW TIFF test file
     */
    FILE_TILED_PLANAR_LZW("tiledplanarlzw.tiff"),

    /**
     * LZW Predictor Floating Point TIFF test file
     */
    FILE_LZW_PREDICTOR_FLOATING("lzw_predictor_floating.tiff");

    /**
     * Get the file
     *
     * @return file
     */
    fun asFile(): File {
        val resourceUrl = TiffTestFiles::class.java.getResource("/${value}")
        val resourcePath: Path
        try {
            resourcePath = Paths.get(resourceUrl!!.toURI())
        } catch (e: URISyntaxException) {
            throw TiffException("Failed to get test file path", e)
        }
        val file = resourcePath.toFile()
        return file
    }

}
