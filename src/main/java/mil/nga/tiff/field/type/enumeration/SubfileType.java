package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum SubfileType {
    FULL(1),
    REDUCED(2),
    SINGLE_PAGE_MULTI_PAGE(3);

    private final int id;

    SubfileType(int id) {
        this.id = id;
    }

    public static SubfileType findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
