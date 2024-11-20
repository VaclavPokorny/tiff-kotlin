package mil.nga.tiff.field.type.enumeration

enum class SampleFormat(val id: Int) {
    UNSIGNED_INT(1),
    SIGNED_INT(2),
    FLOAT(3),
    UNDEFINED(4);

    companion object {

        @JvmField
        public val DEFAULT = UNDEFINED

        @JvmStatic
        fun findById(id: Int?): SampleFormat {
            if (id == null) {
                return DEFAULT
            }

            return entries.first { it.id == id}
        }
    }
}
