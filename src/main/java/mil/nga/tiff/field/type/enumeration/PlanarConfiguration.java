package mil.nga.tiff.util;

import java.util.Arrays;

public enum PlanarConfiguration {
    CHUNKY(1),
    PLANAR(2);

    private static final PlanarConfiguration DEFAULT = CHUNKY;

    private final int id;

    PlanarConfiguration(int id) {
        this.id = id;
    }

    public static PlanarConfiguration findById(Integer id) {
        if (id == null) {
            return DEFAULT;
        }

        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
