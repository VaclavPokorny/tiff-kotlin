package mil.nga.tiff.domain

@JvmRecord
data class UnsignedRational(@JvmField val numerator: Long, @JvmField val denominator: Long) {
    override fun toString(): String {
        return "$numerator/$denominator"
    }
}
