package mil.nga.tiff.internal

import mil.nga.tiff.field.type.enumeration.Compression
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.field.type.enumeration.SampleFormat

@JvmRecord
data class DirectoryStats(
    val imageWidth: Int?,
    val imageHeight: Int?,
    val tileWidth: Int?,
    val tileHeight: Int?,
    val samplesPerPixel: Int?,
    val bitsPerSample: List<Int>?,
    val sampleFormatList: List<SampleFormat>?,
    val planarConfiguration: PlanarConfiguration?,
    val tileOffsets: List<Long>?,
    val tileByteCounts: List<Int>?,
    val stripOffsets: List<Long>?,
    val stripByteCounts: List<Int>?,
    val compression: Compression,
    val predictor: DifferencingPredictor
)
