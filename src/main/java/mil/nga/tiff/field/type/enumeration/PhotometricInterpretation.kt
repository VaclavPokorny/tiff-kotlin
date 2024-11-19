package mil.nga.tiff.field.type.enumeration

enum class PhotometricInterpretation(val id: Int) {
    WHITE_IS_ZERO(0),
    BLACK_IS_ZERO(1),
    RGB(2),
    PALETTE(3),
    TRANSPARENCY(4);

    companion object {
        @JvmStatic
        fun findById(id: Int): PhotometricInterpretation {
            return entries.first { it.id == id}
        }
    }
}
