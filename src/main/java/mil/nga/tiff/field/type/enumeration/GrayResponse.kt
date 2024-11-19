package mil.nga.tiff.field.type.enumeration

enum class GrayResponse(val id: Int) {
    TENTHS(1),
    HUNDREDTHS(2),
    THOUSANDTHS(3),
    TEN_THOUSANDTHS(4),
    HUNDRED_THOUSANDTHS(5);

    companion object {
        @JvmStatic
        fun findById(id: Int): GrayResponse {
            return entries.first { it.id == id}
        }
    }
}
