package mil.nga.tiff

import mil.nga.tiff.util.TiffConstants
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.math.pow

/**
 * TIFF Write tests
 *
 * @author osbornb
 */
class TiffWriteTest {
    /**
     * Test writing and reading a stripped TIFF file
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testWriteStrippedChunky() {
        val strippedFile = TiffTestUtils
            .getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val fileDirectory = strippedTiff.fileDirectory
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = TiffConstants.Compression.COMPRESSION_NO
        fileDirectory.planarConfiguration = TiffConstants.PlanarConfiguration.PLANAR_CONFIGURATION_CHUNKY
        val rowsPerStrip = rasters
            .calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = TiffWriter.writeTiffToBytes(strippedTiff)

        val readTiffImage = TiffReader.readTiff(tiffBytes)
        val fileDirectory2 = readTiffImage.fileDirectory
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        TiffTestUtils.compareRastersSampleValues(rasters, rasters2)
        TiffTestUtils.compareRastersInterleaveValues(
            rastersInterleaved,
            rasters2Interleaved
        )
    }

    /**
     * Test writing and reading a stripped TIFF file
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testWriteStrippedPlanar() {
        val strippedFile = TiffTestUtils
            .getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val fileDirectory = strippedTiff.fileDirectory
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = TiffConstants.Compression.COMPRESSION_NO
        fileDirectory.planarConfiguration = TiffConstants.PlanarConfiguration.PLANAR_CONFIGURATION_PLANAR
        val rowsPerStrip = rasters
            .calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = TiffWriter.writeTiffToBytes(strippedTiff)

        val readTiffImage = TiffReader.readTiff(tiffBytes)
        val fileDirectory2 = readTiffImage.fileDirectory
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        TiffTestUtils.compareRastersSampleValues(rasters, rasters2)
        TiffTestUtils.compareRastersInterleaveValues(
            rastersInterleaved,
            rasters2Interleaved
        )
    }

    /**
     * Test writing and reading and custom tiff
     *
     * @throws IOException
     * upon error
     */
    @Test
    @Throws(IOException::class)
    fun testWriteCustom() {
        val inpWidth = 18
        val inpHeight = 11
        val bitsPerSample = 16
        val samplesPerPixel = 1
        val xResolution: Long = 254
        val yResolution: Long = 254

        val inpPixVals = IntArray(inpHeight * inpWidth)
        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                inpPixVals[y * inpWidth + x] = (Math.random()
                        * 2.0.pow(bitsPerSample.toDouble())).toInt()
            }
        }

        val newRaster = Rasters(
            inpWidth, inpHeight, samplesPerPixel,
            bitsPerSample, TiffConstants.SampleFormat.SAMPLE_FORMAT_UNSIGNED_INT
        )
        val fileDirs = FileDirectory()

        val rowsPerStrip = newRaster.calculateRowsPerStrip(
            TiffConstants.PlanarConfiguration.PLANAR_CONFIGURATION_CHUNKY
        )
        fileDirs.setImageWidth(inpWidth)
        fileDirs.setImageHeight(inpHeight)
        fileDirs.setBitsPerSample(bitsPerSample)
        fileDirs.samplesPerPixel = samplesPerPixel
        fileDirs.setSampleFormat(TiffConstants.SampleFormat.SAMPLE_FORMAT_UNSIGNED_INT)
        fileDirs.setRowsPerStrip(rowsPerStrip)
        fileDirs.resolutionUnit = TiffConstants.ResolutionUnit.RESOLUTION_UNIT_INCH
        fileDirs.setXResolution(xResolution)
        fileDirs.setYResolution(yResolution)
        fileDirs.photometricInterpretation =
            TiffConstants.PhotometricInterpretation.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO
        fileDirs.planarConfiguration = TiffConstants.PlanarConfiguration.PLANAR_CONFIGURATION_CHUNKY
        fileDirs.compression = TiffConstants.Compression.COMPRESSION_NO
        fileDirs.writeRasters = newRaster

        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                newRaster.setFirstPixelSample(
                    x, y,
                    inpPixVals[y * inpWidth + x]
                )
            }
        }
        val newImage = TIFFImage()
        newImage.add(fileDirs)

        val tiffBytes = TiffWriter.writeTiffToBytes(newImage)
        Assertions.assertNotNull(tiffBytes)

        val image = TiffReader.readTiff(tiffBytes)
        Assertions.assertNotNull(image)

        val fileDirectory = image.fileDirectory
        Assertions.assertEquals(inpWidth, fileDirectory.imageWidth)
        Assertions.assertEquals(inpHeight, fileDirectory.imageHeight)
        val bitsPerSamp = fileDirectory.bitsPerSample
        Assertions.assertEquals(1, bitsPerSamp.size)
        Assertions.assertEquals(bitsPerSample, bitsPerSamp[0])
        Assertions.assertEquals(
            samplesPerPixel,
            fileDirectory.samplesPerPixel
        )
        val sampleFormat = fileDirectory.sampleFormat
        Assertions.assertEquals(1, sampleFormat.size)
        Assertions.assertEquals(
            TiffConstants.SampleFormat.SAMPLE_FORMAT_UNSIGNED_INT,
            sampleFormat[0]
        )
        Assertions.assertEquals(rowsPerStrip, fileDirectory.rowsPerStrip)
        Assertions.assertEquals(
            TiffConstants.ResolutionUnit.RESOLUTION_UNIT_INCH,
            fileDirectory.resolutionUnit
        )
        val xRes = fileDirectory.xResolution
        Assertions.assertEquals(2, xRes.size)
        Assertions.assertEquals(xResolution, xRes[0])
        Assertions.assertEquals(1, xRes[1])
        val yRes = fileDirectory.yResolution
        Assertions.assertEquals(2, yRes.size)
        Assertions.assertEquals(yResolution, yRes[0])
        Assertions.assertEquals(1, yRes[1])
        Assertions.assertEquals(
            TiffConstants.PhotometricInterpretation.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO,
            fileDirectory.photometricInterpretation
        )
        Assertions.assertEquals(
            TiffConstants.PlanarConfiguration.PLANAR_CONFIGURATION_CHUNKY,
            fileDirectory.planarConfiguration
        )
        Assertions.assertEquals(
            TiffConstants.Compression.COMPRESSION_NO,
            fileDirectory.compression
        )

        val rasters = fileDirectory.readRasters()
        Assertions.assertEquals(inpWidth * inpHeight, rasters.numPixels)
        Assertions.assertEquals(inpWidth, rasters.width)
        Assertions.assertEquals(inpHeight, rasters.height)
        Assertions.assertEquals(samplesPerPixel, rasters.samplesPerPixel)
        val bps = rasters.bitsPerSample
        Assertions.assertEquals(1, bps.size)
        Assertions.assertEquals(bitsPerSample, bps[0])
        val sf = rasters.sampleFormat
        Assertions.assertEquals(1, sf.size)
        Assertions.assertEquals(
            TiffConstants.SampleFormat.SAMPLE_FORMAT_UNSIGNED_INT,
            sf[0]
        )
        val fieldTypes = rasters.fieldTypes
        Assertions.assertEquals(1, fieldTypes.size)
        Assertions.assertEquals(FieldType.SHORT, fieldTypes[0])

        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                Assertions.assertEquals(
                    inpPixVals[y * inpWidth + x],
                    rasters.getPixelSample(0, x, y)
                )
            }
        }
    }
}
