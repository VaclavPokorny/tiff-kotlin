package mil.nga.tiff.field.type.enumeration;

import mil.nga.tiff.internal.predictor.FloatingPointPredictor;
import mil.nga.tiff.internal.predictor.HorizontalPredictor;
import mil.nga.tiff.internal.predictor.NullPredictor;
import mil.nga.tiff.internal.predictor.Predictor;
import mil.nga.tiff.util.TiffException;

import java.util.Arrays;

public enum DifferencingPredictor {
    NO(1, new NullPredictor()),
    HORIZONTAL(2, new HorizontalPredictor()),
    FLOATINGPOINT(3, new FloatingPointPredictor());

    private static final DifferencingPredictor DEFAULT = NO;

    private final int id;
    private final Predictor implementation;

    DifferencingPredictor(int id, Predictor implementation) {
        this.id = id;
        this.implementation = implementation;
    }

    public static DifferencingPredictor findById(Integer id) {
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

    public Predictor getImplementation() {
        return implementation;
    }

}
