package mil.nga.tiff;

import mil.nga.tiff.internal.TIFFImage;
import mil.nga.tiff.internal.TiffImageReader;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TIFF reader
 *
 * @author osbornb
 */
public class TiffReader {

    /**
     * Read a TIFF from a file
     *
     * @param file TIFF file
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(File file) throws IOException {
        return readTiff(file, false);
    }

    /**
     * Read a TIFF from a file
     *
     * @param file  TIFF file
     * @param cache true to cache tiles and strips
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(File file, boolean cache) throws IOException {
        byte[] bytes = IOUtils.fileBytes(file);
        return readTiff(bytes, cache);
    }

    /**
     * Read a TIFF from an input stream
     *
     * @param stream TIFF input stream
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(InputStream stream) throws IOException {
        return readTiff(stream, false);
    }

    /**
     * Read a TIFF from an input stream
     *
     * @param stream TIFF input stream
     * @param cache  true to cache tiles and strips
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(InputStream stream, boolean cache) throws IOException {
        byte[] bytes = IOUtils.streamBytes(stream);
        return readTiff(bytes, cache);
    }

    /**
     * Read a TIFF from the bytes
     *
     * @param bytes TIFF bytes
     * @return TIFF image
     */
    public static TIFFImage readTiff(byte[] bytes) {
        return readTiff(bytes, false);
    }

    /**
     * Read a TIFF from the bytes
     *
     * @param bytes TIFF bytes
     * @param cache true to cache tiles and strips
     * @return TIFF image
     */
    public static TIFFImage readTiff(byte[] bytes, boolean cache) {
        ByteReader reader = new ByteReader(bytes);
        return readTiff(reader, cache);
    }

    /**
     * Read a TIFF from the byte reader
     *
     * @param reader byte reader
     * @return TIFF image
     */
    public static TIFFImage readTiff(ByteReader reader) {
        return readTiff(reader, false);
    }

    /**
     * Read a TIFF from the byte reader
     *
     * @param reader byte reader
     * @param cache  true to cache tiles and strips
     * @return TIFF image
     */
    public static TIFFImage readTiff(ByteReader reader, boolean cache) {
        return new TiffImageReader(reader).readTiff(cache);
    }

}
