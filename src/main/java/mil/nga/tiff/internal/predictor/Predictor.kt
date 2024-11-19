package mil.nga.tiff.internal.predictor

import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import java.nio.ByteOrder

/**
 * Differencing Predictor decoder
 */
interface Predictor {
    /**
     * Decode the predictor encoded bytes
     *
     * @param bytes               bytes to decode
     * @param width               tile imageWidth
     * @param height              tile imageHeight
     * @param bitsPerSample       bits per samples
     * @param planarConfiguration planar configuration
     * @param byteOrder           byte order
     * @return decoded or original bytes
     */
    fun decode(
        bytes: ByteArray,
        width: Int,
        height: Int,
        bitsPerSample: List<Int>,
        planarConfiguration: PlanarConfiguration,
        byteOrder: ByteOrder
    ): ByteArray
}
