package mil.nga.tiff.util;

import java.util.Arrays;
import java.util.Optional;

public enum Orientation {
    TOP_ROW_LEFT_COLUMN(1),
    TOP_ROW_RIGHT_COLUMN(2),
    BOTTOM_ROW_RIGHT_COLUMN(3),
    BOTTOM_ROW_LEFT_COLUMN(4),
    LEFT_ROW_TOP_COLUMN(5),
    RIGHT_ROW_TOP_COLUMN(6),
    RIGHT_ROW_BOTTOM_COLUMN(7),
    LEFT_ROW_BOTTOM_COLUMN(8);

    private final int id;

    Orientation(int id) {
        this.id = id;
    }

    public static Orientation findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
