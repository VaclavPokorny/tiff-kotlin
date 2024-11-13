package mil.nga.tiff.field.type;

import mil.nga.tiff.domain.Rational;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
abstract sealed class RationalField<N extends Number, T extends Rational<N>> implements GenericFieldType permits UnsignedRationalField, SignedRationalField {

    @Override
    final public List<Object> readDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            T value = readValue(reader);
            values.add(value);
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    final public int writeDirectoryEntryValue(ByteWriter writer, FieldTagType fieldTag, long typeCount, Object value) throws IOException {
        T rational = (T)value;
        return writeValue(writer, (T) value);
    }

    abstract protected T readValue(ByteReader reader);

    abstract protected int writeValue(ByteWriter writer, T value) throws IOException;

}
