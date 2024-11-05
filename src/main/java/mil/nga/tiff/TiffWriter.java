package mil.nga.tiff;

import mil.nga.tiff.internal.FileDirectory;
import mil.nga.tiff.internal.Rasters;
import mil.nga.tiff.internal.TIFFImage;
import mil.nga.tiff.internal.TiffImageWriter;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
 * The {@link Rasters#calculateRowsPerStrip(int)} and
 * {@link Rasters#calculateRowsPerStrip(int, int)} methods provide a mechanism
 * for determining a {@link FileDirectory#getRowsPerStrip()} setting.
 *
 * @author osbornb
 */
public class TiffWriter {

    /**
     * Write a TIFF to a file
     *
     * @param file      file to create
     * @param tiffImage TIFF image
     * @throws IOException upon failure to write
     */
    public static void writeTiff(File file, TIFFImage tiffImage) throws IOException {
        ByteWriter writer = new ByteWriter();
        writeTiff(file, writer, tiffImage);
        writer.close();
    }

    /**
     * Write a TIFF to a file
     *
     * @param file      file to create
     * @param writer    byte writer
     * @param tiffImage TIFF Image
     * @throws IOException upon failure to write
     */
    public static void writeTiff(File file, ByteWriter writer, TIFFImage tiffImage) throws IOException {
        byte[] bytes = writeTiffToBytes(writer, tiffImage);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        IOUtils.copyStream(inputStream, file);
    }

    /**
     * Write a TIFF to bytes
     *
     * @param tiffImage TIFF image
     * @return tiff bytes
     * @throws IOException upon failure to write
     */
    public static byte[] writeTiffToBytes(TIFFImage tiffImage) throws IOException {
        ByteWriter writer = new ByteWriter();
        byte[] bytes = writeTiffToBytes(writer, tiffImage);
        writer.close();
        return bytes;
    }

    /**
     * Write a TIFF to bytes
     *
     * @param writer    byte writer
     * @param tiffImage TIFF image
     * @return tiff bytes
     * @throws IOException upon failure to write
     */
    public static byte[] writeTiffToBytes(ByteWriter writer, TIFFImage tiffImage) throws IOException {
        writeTiff(writer, tiffImage);
        return writer.getBytes();
    }

    /**
     * Write a TIFF to a byte writer
     *
     * @param writer    byte writer
     * @param tiffImage TIFF image
     * @throws IOException upon failure to write
     */
    public static void writeTiff(ByteWriter writer, TIFFImage tiffImage) throws IOException {
        new TiffImageWriter(writer).write(tiffImage);
    }

}
