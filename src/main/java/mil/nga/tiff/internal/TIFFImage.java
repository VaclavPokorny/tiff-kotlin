package mil.nga.tiff.internal;

import mil.nga.tiff.util.TiffConstants;

import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;

/**
 * TIFF Image containing the File Directories
 *
 * @param fileDirectories File directories
 * @param byteOrder       Byte order used in source image file
 * @author osbornb
 */
public record TIFFImage(List<FileDirectory> fileDirectories, ByteOrder byteOrder) {

    /**
     * Constructor, multiple file directories
     *
     * @param fileDirectories file directories
     */
    public TIFFImage(List<FileDirectory> fileDirectories, ByteOrder byteOrder) {
        this.fileDirectories = Collections.unmodifiableList(fileDirectories);
        this.byteOrder = byteOrder;
    }

    /**
     * Size in bytes of the TIFF header and file directories with their entries
     *
     * @return size in bytes
     */
    public long sizeHeaderAndDirectories() {
        return TiffConstants.HEADER_BYTES + fileDirectories.stream()
            .mapToLong(FileDirectory::size)
            .sum();
    }

    /**
     * Size in bytes of the TIFF header and file directories with their entries
     * and entry values
     *
     * @return size in bytes
     */
    public long sizeHeaderAndDirectoriesWithValues() {
        return TiffConstants.HEADER_BYTES + fileDirectories.stream()
            .mapToLong(FileDirectory::sizeWithValues)
            .sum();
    }

}

