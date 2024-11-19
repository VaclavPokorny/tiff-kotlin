package mil.nga.tiff.field.type.enumeration

enum class SampleFormat(val id: Int) {
    UNSIGNED_INT(1),
    SIGNED_INT(2),
    FLOAT(3),
    UNDEFINED(4);

    companion object {
        @JvmStatic
        fun findById(id: Int): SampleFormat {
            return entries.first { it.id == id}
        }
    }
}
