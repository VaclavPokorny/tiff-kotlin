package mil.nga.tiff.internal;

import mil.nga.tiff.compression.Predictor;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

import java.util.HashMap;
import java.util.Map;

public class TileOrStripProcessor {

    private final FileDirectory directory;

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


    public TileOrStripProcessor(FileDirectory directory) {
        this.directory = directory;
    }

    /**
     * Set whether to cache tiles. Does nothing is already caching tiles, clears
     * the existing cache if set to false.
     *
     * @param cacheData true to cache tiles and strips
     */
    public void setCache(boolean cacheData) {
        if (cacheData) {
            if (cache == null) {
                cache = new HashMap<>();
            }
        } else {
            cache = null;
        }
    }

    /**
     * Get the tile or strip for the sample coordinate
     *
     * @param x      x coordinate
     * @param y      y coordinate
     * @param sample sample index
     * @return bytes
     */
    public byte[] run(int x, int y, int sample, ByteReader reader, boolean tiled, int planarConfiguration, Integer predictor) {

        byte[] tileOrStrip;

        int imageWidth = directory.getImageWidth().intValue();
        int imageHeight = directory.getImageHeight().intValue();
        int tileWidth = directory.getTileWidth().intValue();
        int tileHeight = directory.getTileHeight().intValue();
        int numTilesPerRow = (imageWidth + tileWidth - 1) / tileWidth;
        int numTilesPerCol = (imageHeight + tileHeight - 1) / tileHeight;

        int index = 0;
        if (planarConfiguration == TiffConstants.PlanarConfiguration.CHUNKY) {
            index = y * numTilesPerRow + x;
        } else if (planarConfiguration == TiffConstants.PlanarConfiguration.PLANAR) {
            index = sample * numTilesPerRow * numTilesPerCol + y * numTilesPerRow + x;
        }

        // Attempt to pull from the cache
        if (cache != null && cache.containsKey(index)) {
            tileOrStrip = cache.get(index);
        } else if (lastBlockIndex == index && lastBlock != null) {
            tileOrStrip = lastBlock;
        } else {

            // Read and decode the block

            long offset;
            int byteCount;

            if (tiled) {
                offset = directory.getTileOffsets().get(index);
                byteCount = directory.getTileByteCounts().get(index).intValue();
            } else {
                offset = directory.getStripOffsets().get(index).longValue();
                byteCount = directory.getStripByteCounts().get(index).intValue();
            }

            reader.setNextByte(offset);
            byte[] bytes = reader.readBytes(byteCount);
            tileOrStrip = directory.getDecoder().decode(bytes, reader.getByteOrder());

            if (directory.getPredictor() != null) {
                tileOrStrip = Predictor.decode(tileOrStrip, directory.getPredictor(), tileWidth, tileHeight, directory.getBitsPerSample(), planarConfiguration);
            }

            // Cache the data
            if (cache != null) {
                cache.put(index, tileOrStrip);
            } else {
                lastBlockIndex = index;
                lastBlock = tileOrStrip;
            }
        }

        return tileOrStrip;
    }

}
