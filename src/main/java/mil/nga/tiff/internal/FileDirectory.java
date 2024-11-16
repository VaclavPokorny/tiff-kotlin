package mil.nga.tiff.internal;

import mil.nga.tiff.domain.UnsignedRational;
import mil.nga.tiff.field.FieldTypeDictionary;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.tag.TiffBasicTag;
import mil.nga.tiff.field.tag.TiffExtendedTag;
import mil.nga.tiff.field.type.ASCIIField;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.UnsignedLongField;
import mil.nga.tiff.field.type.UnsignedRationalField;
import mil.nga.tiff.field.type.UnsignedShortField;
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PhotometricInterpretation;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.ResolutionUnit;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.internal.rasters.Rasters;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * File Directory, represents all internal entries and can be used to read the
 * image raster
 *
 * @author osbornb
 */
public class FileDirectory {

    /**
     * File internal entries in sorted tag id order
     */
    private final SortedSet<FileDirectoryEntry<?>> entries;

    /**
     * Mapping between tags and entries
     */
    private final Map<FieldTagType, FileDirectoryEntry<?>> fieldTagTypeMapping = new HashMap<>();

    /**
     * Byte reader
     */
    private final ByteReader reader;

    /**
     * Rasters to write to the TIFF file
     */
    private Rasters writeRasters = null;


    private final FileDirectoryRasterReader rasterReader;

    private final DirectoryStats stats;

    /**
     * Constructor, for reading TIFF files
     *
     * @param entries file internal entries
     * @param reader  TIFF file byte reader
     */
    public FileDirectory(SortedSet<FileDirectoryEntry<?>> entries, ByteReader reader, FieldTypeDictionary typeDictionary) {
        this(entries, reader, false, typeDictionary);
    }

    /**
     * Constructor, for reading TIFF files
     *
     * @param entries   file internal entries
     * @param reader    TIFF file byte reader
     * @param cacheData true to cache tiles and strips
     */
    public FileDirectory(SortedSet<FileDirectoryEntry<?>> entries, ByteReader reader, boolean cacheData, FieldTypeDictionary typeDictionary) {
        // Set the entries and the field tag type mapping
        this.entries = entries;
        for (FileDirectoryEntry<?> entry : entries) {
            fieldTagTypeMapping.put(entry.fieldTag(), entry);
        }

        this.stats = new DirectoryStats(this);

        TileOrStripCache cache = new TileOrStripCache(cacheData);
        TileOrStripProcessor tileOrStripProcessor = new TileOrStripProcessor(stats, cache);
        this.rasterReader = new FileDirectoryRasterReader(stats, tileOrStripProcessor, typeDictionary);

        this.reader = reader;
    }

    /**
     * Add an entry
     *
     * @param entry file internal entry
     */
    public void addEntry(FileDirectoryEntry<?> entry) {
        entries.removeIf(o -> o.fieldTag() == entry.fieldTag());
        entries.add(entry);
        fieldTagTypeMapping.put(entry.fieldTag(), entry);
    }

    /**
     * Is this a tiled image
     *
     * @return true if tiled
     */
    public boolean isTiled() {
        return getRowsPerStrip() == null;
    }

    /**
     * Get the number of entries
     *
     * @return entry count
     */
    public int numEntries() {
        return entries.size();
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
     * Get the file internal entries
     *
     * @return file internal entries
     */
    public Set<FileDirectoryEntry<?>> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Get the image imageWidth
     *
     * @return image imageWidth
     */
    public Number getImageWidth() {
        return getSingleValue(TiffBasicTag.ImageWidth);
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    public void setImageWidth(Integer value) {
        setSingleValue(TiffBasicTag.ImageWidth, new UnsignedShortField(), value);
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    public void setImageWidthAsLong(Long value) {
        setSingleValue(TiffBasicTag.ImageWidth, new UnsignedLongField(), value);
    }

    /**
     * Get the image imageHeight
     *
     * @return image imageHeight
     */
    public Number getImageHeight() {
        return getSingleValue(TiffBasicTag.ImageLength);
    }

    /**
     * Set the image imageHeight
     *
     * @param value image imageHeight
     */
    public void setImageHeight(Integer value) {
        setSingleValue(TiffBasicTag.ImageLength, new UnsignedShortField(), value);
    }

    /**
     * Set the image imageHeight
     *
     * @param value image imageHeight
     */
    public void setImageHeightAsLong(Long value) {
        setSingleValue(TiffBasicTag.ImageLength, new UnsignedLongField(), value);
    }

    /**
     * Get the bits per sample
     *
     * @return bits per sample
     */
    public List<Integer> getBitsPerSample() {
        return getMultiValuesWithConversion(TiffBasicTag.BitsPerSample, Number::intValue);
    }

    /**
     * Set the bits per sample
     *
     * @param values bits per sample
     */
    public void setBitsPerSample(List<Integer> values) {
        setMultiValues(TiffBasicTag.BitsPerSample, new UnsignedShortField(), values);
    }

    /**
     * Set a single value bits per sample
     *
     * @param value bits per sample
     */
    public void setBitsPerSample(Integer value) {
        setSingleValue(TiffBasicTag.BitsPerSample, new UnsignedShortField(), value);
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
     * Get the compression
     *
     * @return compression
     */
    public Integer getCompression() {
        return getSingleValue(TiffBasicTag.Compression);
    }

    /**
     * Set the compression
     *
     * @param value compression
     */
    public void setCompression(Integer value) {
        setSingleValue(TiffBasicTag.Compression, new UnsignedShortField(), value);
    }

    /**
     * Get the photometric interpretation
     *
     * @return photometric interpretation
     */
    public PhotometricInterpretation getPhotometricInterpretation() {
        return PhotometricInterpretation.findById(getSingleValue(TiffBasicTag.PhotometricInterpretation));
    }

    /**
     * Set the photometric interpretation
     *
     * @param value photometric interpretation
     */
    public void setPhotometricInterpretation(PhotometricInterpretation value) {
        setSingleValue(TiffBasicTag.PhotometricInterpretation, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the strip offsets
     *
     * @return strip offsets
     */
    public List<Long> getStripOffsets() {
        return getMultiValuesWithConversion(TiffBasicTag.StripOffsets, Number::longValue);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsets(List<Integer> value) {
        setMultiValues(TiffBasicTag.StripOffsets, new UnsignedShortField(), value);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsetsAsLongs(List<Long> value) {
        setMultiValues(TiffBasicTag.StripOffsets, new UnsignedLongField(), value);
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     * @since 2.0.0
     */
    public int getSamplesPerPixel() {
        Integer samplesPerPixel = getSingleValue(TiffBasicTag.SamplesPerPixel);
        if (samplesPerPixel == null) {
            // if SamplesPerPixel tag is missing, use default value defined by
            // TIFF standard
            samplesPerPixel = 1;
        }
        return samplesPerPixel;
    }

    /**
     * Set the samples per pixel
     *
     * @param value samples per pixel
     */
    public void setSamplesPerPixel(Integer value) {
        setSingleValue(TiffBasicTag.SamplesPerPixel, new UnsignedShortField(), value);
    }

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    public Number getRowsPerStrip() {
        return getSingleValue(TiffBasicTag.RowsPerStrip);
    }

    /**
     * Set the rows per strip
     *
     * @param value rows per strip
     */
    public void setRowsPerStrip(Integer value) {
        setSingleValue(TiffBasicTag.RowsPerStrip, new UnsignedShortField(), value);
    }

    /**
     * Get the strip byte counts
     *
     * @return strip byte counts
     */
    public List<Integer> getStripByteCounts() {
        return getMultiValuesWithConversion(TiffBasicTag.StripByteCounts, Number::intValue);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCounts(List<Integer> value) {
        setMultiValues(TiffBasicTag.StripByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCountsAsLongs(List<Long> value) {
        setMultiValues(TiffBasicTag.StripByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    public void setStripByteCounts(Integer value) {
        setSingleValue(TiffBasicTag.StripByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    public void setStripByteCounts(Long value) {
        setSingleValue(TiffBasicTag.StripByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Get the x resolution
     *
     * @return x resolution
     */
    public UnsignedRational getXResolution() {
        return getSingleValue(TiffBasicTag.XResolution);
    }

    /**
     * Set the x resolution
     *
     * @param value x resolution
     */
    public void setXResolution(UnsignedRational value) {
        setRationalEntryValue(TiffBasicTag.XResolution, value);
    }

    /**
     * Get the y resolution
     *
     * @return y resolution
     */
    public UnsignedRational getYResolution() {
        return getSingleValue(TiffBasicTag.YResolution);
    }

    /**
     * Set the y resolution
     *
     * @param value y resolution
     */
    public void setYResolution(UnsignedRational value) {
        setRationalEntryValue(TiffBasicTag.YResolution, value);
    }

    /**
     * Get the planar configuration
     *
     * @return planar configuration
     */
    public PlanarConfiguration getPlanarConfiguration() {
        return PlanarConfiguration.findById(getSingleValue(TiffBasicTag.PlanarConfiguration));
    }

    /**
     * Set the planar configuration
     *
     * @param value planar configuration
     */
    public void setPlanarConfiguration(PlanarConfiguration value) {
        setSingleValue(TiffBasicTag.PlanarConfiguration, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the resolution unit
     *
     * @return resolution unit
     */
    public ResolutionUnit getResolutionUnit() {
        return ResolutionUnit.findById(getSingleValue(TiffBasicTag.ResolutionUnit));
    }

    /**
     * Set the resolution unit
     *
     * @param value resolution unit
     */
    public void setResolutionUnit(ResolutionUnit value) {
        setSingleValue(TiffBasicTag.ResolutionUnit, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the tile imageWidth
     *
     * @return tile imageWidth
     */
    public Number getTileWidth() {
        return isTiled() ? getSingleValue(TiffExtendedTag.TileWidth) : getImageWidth();
    }

    /**
     * Set the tile imageWidth
     *
     * @param value tile imageWidth
     */
    public void setTileWidth(Integer value) {
        setSingleValue(TiffExtendedTag.TileWidth, new UnsignedShortField(), value);
    }

    /**
     * Set the tile imageWidth
     *
     * @param value tile imageWidth
     */
    public void setTileWidthAsLong(Long value) {
        setSingleValue(TiffExtendedTag.TileWidth, new UnsignedLongField(), value);
    }

    /**
     * Get the tile imageHeight
     *
     * @return tile imageHeight
     */
    public Number getTileHeight() {
        return isTiled() ? getSingleValue(TiffExtendedTag.TileLength) : getRowsPerStrip();
    }

    /**
     * Set the tile imageHeight
     *
     * @param value tile imageHeight
     */
    public void setTileHeight(Integer value) {
        setSingleValue(TiffExtendedTag.TileLength, new UnsignedShortField(), value);
    }

    /**
     * Set the tile imageHeight
     *
     * @param value tile imageHeight
     */
    public void setTileHeightAsLong(Long value) {
        setSingleValue(TiffExtendedTag.TileLength, new UnsignedLongField(), value);
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
     * Set the tile offsets
     *
     * @param value tile offsets
     */
    public void setTileOffsets(List<Long> value) {
        setMultiValues(TiffExtendedTag.TileOffsets, new UnsignedLongField(), value);
    }

    /**
     * Set a single value tile offset
     *
     * @param value tile offset
     */
    public void setTileOffsets(Long value) {
        setSingleValue(TiffExtendedTag.TileOffsets, new UnsignedLongField(), value);
    }

    /**
     * Get the tile byte counts
     *
     * @return tile byte counts
     */
    public List<Integer> getTileByteCounts() {
        return getMultiValuesWithConversion(TiffExtendedTag.TileByteCounts, Number::intValue);
    }

    /**
     * Set the tile byte counts
     *
     * @param values tile byte counts
     */
    public void setTileByteCounts(List<Integer> values) {
        setMultiValues(TiffExtendedTag.TileByteCounts, new UnsignedShortField(), values);
    }

    /**
     * Set the tile byte counts
     *
     * @param values tile byte counts
     */
    public void setTileByteCountsAsLongs(List<Long> values) {
        setMultiValues(TiffExtendedTag.TileByteCounts, new UnsignedLongField(), values);
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    public void setTileByteCounts(Integer value) {
        setSingleValue(TiffExtendedTag.TileByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    public void setTileByteCounts(Long value) {
        setSingleValue(TiffExtendedTag.TileByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Get the sample format
     *
     * @return sample format
     */
    public List<SampleFormat> getSampleFormat() {
        List<Integer> idList = getMultiValues(TiffExtendedTag.SampleFormat);
        if (idList != null) {
            return idList.stream()
                .map(SampleFormat::findById)
                .toList();
        }
        return null;
    }

    /**
     * Set the sample format
     *
     * @param value sample format
     */
    public void setSampleFormat(List<SampleFormat> value) {
        setMultiValues(TiffExtendedTag.SampleFormat, new UnsignedShortField(), value.stream().map(SampleFormat::getId).toList());
    }

    /**
     * Set a single value sample format
     *
     * @param sampleFormat sample format
     */
    public void setSampleFormat(SampleFormat sampleFormat) {
        setSampleFormat(List.of(sampleFormat));
    }

    /**
     * Get the max sample format
     *
     * @return max sample format
     */
    public Integer getMaxSampleFormat() {
        List<Integer> sampleFormat = getMultiValues(TiffExtendedTag.SampleFormat);
        OptionalInt result = sampleFormat.stream().mapToInt(Integer::intValue).max();
        if (result.isEmpty()) {
            return null;
        }
        return result.getAsInt();
    }

    /**
     * Get the predictor
     *
     * @return predictor
     * @since 3.0.0
     */
    @NotNull
    public DifferencingPredictor getPredictor() {
        return DifferencingPredictor.findById(getSingleValue(TiffExtendedTag.Predictor));
    }

    /**
     * Set the predictor
     *
     * @param value predictor
     * @since 3.0.0
     */
    public void setPredictor(DifferencingPredictor value) {
        setSingleValue(TiffExtendedTag.Predictor, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the rasters for writing a TIFF file
     *
     * @return rasters image rasters
     */
    public Rasters getWriteRasters() {
        return writeRasters;
    }

    /**
     * Set the rasters for writing a TIFF file
     *
     * @param rasters image rasters
     */
    public void setWriteRasters(Rasters rasters) {
        writeRasters = rasters;
    }

    /**
     * Read the rasters
     *
     * @return rasters
     */
    public Rasters readRasters() {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readRasters(window);
    }

    /**
     * Read the rasters as interleaved
     *
     * @return rasters
     */
    public Rasters readInterleavedRasters() {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readInterleavedRasters(window);
    }

    /**
     * Read the rasters
     *
     * @param window image window
     * @return rasters
     */
    public Rasters readRasters(ImageWindow window) {
        return readRasters(window, null);
    }

    /**
     * Read the rasters as interleaved
     *
     * @param window image window
     * @return rasters
     */
    public Rasters readInterleavedRasters(ImageWindow window) {
        return readInterleavedRasters(window, null);
    }

    /**
     * Read the rasters
     *
     * @param samples pixel samples to read
     * @return rasters
     */
    public Rasters readRasters(int[] samples) {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readRasters(window, samples);
    }

    /**
     * Read the rasters as interleaved
     *
     * @param samples pixel samples to read
     * @return rasters
     */
    public Rasters readInterleavedRasters(int[] samples) {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readInterleavedRasters(window, samples);
    }

    /**
     * Read the rasters
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @return rasters
     */
    public Rasters readRasters(ImageWindow window, int[] samples) {
        return readRasters(window, samples, true, false);
    }

    /**
     * Read the rasters as interleaved
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @return rasters
     */
    public Rasters readInterleavedRasters(ImageWindow window, int[] samples) {
        return readRasters(window, samples, false, true);
    }

    /**
     * Read the rasters
     *
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    public Rasters readRasters(boolean sampleValues, boolean interleaveValues) {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readRasters(window, sampleValues, interleaveValues);
    }

    /**
     * Read the rasters
     *
     * @param window           image window
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    public Rasters readRasters(ImageWindow window, boolean sampleValues, boolean interleaveValues) {
        return readRasters(window, null, sampleValues, interleaveValues);
    }

    /**
     * Read the rasters
     *
     * @param samples          pixel samples to read
     * @param sampleValues     true to read results per sample
     * @param interleaveValues true to read results as interleaved
     * @return rasters
     */
    public Rasters readRasters(int[] samples, boolean sampleValues, boolean interleaveValues) {
        ImageWindow window = ImageWindow.fromZero(stats.imageWidth(), stats.imageHeight());
        return readRasters(window, samples, sampleValues, interleaveValues);
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
    public Rasters readRasters(ImageWindow window, int[] samples, boolean sampleValues, boolean interleaveValues) {
        return rasterReader.readRasters(window, samples, sampleValues, interleaveValues, reader, isTiled());
    }

    /**
     * Get a string entry value for the field tag type
     *
     * @param fieldTagType field tag type
     * @return string value
     * @since 2.0.0
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
     * @since 2.0.1
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
    @SuppressWarnings("unchecked")
    private <T> T getSingleValue(FieldTagType fieldTagType) {
        T value = null;
        FileDirectoryEntry<T> entry = (FileDirectoryEntry<T>)fieldTagTypeMapping.get(fieldTagType);
        if (entry != null) {
            value = entry.values().getFirst();
        }
        return value;
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> getMultiValues(FieldTagType fieldTagType) {
        FileDirectoryEntry<T> entry = (FileDirectoryEntry<T>)fieldTagTypeMapping.get(fieldTagType);
        if (entry != null) {
            return entry.values();
        }
        return null;
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    @SuppressWarnings("unchecked")
    private <F, T> List<T> getMultiValuesWithConversion(FieldTagType fieldTagType, Function<F, T> converter) {
        FileDirectoryEntry<F> entry = (FileDirectoryEntry<F>)fieldTagTypeMapping.get(fieldTagType);
        if (entry != null) {
            return entry.values().stream().map(converter).toList();
        }
        return null;
    }

    /**
     * Create and set single entry value
     *
     * @param fieldTagType field tag type
     * @param value        entry value container
     */
    private <T> void setSingleValue(FieldTagType fieldTagType, GenericFieldType<T> type, T value) {
        addEntry(new FileDirectoryEntry<>(fieldTagType, type, 1, List.of(value)));
    }

    /**
     * Create and set single entry values
     *
     * @param fieldTagType field tag type
     * @param values        entry values container
     */
    private <T> void setMultiValues(FieldTagType fieldTagType, GenericFieldType<T> type, List<T> values) {
        addEntry(new FileDirectoryEntry<>(fieldTagType, type, values.size(), values));
    }

    /**
     * Set string value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        string value
     * @since 2.0.0
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
        return TiffConstants.IFD_HEADER_BYTES + ((long) entries.size() * TiffConstants.IFD_ENTRY_BYTES) + TiffConstants.IFD_OFFSET_BYTES;
    }

    /**
     * Size in bytes of the image file internal including entry values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    public long sizeWithValues() {
        long size = TiffConstants.IFD_HEADER_BYTES + TiffConstants.IFD_OFFSET_BYTES;
        for (FileDirectoryEntry<?> entry : entries) {
            size += entry.sizeWithValues();
        }
        return size;
    }

}
