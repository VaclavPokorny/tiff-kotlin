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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
    private final SortedSet<FileDirectoryEntry> entries;

    /**
     * Mapping between tags and entries
     */
    private final Map<FieldTagType, FileDirectoryEntry> fieldTagTypeMapping = new HashMap<>();

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
    public FileDirectory(SortedSet<FileDirectoryEntry> entries, ByteReader reader, FieldTypeDictionary typeDictionary) {
        this(entries, reader, false, typeDictionary);
    }

    /**
     * Constructor, for reading TIFF files
     *
     * @param entries   file internal entries
     * @param reader    TIFF file byte reader
     * @param cacheData true to cache tiles and strips
     */
    public FileDirectory(SortedSet<FileDirectoryEntry> entries, ByteReader reader, boolean cacheData, FieldTypeDictionary typeDictionary) {
        // Set the entries and the field tag type mapping
        this.entries = entries;
        for (FileDirectoryEntry entry : entries) {
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
    public void addEntry(FileDirectoryEntry entry) {
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
    public FileDirectoryEntry get(FieldTagType fieldTagType) {
        return fieldTagTypeMapping.get(fieldTagType);
    }

    /**
     * Get the file internal entries
     *
     * @return file internal entries
     */
    public Set<FileDirectoryEntry> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Get the image imageWidth
     *
     * @return image imageWidth
     */
    public Number getImageWidth() {
        return getNumberEntryValue(TiffBasicTag.ImageWidth);
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    public void setImageWidth(int value) {
        setEntryValue(TiffBasicTag.ImageWidth, new UnsignedShortField(), 1, value);
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    public void setImageWidthAsLong(long value) {
        setEntryValue(TiffBasicTag.ImageWidth, new UnsignedLongField(), 1, value);
    }

    /**
     * Get the image imageHeight
     *
     * @return image imageHeight
     */
    public Number getImageHeight() {
        return getNumberEntryValue(TiffBasicTag.ImageLength);
    }

    /**
     * Set the image imageHeight
     *
     * @param value image imageHeight
     */
    public void setImageHeight(int value) {
        setEntryValue(TiffBasicTag.ImageLength, new UnsignedShortField(), 1, value);
    }

    /**
     * Set the image imageHeight
     *
     * @param value image imageHeight
     */
    public void setImageHeightAsLong(long value) {
        setEntryValue(TiffBasicTag.ImageLength, new UnsignedLongField(), 1, value);
    }

    /**
     * Get the bits per sample
     *
     * @return bits per sample
     */
    public List<Integer> getBitsPerSample() {
        return getIntegerListEntryValue(TiffBasicTag.BitsPerSample);
    }

    /**
     * Set the bits per sample
     *
     * @param value bits per sample
     */
    public void setBitsPerSample(List<Integer> value) {
        setEntryValue(TiffBasicTag.BitsPerSample, new UnsignedShortField(), value.size(), value);
    }

    /**
     * Set a single value bits per sample
     *
     * @param bitsPerSample bits per sample
     */
    public void setBitsPerSample(int bitsPerSample) {
        setBitsPerSample(List.of(bitsPerSample));
    }

    /**
     * Get the max bits per sample
     *
     * @return max bits per sample
     */
    public Integer getMaxBitsPerSample() {
        return getMaxIntegerEntryValue(TiffBasicTag.BitsPerSample);
    }

    /**
     * Get the compression
     *
     * @return compression
     */
    public Integer getCompression() {
        return getIntegerEntryValue(TiffBasicTag.Compression);
    }

    /**
     * Set the compression
     *
     * @param value compression
     */
    public void setCompression(int value) {
        setEntryValue(TiffBasicTag.Compression, new UnsignedShortField(), 1, value);
    }

    /**
     * Get the photometric interpretation
     *
     * @return photometric interpretation
     */
    public PhotometricInterpretation getPhotometricInterpretation() {
        return PhotometricInterpretation.findById(getIntegerEntryValue(TiffBasicTag.PhotometricInterpretation));
    }

    /**
     * Set the photometric interpretation
     *
     * @param value photometric interpretation
     */
    public void setPhotometricInterpretation(PhotometricInterpretation value) {
        setEntryValue(TiffBasicTag.PhotometricInterpretation, new UnsignedShortField(), 1, value.getId());
    }

    /**
     * Get the strip offsets
     *
     * @return strip offsets
     */
    public List<Number> getStripOffsets() {
        return getNumberListEntryValue(TiffBasicTag.StripOffsets);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsets(List<Integer> value) {
        setEntryValue(TiffBasicTag.StripOffsets, new UnsignedShortField(), value.size(), value);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsetsAsLongs(List<Long> value) {
        setEntryValue(TiffBasicTag.StripOffsets, new UnsignedLongField(), value.size(), value);
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     * @since 2.0.0
     */
    public int getSamplesPerPixel() {
        Integer samplesPerPixel = getIntegerEntryValue(TiffBasicTag.SamplesPerPixel);
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
    public void setSamplesPerPixel(int value) {
        setEntryValue(TiffBasicTag.SamplesPerPixel, new UnsignedShortField(), 1, value);
    }

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    public Number getRowsPerStrip() {
        return getNumberEntryValue(TiffBasicTag.RowsPerStrip);
    }

    /**
     * Set the rows per strip
     *
     * @param value rows per strip
     */
    public void setRowsPerStrip(int value) {
        setEntryValue(TiffBasicTag.RowsPerStrip, new UnsignedShortField(), 1, value);
    }

    /**
     * Get the strip byte counts
     *
     * @return strip byte counts
     */
    public List<Number> getStripByteCounts() {
        return getNumberListEntryValue(TiffBasicTag.StripByteCounts);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCounts(List<Integer> value) {
        setEntryValue(TiffBasicTag.StripByteCounts, new UnsignedShortField(), value.size(), value);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCountsAsLongs(List<Long> value) {
        setEntryValue(TiffBasicTag.StripByteCounts, new UnsignedLongField(), value.size(), value);
    }

    /**
     * Set a single value strip byte count
     *
     * @param stripByteCount strip byte count
     */
    public void setStripByteCounts(int stripByteCount) {
        setStripByteCounts(List.of(stripByteCount));
    }

    /**
     * Set a single value strip byte count
     *
     * @param stripByteCount strip byte count
     */
    public void setStripByteCounts(long stripByteCount) {
        setStripByteCountsAsLongs(List.of(stripByteCount));
    }

    /**
     * Get the x resolution
     *
     * @return x resolution
     */
    public UnsignedRational getXResolution() {
        return getEntryValue(TiffBasicTag.XResolution);
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
        return getEntryValue(TiffBasicTag.YResolution);
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
        return PlanarConfiguration.findById(getIntegerEntryValue(TiffBasicTag.PlanarConfiguration));
    }

    /**
     * Set the planar configuration
     *
     * @param value planar configuration
     */
    public void setPlanarConfiguration(PlanarConfiguration value) {
        setEntryValue(TiffBasicTag.PlanarConfiguration, new UnsignedShortField(), 1, value.getId());
    }

    /**
     * Get the resolution unit
     *
     * @return resolution unit
     */
    public ResolutionUnit getResolutionUnit() {
        return ResolutionUnit.findById(getIntegerEntryValue(TiffBasicTag.ResolutionUnit));
    }

    /**
     * Set the resolution unit
     *
     * @param value resolution unit
     */
    public void setResolutionUnit(ResolutionUnit value) {
        setEntryValue(TiffBasicTag.ResolutionUnit, new UnsignedShortField(), 1, value.getId());
    }

    /**
     * Get the tile imageWidth
     *
     * @return tile imageWidth
     */
    public Number getTileWidth() {
        return isTiled() ? getNumberEntryValue(TiffExtendedTag.TileWidth) : getImageWidth();
    }

    /**
     * Set the tile imageWidth
     *
     * @param value tile imageWidth
     */
    public void setTileWidth(int value) {
        setEntryValue(TiffExtendedTag.TileWidth, new UnsignedShortField(), 1, value);
    }

    /**
     * Set the tile imageWidth
     *
     * @param value tile imageWidth
     */
    public void setTileWidthAsLong(long value) {
        setEntryValue(TiffExtendedTag.TileWidth, new UnsignedLongField(), 1, value);
    }

    /**
     * Get the tile imageHeight
     *
     * @return tile imageHeight
     */
    public Number getTileHeight() {
        return isTiled() ? getNumberEntryValue(TiffExtendedTag.TileLength) : getRowsPerStrip();
    }

    /**
     * Set the tile imageHeight
     *
     * @param value tile imageHeight
     */
    public void setTileHeight(int value) {
        setEntryValue(TiffExtendedTag.TileLength, new UnsignedShortField(), 1, value);
    }

    /**
     * Set the tile imageHeight
     *
     * @param value tile imageHeight
     */
    public void setTileHeightAsLong(long value) {
        setEntryValue(TiffExtendedTag.TileLength, new UnsignedLongField(), 1, value);
    }

    /**
     * Get the tile offsets
     *
     * @return tile offsets
     */
    public List<Long> getTileOffsets() {
        return getLongListEntryValue(TiffExtendedTag.TileOffsets);
    }

    /**
     * Set the tile offsets
     *
     * @param value tile offsets
     */
    public void setTileOffsets(List<Long> value) {
        setEntryValue(TiffExtendedTag.TileOffsets, new UnsignedLongField(), value.size(), value);
    }

    /**
     * Set a single value tile offset
     *
     * @param tileOffset tile offset
     */
    public void setTileOffsets(long tileOffset) {
        setTileOffsets(List.of(tileOffset));
    }

    /**
     * Get the tile byte counts
     *
     * @return tile byte counts
     */
    public List<Number> getTileByteCounts() {
        return getNumberListEntryValue(TiffExtendedTag.TileByteCounts);
    }

    /**
     * Set the tile byte counts
     *
     * @param value tile byte counts
     */
    public void setTileByteCounts(List<Integer> value) {
        setEntryValue(TiffExtendedTag.TileByteCounts, new UnsignedShortField(), value.size(), value);
    }

    /**
     * Set the tile byte counts
     *
     * @param value tile byte counts
     */
    public void setTileByteCountsAsLongs(List<Long> value) {
        setEntryValue(TiffExtendedTag.TileByteCounts, new UnsignedLongField(), value.size(), value);
    }

    /**
     * Set a single value tile byte count
     *
     * @param tileByteCount tile byte count
     */
    public void setTileByteCounts(int tileByteCount) {
        setTileByteCounts(List.of(tileByteCount));
    }

    /**
     * Set a single value tile byte count
     *
     * @param tileByteCount tile byte count
     */
    public void setTileByteCounts(long tileByteCount) {
        setTileByteCountsAsLongs(List.of(tileByteCount));
    }

    /**
     * Get the sample format
     *
     * @return sample format
     */
    public List<SampleFormat> getSampleFormat() {
        List<Integer> idList = getIntegerListEntryValue(TiffExtendedTag.SampleFormat);
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
        setEntryValue(TiffExtendedTag.SampleFormat, new UnsignedShortField(), value.size(), value.stream().map(SampleFormat::getId).toList());
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
        return getMaxIntegerEntryValue(TiffExtendedTag.SampleFormat);
    }

    /**
     * Get the predictor
     *
     * @return predictor
     * @since 3.0.0
     */
    @NotNull
    public DifferencingPredictor getPredictor() {
        return DifferencingPredictor.findById(getIntegerEntryValue(TiffExtendedTag.Predictor));
    }

    /**
     * Set the predictor
     *
     * @param value predictor
     * @since 3.0.0
     */
    public void setPredictor(DifferencingPredictor value) {
        setEntryValue(TiffExtendedTag.Predictor, new UnsignedShortField(), 1, value.getId());
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
     * Get an integer entry value
     *
     * @param fieldTagType field tag type
     * @return integer value
     * @since 2.0.0
     */
    public Integer getIntegerEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
    }

    /**
     * Get an number entry value
     *
     * @param fieldTagType field tag type
     * @return number value
     * @since 2.0.0
     */
    public Number getNumberEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
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
        List<String> values = getEntryValue(fieldTagType);
        if (values != null && !values.isEmpty()) {
            value = values.getFirst();
        }
        return value;
    }

    /**
     * Set string value for the field tag type
     *
     * @param fieldTagType field tag type
     * @param value        string value
     * @since 2.0.0
     */
    public void setStringEntryValue(FieldTagType fieldTagType, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        setEntryValue(fieldTagType, new ASCIIField(), value.length() + 1, values);
    }

    /**
     * Get an integer list entry value
     *
     * @param fieldTagType field tag type
     * @return integer list value
     * @since 2.0.0
     */
    public List<Integer> getIntegerListEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
    }

    /**
     * Get a double list entry value
     *
     * @param fieldTagType field tag type
     * @return double list value
     * @since 2.0.2
     */
    public List<Double> getDoubleListEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
    }

    /**
     * Get the max integer from integer list entry values
     *
     * @param fieldTagType field tag type
     * @return max integer value
     * @since 2.0.0
     */
    public Integer getMaxIntegerEntryValue(FieldTagType fieldTagType) {
        Integer maxValue = null;
        List<Integer> values = getIntegerListEntryValue(fieldTagType);
        if (values != null) {
            maxValue = Collections.max(values);
        }
        return maxValue;
    }

    /**
     * Get a number list entry value
     *
     * @param fieldTagType field tag type
     * @return long list value
     * @since 2.0.0
     */
    public List<Number> getNumberListEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
    }

    /**
     * Get a long list entry value
     *
     * @param fieldTagType field tag type
     * @return long list value
     * @since 2.0.0
     */
    public List<Long> getLongListEntryValue(FieldTagType fieldTagType) {
        return getEntryValue(fieldTagType);
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
        setEntryValue(fieldTagType, new UnsignedRationalField(), 1, value);
    }

    /**
     * Get an entry value
     *
     * @param fieldTagType field tag type
     * @return value
     */
    @SuppressWarnings("unchecked")
    private <T> T getEntryValue(FieldTagType fieldTagType) {
        T value = null;
        FileDirectoryEntry entry = fieldTagTypeMapping.get(fieldTagType);
        if (entry != null) {
            value = (T) entry.values();
        }
        return value;
    }

    /**
     * Create and set the entry value
     *
     * @param fieldTagType field tag type
     * @param fieldType    field type
     * @param typeCount    type count
     * @param values       entry values
     */
    private void setEntryValue(FieldTagType fieldTagType, GenericFieldType fieldType, long typeCount, Object values) {
        FileDirectoryEntry entry = new FileDirectoryEntry(fieldTagType, fieldType, typeCount, values);
        addEntry(entry);
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
        for (FileDirectoryEntry entry : entries) {
            size += entry.sizeWithValues();
        }
        return size;
    }

}
