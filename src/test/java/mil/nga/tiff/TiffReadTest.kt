package mil.nga.tiff

import mil.nga.tiff.TiffTestUtils.compareTIFFImages
import mil.nga.tiff.util.TiffException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*

/**
 * TIFF Read tests
 */
class TiffReadTest {

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data tiled`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_TILED.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as int 32`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_INT32.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff, true, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as unsigned int 32`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_UINT32.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as float 32`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_FLOAT32.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as float 64`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_FLOAT64.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff, false, false)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data compressed as LZW`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_LZW.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data compressed as Packbits`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_PACKBITS.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as interleaved`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_INTERLEAVE.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as tiled planar`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_TILED_PLANAR.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the JPEG file header`() {
        val file = TiffTestFiles.FILE_JPEG.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        Assertions.assertNotNull(tiff)
        Assertions.assertTrue(tiff.fileDirectories.size > 0)
        for (i in tiff.fileDirectories.indices) {
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
            Tiff
                .create()
                .read()
                .fromByteArray(bytes)
            Assertions.fail("Unexpected success")
        } catch (e: TiffException) {
            // expected
        }
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_DEFLATE_PREDICTOR.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor big strips`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_DEFLATE_PREDICTOR_BIG_STRIPS.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as deflate predictor tiled`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_DEFLATE_PREDICTOR_TILED.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as LZW predictor`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_LZW_PREDICTOR.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the stripped TIFF file vs the same data as tiled planar LZW`() {
        val strippedFile = TiffTestFiles.FILE_STRIPPED.asFile()
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val file = TiffTestFiles.FILE_TILED_PLANAR_LZW.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(strippedTiff, tiff)
    }

    @Test
    @Throws(IOException::class)
    fun `Test the float 32 TIFF file vs the same data as LZW predictor floating point`() {
        val float32File = TiffTestFiles.FILE_FLOAT32.asFile()
        val float32Tiff = Tiff
            .create()
            .read()
            .fromFile(float32File)

        val file = TiffTestFiles.FILE_LZW_PREDICTOR_FLOATING.asFile()
        val tiff = Tiff
            .create()
            .read()
            .fromFile(file)

        compareTIFFImages(float32Tiff, tiff)
    }
}
