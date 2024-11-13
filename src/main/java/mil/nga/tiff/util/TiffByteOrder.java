package mil.nga.tiff.util;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * TIFF Byte Order identifier
 *
 * @author osbornb
 */
public enum TiffByteOrder {
    LITTLE_ENDIAN("II", ByteOrder.LITTLE_ENDIAN),
    BIG_ENDIAN("MM", ByteOrder.BIG_ENDIAN);

    private final String id;
    private final ByteOrder byteOrder;

    TiffByteOrder(String id, ByteOrder byteOrder) {
        this.id = id;
        this.byteOrder = byteOrder;
    }

    public static TiffByteOrder findById(String id) {
        return Arrays.stream(values())
            .filter(o -> o.id.equals(id))
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public static TiffByteOrder findByByteOrder(ByteOrder byteOrder) {
        return Arrays.stream(values())
            .filter(o -> o.byteOrder == byteOrder)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid byte order: " + byteOrder));
    }

    public String getId() {
        return id;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public static ByteOrder determineFromData(byte[] bytes) {
        // Read the first 2 bytes
        if (bytes.length < 2) {
            throw new TiffException("No more remaining bytes to read. Total Bytes: " + bytes.length + ", Attempted to read: 2");
        }
        String byteOrderString = new String(bytes, 0, 2, StandardCharsets.US_ASCII);

        // Determine the byte order
        return findById(byteOrderString).getByteOrder();
    }


}
