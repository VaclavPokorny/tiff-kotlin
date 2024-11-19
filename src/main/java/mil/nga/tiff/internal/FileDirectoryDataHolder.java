package mil.nga.tiff.internal;

import mil.nga.tiff.domain.UnsignedRational;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.tag.TiffBasicTag;
import mil.nga.tiff.field.tag.TiffExtendedTag;
import mil.nga.tiff.field.type.ASCIIField;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.UnsignedRationalField;
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.util.List;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.function.Function;

public record FileDirectoryDataHolder(SortedMap<FieldTagType, FileDirectoryEntry<?>> fieldTagTypeMapping) {

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    public Integer getRowsPerStrip() {
        return getSingleValue(TiffBasicTag.RowsPerStrip, Number::intValue);
    }

    /**
     * Add an entry
     *
     * @param entry file internal entry
     */
    public void addEntry(FileDirectoryEntry<?> entry) {
        fieldTagTypeMapping.put(entry.fieldTag, entry);
    }

    /**
     * Get the number of entries
     *
     * @return entry count
     */
    public int numEntries() {
        return fieldTagTypeMapping.size();
    }

    /**
     * Get a file internal entry from the field tag type
     *
     * @param fieldTagType field tag type
     * @return file internal entry
     */
    public FileDirectoryEntry<?> get(FieldTagType fieldTagType) {
        return fieldTagTypeMapping.get(fieldTagType);
    }

    /**
     * Get the image imageWidth
     *
     * @return image imageWidth
     */
    public Integer getImageWidth() {
        return getSingleValue(TiffBasicTag.ImageWidth, Number::intValue);
    }

    /**
     * Get the image imageHeight
     *
     * @return image imageHeight
     */
    public Integer getImageHeight() {
        return getSingleValue(TiffBasicTag.ImageLength, Number::intValue);
    }

    /**
     * Get a string entry value for the field tag type
     *
     * @param fieldTagType field tag type
     * @return string value
     */
    public String getStringEntryValue(FieldTagType fieldTagType) {
        String value = null;
        List<String> values = getMultiValues(fieldTagType);
        if (values != null && !values.isEmpty()) {
            value = values.getFirst();
        }
        return value;
    }

    /**
     * Set rational value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        long list value
     */
    public void setRationalEntryValue(FieldTagType fieldTagType, UnsignedRational value) {
        if (value == null) {
            throw new TiffException("Invalid rational value.");
        }
        setSingleValue(fieldTagType, new UnsignedRationalField(), value);
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    public <T> T getSingleValue(FieldTagType fieldTagType, T defaultValue) {
        return getSingleValue(fieldTagType, Function.identity(), defaultValue);
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    public <T> T getSingleValue(FieldTagType fieldTagType) {
        return getSingleValue(fieldTagType, Function.identity());
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    public <F, T> T getSingleValue(FieldTagType fieldTagType, Function<F, T> converter) {
        return getSingleValue(fieldTagType, converter, null);
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    public <F, T> T getSingleValue(FieldTagType fieldTagType, Function<F, T> converter, T defaultValue) {
        List<T> values = getMultiValues(fieldTagType, converter);
        if (values == null || values.isEmpty() || values.getFirst() == null) {
            return defaultValue;
        }
        return values.getFirst();
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    public <T> List<T> getMultiValues(FieldTagType fieldTagType) {
        return getMultiValues(fieldTagType, Function.identity());
    }

    /**
     * Get an entry values
     *
     * @param fieldTagType field tag type
     * @param converter conversion function
     * @return values
     */
    @SuppressWarnings("unchecked")
    public <F, T> List<T> getMultiValues(FieldTagType fieldTagType, Function<F, T> converter) {
        FileDirectoryEntry<F> entry = (FileDirectoryEntry<F>) fieldTagTypeMapping.get(fieldTagType);
        if (entry != null) {
            return entry.values.stream().map(converter).toList();
        }
        return null;
    }

    /**
     * Create and set single entry value
     *
     * @param fieldTagType field tag type
     * @param value        entry value container
     */
    public <T> void setSingleValue(FieldTagType fieldTagType, GenericFieldType<T> type, T value) {
        addEntry(new FileDirectoryEntry<>(fieldTagType, type, 1, List.of(value)));
    }

    /**
     * Create and set single entry values
     *
     * @param fieldTagType field tag type
     * @param values       entry values container
     */
    public <T> void setMultiValues(FieldTagType fieldTagType, GenericFieldType<T> type, List<T> values) {
        addEntry(new FileDirectoryEntry<>(fieldTagType, type, values.size(), values));
    }

    /**
     * Set string value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        string value
     */
    public void setStringEntryValue(FieldTagType fieldTagType, String value) {
        addEntry(new FileDirectoryEntry<>(fieldTagType, new ASCIIField(), value.length() + 1, List.of(value)));
    }


    /**
     * Size in bytes of the Image File Directory (all contiguous)
     *
     * @return size in bytes
     */
    public long size() {
        return TiffConstants.IFD_HEADER_BYTES + ((long) fieldTagTypeMapping.size() * TiffConstants.IFD_ENTRY_BYTES) + TiffConstants.IFD_OFFSET_BYTES;
    }

    /**
     * Size in bytes of the image file internal including entry values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    public long sizeWithValues() {
        return TiffConstants.IFD_HEADER_BYTES + TiffConstants.IFD_OFFSET_BYTES + fieldTagTypeMapping.values().stream().mapToLong(FileDirectoryEntry::sizeWithValues).sum();
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     */
    public int getSamplesPerPixel() {
        return getSingleValue(TiffBasicTag.SamplesPerPixel, 1);
    }

    /**
     * Get the bits per sample
     *
     * @return bits per sample
     */
    public List<Integer> getBitsPerSample() {
        return getMultiValues(TiffBasicTag.BitsPerSample, Number::intValue);
    }


    /**
     * Get the max bits per sample
     *
     * @return max bits per sample
     */
    public Integer getMaxBitsPerSample() {
        List<Integer> bitsPerSample = getMultiValues(TiffBasicTag.BitsPerSample);
        OptionalInt result = bitsPerSample.stream().mapToInt(Integer::intValue).max();
        if (result.isEmpty()) {
            return null;
        }
        return result.getAsInt();
    }

    /**
     * Get the strip offsets
     *
     * @return strip offsets
     */
    public List<Long> getStripOffsets() {
        return getMultiValues(TiffBasicTag.StripOffsets, Number::longValue);
    }

    /**
     * Get the sample format
     *
     * @return sample format
     */
    public List<SampleFormat> getSampleFormat() {
        return getMultiValues(TiffExtendedTag.SampleFormat, SampleFormat::findById);
    }

    /**
     * Get the planar configuration
     *
     * @return planar configuration
     */
    public PlanarConfiguration getPlanarConfiguration() {
        return getSingleValue(TiffBasicTag.PlanarConfiguration, PlanarConfiguration::findById);
    }


    /**
     * Get the tile offsets
     *
     * @return tile offsets
     */
    public List<Long> getTileOffsets() {
        return getMultiValues(TiffExtendedTag.TileOffsets);
    }

    /**
     * Get the tile byte counts
     *
     * @return tile byte counts
     */
    public List<Integer> getTileByteCounts() {
        return getMultiValues(TiffExtendedTag.TileByteCounts, Number::intValue);
    }

    /**
     * Get the strip byte counts
     *
     * @return strip byte counts
     */
    public List<Integer> getStripByteCounts() {
        return getMultiValues(TiffBasicTag.StripByteCounts, Number::intValue);
    }

    /**
     * Get the compression
     *
     * @return compression
     */
    public Integer getCompression() {
        return getSingleValue(TiffBasicTag.Compression);
    }

    /**
     * Get the predictor
     *
     * @return predictor
     */
    public DifferencingPredictor getPredictor() {
        return getSingleValue(TiffExtendedTag.Predictor, DifferencingPredictor::findById);
    }

}
