package mil.nga.tiff.internal;

import mil.nga.tiff.compression.CompressionDecoder;
import mil.nga.tiff.field.FieldTagType;
import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.TiffBasicTag;
import mil.nga.tiff.field.type.enumeration.Compression;
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PhotometricInterpretation;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.ResolutionUnit;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

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
     * Differencing Predictor
     */
    private final DifferencingPredictor predictor;

    /**
     * Compression decoder
     */
    private final CompressionDecoder decoder;

    /**
     * Rasters to write to the TIFF file
     */
    private Rasters writeRasters = null;


    private final FileDirectoryRasterReader rasterReader;


    /**
     * Constructor, for reading TIFF files
     *
     * @param entries file internal entries
     * @param reader  TIFF file byte reader
     */
    public FileDirectory(SortedSet<FileDirectoryEntry> entries, ByteReader reader) {
        this(entries, reader, false);
    }

    /**
     * Constructor, for reading TIFF files
     *
     * @param entries   file internal entries
     * @param reader    TIFF file byte reader
     * @param cacheData true to cache tiles and strips
     */
    public FileDirectory(SortedSet<FileDirectoryEntry> entries, ByteReader reader, boolean cacheData) {
        TileOrStripCache cache = new TileOrStripCache(cacheData);
        TileOrStripProcessor tileOrStripProcessor = new TileOrStripProcessor(this, cache);
        this.rasterReader = new FileDirectoryRasterReader(this, tileOrStripProcessor);

        // Set the entries and the field tag type mapping
        this.entries = entries;
        for (FileDirectoryEntry entry : entries) {
            fieldTagTypeMapping.put(entry.fieldTag(), entry);
        }

        this.reader = reader;

        // Determine the decoder based upon the compression
        this.decoder = Compression.getDecoder(getCompression());

        // Determine the differencing predictor
        this.predictor = getPredictor();
    }

    /**
     * Add an entry
     *
     * @param entry file internal entry
     */
    public void addEntry(FileDirectoryEntry entry) {
        entries.remove(entry);
        entries.add(entry);
        fieldTagTypeMapping.put(entry.fieldTag(), entry);
    }

    /**
     * Get the byte reader
     *
     * @return byte reader
     */
    public ByteReader getReader() {
        return reader;
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
     * Get the compression decoder
     *
     * @return compression decoder
     */
    public CompressionDecoder getDecoder() {
        return decoder;
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
     * Get the field tag type to file internal entry mapping
     *
     * @return field tag type mapping
     */
    public Map<FieldTagType, FileDirectoryEntry> getFieldTagTypeMapping() {
        return Collections.unmodifiableMap(fieldTagTypeMapping);
    }

    /**
     * Get the image width
     *
     * @return image width
     */
    public Number getImageWidth() {
        return getNumberEntryValue(TiffBasicTag.ImageWidth);
    }

    /**
     * Set the image width
     *
     * @param value image width
     */
    public void setImageWidth(int value) {
        setEntryValue(TiffBasicTag.ImageWidth, FieldType.SHORT, 1, value);
    }

    /**
     * Set the image width
     *
     * @param value image width
     */
    public void setImageWidthAsLong(long value) {
        setEntryValue(TiffBasicTag.ImageWidth, FieldType.LONG, 1, value);
    }

    /**
     * Get the image height
     *
     * @return image height
     */
    public Number getImageHeight() {
        return getNumberEntryValue(TiffBasicTag.ImageLength);
    }

    /**
     * Set the image height
     *
     * @param value image height
     */
    public void setImageHeight(int value) {
        setEntryValue(TiffBasicTag.ImageLength, FieldType.SHORT, 1, value);
    }

    /**
     * Set the image height
     *
     * @param value image height
     */
    public void setImageHeightAsLong(long value) {
        setEntryValue(TiffBasicTag.ImageLength, FieldType.LONG, 1, value);
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
        setEntryValue(TiffBasicTag.BitsPerSample, FieldType.SHORT, value.size(), value);
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
        setEntryValue(TiffBasicTag.Compression, FieldType.SHORT, 1, value);
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
        setEntryValue(TiffBasicTag.PhotometricInterpretation, FieldType.SHORT, 1, value.getId());
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
        setEntryValue(TiffBasicTag.StripOffsets, FieldType.SHORT, value.size(), value);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsetsAsLongs(List<Long> value) {
        setEntryValue(TiffBasicTag.StripOffsets, FieldType.LONG, value.size(), value);
    }

    /**
     * Set a single value strip offset
     *
     * @param stripOffset strip offset
     */
    public void setStripOffsets(int stripOffset) {
        setStripOffsets(List.of(stripOffset));
    }

    /**
     * Set a single value strip offset
     *
     * @param stripOffset strip offset
     */
    public void setStripOffsets(long stripOffset) {
        setStripOffsetsAsLongs(List.of(stripOffset));
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
        setEntryValue(TiffBasicTag.SamplesPerPixel, FieldType.SHORT, 1, value);
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
        setEntryValue(TiffBasicTag.RowsPerStrip, FieldType.SHORT, 1, value);
    }

    /**
     * Set the rows per strip
     *
     * @param value rows per strip
     */
    public void setRowsPerStripAsLong(long value) {
        setEntryValue(TiffBasicTag.RowsPerStrip, FieldType.LONG, 1, value);
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
        setEntryValue(TiffBasicTag.StripByteCounts, FieldType.SHORT, value.size(), value);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCountsAsLongs(List<Long> value) {
        setEntryValue(TiffBasicTag.StripByteCounts, FieldType.LONG, value.size(), value);
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
    public List<Long> getXResolution() {
        return getLongListEntryValue(TiffBasicTag.XResolution);
    }

    /**
     * Set the x resolution
     *
     * @param xResolution x resolution
     */
    public void setXResolution(List<Long> xResolution) {
        setRationalEntryValue(TiffBasicTag.XResolution, xResolution);
    }

    /**
     * Set a single value x resolution
     *
     * @param xResolution x resolution
     */
    public void setXResolution(long xResolution) {
        setXResolution(new ArrayList<>(List.of(xResolution, 1L)));
    }

    /**
     * Get the y resolution
     *
     * @return y resolution
     */
    public List<Long> getYResolution() {
        return getLongListEntryValue(TiffBasicTag.YResolution);
    }

    /**
     * Set the y resolution
     *
     * @param yResolution y resolution
     */
    public void setYResolution(List<Long> yResolution) {
        setRationalEntryValue(TiffBasicTag.YResolution, yResolution);
    }

    /**
     * Set a single value y resolution
     *
     * @param yResolution y resolution
     */
    public void setYResolution(long yResolution) {
        setYResolution(new ArrayList<>(List.of(yResolution, 1L)));
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
        setEntryValue(TiffBasicTag.PlanarConfiguration, FieldType.SHORT, 1, value.getId());
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
        setEntryValue(TiffBasicTag.ResolutionUnit, FieldType.SHORT, 1, value.getId());
    }

    /**
     * Get the model pixel scale
     *
     * @return model pixel scale
     * @since 2.0.2
     */
    public List<Double> getModelPixelScale() {
        return getDoubleListEntryValue(TiffBasicTag.ModelPixelScale);
    }

    /**
     * Set the model pixel scale
     *
     * @param value pixel scale
     * @since 2.0.5
     */
    public void setModelPixelScale(List<Double> value) {
        setEntryValue(TiffBasicTag.ModelPixelScale, FieldType.DOUBLE, value.size(), value);
    }

    /**
     * Get the model tiepoint
     *
     * @return model tiepoint
     * @since 2.0.2
     */
    public List<Double> getModelTiepoint() {
        return getDoubleListEntryValue(TiffBasicTag.ModelTiepoint);
    }

    /**
     * Set the model tiepoint
     *
     * @param value model tiepoint
     * @since 2.0.5
     */
    public void setModelTiepoint(List<Double> value) {
        setEntryValue(TiffBasicTag.ModelTiepoint, FieldType.DOUBLE, value.size(), value);
    }

    /**
     * Get the color map
     *
     * @return color map
     */
    public List<Integer> getColorMap() {
        return getIntegerListEntryValue(TiffBasicTag.ColorMap);
    }

    /**
     * Set the color map
     *
     * @param value color map
     */
    public void setColorMap(List<Integer> value) {
        setEntryValue(TiffBasicTag.ColorMap, FieldType.SHORT, value.size(), value);
    }

    /**
     * Set a single value color map
     *
     * @param colorMap color map
     */
    public void setColorMap(int colorMap) {
        setColorMap(List.of(colorMap));
    }

    /**
     * Get the tile width
     *
     * @return tile width
     */
    public Number getTileWidth() {
        return isTiled() ? getNumberEntryValue(TiffBasicTag.TileWidth) : getImageWidth();
    }

    /**
     * Set the tile width
     *
     * @param value tile width
     */
    public void setTileWidth(int value) {
        setEntryValue(TiffBasicTag.TileWidth, FieldType.SHORT, 1, value);
    }

    /**
     * Set the tile width
     *
     * @param value tile width
     */
    public void setTileWidthAsLong(long value) {
        setEntryValue(TiffBasicTag.TileWidth, FieldType.LONG, 1, value);
    }

    /**
     * Get the tile height
     *
     * @return tile height
     */
    public Number getTileHeight() {
        return isTiled() ? getNumberEntryValue(TiffBasicTag.TileLength) : getRowsPerStrip();
    }

    /**
     * Set the tile height
     *
     * @param value tile height
     */
    public void setTileHeight(int value) {
        setEntryValue(TiffBasicTag.TileLength, FieldType.SHORT, 1, value);
    }

    /**
     * Set the tile height
     *
     * @param value tile height
     */
    public void setTileHeightAsLong(long value) {
        setEntryValue(TiffBasicTag.TileLength, FieldType.LONG, 1, value);
    }

    /**
     * Get the tile offsets
     *
     * @return tile offsets
     */
    public List<Long> getTileOffsets() {
        return getLongListEntryValue(TiffBasicTag.TileOffsets);
    }

    /**
     * Set the tile offsets
     *
     * @param value tile offsets
     */
    public void setTileOffsets(List<Long> value) {
        setEntryValue(TiffBasicTag.TileOffsets, FieldType.LONG, value.size(), value);
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
        return getNumberListEntryValue(TiffBasicTag.TileByteCounts);
    }

    /**
     * Set the tile byte counts
     *
     * @param value tile byte counts
     */
    public void setTileByteCounts(List<Integer> value) {
        setEntryValue(TiffBasicTag.TileByteCounts, FieldType.SHORT, value.size(), value);
    }

    /**
     * Set the tile byte counts
     *
     * @param value tile byte counts
     */
    public void setTileByteCountsAsLongs(List<Long> value) {
        setEntryValue(TiffBasicTag.TileByteCounts, FieldType.LONG, value.size(), value);
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
        return getIntegerListEntryValue(TiffBasicTag.SampleFormat)
            .stream()
            .map(SampleFormat::findById)
            .toList();
    }

    /**
     * Set the sample format
     *
     * @param value sample format
     */
    public void setSampleFormat(List<SampleFormat> value) {
        setEntryValue(TiffBasicTag.SampleFormat, FieldType.SHORT, value.size(), value.stream().map(SampleFormat::getId).toList());
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
        return getMaxIntegerEntryValue(TiffBasicTag.SampleFormat);
    }

    /**
     * Get the predictor
     *
     * @return predictor
     * @since 3.0.0
     */
    public DifferencingPredictor getPredictor() {
        return DifferencingPredictor.findById(getIntegerEntryValue(TiffBasicTag.Predictor));
    }

    /**
     * Set the predictor
     *
     * @param value predictor
     * @since 3.0.0
     */
    public void setPredictor(DifferencingPredictor value) {
        setEntryValue(TiffBasicTag.Predictor, FieldType.SHORT, 1, value.getId());
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
        ImageWindow window = new ImageWindow(this);
        return readRasters(window);
    }

    /**
     * Read the rasters as interleaved
     *
     * @return rasters
     */
    public Rasters readInterleavedRasters() {
        ImageWindow window = new ImageWindow(this);
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
        ImageWindow window = new ImageWindow(this);
        return readRasters(window, samples);
    }

    /**
     * Read the rasters as interleaved
     *
     * @param samples pixel samples to read
     * @return rasters
     */
    public Rasters readInterleavedRasters(int[] samples) {
        ImageWindow window = new ImageWindow(this);
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
        ImageWindow window = new ImageWindow(this);
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
        ImageWindow window = new ImageWindow(this);
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
        return rasterReader.readRasters(window, samples, sampleValues, interleaveValues, reader, getPlanarConfiguration(), isTiled(), predictor);
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
        setEntryValue(fieldTagType, FieldType.ASCII, value.length() + 1, values);
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
    public void setRationalEntryValue(FieldTagType fieldTagType, List<Long> value) {
        if (value == null || value.size() != 2) {
            throw new TiffException("Invalid rational value, must be two longs. Size: " + (value == null ? null : value.size()));
        }
        setEntryValue(fieldTagType, FieldType.RATIONAL, 1, value);
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
    private void setEntryValue(FieldTagType fieldTagType, FieldType fieldType, long typeCount, Object values) {
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
