package mil.nga.tiff.util;

import java.util.Arrays;
import java.util.Optional;

public enum FillOrder {
    LOWER_COLUMN_HIGHER_ORDER(1),
    LOWER_COLUMN_LOWER_ORDER(2);

    private final int id;

    FillOrder(int id) {
        this.id = id;
    }

    public static FillOrder findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
