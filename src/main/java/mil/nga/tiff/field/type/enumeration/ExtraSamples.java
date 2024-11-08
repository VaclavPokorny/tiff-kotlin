package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum ExtraSamples {
    UNSPECIFIED(0),
    ASSOCIATED_ALPHA(1),
    UNASSOCIATED_ALPHA(2);

    private final int id;

    ExtraSamples(int id) {
        this.id = id;
    }

    public static ExtraSamples findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
