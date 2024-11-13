package mil.nga.tiff.internal;

import mil.nga.tiff.compression.CompressionEncoder;
import mil.nga.tiff.field.type.enumeration.Compression;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.internal.rasters.Rasters;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffByteOrder;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TiffImageWriter {

    private final ByteWriter writer;

    public TiffImageWriter(ByteWriter writer) {
        this.writer = writer;
    }

    /**
     * Write a TIFF to a byte writer
     *
     * @param tiffImage TIFF image
     * @throws IOException upon failure to write
     */
    public void write(TIFFImage tiffImage) throws IOException {

        // Write the byte order (bytes 0-1)
        String byteOrderId = TiffByteOrder.findByByteOrder(writer.getByteOrder()).getId();
        writer.writeString(byteOrderId);

        // Write the TIFF file identifier (bytes 2-3)
        writer.writeUnsignedShort(TiffConstants.FILE_IDENTIFIER);

        // Write the first IFD offset (bytes 4-7), set to start right away at
        // byte 8
        writer.writeUnsignedInt(TiffConstants.HEADER_BYTES);

        // Write each file internal
        for (int i = 0; i < tiffImage.fileDirectories().size(); i++) {
            boolean isLast = (i + 1) == tiffImage.fileDirectories().size();
            FileDirectory fileDirectory = tiffImage.fileDirectories().get(i);
            write(fileDirectory, isLast);
        }
    }

    private void write(FileDirectory fileDirectory, boolean isLast) throws IOException {
        // Populate strip entries with placeholder values so the sizes come
        // out correctly
        populateRasterEntries(fileDirectory);

        // Track of the starting byte of this internal
        int startOfDirectory = writer.size();
        long afterDirectory = startOfDirectory + fileDirectory.size();
        long afterValues = startOfDirectory + fileDirectory.sizeWithValues();

        // Write the number of internal entries
        writer.writeUnsignedShort(fileDirectory.numEntries());

        List<FileDirectoryEntry> entryValues = new ArrayList<>();

        // Byte to write the next values
        long nextByte = afterDirectory;

        List<Long> valueBytesCheck = new ArrayList<>();

        // Write the raster bytes to temporary storage
        if (fileDirectory.isTiled()) {
            throw new TiffException("Tiled images are not supported");
        }

        // Create the raster bytes, written to the stream later
        byte[] rastersBytes = writeRasters(writer.getByteOrder(), fileDirectory, afterValues);

        // Write each entry
        for (FileDirectoryEntry entry : fileDirectory.getEntries()) {
            writer.writeUnsignedShort(entry.fieldTag().getId());
            writer.writeUnsignedShort(entry.fieldType().metadata().id());
            writer.writeUnsignedInt(entry.typeCount());
            long valueBytes = entry.valueBytes();
            if (valueBytes > 4) {
                // Write the value offset
                entryValues.add(entry);
                writer.writeUnsignedInt(nextByte);
                valueBytesCheck.add(nextByte);
                nextByte += entry.sizeOfValues();
            } else {
                // Write the value in the inline 4 byte space, left aligned
                int bytesWritten = writeValues(writer, entry);
                if (bytesWritten != valueBytes) {
                    throw new TiffException("Unexpected bytes written. Expected: " + valueBytes + ", Actual: " + bytesWritten);
                }
                writer.writeFillerBytes(4 - valueBytes);
            }
        }

        if (isLast) {
            // Write 0's since there are not more file directories
            writer.writeFillerBytes(4);
        } else {
            // Write the start address of the next file internal
            long nextFileDirectory = afterValues + rastersBytes.length;
            writer.writeUnsignedInt(nextFileDirectory);
        }

        // Write the external entry values
        for (int entryIndex = 0; entryIndex < entryValues.size(); entryIndex++) {
            FileDirectoryEntry entry = entryValues.get(entryIndex);
            long entryValuesByte = valueBytesCheck.get(entryIndex);
            if (entryValuesByte != writer.size()) {
                throw new TiffException("Entry values byte does not match the write location. Entry Values Byte: " + entryValuesByte + ", Current Byte: " + writer.size());
            }
            int bytesWritten = writeValues(writer, entry);
            long valueBytes = entry.valueBytes();
            if (bytesWritten != valueBytes) {
                throw new TiffException("Unexpected bytes written. Expected: " + valueBytes + ", Actual: " + bytesWritten);
            }
        }

        // Write the image bytes
        writer.writeBytes(rastersBytes);
    }

    /**
     * Populate the raster entry values with placeholder values for correct size
     * calculations
     *
     * @param fileDirectory file internal
     */
    private void populateRasterEntries(FileDirectory fileDirectory) {

        Rasters rasters = fileDirectory.getWriteRasters();
        if (rasters == null) {
            throw new TiffException("File Directory Writer Rasters is required to create a TIFF");
        }

        // Populate the raster entries
        if (!fileDirectory.isTiled()) {
            populateStripEntries(fileDirectory);
        } else {
            throw new TiffException("Tiled images are not supported");
        }

    }

    /**
     * Populate the strip entries with placeholder values
     *
     * @param fileDirectory file internal
     */
    private void populateStripEntries(FileDirectory fileDirectory) {

        int rowsPerStrip = fileDirectory.getRowsPerStrip().intValue();
        int imageHeight = fileDirectory.getImageHeight().intValue();
        int strips = (imageHeight + rowsPerStrip - 1) / rowsPerStrip;
        if (fileDirectory.getPlanarConfiguration() == PlanarConfiguration.PLANAR) {
            strips *= fileDirectory.getSamplesPerPixel();
        }

        fileDirectory.setStripOffsetsAsLongs(new ArrayList<>(Collections.nCopies(strips, 0L)));
        fileDirectory.setStripByteCounts(new ArrayList<>(Collections.nCopies(strips, 0)));
    }

    /**
     * Write the rasters as bytes
     *
     * @param byteOrder     byte order
     * @param fileDirectory file internal
     * @param offset        byte offset
     * @return rasters bytes
     * @throws IOException IO exception
     */
    private byte[] writeRasters(ByteOrder byteOrder, FileDirectory fileDirectory, long offset) throws IOException {

        Rasters rasters = fileDirectory.getWriteRasters();
        if (rasters == null) {
            throw new TiffException("File Directory Writer Rasters is required to create a TIFF");
        }

        // Get the compression encoder
        CompressionEncoder encoder = Compression.getEncoder(fileDirectory.getCompression());

        // Byte writer to write the raster
        ByteWriter writer = new ByteWriter(byteOrder);

        // Write the rasters
        if (!fileDirectory.isTiled()) {
            writeStripRasters(writer, fileDirectory, offset, encoder);
        } else {
            throw new TiffException("Tiled images are not supported");
        }

        // Return the rasters bytes
        byte[] bytes = writer.getBytes();
        writer.close();

        return bytes;
    }

    /**
     * Write the rasters as bytes
     *
     * @param writer        byte writer
     * @param fileDirectory file internal
     * @param offset        byte offset
     * @param encoder       compression encoder
     * @throws IOException IO exception
     */
    private void writeStripRasters(ByteWriter writer, FileDirectory fileDirectory, long offset, CompressionEncoder encoder) throws IOException {

        Rasters rasters = fileDirectory.getWriteRasters();

        // Get the row and strip counts
        int rowsPerStrip = fileDirectory.getRowsPerStrip().intValue();
        int maxY = fileDirectory.getImageHeight().intValue();
        int stripsPerSample = (maxY + rowsPerStrip - 1) / rowsPerStrip;
        int strips = stripsPerSample;
        if (fileDirectory.getPlanarConfiguration() == PlanarConfiguration.PLANAR) {
            strips *= fileDirectory.getSamplesPerPixel();
        }

        // Build the strip offsets and byte counts
        List<Long> stripOffsets = new ArrayList<>();
        List<Integer> stripByteCounts = new ArrayList<>();

        // Write each strip
        for (int strip = 0; strip < strips; strip++) {

            int startingY;
            Integer sample = null;
            if (fileDirectory.getPlanarConfiguration() == PlanarConfiguration.PLANAR) {
                sample = strip / stripsPerSample;
                startingY = (strip % stripsPerSample) * rowsPerStrip;
            } else {
                startingY = strip * rowsPerStrip;
            }

            // Write the strip of bytes
            ByteWriter stripWriter = new ByteWriter(writer.getByteOrder());

            int endingY = Math.min(startingY + rowsPerStrip, maxY);
            for (int y = startingY; y < endingY; y++) {
                // Get the row bytes and encode if needed
                byte[] rowBytes;
                if (sample != null) {
                    rowBytes = rasters.getSampleRow(y, sample, writer.getByteOrder());
                } else {
                    rowBytes = rasters.getPixelRow(y, writer.getByteOrder());
                }

                if (encoder.rowEncoding()) {
                    rowBytes = encoder.encode(rowBytes, writer.getByteOrder());
                }

                // Write the row
                stripWriter.writeBytes(rowBytes);
            }

            // Get the strip bytes and encode if needed
            byte[] stripBytes = stripWriter.getBytes();
            stripWriter.close();
            if (!encoder.rowEncoding()) {
                stripBytes = encoder.encode(stripBytes, writer.getByteOrder());
            }

            // Write the strip bytes
            writer.writeBytes(stripBytes);

            // Add the strip byte count
            int bytesWritten = stripBytes.length;
            stripByteCounts.add(bytesWritten);

            // Add the strip offset
            stripOffsets.add(offset);
            offset += bytesWritten;

        }

        // Set the strip offsets and byte counts
        fileDirectory.setStripOffsetsAsLongs(stripOffsets);
        fileDirectory.setStripByteCounts(stripByteCounts);

    }

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @param entry  file internal entry
     * @return bytes written
     * @throws IOException IO exception
     */
    private int writeValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        return entry.fieldType().writeDirectoryEntryValues(writer, entry);
    }

}
