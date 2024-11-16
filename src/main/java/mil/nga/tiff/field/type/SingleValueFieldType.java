package mil.nga.tiff.field.type;

import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public sealed class SingleValueFieldType<T> implements GenericFieldType<T> permits NumericFieldType, UndefinedField, RationalField {


    @Override
    final public List<T> readDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<T> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(readValue(reader));
        }

        return values;
    }

    /**
     * Read the value from the reader according to the field type
     *
     * @param reader byte reader
     * @return value
     */
    abstract public T readValue(ByteReader reader);

    @Override
    final public int writeDirectoryEntryValues(ByteWriter writer, FieldTagType fieldTag, long typeCount, List<T> values) throws IOException {
        int bytesWritten = 0;

        for (T value : values) {
            bytesWritten += writeValue(writer, value);
        }

        return bytesWritten;
    }

    /**
     * Write value
     *
     * @param writer byte writer
     * @param value  value
     */
    abstract protected int writeValue(ByteWriter writer, T value) throws IOException;


}
