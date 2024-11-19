package mil.nga.tiff.field.tag

interface FieldTagType {
    val isArray: Boolean
    val id: Int

    companion object {
        private val ALL: List<FieldTagType> = listOf(
                ExifTag.entries,
                GdalTag.entries,
                GeoTiffTag.entries,
                JpegTag.entries,
                MetaTag.entries,
                TiffBasicTag.entries,
                TiffExtendedTag.entries
            ).flatten();

        /**
         * Get a field tag type by id
         *
         * @param id tag id
         * @return field tag type
         */
        fun getById(id: Int): FieldTagType? {
            return ALL.firstOrNull { it.id == id }
        }
    }
}
