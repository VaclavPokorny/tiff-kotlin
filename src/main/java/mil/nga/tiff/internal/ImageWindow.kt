package mil.nga.tiff.internal

import mil.nga.tiff.util.TiffException

/**
 * Coordinates of a window over a portion or the entire image coordinates
 *
 * @param minX Min x
 * @param minY Min y
 * @param maxX Max x
 * @param maxY Max y
 * @author osbornb
 */
@JvmRecord
data class ImageWindow(@JvmField val minX: Int, @JvmField val minY: Int, @JvmField val maxX: Int, @JvmField val maxY: Int) {
    fun validate() {
        if (minX < 0 || minY < 0 || minX > maxX || minY > maxY) {
            throw TiffException("Invalid window range: $this")
        }
    }

    fun validateFitsInImage(width: Int, height: Int) {
        if (maxX > width || maxY > height) {
            throw TiffException("Window is out of the image bounds. Width: $width, Height: $height, Window: $this")
        }
    }

    fun width(): Int {
        return maxX - minX
    }

    fun height(): Int {
        return maxY - minY
    }

    fun numPixels(): Int {
        return width() * height()
    }

    companion object {
        /**
         * Constructor for a single coordinate
         *
         * @param x x coordinate
         * @param y y coordinate
         */
        fun singlePixel(x: Int, y: Int): ImageWindow {
            return ImageWindow(x, y, x + 1, y + 1)
        }

        /**
         * Constructor for window starting from zero X and zero Y
         *
         * @param maxX Max x
         * @param maxY Max y
         */
        @JvmStatic
        fun fromZero(maxX: Int, maxY: Int): ImageWindow {
            return ImageWindow(0, 0, maxX, maxY)
        }
    }
}
