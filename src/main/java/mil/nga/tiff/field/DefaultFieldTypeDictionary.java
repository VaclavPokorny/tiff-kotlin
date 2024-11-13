package mil.nga.tiff.field;

import mil.nga.tiff.field.type.ASCIIField;
import mil.nga.tiff.field.type.DoubleField;
import mil.nga.tiff.field.type.FloatField;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.NumericFieldType;
import mil.nga.tiff.field.type.SignedByteField;
import mil.nga.tiff.field.type.SignedLongField;
import mil.nga.tiff.field.type.SignedRationalField;
import mil.nga.tiff.field.type.SignedShortField;
import mil.nga.tiff.field.type.UndefinedField;
import mil.nga.tiff.field.type.UnsignedByteField;
import mil.nga.tiff.field.type.UnsignedLongField;
import mil.nga.tiff.field.type.UnsignedRationalField;
import mil.nga.tiff.field.type.UnsignedShortField;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.util.TiffException;

import java.util.List;
import java.util.stream.Stream;

public class DefaultFieldTypeDictionary implements FieldTypeDictionary {

    private static final List<NumericFieldType> NUMERIC_TYPES = List.of(
        new UnsignedByteField(),
        new UnsignedShortField(),
        new UnsignedLongField(),
        new SignedByteField(),
        new SignedShortField(),
        new SignedLongField(),
        new FloatField(),
        new DoubleField()
    );

    private static final List<GenericFieldType> ALL_TYPES = Stream.concat(
        Stream.of(
            new UndefinedField(),
            new ASCIIField(),
            new UnsignedRationalField(),
            new SignedRationalField()
        ),
        NUMERIC_TYPES.stream()
    ).toList();

    @Override
    public GenericFieldType findById(int id) {
        return ALL_TYPES.stream()
            .filter(o -> o.metadata().id() == id)
            .findAny()
            .orElseThrow(() -> new TiffException("Unsupported field type ID: " + id));
    }

    @Override
    public NumericFieldType findBySampleParams(SampleFormat sampleFormat, int bitsPerSample) {
        return NUMERIC_TYPES.stream()
            .filter(o -> o.metadata().sampleFormat() == sampleFormat && o.metadata().bytesPerSample() * 8 == bitsPerSample)
            .findAny()
            .orElseThrow(() -> new TiffException("Unsupported field type for sample format: " + sampleFormat + ", bits per sample: " + bitsPerSample));
    }

}
