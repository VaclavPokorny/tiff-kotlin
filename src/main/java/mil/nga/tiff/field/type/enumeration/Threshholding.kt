package mil.nga.tiff.field.type.enumeration

enum class Threshholding(val id: Int) {
    NO(1),
    ORDERED(2),
    RANDOM(3);

    companion object {
        fun findById(id: Int): Threshholding {
            return entries.first { it.id == id}
        }
    }
}
