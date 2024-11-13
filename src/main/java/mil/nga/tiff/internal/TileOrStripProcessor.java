package mil.nga.tiff.internal;

import mil.nga.tiff.compression.CompressionDecoder;
import mil.nga.tiff.field.type.enumeration.Compression;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.io.ByteReader;

import java.nio.ByteOrder;

public class TileOrStripProcessor {

    private final DirectoryStats stats;
    private final TileOrStripCache cache;


    /**
     * Create tile or strip processor
     *
     * @param stats directory stats
     * @param cache true to cache tiles and strips
     */
    public TileOrStripProcessor(DirectoryStats stats, TileOrStripCache cache) {
        this.stats = stats;
        this.cache = cache;
    }

    /**
     * Get the tile or strip for the sample coordinate
     *
     * @param x      x coordinate
     * @param y      y coordinate
     * @param sample sample index
     * @return bytes
     */
    public byte[] run(int x, int y, int sample, ByteReader reader, boolean tiled, ByteOrder byteOrder) {
        int index = determineIndex(x, y, sample);

        return cache.getOrSet(index, () -> {
            // Read and decode the block
            long offset;
            int byteCount;
            byte[] tileOrStrip;

            if (tiled) {
                offset = stats.tileOffsets().get(index);
                byteCount = stats.tileByteCounts().get(index);
            } else {
                offset = stats.stripOffsets().get(index);
                byteCount = stats.stripByteCounts().get(index);
            }

            reader.setNextByte(offset);
            byte[] bytes = reader.readBytes(byteCount);
            CompressionDecoder decoder = Compression.getDecoder(stats.compression());
            tileOrStrip = decoder.decode(bytes, reader.getByteOrder());

            tileOrStrip = stats.predictor().getImplementation().decode(
                tileOrStrip,
                stats.tileWidth(),
                stats.tileHeight(),
                stats.bitsPerSample(),
                stats.planarConfiguration(),
                byteOrder
            );

            // Cache the data
            return tileOrStrip;
        });
    }

    private int determineIndex(int x, int y, int sample) {
        int imageWidth = stats.imageWidth();
        int imageHeight = stats.imageHeight();
        int tileWidth = stats.tileWidth();
        int tileHeight = stats.tileHeight();
        int numTilesPerRow = (imageWidth + tileWidth - 1) / tileWidth;
        int numTilesPerCol = (imageHeight + tileHeight - 1) / tileHeight;

        if (stats.planarConfiguration() == PlanarConfiguration.CHUNKY) {
            return y * numTilesPerRow + x;
        } else if (stats.planarConfiguration() == PlanarConfiguration.PLANAR) {
            return sample * numTilesPerRow * numTilesPerCol + y * numTilesPerRow + x;
        }

        return 0;
    }

}
