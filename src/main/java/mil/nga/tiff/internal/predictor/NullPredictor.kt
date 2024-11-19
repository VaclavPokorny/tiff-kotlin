package mil.nga.tiff.internal.predictor

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import java.nio.ByteOrder

/**
 * Null differencing predictor decoder
 */
class NullPredictor : Predictor {
    override fun decode(
        bytes: ByteArray,
        width: Int,
        height: Int,
        bitsPerSample: List<Int>,
        planarConfiguration: PlanarConfiguration,
        byteOrder: ByteOrder
    ): ByteArray {
        return bytes
    }
}
