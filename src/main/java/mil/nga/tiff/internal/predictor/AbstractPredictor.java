package mil.nga.tiff.internal.predictor;

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffException;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

abstract class AbstractPredictor implements Predictor {

    @Override
    public byte[] decode(byte[] bytes, int width, int height, List<Integer> bitsPerSample, PlanarConfiguration planarConfiguration, ByteOrder byteOrder) {
        int bytesPerSample = getBytesPerSample(bitsPerSample);
        int samples = planarConfiguration == PlanarConfiguration.PLANAR ? 1 : bitsPerSample.size();

        ByteReader reader = new ByteReader(bytes, byteOrder);
        try (ByteWriter writer = new ByteWriter(byteOrder)) {

            for (int row = 0; row < height; row++) {
                // Last strip will be truncated if imageHeight % stripHeight != 0
                if (row * samples * width * bytesPerSample >= bytes.length) {
                    break;
                }

                decode(reader, writer, width, bytesPerSample, samples);
            }

            return writer.getBytes();
        } catch (IOException e) {
            throw new TiffException(e);
        }
    }

    abstract protected void decode(ByteReader reader, ByteWriter writer, int width, int bytesPerSample, int samples);

    protected int getBytesPerSample(List<Integer> bitsPerSample) {
        int numBitsPerSample = bitsPerSample.getFirst();
        if (numBitsPerSample % 8 != 0) {
            throw new TiffException("When decoding with predictor, only multiple of 8 bits are supported");
        }

        for (int i = 1; i < bitsPerSample.size(); i++) {
            if (bitsPerSample.get(i) != numBitsPerSample) {
                throw new TiffException("When decoding with predictor, all samples must have the same size");
            }
        }

        return numBitsPerSample / 8;
    }

    /**
     * Read a sample value
     *
     * @param reader         byte reader
     * @param bytesPerSample bytes per sample
     * @return sample value
     */
    protected int readValue(ByteReader reader, int bytesPerSample) {
        return switch (bytesPerSample) {
            case 1 -> reader.readByte();
            case 2 -> reader.readShort();
            case 4 -> reader.readInt();
            default -> throw new TiffException("Predictor not supported with " + bytesPerSample + " bytes per sample");
        };
    }

    /**
     * Write a sample value
     *
     * @param writer         byte writer
     * @param bytesPerSample bytes per sample
     * @param value          sample value
     */
    protected void writeValue(ByteWriter writer, int bytesPerSample, int value) {
        try {
            switch (bytesPerSample) {
                case 1:
                    writer.writeByte((byte) value);
                    break;
                case 2:
                    writer.writeShort((short) value);
                    break;
                case 4:
                    writer.writeInt(value);
                    break;
                default:
                    throw new TiffException("Predictor not supported with " + bytesPerSample + " bytes per sample");
            }

        } catch (IOException e) {
            throw new TiffException("Failed to write value: " + value + ", bytes: " + bytesPerSample, e);
        }

    }

}
