package mil.nga.tiff.internal.rasters;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.NumericFieldType;
import mil.nga.tiff.util.TiffException;

import java.util.List;

/**
 * Raster precalculated metadata
 *
 * @param width          Width of pixels
 * @param height         Height of pixels
 * @param fields         Field type for each sample
 * @param pixelSize      Calculated pixel size in bytes
 */
record RasterMetadata(
    int width,
    int height,
    List<NumericFieldType> fields,
    int pixelSize
) {

    public RasterMetadata(int width, int height, List<NumericFieldType> fields) {
        this(
            width,
            height,
            fields,
            fields.stream().map(GenericFieldType::metadata).mapToInt(FieldType::bytesPerSample).sum()
        );
    }

    public NumericFieldType field(int index) {
        return fields.get(index);
    }

    public FieldType fieldMetadata(int index) {
        return fields.get(index).metadata();
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
            .map(GenericFieldType::metadata)
            .mapToInt(FieldType::bytesPerSample)
            .map(bytes -> bytes * width)
            .map(bytesPerRow -> Math.max(1, maxBytesPerStrip / bytesPerRow))
            .min()
            .orElseThrow(() -> new IllegalStateException("No fields defined."));
    }

}
