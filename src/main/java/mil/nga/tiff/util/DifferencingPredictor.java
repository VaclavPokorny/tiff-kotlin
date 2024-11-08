package mil.nga.tiff.util;

import java.util.Arrays;
import java.util.Optional;

public enum DifferencingPredictor {
    NO(1),
    HORIZONTAL(2),
    FLOATINGPOINT(3);

    private final int id;

    DifferencingPredictor(int id) {
        this.id = id;
    }

    public static DifferencingPredictor findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
