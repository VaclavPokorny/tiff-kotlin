package mil.nga.tiff.internal.rasters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

record InterleaveValues(ByteBuffer values, RasterMetadata metadata) {

    public boolean isNotEmpty() {
        return values != null;
    }

    private int interleaveIndexLocationForPixel(int x, int y) {
        return (y * metadata.width() + x) * metadata.pixelSize();
    }

    private int interleaveIndexLocation(int x, int y) {
        return (y * metadata.width() * metadata.pixelSize()) + (x * metadata.pixelSize());
    }


    public void setPixelSample(int sample, int x, int y, Number value) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(sample);

        int interleaveIndex = interleaveIndexLocationForPixel(x, y);
        for (int i = 0; i < sample; ++i) {
            interleaveIndex += metadata.field(sample).metadata().bytesPerSample();
        }
        metadata.field(sample).updateSampleInByteBuffer(values, interleaveIndex, sample, value);
    }

    public Number getPixelSample(int sample, int x, int y) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(sample);

        int bufferPos = interleaveIndexLocation(x, y);
        for (int i = 0; i < sample; i++) {
            bufferPos += metadata.field(sample).metadata().bytesPerSample();
        }

        return metadata.field(sample).getSampleFromByteBuffer(values, bufferPos, sample);
    }

    public byte[] getSampleRow(int y, int sample, ByteOrder newOrder) {
        ByteBuffer outBuffer = ByteBuffer.allocate(metadata.width() * metadata.field(sample).metadata().bytesPerSample());
        outBuffer.order(newOrder);

        int sampleOffset = 0;
        for (int i = 0; i < sample; ++i) {
            sampleOffset += metadata.field(sample).metadata().bytesPerSample();
        }

        for (int i = 0; i < metadata.width(); ++i) {
            values.position((y * metadata.width() + i) * metadata.pixelSize() + sampleOffset);
            metadata.field(sample).writeSample(outBuffer, values);
        }

        return outBuffer.array();
    }

    public byte[] getPixelRow(int y, ByteOrder newOrder) {
        ByteBuffer outBuffer = ByteBuffer.allocate(metadata.width() * metadata.pixelSize());
        outBuffer.order(newOrder);

        values.position(y * metadata.width() * metadata.pixelSize());

        for (int i = 0; i < metadata.width(); ++i) {
            for (int j = 0; j < metadata.samplesPerPixel(); ++j) {
                metadata.field(j).writeSample(outBuffer, values);
            }
        }

        return outBuffer.array();
    }

    public void setPixel(int x, int y, Number[] values) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(values.length + 1);

        int interleaveIndex = interleaveIndexLocationForPixel(x, y);
        for (int i = 0; i < metadata.samplesPerPixel(); i++) {
            metadata.field(i).updateSampleInByteBuffer(this.values, interleaveIndex, i, values[i]);
            interleaveIndex += metadata.field(i).metadata().bytesPerSample();
        }
    }

    public Number[] getPixel(int x, int y) {

        metadata.validateCoordinates(x, y);

        // Pixel with each sample value
        Number[] pixel = new Number[metadata.samplesPerPixel()];

        // Get the pixel values from each sample
        int interleaveIndex = interleaveIndexLocation(x, y);
        for (int i = 0; i < metadata.samplesPerPixel(); i++) {
            pixel[i] = metadata.field(i).getSampleFromByteBuffer(values, interleaveIndex, i);
            interleaveIndex += metadata.field(i).metadata().bytesPerSample();
        }

        return pixel;
    }

    /**
     * Add a value to the interleaved results
     *
     * @param sampleIndex sample index
     * @param coordinate  coordinate location
     * @param value       value
     * @since 2.0.0
     */
    public void addValue(int sampleIndex, int coordinate, Number value) {
        int bufferPos = coordinate * metadata.pixelSize();
        for (int i = 0; i < sampleIndex; ++i) {
            bufferPos += metadata.field(i).metadata().bytesPerSample();
        }

        metadata.field(sampleIndex).updateSampleInByteBuffer(values, bufferPos, sampleIndex, value);
    }

}
