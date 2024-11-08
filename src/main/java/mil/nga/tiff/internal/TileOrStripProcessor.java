package mil.nga.tiff.internal;

import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.io.ByteReader;

public class TileOrStripProcessor {

    private final FileDirectory directory;
    private final TileOrStripCache cache;


    /**
     * Create tile or strip processor
     *
     * @param directory IFD
     * @param cache true to cache tiles and strips
     */
    public TileOrStripProcessor(FileDirectory directory, TileOrStripCache cache) {
        this.directory = directory;
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
    public byte[] run(int x, int y, int sample, ByteReader reader, boolean tiled, PlanarConfiguration planarConfiguration, DifferencingPredictor predictor) {
        int index = determineIndex(x, y, sample, planarConfiguration);

        return cache.getOrSet(index, () -> {
            // Read and decode the block
            long offset;
            int byteCount;
            byte[] tileOrStrip;

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
                tileOrStrip = predictor.getImplementation().decode(
                    tileOrStrip,
                    directory.getTileWidth().intValue(),
                    directory.getTileHeight().intValue(),
                    directory.getBitsPerSample(),
                    planarConfiguration
                );
            }

            // Cache the data
            return tileOrStrip;
        });
    }

    private int determineIndex(int x, int y, int sample, PlanarConfiguration planarConfiguration) {
        int imageWidth = directory.getImageWidth().intValue();
        int imageHeight = directory.getImageHeight().intValue();
        int tileWidth = directory.getTileWidth().intValue();
        int tileHeight = directory.getTileHeight().intValue();
        int numTilesPerRow = (imageWidth + tileWidth - 1) / tileWidth;
        int numTilesPerCol = (imageHeight + tileHeight - 1) / tileHeight;

        if (planarConfiguration == PlanarConfiguration.CHUNKY) {
            return y * numTilesPerRow + x;
        } else if (planarConfiguration == PlanarConfiguration.PLANAR) {
            return sample * numTilesPerRow * numTilesPerCol + y * numTilesPerRow + x;
        }

        return 0;
    }

}
