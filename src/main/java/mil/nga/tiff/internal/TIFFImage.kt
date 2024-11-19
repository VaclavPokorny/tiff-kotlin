package mil.nga.tiff.internal

import mil.nga.tiff.util.TiffConstants
import java.nio.ByteOrder

/**
 * TIFF Image containing the File Directories
 *
 * @param fileDirectories File directories
 * @param byteOrder       Byte order used in source image file
 */
@JvmRecord
data class TIFFImage(val fileDirectories: List<FileDirectory>, val byteOrder: ByteOrder) {
    /**
     * Size in bytes of the TIFF header and file directories with their entries
     *
     * @return size in bytes
     */
    fun sizeHeaderAndDirectories(): Long {
        return TiffConstants.HEADER_BYTES + fileDirectories.stream()
            .mapToLong { obj: FileDirectory -> obj.size() }
            .sum()
    }

    /**
     * Size in bytes of the TIFF header and file directories with their entries
     * and entry values
     *
     * @return size in bytes
     */
    fun sizeHeaderAndDirectoriesWithValues(): Long {
        return TiffConstants.HEADER_BYTES + fileDirectories.stream()
            .mapToLong { obj: FileDirectory -> obj.sizeWithValues() }
            .sum()
    }
}

