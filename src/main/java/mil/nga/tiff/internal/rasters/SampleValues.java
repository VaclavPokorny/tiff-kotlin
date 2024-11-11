package mil.nga.tiff.internal.rasters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

record SampleValues(ByteBuffer[] values, RasterMetadata metadata) {

    public boolean isNotEmpty() {
        return values != null;
    }

    private int sampleIndexLocation(int x, int y) {
        return y * metadata.width() + x;
    }

    public void setPixelSample(int sample, int x, int y, Number value) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(sample);

        int bufferIndex = sampleIndexLocation(x, y) * metadata.field(sample).getBytes();
        metadata.field(sample).updateSampleInByteBuffer(values[sample], bufferIndex, sample, value);
    }

    public Number getPixelSample(int sample, int x, int y) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(sample);

        int bufferPos = sampleIndexLocation(x, y) * metadata.field(sample).getBytes();
        return metadata.field(sample).getSampleFromByteBuffer(values[sample], bufferPos, sample);
    }

    public byte[] getSampleRow(int y, int sample, ByteOrder newOrder) {
        ByteBuffer outBuffer = ByteBuffer.allocate(metadata.width() * metadata.field(sample).getBytes());
        outBuffer.order(newOrder);

        values[sample].position(y * metadata.width() * metadata.field(sample).getBytes());
        for (int x = 0; x < metadata.width(); ++x) {
            metadata.field(sample).writeSample(outBuffer, values[sample]);
        }

        return outBuffer.array();
    }

    public byte[] getPixelRow(int y, ByteOrder newOrder) {
        ByteBuffer outBuffer = ByteBuffer.allocate(metadata.width() * metadata.pixelSize());
        outBuffer.order(newOrder);

        for (int i = 0; i < metadata.samplesPerPixel(); ++i) {
            values[i].position(y * metadata.width() * metadata.field(i).getBytes());
        }
        for (int i = 0; i < metadata.width(); ++i) {
            for (int j = 0; j < metadata.samplesPerPixel(); ++j) {
                metadata.field(j).writeSample(outBuffer, values[j]);
            }
        }

        return outBuffer.array();
    }

    public void setPixel(int x, int y, Number[] values) {
        metadata.validateCoordinates(x, y);
        metadata.validateSample(values.length + 1);

        // Set the pixel values from each sample
        for (int i = 0; i < metadata.samplesPerPixel(); i++) {
            int bufferIndex = sampleIndexLocation(x, y) * metadata.field(i).getBytes();
            metadata.field(i).updateSampleInByteBuffer(this.values[i], bufferIndex, i, values[i]);
        }
    }

    public Number[] getPixel(int x, int y) {
        metadata.validateCoordinates(x, y);

        // Pixel with each sample value
        Number[] pixel = new Number[metadata.samplesPerPixel()];

        // Get the pixel values from each sample
        int sampleIndex = sampleIndexLocation(x, y);
        for (int i = 0; i < metadata.samplesPerPixel(); i++) {
            int bufferIndex = sampleIndex * metadata.field(i).getBytes();
            pixel[i] = metadata.field(i).getSampleFromByteBuffer(values[i], bufferIndex, i);
        }

        return pixel;
    }

    public void addValue(int sampleIndex, int coordinate, Number value) {
        int bufferIndex = coordinate * metadata.field(sampleIndex).getBytes();
        metadata.field(sampleIndex).updateSampleInByteBuffer(values[sampleIndex], bufferIndex, sampleIndex, value);
    }

}
