package mil.nga.tiff.internal.rasters;

import mil.nga.tiff.field.type.AbstractFieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.util.TiffException;

import java.util.List;

/**
 * Raster precalculated metadata
 *
 * @param width         Width of pixels
 * @param height        Height of pixels
 * @param fields    Field type for each sample
 * @param pixelSize     Calculated pixel size in bytes
 * @param bitsPerSample Bits per sample
 * @param sampleFormat  List of sample types constants
 */
record RasterMetadata(
    int width,
    int height,
    List<AbstractFieldType> fields,
    int pixelSize,
    List<Integer> bitsPerSample,
    List<SampleFormat> sampleFormat
) {

    public RasterMetadata(int width, int height, List<AbstractFieldType> fields) {
        this(
            width,
            height,
            fields,
            fields.stream().mapToInt(AbstractFieldType::getBytes).sum(),
            fields.stream().map(AbstractFieldType::getBits).toList(),
            fields.stream().map(AbstractFieldType::getSampleFormat).toList()
        );
    }

    public AbstractFieldType field(int index) {
        return fields.get(index);
    }

    /**
     * Validate the coordinates range
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void validateCoordinates(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y > height) {
            throw new TiffException("Pixel oustide of raster range. Width: " + width + ", Height: " + height + ", x: " + x + ", y: " + y);
        }
    }

    /**
     * Validate the sample index
     *
     * @param sample sample index
     */
    public void validateSample(int sample) {
        if (sample < 0 || sample >= fields.size()) {
            throw new TiffException("Pixel sample out of bounds. sample: " + sample + ", samples per pixel: " + fields.size());
        }
    }

    /**
     * Get the number of samples per pixel
     *
     * @return samples per pixel
     */
    public int samplesPerPixel() {
        return fields().size();
    }

    public int calculateRowsPerStripChunky(int maxBytesPerStrip) {
        int bytesPerRow = pixelSize * width;
        return Math.max(1, maxBytesPerStrip / bytesPerRow);
    }

    public int calculateRowsPerStripPlanar(int maxBytesPerStrip) {
        return fields.stream()
            .mapToInt(AbstractFieldType::getBytes)
            .map(bytes -> bytes * width)
            .map(bytesPerRow -> Math.max(1, maxBytesPerStrip / bytesPerRow))
            .min()
            .orElseThrow(() -> new IllegalStateException("No fields defined."));
    }

}
