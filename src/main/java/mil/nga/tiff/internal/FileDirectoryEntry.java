package mil.nga.tiff.internal;

import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.util.TiffConstants;

/**
 * TIFF File Directory Entry
 *
 * @param fieldTag  Field Tag Type
 * @param fieldType Field Type
 * @param typeCount Type Count
 * @param values    Values
 * @author osbornb
 */
public record FileDirectoryEntry(FieldTagType fieldTag, GenericFieldType fieldType, long typeCount, Object values) implements Comparable<FileDirectoryEntry> {

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

}
