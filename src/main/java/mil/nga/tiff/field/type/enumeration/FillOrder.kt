package mil.nga.tiff.field.type.enumeration

enum class FillOrder(val id: Int) {
    LOWER_COLUMN_HIGHER_ORDER(1),
    LOWER_COLUMN_LOWER_ORDER(2);

    companion object {
        @JvmStatic
        fun findById(id: Int): FillOrder {
            return entries.first { it.id == id}
        }
    }
}
