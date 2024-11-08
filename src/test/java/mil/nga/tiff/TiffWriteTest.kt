package mil.nga.tiff

import mil.nga.tiff.field.*
import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.*
import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.Rasters
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*
import kotlin.math.pow

/**
 * TIFF Write tests
 *
 * @author osbornb
 */
class TiffWriteTest {

    @Test
    @Throws(IOException::class)
    fun `Test writing and reading a stripped chunky file`() {
        val strippedFile = TiffTestUtils.getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val fileDirectory = strippedTiff.fileDirectory
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = Compression.NO.id
        fileDirectory.planarConfiguration = PlanarConfiguration.CHUNKY
        val rowsPerStrip = rasters.calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = TiffWriter.writeTiffToBytes(strippedTiff)

        val readTiffImage = TiffReader.readTiff(tiffBytes)
        val fileDirectory2 = readTiffImage.fileDirectory
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        TiffTestUtils.compareRastersSampleValues(rasters, rasters2)
        TiffTestUtils.compareRastersInterleaveValues(
            rastersInterleaved, rasters2Interleaved
        )
    }

    @Test
    @Throws(IOException::class)
    fun `Test writing and reading a stripped planar file`() {
        val strippedFile = TiffTestUtils.getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = TiffReader.readTiff(strippedFile)

        val fileDirectory = strippedTiff.fileDirectory
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = Compression.NO.id
        fileDirectory.planarConfiguration = PlanarConfiguration.PLANAR
        val rowsPerStrip = rasters.calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = TiffWriter.writeTiffToBytes(strippedTiff)

        val readTiffImage = TiffReader.readTiff(tiffBytes)
        val fileDirectory2 = readTiffImage.fileDirectory
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        TiffTestUtils.compareRastersSampleValues(rasters, rasters2)
        TiffTestUtils.compareRastersInterleaveValues(
            rastersInterleaved, rasters2Interleaved
        )
    }

    @Test
    @Throws(IOException::class)
    fun `Test writing and reading and custom TIFF`() {
        val inpWidth = 18
        val inpHeight = 11
        val bitsPerSample = 16
        val samplesPerPixel = 1
        val xResolution: Long = 254
        val yResolution: Long = 254

        val inpPixVals = IntArray(inpHeight * inpWidth)
        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                inpPixVals[y * inpWidth + x] = (Math.random() * 2.0.pow(bitsPerSample.toDouble())).toInt()
            }
        }

        val newRaster = Rasters(
            inpWidth, inpHeight, samplesPerPixel, bitsPerSample, SampleFormat.UNSIGNED_INT
        )
        val fileDirs = FileDirectory(TreeSet(), null, false)

        val rowsPerStrip = newRaster.calculateRowsPerStrip(
            PlanarConfiguration.CHUNKY
        )
        fileDirs.setImageWidth(inpWidth)
        fileDirs.setImageHeight(inpHeight)
        fileDirs.setBitsPerSample(bitsPerSample)
        fileDirs.samplesPerPixel = samplesPerPixel
        fileDirs.setSampleFormat(SampleFormat.UNSIGNED_INT)
        fileDirs.setRowsPerStrip(rowsPerStrip)
        fileDirs.resolutionUnit = ResolutionUnit.INCH
        fileDirs.setXResolution(xResolution)
        fileDirs.setYResolution(yResolution)
        fileDirs.photometricInterpretation = PhotometricInterpretation.BLACK_IS_ZERO
        fileDirs.planarConfiguration = PlanarConfiguration.CHUNKY
        fileDirs.compression = Compression.NO.id
        fileDirs.writeRasters = newRaster

        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                newRaster.setFirstPixelSample(
                    x, y, inpPixVals[y * inpWidth + x]
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
            samplesPerPixel, fileDirectory.samplesPerPixel
        )
        val sampleFormat = fileDirectory.sampleFormat
        Assertions.assertEquals(1, sampleFormat.size)
        Assertions.assertEquals(
            SampleFormat.UNSIGNED_INT, sampleFormat[0]
        )
        Assertions.assertEquals(rowsPerStrip, fileDirectory.rowsPerStrip)
        Assertions.assertEquals(
            ResolutionUnit.INCH, fileDirectory.resolutionUnit
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
            PhotometricInterpretation.BLACK_IS_ZERO, fileDirectory.photometricInterpretation
        )
        Assertions.assertEquals(
            PlanarConfiguration.CHUNKY, fileDirectory.planarConfiguration
        )
        Assertions.assertEquals(
            Compression.NO.id, fileDirectory.compression
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
            SampleFormat.UNSIGNED_INT, sf[0]
        )
        val fieldTypes = rasters.fieldTypes
        Assertions.assertEquals(1, fieldTypes.size)
        Assertions.assertEquals(FieldType.SHORT, fieldTypes[0])

        for (y in 0 until inpHeight) {
            for (x in 0 until inpWidth) {
                Assertions.assertEquals(
                    inpPixVals[y * inpWidth + x], rasters.getPixelSample(0, x, y)
                )
            }
        }
    }
}
