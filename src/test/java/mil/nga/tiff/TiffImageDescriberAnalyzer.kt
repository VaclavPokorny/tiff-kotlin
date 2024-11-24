package mil.nga.tiff.internal

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.tag.FieldTagType
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TiffImageDescriberAnalyzer : TiffImageAnalyzer {

    private val o = StringBuilder()

    private fun add(key: String, value: Any?) {
        o.append("$key: $value").append("${'\n'}");
    }

    override fun describeImage(byteOrder: ByteOrder, totalSize: Long, directoriesCount: Int) {
        add("Directories count", directoriesCount)
        add("Total size", totalSize)
        add("Byte order", byteOrder)
    }

    override fun commenceDirectory(size: Long, stats: DirectoryStats) {
        add("Commencing directory description", "=============================")
        add("Entries count", size)
        add("Image width", stats.imageWidth)
        add("Image height", stats.imageHeight)
        add("Tile width", stats.tileWidth)
        add("Tile height", stats.tileHeight)
        add("Samples per pixel", stats.samplesPerPixel)
        add("Bits per sample", stats.bitsPerSample)
        add("Sample formats", stats.sampleFormatList?.map { it.name })
        add("Planar configuration", stats.planarConfiguration?.name)
        add("Tile offsets", stats.tileOffsets)
        add("Tile byte counts", stats.tileByteCounts)
        add("Strip offsets", stats.stripOffsets)
        add("Strip byte counts", stats.stripByteCounts)
        add("Compression", stats.compression)
        add("Differencing predictor", stats.predictor.name)
    }

    override fun describeRasters(width: Int, height: Int, fields: List<FieldType>, pixelSize: Int, samples: Array<ByteBuffer>?, interleave: ByteBuffer?) {
        add("Rasters width", width)
        add("Rasters height", height)
        add("Rasters pixel size", pixelSize)
        add("Rasters fields", fields.forEach { it.name }.toString())
        add("Raster values count", samples?.size)
        add("Raster interleave values present", interleave != null)
    }

    override fun describeDirectoryEntry(fieldTag: FieldTagType?, fieldTagId: Int, type: FieldType, typeCount: Long, values: String) {
        add("Entry", "tag: #$fieldTagId ($fieldTag), type: ${type.name}, count: $typeCount, values: $values")
    }

    fun dump(out: PrintStream) {
        out.println("Analyzer results as follows")
        out.println("===========================")
        out.println(o.toString())
        out.println("===========================")
        out.println("End of results")
    }

}
