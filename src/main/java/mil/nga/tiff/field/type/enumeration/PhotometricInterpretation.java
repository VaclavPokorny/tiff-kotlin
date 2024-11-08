package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum PhotometricInterpretation {
    WHITE_IS_ZERO(0),
    BLACK_IS_ZERO(1),
    RGB(2),
    PALETTE(3),
    TRANSPARENCY(4);

    private final int id;

    PhotometricInterpretation(int id) {
        this.id = id;
    }

    public static PhotometricInterpretation findById(int id) {
        return Arrays.stream(values())
            .filter(o -> o.id == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Invalid ID: " + id));
    }

    public int getId() {
        return id;
    }
}
