package mil.nga.tiff;

import mil.nga.tiff.field.DefaultFieldTypeDictionary;
import mil.nga.tiff.field.FieldTypeDictionary;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.internal.FileDirectory;
import mil.nga.tiff.internal.TIFFImage;
import mil.nga.tiff.internal.TiffImageReader;
import mil.nga.tiff.internal.TiffImageWriter;
import mil.nga.tiff.internal.rasters.Rasters;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.io.IOUtils;
import mil.nga.tiff.util.TiffByteOrder;
import mil.nga.tiff.util.TiffException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.List;

/**
 * TIFF Fluent API
 */
public class Tiff {

    public static Tiff create() {
        return new Tiff();
    }

    /**
     * Create a TIFF image read query
     */
    public Reader read() {
        return new Reader(true, new DefaultFieldTypeDictionary());
    }

    /**
     * Create a TIFF image read query
     *
     * @param useCache       true to use caching for reading operations
     * @param typeDictionary field type dictionary
     */
    public Reader read(boolean useCache, FieldTypeDictionary typeDictionary) {
        return new Reader(useCache, typeDictionary);
    }

    /**
     * Create a TIFF image write query
     *
     * @param image TIFF image
     */
    public Writer write(TIFFImage image) {
        return new Writer(image);
    }

    /**
     * TIFF reader
     *
     * @author osbornb
     */
    public static class Reader {

        private final boolean useCache;
        private final FieldTypeDictionary typeDictionary;

        public Reader(boolean useCache, FieldTypeDictionary typeDictionary) {
            this.useCache = useCache;
            this.typeDictionary = typeDictionary;
        }

        /**
         * Read a TIFF from a file
         *
         * @param file TIFF file
         * @return TIFF image
         * @throws IOException upon failure to read
         */
        public TIFFImage fromFile(File file) throws IOException {
            byte[] bytes = IOUtils.fileBytes(file);
            return fromByteArray(bytes);
        }

        /**
         * Read a TIFF from an input stream
         *
         * @param stream TIFF input stream
         * @return TIFF image
         * @throws IOException upon failure to read
         */
        public TIFFImage fromInputStream(InputStream stream) throws IOException {
            byte[] bytes = IOUtils.streamBytes(stream);
            return fromByteArray(bytes);
        }

        /**
         * Read a TIFF from the bytes
         *
         * @param bytes TIFF bytes
         * @return TIFF image
         */
        public TIFFImage fromByteArray(byte[] bytes) {
            ByteOrder byteOrder = TiffByteOrder.determineFromData(bytes);
            ByteReader reader = new ByteReader(bytes, byteOrder);
            TiffImageReader imageReader = new TiffImageReader(reader, typeDictionary);
            return imageReader.readTiff(useCache);
        }
    }

    /**
     * TIFF Writer.
     * <p>
     * For a striped TIFF, the {@link FileDirectory#setStripOffsets(List)} and
     * {@link FileDirectory#setStripByteCounts(List)} methods are automatically set
     * or adjusted based upon attributes including:
     * {@link FileDirectory#getRowsPerStrip()},
     * {@link FileDirectory#getImageHeight()},
     * {@link FileDirectory#getPlanarConfiguration()}, and
     * {@link FileDirectory#getSamplesPerPixel()}.
     * <p>
     * The {@link Rasters#calculateRowsPerStrip(PlanarConfiguration)} and
     * {@link Rasters#calculateRowsPerStrip(PlanarConfiguration, int)} methods provide a mechanism
     * for determining a {@link FileDirectory#getRowsPerStrip()} setting.
     */
    public static class Writer {

        private final TIFFImage image;

        private Writer(TIFFImage image) {
            this.image = image;
        }

        /**
         * Write a TIFF to a file
         *
         * @param file file to create
         * @throws TiffException upon failure to write
         */
        public void toFile(File file) {
            try (ByteWriter writer = new ByteWriter(image.byteOrder())) {
                TiffImageWriter imageWriter = new TiffImageWriter(writer);
                imageWriter.write(image);
                byte[] bytes = writer.getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                IOUtils.copyStream(inputStream, file);
            } catch (IOException e) {
                throw new TiffException(e);
            }
        }

        /**
         * Write a TIFF to bytes
         *
         * @return tiff bytes
         * @throws TiffException upon failure to write
         */
        public byte[] toByteArray() {
            try (ByteWriter writer = new ByteWriter(image.byteOrder())) {
                TiffImageWriter imageWriter = new TiffImageWriter(writer);
                imageWriter.write(image);
                return writer.getBytes();
            } catch (IOException e) {
                throw new TiffException(e);
            }
        }

    }

}
