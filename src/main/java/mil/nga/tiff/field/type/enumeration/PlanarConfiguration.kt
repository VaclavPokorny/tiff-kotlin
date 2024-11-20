package mil.nga.tiff.field.type.enumeration

enum class PlanarConfiguration(val id: Int) {
    CHUNKY(1),
    PLANAR(2);

    companion object {

        @JvmField
        public val DEFAULT = CHUNKY

        @JvmStatic
        fun findById(id: Int?): PlanarConfiguration {
            if (id == null) {
                return DEFAULT
            }

            return entries.first { it.id == id }
        }
    }
}
