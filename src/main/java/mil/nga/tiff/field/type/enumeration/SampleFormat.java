package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum SampleFormat {
    UNSIGNED_INT(1),
    SIGNED_INT(2),
    FLOAT(3),
    UNDEFINED(4);

    private final int id;

    SampleFormat(int id) {
        this.id = id;
    }

    public static SampleFormat findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
