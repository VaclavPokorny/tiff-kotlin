package mil.nga.tiff

import mil.nga.tiff.TiffTestUtils.compareTIFFImages
import mil.nga.tiff.TiffTestUtils.getTestFile
import mil.nga.tiff.util.TiffException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*

/**
 * TIFF Read tests
 *
 * @author osbornb
 */
class TiffReadTest {
    /**
     * Test the stripped TIFF file vs the same data tiled
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsTiled() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as int 32
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsInt32() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_INT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, true, false)
    }

    /**
     * Test the stripped TIFF file vs the same data as unsigned int 32
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsUInt32() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_UINT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    /**
     * Test the stripped TIFF file vs the same data as float 32
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsFloat32() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_FLOAT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    /**
     * Test the stripped TIFF file vs the same data as float 64
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsFloat64() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_FLOAT64)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    /**
     * Test the stripped TIFF file vs the same data compressed as LZW
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsLzw() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_LZW)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data compressed as Packbits
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsPackbits() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_PACKBITS)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as interleaved
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsInterleave() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_INTERLEAVE)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as tiled planar
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsTiledPlanar() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED_PLANAR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the JPEG file header
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testJPEGHeader() {
        val file = getTestFile(TiffTestConstants.FILE_JPEG)
        val tiff = TiffReader.readTiff(file)

        Assertions.assertNotNull(tiff)
        Assertions.assertTrue(tiff.fileDirectories.size > 0)
        for (i in tiff.fileDirectories.indices) {
            val fileDirectory = tiff.getFileDirectory(i)
            Assertions.assertNotNull(fileDirectory)
            try {
                fileDirectory.readRasters()
                Assertions.fail(
                    "JPEG compression was not expected to be implemented"
                )
            } catch (e: Exception) {
                // expected
            }
        }
    }

    /**
     * Test an invalid offset value
     */
    @Test
    fun testInvalidOffset() {
        val base64Bytes = "TU0AKoAAAAAAAAAAAAAAAQAAKgAAGABNAA=="
        val bytes = Base64.getDecoder().decode(base64Bytes)
        try {
            TiffReader.readTiff(bytes)
            Assertions.fail("Unexpected success")
        } catch (e: TiffException) {
            // expected
        }
    }

    /**
     * Test the stripped TIFF file vs the same data as deflate predictor
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsDeflatePredictor() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_DEFLATE_PREDICTOR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as deflate predictor big
     * strips
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsDeflatePredictorBigStrips() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(
            TiffTestConstants.FILE_DEFLATE_PREDICTOR_BIG_STRIPS
        )
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as deflate predictor tiled
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsDeflatePredictorTiled() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_DEFLATE_PREDICTOR_TILED)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as LZW predictor
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsLZWPredictor() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_LZW_PREDICTOR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the stripped TIFF file vs the same data as tiled planar LZW
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testStrippedVsTiledPlanarLZW() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED_PLANAR_LZW)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    /**
     * Test the float 32 TIFF file vs the same data as LZW predictor floating
     * point
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testFloat32VsLZWPredictorFloatingPoint() {
        val float32File = getTestFile(TiffTestConstants.FILE_FLOAT32)
        val float32Tiff = TiffReader.readTiff(float32File)

        val file = getTestFile(TiffTestConstants.FILE_LZW_PREDICTOR_FLOATING)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(float32Tiff, tiff)
    }
}
