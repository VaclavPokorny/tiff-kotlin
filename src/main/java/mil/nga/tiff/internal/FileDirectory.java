package mil.nga.tiff.internal;

import mil.nga.tiff.domain.UnsignedRational;
import mil.nga.tiff.field.FieldTypeDictionary;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.tag.TiffBasicTag;
import mil.nga.tiff.field.tag.TiffExtendedTag;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.UnsignedLongField;
import mil.nga.tiff.field.type.UnsignedShortField;
import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PhotometricInterpretation;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.ResolutionUnit;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.internal.rasters.Rasters;
import mil.nga.tiff.io.ByteReader;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * File Directory, represents all internal entries and can be used to read the
 * image raster
 */
public class FileDirectory {

    /**
     * Mapping between tags and entries
     */
    private final FileDirectoryDataHolder data;

    /**
     * Rasters to write to the TIFF file
     */
    private Rasters writeRasters = null;

    /**
     * Raster reader
     */
    private final FileDirectoryRasterReader rasterReader;

    /**
     * Directory basic statistics
     */
    private final DirectoryStats stats;

    public FileDirectory(FileDirectoryDataHolder data, Rasters writeRasters, FileDirectoryRasterReader rasterReader, DirectoryStats stats) {
        this.data = data;
        this.writeRasters = writeRasters;
        this.rasterReader = rasterReader;
        this.stats = stats;
    }

    public DirectoryStats getStats() {
        return stats;
    }

    public FileDirectoryRasterReader getRasterReader() {
        return rasterReader;
    }

    public FileDirectoryDataHolder getData() {
        return data;
    }

    /**
     * Constructor, for reading TIFF files
     *
     * @param entries file internal entries
     * @param reader TIFF file byte reader
     * @param cacheData true to cache tiles and strips
     */
    public static FileDirectory create(Set<FileDirectoryEntry<?>> entries, ByteReader reader, boolean cacheData, FieldTypeDictionary typeDictionary, Rasters writeRasters) {
        SortedMap<FieldTagType, FileDirectoryEntry<?>> fieldTagTypeMapping = new TreeMap<>(Comparator.comparingInt(FieldTagType::getId));
        for (FileDirectoryEntry<?> entry : entries) {
            fieldTagTypeMapping.put(entry.fieldTag, entry);
        }

        FileDirectoryDataHolder data = new FileDirectoryDataHolder(fieldTagTypeMapping);
        DirectoryStats stats = createStats(data);

        TileOrStripCache cache = new TileOrStripCache(cacheData);
        TileOrStripProcessor tileOrStripProcessor = new TileOrStripProcessor(stats, cache);

        FileDirectoryRasterReader rasterReader = new FileDirectoryRasterReader(stats, tileOrStripProcessor, typeDictionary, reader);

        return new FileDirectory(data, writeRasters, rasterReader, stats);
    }

    private static DirectoryStats createStats(FileDirectoryDataHolder data) {
        Integer rowsPerStrip = data.getSingleValue(TiffBasicTag.RowsPerStrip, Number::intValue);
        boolean isTiled = rowsPerStrip == null;
        Integer width = data.getSingleValue(TiffBasicTag.ImageWidth, Number::intValue);
        Integer height = data.getSingleValue(TiffBasicTag.ImageLength, Number::intValue);

        return new DirectoryStats(
            width,
            height,
            isTiled ? data.getSingleValue(TiffExtendedTag.TileWidth, Number::intValue) : width,
            isTiled ? data.getSingleValue(TiffExtendedTag.TileLength, Number::intValue) : rowsPerStrip,
            data.getSingleValue(TiffBasicTag.SamplesPerPixel, 1),
            data.getMultiValues(TiffBasicTag.BitsPerSample, Number::intValue),
            data.getMultiValues(TiffExtendedTag.SampleFormat, SampleFormat::findById),
            data.getSingleValue(TiffBasicTag.PlanarConfiguration, PlanarConfiguration::findById, PlanarConfiguration.DEFAULT),
            data.getMultiValues(TiffExtendedTag.TileOffsets),
            data.getMultiValues(TiffExtendedTag.TileByteCounts, Number::intValue),
            data.getMultiValues(TiffBasicTag.StripOffsets, Number::longValue),
            data.getMultiValues(TiffBasicTag.StripByteCounts, Number::intValue),
            data.getSingleValue(TiffBasicTag.Compression),
            data.getSingleValue(TiffExtendedTag.Predictor, DifferencingPredictor::findById, DifferencingPredictor.DEFAULT)
        );
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
        return data.numEntries();
    }

    /**
     * Get a file internal entry from the field tag type
     *
     * @param fieldTagType field tag type
     * @return file internal entry
     */
    public FileDirectoryEntry<?> get(FieldTagType fieldTagType) {
        return data.get(fieldTagType);
    }

    /**
     * Set the image imageWidth
     *
     * @param value image imageWidth
     */
    //TODO REMOVE
    public void setImageWidth(Integer value) {
        data.setSingleValue(TiffBasicTag.ImageWidth, new UnsignedShortField(), value);
    }

    /**
     * Get the image imageHeight
     *
     * @return image imageHeight
     */
    public Integer getImageHeight() {
        return data.getSingleValue(TiffBasicTag.ImageLength, Number::intValue);
    }

    /**
     * Set the image imageHeight
     *
     * @param value image imageHeight
     */
    //TODO REMOVE
    public void setImageHeight(Integer value) {
        data.setSingleValue(TiffBasicTag.ImageLength, new UnsignedShortField(), value);
    }

    /**
     * Set a single value bits per sample
     *
     * @param value bits per sample
     */
    //TODO REMOVE
    public void setBitsPerSample(Integer value) {
        data.setSingleValue(TiffBasicTag.BitsPerSample, new UnsignedShortField(), value);
    }

    /**
     * Get the compression
     *
     * @return compression
     */
    public Integer getCompression() {
        return data.getCompression();
    }

    /**
     * Set the compression
     *
     * @param value compression
     */
    public void setCompression(Integer value) {
        data.setSingleValue(TiffBasicTag.Compression, new UnsignedShortField(), value);
    }

    /**
     * Get the photometric interpretation
     *
     * @return photometric interpretation
     */
    public PhotometricInterpretation getPhotometricInterpretation() {
        return PhotometricInterpretation.findById(data.getSingleValue(TiffBasicTag.PhotometricInterpretation));
    }

    /**
     * Set the photometric interpretation
     *
     * @param value photometric interpretation
     */
    public void setPhotometricInterpretation(PhotometricInterpretation value) {
        data.setSingleValue(TiffBasicTag.PhotometricInterpretation, new UnsignedShortField(), value.getId());
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsets(List<Integer> value) {
        data.setMultiValues(TiffBasicTag.StripOffsets, new UnsignedShortField(), value);
    }

    /**
     * Set the strip offsets
     *
     * @param value strip offsets
     */
    public void setStripOffsetsAsLongs(List<Long> value) {
        data.setMultiValues(TiffBasicTag.StripOffsets, new UnsignedLongField(), value);
    }

    /**
     * Get the samples per pixel
     *
     * @return samples per pixel
     */
    public int getSamplesPerPixel() {
        return data.getSamplesPerPixel();
    }

    /**
     * Set the samples per pixel
     *
     * @param value samples per pixel
     */
    public void setSamplesPerPixel(Integer value) {
        data.setSingleValue(TiffBasicTag.SamplesPerPixel, new UnsignedShortField(), value);
    }

    /**
     * Get the rows per strip
     *
     * @return rows per strip
     */
    public Integer getRowsPerStrip() {
        return data.getRowsPerStrip();
    }

    /**
     * Set the rows per strip
     *
     * @param value rows per strip
     */
    public void setRowsPerStrip(Integer value) {
        data.setSingleValue(TiffBasicTag.RowsPerStrip, new UnsignedShortField(), value);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCounts(List<Integer> value) {
        data.setMultiValues(TiffBasicTag.StripByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set the strip byte counts
     *
     * @param value strip byte counts
     */
    public void setStripByteCountsAsLongs(List<Long> value) {
        data.setMultiValues(TiffBasicTag.StripByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    public void setStripByteCounts(Integer value) {
        data.setSingleValue(TiffBasicTag.StripByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set a single value strip byte count
     *
     * @param value strip byte count
     */
    public void setStripByteCounts(Long value) {
        data.setSingleValue(TiffBasicTag.StripByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Get the x resolution
     *
     * @return x resolution
     */
    public UnsignedRational getXResolution() {
        return data.getSingleValue(TiffBasicTag.XResolution);
    }

    /**
     * Set the x resolution
     *
     * @param value x resolution
     */
    public void setXResolution(UnsignedRational value) {
        data.setRationalEntryValue(TiffBasicTag.XResolution, value);
    }

    /**
     * Get the y resolution
     *
     * @return y resolution
     */
    public UnsignedRational getYResolution() {
        return data.getSingleValue(TiffBasicTag.YResolution);
    }

    /**
     * Set the y resolution
     *
     * @param value y resolution
     */
    public void setYResolution(UnsignedRational value) {
        data.setRationalEntryValue(TiffBasicTag.YResolution, value);
    }

    /**
     * Get the planar configuration
     *
     * @return planar configuration
     */
    public PlanarConfiguration getPlanarConfiguration() {
        return data.getPlanarConfiguration();
    }

    /**
     * Set the planar configuration
     *
     * @param value planar configuration
     */
    public void setPlanarConfiguration(PlanarConfiguration value) {
        data.setSingleValue(TiffBasicTag.PlanarConfiguration, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the resolution unit
     *
     * @return resolution unit
     */
    public ResolutionUnit getResolutionUnit() {
        return ResolutionUnit.findById(data.getSingleValue(TiffBasicTag.ResolutionUnit));
    }

    /**
     * Set the resolution unit
     *
     * @param value resolution unit
     */
    public void setResolutionUnit(ResolutionUnit value) {
        data.setSingleValue(TiffBasicTag.ResolutionUnit, new UnsignedShortField(), value.getId());
    }

    /**
     * Get the tile offsets
     *
     * @return tile offsets
     */
    public List<Long> getTileOffsets() {
        return data.getTileOffsets();
    }

    /**
     * Set the tile offsets
     *
     * @param value tile offsets
     */
    public void setTileOffsets(List<Long> value) {
        data.setMultiValues(TiffExtendedTag.TileOffsets, new UnsignedLongField(), value);
    }

    /**
     * Set a single value tile offset
     *
     * @param value tile offset
     */
    public void setTileOffsets(Long value) {
        data.setSingleValue(TiffExtendedTag.TileOffsets, new UnsignedLongField(), value);
    }

    /**
     * Get the tile byte counts
     *
     * @return tile byte counts
     */
    public List<Integer> getTileByteCounts() {
        return data.getTileByteCounts();
    }

    /**
     * Set the tile byte counts
     *
     * @param values tile byte counts
     */
    public void setTileByteCounts(List<Integer> values) {
        data.setMultiValues(TiffExtendedTag.TileByteCounts, new UnsignedShortField(), values);
    }

    /**
     * Set the tile byte counts
     *
     * @param values tile byte counts
     */
    public void setTileByteCountsAsLongs(List<Long> values) {
        data.setMultiValues(TiffExtendedTag.TileByteCounts, new UnsignedLongField(), values);
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    public void setTileByteCounts(Integer value) {
        data.setSingleValue(TiffExtendedTag.TileByteCounts, new UnsignedShortField(), value);
    }

    /**
     * Set a single value tile byte count
     *
     * @param value tile byte count
     */
    public void setTileByteCounts(Long value) {
        data.setSingleValue(TiffExtendedTag.TileByteCounts, new UnsignedLongField(), value);
    }

    /**
     * Get the sample format
     *
     * @return sample format
     */
    public List<SampleFormat> getSampleFormat() {
        return data.getSampleFormat();
    }

    /**
     * Set the sample format
     *
     * @param value sample format
     */
    public void setSampleFormat(List<SampleFormat> value) {
        data.setMultiValues(TiffExtendedTag.SampleFormat, new UnsignedShortField(), value.stream().map(SampleFormat::getId).toList());
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
        List<Integer> sampleFormat = data.getMultiValues(TiffExtendedTag.SampleFormat);
        OptionalInt result = sampleFormat.stream().mapToInt(Integer::intValue).max();
        if (result.isEmpty()) {
            return null;
        }
        return result.getAsInt();
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
     * Set write rasters
     *
     * @param writeRasters write rasters
     */
    public void setWriteRasters(Rasters writeRasters) {
        this.writeRasters = writeRasters;
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
        return rasterReader.readRasters(window, samples, sampleValues, interleaveValues, isTiled());
    }


    /**
     * Size in bytes of the Image File Directory (all contiguous)
     *
     * @return size in bytes
     */
    public long size() {
        return data.size();
    }

    /**
     * Size in bytes of the image file internal including entry values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    public long sizeWithValues() {
        return data.sizeWithValues();
    }


    /**
     * Create and set single entry value
     *
     * @param fieldTagType field tag type
     * @param value        entry value container
     */
    public <T> void setSingleValue(FieldTagType fieldTagType, GenericFieldType<T> type, T value) {
        data.setSingleValue(fieldTagType, type, value);
    }

    /**
     * Create and set single entry values
     *
     * @param fieldTagType field tag type
     * @param values        entry values container
     */
    public <T> void setMultiValues(FieldTagType fieldTagType, GenericFieldType<T> type, List<T> values) {
        data.setMultiValues(fieldTagType, type, values);
    }

}
