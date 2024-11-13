package mil.nga.tiff.internal;

import mil.nga.tiff.field.FieldTypeDictionary;
import mil.nga.tiff.field.type.NumericFieldType;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.internal.rasters.Rasters;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.IntStream;

public class FileDirectoryRasterReader {

    private final DirectoryStats stats;
    private final TileOrStripProcessor tileOrStripProcessor;
    private final FieldTypeDictionary typeDictionary;


    public FileDirectoryRasterReader(DirectoryStats stats, TileOrStripProcessor tileOrStripProcessor, FieldTypeDictionary typeDictionary) {
        this.stats = stats;
        this.tileOrStripProcessor = tileOrStripProcessor;
        this.typeDictionary = typeDictionary;
    }

    public Rasters readRasters(ImageWindow window, int[] samples, boolean sampleValues, boolean interleaveValues, ByteReader reader, boolean tiled) {

        // Validate the image window
        window.validate();
        window.validateFitsInImage(stats.imageWidth(), stats.imageHeight());

        int numPixels = window.numPixels();

        // Set or validate the samples
        int samplesPerPixel = stats.samplesPerPixel();
        if (samples == null) {
            samples = new int[samplesPerPixel];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = i;
            }
        } else {
            for (int sample : samples) {
                if (sample >= samplesPerPixel) {
                    throw new TiffException("Invalid sample index: " + sample);
                }
            }
        }

        // Create the interleaved result buffer
        List<Integer> bitsPerSample = stats.bitsPerSample();
        int bytesPerPixel = 0;
        for (int i = 0; i < samplesPerPixel; ++i) {
            bytesPerPixel += bitsPerSample.get(i) / 8;
        }
        ByteBuffer interleave = null;
        if (interleaveValues) {
            interleave = ByteBuffer.allocateDirect(numPixels * bytesPerPixel);
            interleave.order(reader.getByteOrder());
        }

        // Create the sample indexed result buffer array
        ByteBuffer[] sample = null;
        if (sampleValues) {
            sample = new ByteBuffer[samplesPerPixel];
            for (int i = 0; i < sample.length; ++i) {
                double numberOfBytes = (double) numPixels * Double.valueOf(bitsPerSample.get(i)) / 8;

                if (numberOfBytes > Integer.MAX_VALUE) {
                    throw new TiffException("Number of sample value bytes is above max byte buffer capacity: " + numberOfBytes);
                }

                sample[i] = ByteBuffer.allocateDirect((int) numberOfBytes);
                sample[i].order(reader.getByteOrder());
            }
        }

        List<NumericFieldType> fieldTypes = IntStream.range(0, samples.length)
            .mapToObj(this::getFieldTypeForSample)
            .toList();

        // Create the rasters results
        Rasters rasters = new Rasters(window.width(), window.height(), fieldTypes, sample, interleave);

        // Read the rasters
        readRaster(window, samples, rasters, reader, tiled);

        return rasters;
    }

    /**
     * Read and populate the rasters
     *
     * @param window  image window
     * @param samples pixel samples to read
     * @param rasters rasters to populate
     */
    private void readRaster(ImageWindow window, int[] samples, Rasters rasters, ByteReader reader, boolean tiled) {

        int tileWidth = stats.tileWidth();
        int tileHeight = stats.tileHeight();

        int minXTile = window.minX() / tileWidth;
        int maxXTile = (window.maxX() + tileWidth - 1) / tileWidth;
        int minYTile = window.minY() / tileHeight;
        int maxYTile = (window.maxY() + tileHeight - 1) / tileHeight;

        int bytesPerPixel = getBytesPerPixel();

        int[] srcSampleOffsets = new int[samples.length];
        NumericFieldType[] sampleFieldTypes = new NumericFieldType[samples.length];
        for (int i = 0; i < samples.length; i++) {
            int sampleOffset = 0;
            if (stats.planarConfiguration() == PlanarConfiguration.CHUNKY) {
                sampleOffset = stats.bitsPerSample()
                        .subList(0, samples[i])
                        .stream()
                        .mapToInt(Integer::intValue)
                        .sum() / 8;
            }
            srcSampleOffsets[i] = sampleOffset;
            sampleFieldTypes[i] = getFieldTypeForSample(samples[i]);
        }

        for (int yTile = minYTile; yTile < maxYTile; yTile++) {
            for (int xTile = minXTile; xTile < maxXTile; xTile++) {

                int firstLine = yTile * tileHeight;
                int firstCol = xTile * tileWidth;
                int lastLine = (yTile + 1) * tileHeight;
                int lastCol = (xTile + 1) * tileWidth;

                for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                    int sample = samples[sampleIndex];
                    if (stats.planarConfiguration() == PlanarConfiguration.PLANAR) {
                        bytesPerPixel = getSampleByteSize(sample);
                    }

                    byte[] block = tileOrStripProcessor.run(xTile, yTile, sample, reader, tiled, reader.getByteOrder());
                    ByteReader blockReader = new ByteReader(block, reader.getByteOrder());

                    for (int y = Math.max(0, window.minY() - firstLine); y < Math.min(tileHeight, tileHeight - (lastLine - window.maxY())); y++) {

                        for (int x = Math.max(0, window.minX() - firstCol); x < Math.min(tileWidth, tileWidth - (lastCol - window.maxX())); x++) {

                            int pixelOffset = (y * tileWidth + x) * bytesPerPixel;
                            int valueOffset = pixelOffset + srcSampleOffsets[sampleIndex];
                            blockReader.setNextByte(valueOffset);

                            // Read the value
                            Number value = sampleFieldTypes[sampleIndex].readValue(blockReader);

                            if (rasters.hasInterleaveValues()) {
                                int windowCoordinate = (y + firstLine - window.minY()) * window.width() + (x + firstCol - window.minX());
                                rasters.addToInterleave(sampleIndex, windowCoordinate, value);
                            }

                            if (rasters.hasSampleValues()) {
                                int windowCoordinate = (y + firstLine - window.minY()) * window.width() + x + firstCol - window.minX();
                                rasters.addToSample(sampleIndex, windowCoordinate, value);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Get the sample byte size
     *
     * @param sampleIndex sample index
     * @return byte size
     */
    private int getSampleByteSize(int sampleIndex) {
        List<Integer> bitsPerSample = stats.bitsPerSample();
        if (sampleIndex >= bitsPerSample.size()) {
            throw new TiffException("Sample index " + sampleIndex + " is out of range");
        }
        int bits = bitsPerSample.get(sampleIndex);
        if ((bits % 8) != 0) {
            throw new TiffException("Sample bit-imageWidth of " + bits + " is not supported");
        }
        return (bits / 8);
    }

    /**
     * Calculates the number of bytes for each pixel across all samples. Only
     * full bytes are supported, an exception is thrown when this is not the
     * case.
     *
     * @return the bytes per pixel
     */
    private int getBytesPerPixel() {
        int bitsPerSample = 0;
        List<Integer> bitsPerSamples = stats.bitsPerSample();
        for (int i = 0; i < bitsPerSamples.size(); i++) {
            int bits = bitsPerSamples.get(i);
            if ((bits % 8) != 0) {
                throw new TiffException("Sample bit-imageWidth of " + bits + " is not supported");
            } else if (bits != bitsPerSamples.getFirst()) {
                throw new TiffException("Differing size of samples in a pixel are not supported. sample 0 = " + bitsPerSamples.getFirst() + ", sample " + i + " = " + bits);
            }
            bitsPerSample += bits;
        }
        return bitsPerSample / 8;
    }

    /**
     * Get the field type for the sample
     *
     * @param sampleIndex sample index
     * @return field type
     */
    public NumericFieldType getFieldTypeForSample(int sampleIndex) {
        SampleFormat sampleFormat;
        List<SampleFormat> sampleFormatList = stats.sampleFormatList();
        if (sampleFormatList.isEmpty()) {
            sampleFormat = SampleFormat.UNSIGNED_INT;
        } else {
            int listId = sampleIndex < sampleFormatList.size() ? sampleIndex : 0;
            sampleFormat = sampleFormatList.get(listId);
        }
        int bitsPerSample = stats.bitsPerSample().get(sampleIndex);
        return typeDictionary.findBySampleParams(sampleFormat, bitsPerSample);
    }

}
