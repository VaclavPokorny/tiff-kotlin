package mil.nga.tiff

import mil.nga.tiff.internal.rasters.Rasters
import java.io.File
import java.io.IOException

/**
 * Test reading an argument provided TIFF file
 *
 * @author osbornb
 */
object TiffFileTester {

    /**
     * Main method, provide a single file path argument
     *
     * @param args arguments
     * @throws IOException upon error
     */
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size == 1) { "Provide a single TIFF file path argument" }

        val file = File(args[0])
        require(file.exists()) { "TIFF file does not exist: " + file.absolutePath }

        val tiffImage = Tiff
            .create()
            .read()
            .fromFile(file)

        println("TIFF Image: " + file.name)

        val fileDirectories = tiffImage.fileDirectories
        for (i in fileDirectories.indices) {
            val fileDirectory = fileDirectories[i]

            println()
            print("-- File Directory ")
            if (fileDirectories.size > 1) {
                print((i + 1).toString() + " ")
            }
            println("--")

            for (entry in fileDirectory.data.fieldTagTypeMapping.values) {
                println()
                println(
                    (entry.fieldTag.toString() + " (" + entry.fieldTag.id + ")")
                )
                println(
                    (entry.fieldType.toString() + " (" + entry.fieldType.metadata().bytesPerSample + " bytes)")
                )
                println("Count: " + entry.typeCount)
                println("Values: " + entry.values)
            }

            val rasters = fileDirectory.readRasters()
            println()
            println("-- Rasters --")
            println()
            println("Width: " + rasters.width)
            println("Height: " + rasters.height)
            println("Number of Pixels: " + rasters.numPixels)
            println(
                "Samples Per Pixel: " + rasters.samplesPerPixel
            )
            println("Bits Per Sample: " + (rasters.fields.map { it.bytesPerSample * 8 }))

            println()
            printPixel(rasters, 0, 0)
            printPixel(
                rasters, (rasters.width / 2.0).toInt(), (rasters.height / 2.0).toInt()
            )
            printPixel(
                rasters, rasters.width - 1, rasters.height - 1
            )

            println()
        }
    }

    /**
     * Print a pixel from the rasters
     *
     * @param rasters rasters
     * @param x x coordinate
     * @param y y coordinate
     */
    private fun printPixel(rasters: Rasters, x: Int, y: Int) {
        print("Pixel x = $x, y = $y: [")
        val pixel = rasters.getPixel(x, y)
        for (i in pixel.indices) {
            if (i > 0) {
                print(", ")
            }
            print(pixel[i])
        }
        println("]")
    }
}
