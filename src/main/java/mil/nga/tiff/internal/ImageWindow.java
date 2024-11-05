package mil.nga.tiff.internal;

import mil.nga.tiff.util.TiffException;

/**
 * Coordinates of a window over a portion or the entire image coordinates
 *
 * @param minX Min x
 * @param minY Min y
 * @param maxX Max x
 * @param maxY Max y
 * @author osbornb
 */
public record ImageWindow(int minX, int minY, int maxX, int maxY) {

    /**
     * Constructor for a single coordinate
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public ImageWindow(int x, int y) {
        this(x, y, x + 1, y + 1);
    }

    /**
     * Constructor, full image size
     *
     * @param fileDirectory file internal
     */
    public ImageWindow(FileDirectory fileDirectory) {
        this(0, 0, fileDirectory.getImageWidth().intValue(), fileDirectory.getImageHeight().intValue());
    }

    public void validate() {
        if (minX < 0 || minY < 0 || minX > maxX || minY > maxY) {
            throw new TiffException("Invalid window range: " + this);
        }
    }

    public void validateFitsInImage(int width, int height) {
        if (maxX > width || maxY > height) {
            throw new TiffException("Window is out of the image bounds. Width: " + width + ", Height: " + height + ", Window: " + this);
        }
    }

    public int width() {
        return maxX - minX;
    }

    public int height() {
        return maxY - minY;
    }

    public int numPixels() {
        return width() * height();
    }

}
