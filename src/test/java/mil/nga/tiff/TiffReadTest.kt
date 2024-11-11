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

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data tiled`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as int 32`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_INT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, true, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as unsigned int 32`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_UINT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as float 32`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_FLOAT32)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as float 64`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_FLOAT64)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data compressed as LZW`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_LZW)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data compressed as Packbits`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_PACKBITS)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as interleaved`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_INTERLEAVE)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as tiled planar`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED_PLANAR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the JPEG file header`() {
        val file = getTestFile(TiffTestConstants.FILE_JPEG)
        val tiff = TiffReader.readTiff(file)

        Assertions.assertNotNull(tiff)
        Assertions.assertTrue(tiff.fileDirectories().size > 0)
        for (i in tiff.fileDirectories().indices) {
            val fileDirectory = tiff.fileDirectories[i]
            Assertions.assertNotNull(fileDirectory)
            try {
                fileDirectory.readRasters()
                Assertions.fail("JPEG compression was not expected to be implemented")
            } catch (e: Exception) {
                // expected
            }
        }
    }

    @Test
    fun `Test an invalid offset value`() {
        val base64Bytes = "TU0AKoAAAAAAAAAAAAAAAQAAKgAAGABNAA=="
        val bytes = Base64.getDecoder().decode(base64Bytes)
        try {
            TiffReader.readTiff(bytes)
            Assertions.fail("Unexpected success")
        } catch (e: TiffException) {
            // expected
        }
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_DEFLATE_PREDICTOR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor big strips`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(
            TiffTestConstants.FILE_DEFLATE_PREDICTOR_BIG_STRIPS
        )
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor tiled`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_DEFLATE_PREDICTOR_TILED)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as LZW predictor`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_LZW_PREDICTOR)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as tiled planar LZW`() {
        val strippedFile = getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val file = getTestFile(TiffTestConstants.FILE_TILED_PLANAR_LZW)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the float 32 TIFF file vs the same data as LZW predictor floating point`() {
        val float32File = getTestFile(TiffTestConstants.FILE_FLOAT32)
        val float32Tiff = TiffReader.readTiff(float32File)

        val file = getTestFile(TiffTestConstants.FILE_LZW_PREDICTOR_FLOATING)
        val tiff = TiffReader.readTiff(file)

        compareTIFFImages(float32Tiff, tiff)
    }
}
