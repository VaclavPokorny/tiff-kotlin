package mil.nga.tiff.domain

@JvmRecord
data class SignedRational(@JvmField val numerator: Int, @JvmField val denominator: Int) {
    override fun toString(): String {
        return "$numerator/$denominator"
    }
}
