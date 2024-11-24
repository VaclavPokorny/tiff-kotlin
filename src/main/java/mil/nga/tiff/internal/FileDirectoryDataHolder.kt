package mil.nga.tiff.internal

import mil.nga.tiff.domain.UnsignedRational
import mil.nga.tiff.field.FieldTagType
import mil.nga.tiff.field.tag.TiffBaselineTag
import mil.nga.tiff.field.tag.TiffExtendedTag
import mil.nga.tiff.field.type.ASCIIField
import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.field.type.UnsignedRationalField
import mil.nga.tiff.field.type.enumeration.Compression
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.util.TiffConstants
import mil.nga.tiff.util.TiffException
import java.util.*

@JvmRecord
data class FileDirectoryDataHolder(val entries: SortedSet<FileDirectoryEntry<*>>) {

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    fun getRowsPerStrip(): Int? {
        return getSingleValue<Int>(TiffBaselineTag.RowsPerStrip)?.toInt()
    }

    /**
     * Add an entry
     *
     * @param entry file internal entry
     */
    private fun addEntry(entry: FileDirectoryEntry<*>) {
        entries.removeIf { it.fieldTagId == entry.fieldTagId }
        entries.add(entry)
    }

    /**
     * Get the number of entries
     *
     * @return entry count
     */
    fun numEntries(): Int {
        return entries.size
    }

    /**
     * Get a file internal entry from the field tag type
     *
     * @param fieldTag field tag type
     * @return file internal entry
     */
    fun get(fieldTag: FieldTagType): FileDirectoryEntry<*>? {
        return entries.firstOrNull { it.fieldTag == fieldTag }
    }

    /**
     * Get the image imageWidth
     *
     * @return image imageWidth
     */
    fun getImageWidth(): Int? {
        return getSingleValue<Int>(TiffBaselineTag.ImageWidth)?.toInt()
    }

    /**
     * Get the image imageHeight
     *
     * @return image imageHeight
     */
    fun getImageHeight(): Int? {
        return getSingleValue<Int>(TiffBaselineTag.ImageLength)?.toInt()
    }

    /**
     * Get a string entry value for the field tag type
     *
     * @param fieldTagType field tag type
     * @return string value
     */
    fun getStringEntryValue(fieldTagType: FieldTagType): String? {
        var value: String? = null
        val values = getMultiValues<String>(fieldTagType)
        if (!values.isNullOrEmpty()) {
            value = values.first()
        }
        return value
    }

    /**
     * Set rational value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        long list value
     */
    fun setRationalEntryValue(fieldTagType: FieldTagType, value: UnsignedRational?) {
        if (value == null) {
            throw TiffException("Invalid rational value.")
        }
        setSingleValue(fieldTagType, UnsignedRationalField, value)
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    fun <T> getSingleValue(fieldTagType: FieldTagType): T? {
        return getSingleValue<T>(fieldTagType, null)
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    fun <T> getSingleValue(fieldTagType: FieldTagType, defaultValue: T?): T? {
        val values = getMultiValues<T>(fieldTagType)
        if (values.isNullOrEmpty() || values.first() == null) {
            return defaultValue
        }
        return values.first()
    }

    /**
     * Get an entry value
     *
     * @param fieldTag field tag type
     * @return value
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getMultiValues(fieldTag: FieldTagType): List<T>? {
        val entry = get(fieldTag) as FileDirectoryEntry<T>?
        if (entry != null) {
            return entry.values
        }
        return null
    }

    /**
     * Create and set single entry value
     *
     * @param fieldTagType field tag type
     * @param value        entry value container
     */
    fun <T> setSingleValue(fieldTagType: FieldTagType, type: GenericFieldType<T>, value: T) {
        addEntry(FileDirectoryEntry(fieldTagType, type, 1, listOf(value)))
    }

    /**
     * Create and set single entry values
     *
     * @param fieldTagType field tag type
     * @param values       entry values container
     */
    fun <T> setMultiValues(fieldTagType: FieldTagType, type: GenericFieldType<T>, values: List<T>) {
        addEntry(FileDirectoryEntry(fieldTagType, type, values.size.toLong(), values))
    }

    /**
     * Set string value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        string value
     */
    fun setStringEntryValue(fieldTagType: FieldTagType, value: String) {
        addEntry(FileDirectoryEntry(fieldTagType, ASCIIField, (value.length + 1).toLong(), listOf(value)))
    }


    /**
     * Size in bytes of the Image File Directory (all contiguous)
     *
     * @return size in bytes
     */
    fun size(): Long {
        return TiffConstants.IFD_HEADER_BYTES + (entries.size.toLong() * TiffConstants.IFD_ENTRY_BYTES) + TiffConstants.IFD_OFFSET_BYTES
    }

    /**
     * Size in bytes of the image file internal including entry values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    fun sizeWithValues(): Long {
        return TiffConstants.IFD_HEADER_BYTES + TiffConstants.IFD_OFFSET_BYTES + entries.stream()
            .mapToLong { obj: FileDirectoryEntry<*> -> obj.sizeWithValues() }.sum()
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     */
    fun getSamplesPerPixel(): Int? {
        return getSingleValue(TiffBaselineTag.SamplesPerPixel, 1)
    }

    /**
     * Get the bits per sample
     *
     * @return bits per sample
     */
    fun getBitsPerSample(): List<Int>? {
        return getMultiValues<Int>(TiffBaselineTag.BitsPerSample)?.map { it.toInt() }
    }

    /**
     * Get the max bits per sample
     *
     * @return max bits per sample
     */
    fun getMaxBitsPerSample(): Int? {
        val bitsPerSample = getMultiValues<Int>(TiffBaselineTag.BitsPerSample)
        return bitsPerSample?.maxOrNull()
    }

    /**
     * Get the strip offsets
     *
     * @return strip offsets
     */
    fun getStripOffsets(): List<Long>? {
        return getMultiValues<Number>(TiffBaselineTag.StripOffsets)?.map { it.toLong() }
    }

    /**
     * Get the sample format
     *
     * @return sample format
     */
    fun getSampleFormat(): List<SampleFormat>? {
        return getMultiValues<Int>(TiffExtendedTag.SampleFormat)?.map { SampleFormat.findById(it) }
    }

    /**
     * Get the planar configuration
     *
     * @return planar configuration
     */
    fun getPlanarConfiguration(): PlanarConfiguration {
        return PlanarConfiguration.findById(getSingleValue<Int>(TiffBaselineTag.PlanarConfiguration))
    }

    /**
     * Get the tile offsets
     *
     * @return tile offsets
     */
    fun getTileOffsets(): List<Long>? {
        return getMultiValues(TiffExtendedTag.TileOffsets)
    }

    /**
     * Get the tile byte counts
     *
     * @return tile byte counts
     */
    fun getTileByteCounts(): List<Int>? {
        return getMultiValues<Number>(TiffExtendedTag.TileByteCounts)?.map { it.toInt() }
    }

    /**
     * Get the strip byte counts
     *
     * @return strip byte counts
     */
    fun getStripByteCounts(): List<Int>? {
        return getMultiValues<Number>(TiffBaselineTag.StripByteCounts)?.map { it.toInt() }
    }

    /**
     * Get the compression
     *
     * @return compression
     */
    fun getCompression(): Compression {
        return Compression.findById(getSingleValue<Int>(TiffBaselineTag.Compression))
    }

    /**
     * Get the predictor
     *
     * @return predictor
     */
    fun getPredictor(): DifferencingPredictor {
        return DifferencingPredictor.findById(getSingleValue<Int>(TiffExtendedTag.Predictor))
    }

    fun analyze(analyzer: TiffImageAnalyzer) {
        entries.forEach { it.analyze(analyzer) }
    }

}
