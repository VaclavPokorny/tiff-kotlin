package mil.nga.tiff.field.type.enumeration

enum class ExtraSamples(val id: Int) {
    UNSPECIFIED(0),
    ASSOCIATED_ALPHA(1),
    UNASSOCIATED_ALPHA(2);

    companion object {

        @JvmStatic
        fun findById(id: Int): ExtraSamples {
            return entries.first { it.id == id}
        }
    }
}
