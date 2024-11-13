package mil.nga.tiff.internal.rasters;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.NumericFieldType;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Raster image values
 *
 * @author osbornb
 */
public class Rasters {

    /**
     * Values separated by sample
     */
    @NotNull
    final SampleValues sampleValues;

    /**
     * Interleaved pixel sample values
     */
    @NotNull
    final InterleaveValues interleaveValues;

    @NotNull
    final RasterMetadata metadata;


    /**
     * Constructor
     *
     * @param width            imageWidth of pixels
     * @param height           imageHeight of pixels
     * @param fieldTypes       Field type for each sample
     * @param sampleValues     empty sample values buffer array
     * @param interleaveValues empty interleaved values buffer
     * @since 2.0.0
     */
    public Rasters(int width, int height, List<NumericFieldType> fieldTypes, ByteBuffer[] sampleValues, ByteBuffer interleaveValues) {
        this.metadata = new RasterMetadata(width, height, fieldTypes);

        this.sampleValues = new SampleValues(sampleValues, metadata);
        this.interleaveValues = new InterleaveValues(interleaveValues, metadata);

        if (!this.sampleValues.isNotEmpty() && !this.interleaveValues.isNotEmpty()) {
            throw new TiffException("Results must be sample and/or interleave based");
        }
    }

    /**
     * True if the results are stored by samples
     *
     * @return true if results exist
     */
    public boolean hasSampleValues() {
        return sampleValues.isNotEmpty();
    }

    /**
     * True if the results are stored interleaved
     *
     * @return true if results exist
     */
    public boolean hasInterleaveValues() {
        return interleaveValues.isNotEmpty();
    }

    /**
     * Add a value to the sample results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     */
    public void addToSample(int sampleIndex, int coordinate, Number value) {
        sampleValues.addValue(sampleIndex, coordinate, value);
    }

    /**
     * Add a value to the interleaved results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     * @since 2.0.0
     */
    public void addToInterleave(int sampleIndex, int coordinate, Number value) {
        interleaveValues.addValue(sampleIndex, coordinate, value);
    }

    /**
     * Get the imageWidth of pixels
     *
     * @return imageWidth
     */
    public int getWidth() {
        return metadata.width();
    }

    /**
     * Get the imageHeight of pixels
     *
     * @return imageHeight
     */
    public int getHeight() {
        return metadata.height();
    }

    /**
     * Return the number of pixels
     *
     * @return number of pixels
     */
    public int getNumPixels() {
        return metadata.width() * metadata.height();
    }

    /**
     * Get the number of samples per pixel
     *
     * @return samples per pixel
     */
    public int getSamplesPerPixel() {
        return metadata.samplesPerPixel();
    }

    public List<FieldType> getFields() {
        return metadata.fields().stream().map(GenericFieldType::metadata).toList();
    }

    /**
     * Get the pixel sample values
     *
     * @param x x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @return pixel sample values
     */
    public Number[] getPixel(int x, int y) {
        // Get the pixel values from each sample
        if (sampleValues.isNotEmpty()) {
            return sampleValues.getPixel(x, y);
        } else {
            return interleaveValues.getPixel(x, y);
        }
    }

    /**
     * Set the pixel sample values
     *
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @param values pixel values
     */
    public void setPixel(int x, int y, Number[] values) {
        // Set the pixel values from each sample
        if (sampleValues.isNotEmpty()) {
            sampleValues.setPixel(x, y, values);
        } else {
            interleaveValues.setPixel(x, y, values);
        }
    }

    /**
     * Returns byte array of pixel row.
     *
     * @param y        Row index
     * @param newOrder Desired byte order of result byte array
     * @return Byte array of pixel row
     * @since 2.0.0
     */
    public byte[] getPixelRow(int y, ByteOrder newOrder) {
        if (sampleValues.isNotEmpty()) {
            return sampleValues.getPixelRow(y, newOrder);
        } else {
            return interleaveValues.getPixelRow(y, newOrder);
        }
    }

    /**
     * Returns byte array of sample row.
     *
     * @param y        Row index
     * @param sample   Sample index
     * @param newOrder Desired byte order of resulting byte array
     * @return Byte array of sample row
     * @since 2.0.0
     */
    public byte[] getSampleRow(int y, int sample, ByteOrder newOrder) {
        if (sampleValues.isNotEmpty()) {
            return sampleValues.getSampleRow(y, sample, newOrder);
        } else {
            return interleaveValues.getSampleRow(y, sample, newOrder);
        }
    }

    /**
     * Get a pixel sample value
     *
     * @param sample sample index (&gt;= 0 &amp;&amp; &lt;
     *               {@link #getSamplesPerPixel()})
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @return pixel sample
     */
    public Number getPixelSample(int sample, int x, int y) {
        if (sampleValues.isNotEmpty()) {
            return sampleValues.getPixelSample(sample, x, y);
        } else {
            return interleaveValues.getPixelSample(sample, x, y);
        }
    }

    /**
     * Set a pixel sample value
     *
     * @param sample sample index (&gt;= 0 &amp;&amp; &lt;
     *               {@link #getSamplesPerPixel()})
     * @param x      x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y      y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @param value  pixel value
     */
    public void setPixelSample(int sample, int x, int y, Number value) {
        if (sampleValues.isNotEmpty()) {
            sampleValues.setPixelSample(sample, x, y, value);
        }
        if (interleaveValues.isNotEmpty()) {
            interleaveValues.setPixelSample(sample, x, y, value);
        }
    }

    /**
     * Get the first pixel sample value, useful for single sample pixels
     * (grayscale)
     *
     * @param x x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @return first pixel sample
     */
    public Number getFirstPixelSample(int x, int y) {
        return getPixelSample(0, x, y);
    }

    /**
     * Set the first pixel sample value, useful for single sample pixels
     * (grayscale)
     *
     * @param x     x coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getWidth()})
     * @param y     y coordinate (&gt;= 0 &amp;&amp; &lt; {@link #getHeight()})
     * @param value pixel value
     */
    public void setFirstPixelSample(int x, int y, Number value) {
        setPixelSample(0, x, y, value);
    }

    /**
     * Size in bytes of the image
     *
     * @return bytes
     */
    public int size() {
        return getNumPixels() * metadata.pixelSize();
    }

    /**
     * Size in bytes of a pixel
     *
     * @return bytes
     */
    public int sizePixel() {
        return metadata.pixelSize();
    }

    /**
     * Calculate the rows per strip to write
     *
     * @param planarConfiguration chunky or planar
     * @return rows per strip
     */
    public int calculateRowsPerStrip(PlanarConfiguration planarConfiguration) {
        return calculateRowsPerStrip(planarConfiguration, TiffConstants.DEFAULT_MAX_BYTES_PER_STRIP);
    }

    /**
     * Calculate the rows per strip to write
     *
     * @param planarConfiguration chunky or planar
     * @param maxBytesPerStrip    attempted max bytes per strip
     * @return rows per strip
     */
    public int calculateRowsPerStrip(PlanarConfiguration planarConfiguration, int maxBytesPerStrip) {
        return switch (planarConfiguration) {
            case CHUNKY -> metadata.calculateRowsPerStripChunky(maxBytesPerStrip);
            case PLANAR -> metadata.calculateRowsPerStripPlanar(maxBytesPerStrip);
        };
    }

}
