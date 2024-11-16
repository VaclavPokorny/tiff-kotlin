package mil.nga.tiff.internal;

import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.util.List;

/**
 * TIFF File Directory Entry
 *
 * @param fieldTag  Field Tag Type
 * @param fieldType Field Type
 * @param typeCount Type Count
 * @param values    Values
 * @author osbornb
 */
public record FileDirectoryEntry<T>(FieldTagType fieldTag, GenericFieldType<T> fieldType, long typeCount, List<T> values) implements Comparable<FileDirectoryEntry<T>> {

    /**
     * Size in bytes of the image file internal entry and its values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    public long sizeWithValues() {
        return TiffConstants.IFD_ENTRY_BYTES + sizeOfValues();
    }

    /**
     * Size of the values not included in the internal entry bytes
     *
     * @return size in bytes
     */
    public long sizeOfValues() {
        long size = 0;
        long valueBytes = valueBytes();
        if (valueBytes > 4) {
            size = valueBytes;
        }
        return size;
    }

    @Override
    public int compareTo(FileDirectoryEntry other) {
        return fieldTag.getId() - other.fieldTag().getId();
    }

    public long valueBytes() {
        return fieldType.metadata().bytesPerSample() * typeCount;
    }

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @return bytes written
     * @throws IOException IO exception
     */
    public int write(ByteWriter writer) throws IOException {
        return fieldType.writeDirectoryEntryValues(writer, fieldTag, typeCount, values);
    }

}
