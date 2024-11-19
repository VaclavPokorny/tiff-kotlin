package mil.nga.tiff.internal

import java.util.function.Supplier

/**
 * Provides caching for tile data
 *
 * @param cacheData true to cache tiles and strips
 */
class TileOrStripCache(cacheData: Boolean) {
    /**
     * Cache
     */
    private var cache: MutableMap<Int, ByteArray>? = null

    /**
     * Last block index, index of single block cache
     */
    private var lastBlockIndex = -1

    /**
     * Last block, single block cache when caching is not enabled
     */
    private var lastBlock: ByteArray? = null

    init {
        if (cacheData) {
            this.cache = HashMap()
        } else {
            this.cache = null
        }
    }

    fun getOrSet(index: Int, supplier: Supplier<ByteArray>): ByteArray {
        val current = get(index)

        if (current != null) {
            return current
        }

        val created = supplier.get()

        set(index, created)

        return created
    }

    private fun get(index: Int): ByteArray? {
        return if (cache != null && cache!!.containsKey(index)) {
            cache!![index]
        } else if (lastBlockIndex == index && lastBlock != null) {
            lastBlock
        } else {
            null
        }
    }

    fun set(index: Int, tileOrStrip: ByteArray) {
        if (cache != null) {
            cache!![index] = tileOrStrip
        } else {
            lastBlockIndex = index
            lastBlock = tileOrStrip
        }
    }
}
