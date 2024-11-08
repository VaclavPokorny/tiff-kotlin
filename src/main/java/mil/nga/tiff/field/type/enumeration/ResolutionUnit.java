package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

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
