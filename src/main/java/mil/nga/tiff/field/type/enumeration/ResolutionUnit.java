package mil.nga.tiff.util;

import java.util.Arrays;
import java.util.Optional;

public enum ResolutionUnit {
    NO(1),
    INCH(2),
    CENTIMETER(3);

    private final int id;

    ResolutionUnit(int id) {
        this.id = id;
    }

    public static ResolutionUnit findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
