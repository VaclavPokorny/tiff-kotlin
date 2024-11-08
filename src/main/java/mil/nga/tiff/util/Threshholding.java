package mil.nga.tiff.util;

import java.util.Arrays;
import java.util.Optional;

public enum Threshholding {
    NO(1),
    ORDERED(2),
    RANDOM(3);

    private final int id;

    Threshholding(int id) {
        this.id = id;
    }

    public static Threshholding findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
