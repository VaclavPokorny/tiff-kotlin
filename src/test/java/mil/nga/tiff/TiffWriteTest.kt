package mil.nga.tiff

import mil.nga.tiff.TiffTestUtils.createFieldTypeArray
import mil.nga.tiff.TiffTestUtils.createSampleValues
import mil.nga.tiff.domain.UnsignedRational
import mil.nga.tiff.field.*
import mil.nga.tiff.field.type.enumeration.*
import mil.nga.tiff.internal.FileDirectory
import mil.nga.tiff.internal.TIFFImage
import mil.nga.tiff.internal.rasters.RasterTestUtils
import mil.nga.tiff.internal.rasters.Rasters
import mil.nga.tiff.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.ByteOrder
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
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val fileDirectory = strippedTiff.fileDirectories.first()
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = Compression.NO.id
        fileDirectory.planarConfiguration = PlanarConfiguration.CHUNKY
        val rowsPerStrip = rasters.calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = Tiff
            .create()
            .write(strippedTiff)
            .toByteArray()

        val readTiffImage = Tiff
            .create()
            .read()
            .fromByteArray(tiffBytes)

        val fileDirectory2 = readTiffImage.fileDirectories.first()
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        RasterTestUtils.compareRastersSampleValues(rasters, rasters2)
        RasterTestUtils.compareRastersInterleaveValues(
            rastersInterleaved, rasters2Interleaved
        )
    }

    @Test
    @Throws(IOException::class)
    fun `Test writing and reading a stripped planar file`() {
        val strippedFile = TiffTestUtils.getTestFile(TiffTestConstants.FILE_STRIPPED)
        val strippedTiff = Tiff
            .create()
            .read()
            .fromFile(strippedFile)

        val fileDirectory = strippedTiff.fileDirectories.first()
        val rasters = fileDirectory.readRasters()
        val rastersInterleaved = fileDirectory.readInterleavedRasters()

        fileDirectory.writeRasters = rasters
        fileDirectory.compression = Compression.NO.id
        fileDirectory.planarConfiguration = PlanarConfiguration.PLANAR
        val rowsPerStrip = rasters.calculateRowsPerStrip(fileDirectory.planarConfiguration)
        fileDirectory.setRowsPerStrip(rowsPerStrip)
        val tiffBytes = Tiff
            .create()
            .write(strippedTiff)
            .toByteArray()

        val readTiffImage = Tiff
            .create()
            .read()
            .fromByteArray(tiffBytes)

        val fileDirectory2 = readTiffImage.fileDirectories.first()
        val rasters2 = fileDirectory2.readRasters()
        val rasters2Interleaved = fileDirectory2.readInterleavedRasters()

        RasterTestUtils.compareRastersSampleValues(rasters, rasters2)
        RasterTestUtils.compareRastersInterleaveValues(rastersInterleaved, rasters2Interleaved)
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

        val dictionary = DefaultFieldTypeDictionary()
        val rasterFieldTypes = createFieldTypeArray(samplesPerPixel, dictionary.findBySampleParams(SampleFormat.UNSIGNED_INT, bitsPerSample))
        val order = ByteOrder.LITTLE_ENDIAN
        val sampleValues = createSampleValues(inpWidth, inpHeight, rasterFieldTypes, order)
        val newRaster = Rasters(inpWidth, inpHeight, rasterFieldTypes, sampleValues, null)

        val fileDirs = FileDirectory(TreeSet(), null, false, DefaultFieldTypeDictionary())

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
        fileDirs.setXResolution(UnsignedRational(xResolution, 1L))
        fileDirs.setYResolution(UnsignedRational(yResolution, 1L))
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
        val newImage = TIFFImage(listOf(fileDirs), ByteOrder.LITTLE_ENDIAN)

        val tiffBytes = Tiff
            .create()
            .write(newImage)
            .toByteArray()

        Assertions.assertNotNull(tiffBytes)

        val image = Tiff
            .create()
            .read()
            .fromByteArray(tiffBytes)

        Assertions.assertNotNull(image)

        val fileDirectory = image.fileDirectories.first()
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
        Assertions.assertEquals(xResolution, xRes.numerator.toLong())
        Assertions.assertEquals(1L, xRes.denominator.toLong())
        val yRes = fileDirectory.yResolution
        Assertions.assertEquals(yResolution, yRes.numerator.toLong())
        Assertions.assertEquals(1L, yRes.denominator.toLong())
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
        val bps = rasters.fields.map { it.bytesPerSample * 8 }
        Assertions.assertEquals(1, bps.size)
        Assertions.assertEquals(bitsPerSample, bps[0])
        val sf = rasters.fields.map { it.sampleFormat }
        Assertions.assertEquals(1, sf.size)
        Assertions.assertEquals(
            SampleFormat.UNSIGNED_INT, sf[0]
        )

        RasterTestUtils.testRastersMetadata(inpWidth, inpHeight, inpPixVals, rasters)
    }
}
