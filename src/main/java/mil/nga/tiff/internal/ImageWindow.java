package mil.nga.tiff.internal;

import mil.nga.tiff.util.TiffException;
import org.jetbrains.annotations.NotNull;

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
    public static ImageWindow singlePixel(int x, int y) {
        return new ImageWindow(x, y, x + 1, y + 1);
    }

    /**
     * Constructor for window starting from zero X and zero Y
     *
     * @param maxX Max x
     * @param maxY Max y
     */
    public static ImageWindow fromZero(int maxX, int maxY) {
        return new ImageWindow(0, 0, maxX, maxY);
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
