package mil.nga.tiff.field.type.enumeration

enum class ResolutionUnit(val id: Int) {
    NO(1),
    INCH(2),
    CENTIMETER(3);

    companion object {
        @JvmStatic
        fun findById(id: Int): ResolutionUnit {
            return entries.first { it.id == id}
        }
    }
}
