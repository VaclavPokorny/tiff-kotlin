package mil.nga.tiff.internal

import mil.nga.tiff.domain.UnsignedRational
import mil.nga.tiff.field.TagDictionary
import mil.nga.tiff.field.FieldTagType
import mil.nga.tiff.field.tag.TiffBaselineTag
import mil.nga.tiff.field.tag.TiffExtendedTag
import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.field.type.UnsignedLongField
import mil.nga.tiff.field.type.UnsignedShortField
import mil.nga.tiff.field.type.enumeration.*
import mil.nga.tiff.internal.ImageWindow.Companion.fromZero
import mil.nga.tiff.internal.rasters.Rasters
import mil.nga.tiff.io.ByteReader
import java.util.*

/**
 * File Directory, represents all internal entries and can be used to read the image raster
 */
class FileDirectory(
    /**
     * Mapping between tags and entries
     */
    val data: FileDirectoryDataHolder,
    writeRasters: Rasters?,
    rasterReader: FileDirectoryRasterReader,
    stats: DirectoryStats
) {
    /**
     * Rasters to write to the TIFF file
     */
    var writeRasters: Rasters? = null

    /**
     * Raster reader
     */
    private val rasterReader: FileDirectoryRasterReader

    /**
     * Directory basic statistics
     */
    val stats: DirectoryStats

    init {
        this.writeRasters = writeRasters
        this.rasterReader = rasterReader
        this.stats = stats
    }

    fun analyze(analyzer: TiffImageAnalyzer) {
        analyzer.commenceDirectory(data.size(), stats)
        writeRasters?.analyze(analyzer)
        data.analyze(analyzer)
    }

    /**
     * Is this a tiled image
     *
     * @return true if tiled
     */
    fun isTiled(): Boolean {
        return getRowsPerStrip() == null
    }

    /**
     * Get the number of entries
     *
     * @return entry count
     */
    fun numEntries(): Int {
        return data.numEntries()
    }

    /**
     * Get a file internal entry from the field tag type
     *
     * @param fieldTagType field tag type
     * @return file internal entry
     */
    fun get(fieldTagType: FieldTagType): FileDirectoryEntry<*>? {
        return data.get(fieldTagType)
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    //TODO REMOVE
    fun setImageWidth(value: Int) {
        data.setSingleValue(TiffBaselineTag.ImageWidth, UnsignedShortField, value)
    }

    var imageHeight: Int?
        /**
         * Get the image imageHeight
         *
         * @return image imageHeight
         */
        get() = data.getSingleValue<Number>(TiffBaselineTag.ImageLength)?.toInt()
        /**
         * Set the image imageHeight
         *
         * @param value image imageHeight
         */
        set(value) {
            data.setSingleValue(TiffBaselineTag.ImageLength, UnsignedShortField, value!!)
        }

    /**
     * Set a single value bits per sample
     *
     * @param value bits per sample
     */
    //TODO REMOVE
    fun setBitsPerSample(value: Int) {
        data.setSingleValue(TiffBaselineTag.BitsPerSample, UnsignedShortField, value)
    }

    var compression: Compression
        /**
         * Get the compression
         *
         * @return compression
         */
        get() = data.getCompression()
        /**
         * Set the compression
         *
         * @param value compression
         */
        set(value) {
            data.setSingleValue(TiffBaselineTag.Compression, UnsignedShortField, value.id)
        }

    var photometricInterpretation: PhotometricInterpretation?
        /**
         * Get the photometric interpretation
         *
         * @return photometric interpretation
         */
        get() {
            val id = data.getSingleValue<Int>(TiffBaselineTag.PhotometricInterpretation)
            if (id != null) {
                return PhotometricInterpretation.findById(id)
            }
            return null
        }
        /**
         * Set the photometric interpretation
         *
         * @param value photometric interpretation
         */
        set(value) {
            data.setSingleValue(TiffBaselineTag.PhotometricInterpretation, UnsignedShortField, value!!.id)
        }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    fun setStripOffsets(value: List<Int>) {
        data.setMultiValues(TiffBaselineTag.StripOffsets, UnsignedShortField, value)
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    fun setStripOffsetsAsLongs(value: List<Long>) {
        data.setMultiValues(TiffBaselineTag.StripOffsets, UnsignedLongField, value)
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     */
    fun getSamplesPerPixel(): Int? {
        return data.getSamplesPerPixel()
    }

    /**
     * Set the samples per pixel
     *
     * @param value samples per pixel
     */
    fun setSamplesPerPixel(value: Int) {
        return data.setSingleValue(TiffBaselineTag.SamplesPerPixel, UnsignedShortField, value)
    }

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    fun getRowsPerStrip(): Int? {
        return data.getRowsPerStrip()
    }

    /**
     * Set the rows per strip
     *
     * @param value rows per strip
     */
    fun setRowsPerStrip(value: Int) {
        return data.setSingleValue(TiffBaselineTag.RowsPerStrip, UnsignedShortField, value)
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    fun setStripByteCounts(value: List<Int>) {
        data.setMultiValues(TiffBaselineTag.StripByteCounts, UnsignedShortField, value)
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    fun setStripByteCountsAsLongs(value: List<Long>) {
        data.setMultiValues(TiffBaselineTag.StripByteCounts, UnsignedLongField, value)
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    fun setStripByteCounts(value: Int) {
        data.setSingleValue(TiffBaselineTag.StripByteCounts, UnsignedShortField, value)
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    fun setStripByteCounts(value: Long) {
        data.setSingleValue(TiffBaselineTag.StripByteCounts, UnsignedLongField, value)
    }

    var xResolution: UnsignedRational?
        /**
         * Get the x resolution
         *
         * @return x resolution
         */
        get() = data.getSingleValue<UnsignedRational>(TiffBaselineTag.XResolution)
        /**
         * Set the x resolution
         *
         * @param value x resolution
         */
        set(value) {
            data.setRationalEntryValue(TiffBaselineTag.XResolution, value)
        }

    var yResolution: UnsignedRational?
        /**
         * Get the y resolution
         *
         * @return y resolution
         */
        get() = data.getSingleValue<UnsignedRational>(TiffBaselineTag.YResolution)
        /**
         * Set the y resolution
         *
         * @param value y resolution
         */
        set(value) {
            data.setRationalEntryValue(TiffBaselineTag.YResolution, value)
        }

    var planarConfiguration: PlanarConfiguration
        /**
         * Get the planar configuration
         *
         * @return planar configuration
         */
        get() = data.getPlanarConfiguration()
        /**
         * Set the planar configuration
         *
         * @param value planar configuration
         */
        set(value) {
            data.setSingleValue(TiffBaselineTag.PlanarConfiguration, UnsignedShortField, value.id)
        }

    var resolutionUnit: ResolutionUnit?
        /**
         * Get the resolution unit
         *
         * @return resolution unit
         */
        get() {
            val id = data.getSingleValue<Int>(TiffBaselineTag.ResolutionUnit)
            if (id != null) {
                return ResolutionUnit.findById(id)
            }
            return null
        }
        /**
         * Set the resolution unit
         *
         * @param value resolution unit
         */
        set(value) {
            data.setSingleValue(TiffBaselineTag.ResolutionUnit, UnsignedShortField, value!!.id)
        }

    var tileOffsets: List<Long>?
        /**
         * Get the tile offsets
         *
         * @return tile offsets
         */
        get() = data.getTileOffsets()
        /**
         * Set the tile offsets
         *
         * @param value tile offsets
         */
        set(value) {
            data.setMultiValues(TiffExtendedTag.TileOffsets, UnsignedLongField, value!!)
        }

    /**
     * Set a single value tile offset
     *
     * @param value tile offset
     */
    fun setTileOffsets(value: Long) {
        data.setSingleValue(TiffExtendedTag.TileOffsets, UnsignedLongField, value)
    }

    var tileByteCounts: List<Int>?
        /**
         * Get the tile byte counts
         *
         * @return tile byte counts
         */
        get() = data.getTileByteCounts()
        /**
         * Set the tile byte counts
         *
         * @param values tile byte counts
         */
        set(values) {
            data.setMultiValues(TiffExtendedTag.TileByteCounts, UnsignedShortField, values!!)
        }

    /**
     * Set the tile byte counts
     *
     * @param values tile byte counts
     */
    fun setTileByteCountsAsLongs(values: List<Long>) {
        data.setMultiValues(TiffExtendedTag.TileByteCounts, UnsignedLongField, values)
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    fun setTileByteCounts(value: Int) {
        data.setSingleValue(TiffExtendedTag.TileByteCounts, UnsignedShortField, value)
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    fun setTileByteCounts(value: Long) {
        data.setSingleValue(TiffExtendedTag.TileByteCounts, UnsignedLongField, value)
    }

    var sampleFormat: List<SampleFormat>?
        /**
         * Get the sample format
         *
         * @return sample format
         */
        get() = data.getSampleFormat()
        /**
         * Set the sample format
         *
         * @param value sample format
         */
        set(value) {
            data.setMultiValues(
                TiffExtendedTag.SampleFormat,
                UnsignedShortField,
                value!!.stream().map(SampleFormat::id).toList()
            )
        }

    /**
     * Set a single value sample format
     *
     * @param sampleFormat sample format
     */
    fun setSampleFormat(sampleFormat: SampleFormat) {
        this.sampleFormat = java.util.List.of(sampleFormat)
    }

    val maxSampleFormat: Int?
        /**
         * Get the max sample format
         *
         * @return max sample format
         */
        get() {
            val sampleFormat = data.getMultiValues<Int>(TiffExtendedTag.SampleFormat) ?: return null
            val result = sampleFormat.stream().mapToInt { obj: Int -> obj.toInt() }.max()
            if (result.isEmpty) {
                return null
            }
            return result.asInt
        }

    /**
     * Read the rasters
     *
     * @return rasters
     */
    fun readRasters(): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readRasters(window)
    }

    /**
     * Read the rasters as interleaved
     *
     * @return rasters
     */
    fun readInterleavedRasters(): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readInterleavedRasters(window)
    }

    /**
     * Read the rasters
     *
     * @param samples pixel samples to read
     * @return rasters
     */
    fun readRasters(samples: IntArray?): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readRasters(window, samples)
    }

    /**
     * Read the rasters as interleaved
     *
     * @param samples pixel samples to read
     * @return rasters
     */
    fun readInterleavedRasters(samples: IntArray?): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readInterleavedRasters(window, samples)
    }

    /**
     * Read the rasters
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @return rasters
     */
    @JvmOverloads
    fun readRasters(window: ImageWindow, samples: IntArray? = null): Rasters {
        return readRasters(window, samples, sampleValues = true, interleaveValues = false)
    }

    /**
     * Read the rasters as interleaved
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @return rasters
     */
    @JvmOverloads
    fun readInterleavedRasters(window: ImageWindow, samples: IntArray? = null): Rasters {
        return readRasters(window, samples, sampleValues = false, interleaveValues = true)
    }

    /**
     * Read the rasters
     *
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    fun readRasters(sampleValues: Boolean, interleaveValues: Boolean): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readRasters(window, sampleValues, interleaveValues)
    }

    /**
     * Read the rasters
     *
     * @param window           image window
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    fun readRasters(window: ImageWindow, sampleValues: Boolean, interleaveValues: Boolean): Rasters {
        return readRasters(window, null, sampleValues, interleaveValues)
    }

    /**
     * Read the rasters
     *
     * @param samples          pixel samples to read
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    fun readRasters(samples: IntArray?, sampleValues: Boolean, interleaveValues: Boolean): Rasters {
        val window = fromZero(stats.imageWidth!!, stats.imageHeight!!)
        return readRasters(window, samples, sampleValues, interleaveValues)
    }

    /**
     * Read the rasters
     *
     * @param window           image window
     * @param samples          pixel samples to read
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    fun readRasters(
        window: ImageWindow,
        samples: IntArray?,
        sampleValues: Boolean,
        interleaveValues: Boolean
    ): Rasters {
        return rasterReader.readRasters(window, samples, sampleValues, interleaveValues, isTiled())
    }


    /**
     * Size in bytes of the Image File Directory (all contiguous)
     *
     * @return size in bytes
     */
    fun size(): Long {
        return data.size()
    }

    /**
     * Size in bytes of the image file internal including entry values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    fun sizeWithValues(): Long {
        return data.sizeWithValues()
    }


    /**
     * Create and set single entry value
     *
     * @param fieldTagType field tag type
     * @param value        entry value container
     */
    fun <T> setSingleValue(fieldTagType: FieldTagType, type: GenericFieldType<T>, value: T) {
        data.setSingleValue(fieldTagType, type, value)
    }

    /**
     * Create and set single entry values
     *
     * @param fieldTagType field tag type
     * @param values        entry values container
     */
    fun <T> setMultiValues(fieldTagType: FieldTagType, type: GenericFieldType<T>, values: List<T>) {
        data.setMultiValues(fieldTagType, type, values)
    }

    companion object {
        /**
         * Constructor, for reading TIFF files
         *
         * @param entries file internal entries
         * @param reader TIFF file byte reader
         * @param cacheData true to cache tiles and strips
         */
        fun create(
            entries: Set<FileDirectoryEntry<*>>,
            reader: ByteReader?,
            cacheData: Boolean,
            typeDictionary: TagDictionary,
            writeRasters: Rasters?
        ): FileDirectory {
            val data = FileDirectoryDataHolder(entries.toSortedSet(Comparator.comparingInt(FileDirectoryEntry<*>::fieldTagId)))
            val stats = createStats(data)

            val cache = TileOrStripCache(cacheData)
            val tileOrStripProcessor = TileOrStripProcessor(stats, cache)

            val rasterReader = FileDirectoryRasterReader(stats, tileOrStripProcessor, typeDictionary, reader)

            return FileDirectory(data, writeRasters, rasterReader, stats)
        }

        private fun createStats(data: FileDirectoryDataHolder): DirectoryStats {
            val rowsPerStrip = data.getSingleValue<Number>(TiffBaselineTag.RowsPerStrip)?.toInt()
            val isTiled = rowsPerStrip == null
            val width = data.getSingleValue<Number>(TiffBaselineTag.ImageWidth)?.toInt()
            val height = data.getSingleValue<Number>(TiffBaselineTag.ImageLength)?.toInt()

            return DirectoryStats(
                width,
                height,
                if (isTiled) data.getSingleValue<Int>(TiffExtendedTag.TileWidth)?.toInt() else width,
                if (isTiled) data.getSingleValue<Int>(TiffExtendedTag.TileLength)?.toInt() else rowsPerStrip,
                data.getSingleValue(TiffBaselineTag.SamplesPerPixel, 1),
                data.getMultiValues<Number>(TiffBaselineTag.BitsPerSample)?.map { it.toInt() },
                data.getMultiValues<Int>(TiffExtendedTag.SampleFormat)?.map { SampleFormat.findById(it) },
                PlanarConfiguration.findById(data.getSingleValue<Int>(TiffBaselineTag.PlanarConfiguration)),
                data.getMultiValues(TiffExtendedTag.TileOffsets),
                data.getMultiValues<Number>(TiffExtendedTag.TileByteCounts)?.map { it.toInt() },
                data.getMultiValues<Number>(TiffBaselineTag.StripOffsets)?.map { it.toLong() },
                data.getMultiValues<Number>(TiffBaselineTag.StripByteCounts)?.map { it.toInt() },
                Compression.findById(data.getSingleValue<Number>(TiffBaselineTag.Compression)?.toInt()),
                DifferencingPredictor.findById(data.getSingleValue<Int>(TiffExtendedTag.Predictor))
            )
        }
    }
}
