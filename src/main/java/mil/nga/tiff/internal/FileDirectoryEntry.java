package mil.nga.tiff.internal;

import mil.nga.tiff.FieldTagType;
import mil.nga.tiff.FieldType;
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
public record FileDirectoryEntry(FieldTagType fieldTag, FieldType fieldType, long typeCount, Object values) implements Comparable<FileDirectoryEntry> {

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
        long valueBytes = fieldType.getBytes() * typeCount;
        if (valueBytes > 4) {
            size = valueBytes;
        }
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(FileDirectoryEntry other) {
        return fieldTag.getId() - other.fieldTag().getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return fieldTag.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileDirectoryEntry other = (FileDirectoryEntry) obj;
        return fieldTag == other.fieldTag;
    }

}
