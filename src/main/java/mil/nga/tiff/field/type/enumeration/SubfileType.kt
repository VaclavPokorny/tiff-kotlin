package mil.nga.tiff.field.type.enumeration

enum class SubfileType(val id: Int) {
    FULL(1),
    REDUCED(2),
    SINGLE_PAGE_MULTI_PAGE(3);

    companion object {
        @JvmStatic
        fun findById(id: Int): SubfileType {
            return entries.first { it.id == id}
        }
    }
}
