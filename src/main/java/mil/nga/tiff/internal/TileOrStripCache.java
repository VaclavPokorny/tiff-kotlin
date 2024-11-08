package mil.nga.tiff.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides caching for tile data
 */
public class TileOrStripCache {

    /**
     * Cache
     */
    private Map<Integer, byte[]> cache = null;

    /**
     * Last block index, index of single block cache
     */
    private int lastBlockIndex = -1;

    /**
     * Last block, single block cache when caching is not enabled
     */
    private byte[] lastBlock;


    /**
     * Create tile or strip processor
     *
     * @param cacheData true to cache tiles and strips
     */
    public TileOrStripCache(boolean cacheData) {
        if (cacheData) {
            this.cache = new HashMap<>();
        } else {
            this.cache = null;
        }
    }

    public byte[] getOrSet(int index, Supplier<byte[]> supplier) {
        Optional<byte[]> current = get(index);

        if (current.isPresent()) {
            return current.get();
        }

        byte[] created = supplier.get();

        set(index, created);

        return created;
    }

    public Optional<byte[]> get(int index) {
        if (cache != null && cache.containsKey(index)) {
            return Optional.of(cache.get(index));
        } else if (lastBlockIndex == index && lastBlock != null) {
            return Optional.of(lastBlock);
        } else {
            return Optional.empty();
        }
    }

    public void set(int index, byte[] tileOrStrip) {
        if (cache != null) {
            cache.put(index, tileOrStrip);
        } else {
            lastBlockIndex = index;
            lastBlock = tileOrStrip;
        }
    }

}
