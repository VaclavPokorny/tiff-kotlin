package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum GrayResponse {
    TENTHS(1),
    HUNDREDTHS(2),
    THOUSANDTHS(3),
    TEN_THOUSANDTHS(4),
    HUNDRED_THOUSANDTHS(5);

    private final int id;

    GrayResponse(int id) {
        this.id = id;
    }

    public static GrayResponse findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
