package mil.nga.tiff.field.type.enumeration

enum class Orientation(val id: Int) {
    TOP_ROW_LEFT_COLUMN(1),
    TOP_ROW_RIGHT_COLUMN(2),
    BOTTOM_ROW_RIGHT_COLUMN(3),
    BOTTOM_ROW_LEFT_COLUMN(4),
    LEFT_ROW_TOP_COLUMN(5),
    RIGHT_ROW_TOP_COLUMN(6),
    RIGHT_ROW_BOTTOM_COLUMN(7),
    LEFT_ROW_BOTTOM_COLUMN(8);

    companion object {
        @JvmStatic
        fun findById(id: Int): Orientation {
            return entries.first { it.id == id}
        }
    }
}
