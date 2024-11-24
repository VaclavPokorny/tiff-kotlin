package mil.nga.tiff.internal

import mil.nga.tiff.field.type.enumeration.Compression
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration
import mil.nga.tiff.io.ByteReader
import java.nio.ByteOrder

/**
 * Create tile or strip processor
 *
 * @param stats directory stats
 * @param cache true to cache tiles and strips
 */
class TileOrStripProcessor(private val stats: DirectoryStats, private val cache: TileOrStripCache) {

    /**
     * Get the tile or strip for the sample coordinate
     *
     * @param x      x coordinate
     * @param y      y coordinate
     * @param sample sample index
     * @return bytes
     */
    fun run(x: Int, y: Int, sample: Int, reader: ByteReader, tiled: Boolean, byteOrder: ByteOrder): ByteArray {
        val index = determineIndex(x, y, sample)

        return cache.getOrSet(index) {
            // Read and decode the block
            val offset: Long
            val byteCount: Int
            var tileOrStrip: ByteArray?

            if (tiled) {
                offset = stats.tileOffsets!![index]
                byteCount = stats.tileByteCounts!![index]
            } else {
                offset = stats.stripOffsets!![index]
                byteCount = stats.stripByteCounts!![index]
            }

            reader.setNextByte(offset)
            val bytes = reader.readBytes(byteCount)
            val decoder = stats.compression.decoder()
            tileOrStrip = decoder.decode(bytes, reader.byteOrder)

            tileOrStrip = stats.predictor.implementation.decode(
                tileOrStrip,
                stats.tileWidth!!,
                stats.tileHeight!!,
                stats.bitsPerSample!!,
                stats.planarConfiguration!!,
                byteOrder
            )
            tileOrStrip
        }
    }

    private fun determineIndex(x: Int, y: Int, sample: Int): Int {
        val imageWidth = stats.imageWidth!!
        val imageHeight = stats.imageHeight!!
        val tileWidth = stats.tileWidth!!
        val tileHeight = stats.tileHeight!!
        val numTilesPerRow = (imageWidth + tileWidth - 1) / tileWidth
        val numTilesPerCol = (imageHeight + tileHeight - 1) / tileHeight

        if (stats.planarConfiguration == PlanarConfiguration.CHUNKY) {
            return y * numTilesPerRow + x
        } else if (stats.planarConfiguration == PlanarConfiguration.PLANAR) {
            return sample * numTilesPerRow * numTilesPerCol + y * numTilesPerRow + x
        }

        return 0
    }
}
