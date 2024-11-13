package mil.nga.tiff.internal;

import mil.nga.tiff.field.type.enumeration.DifferencingPredictor;
import mil.nga.tiff.field.type.enumeration.PlanarConfiguration;
import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.util.List;

public record DirectoryStats(
    Integer imageWidth,
    Integer imageHeight,
    Integer tileWidth,
    Integer tileHeight,
    int samplesPerPixel,
    List<Integer> bitsPerSample,
    List<SampleFormat> sampleFormatList,
    PlanarConfiguration planarConfiguration,
    List<Long> tileOffsets,
    List<Integer> tileByteCounts,
    List<Long> stripOffsets,
    List<Integer> stripByteCounts,
    Integer compression,
    DifferencingPredictor predictor
) {

    public DirectoryStats(FileDirectory directory) {
        this(
            directory.getImageWidth() != null ? directory.getImageWidth().intValue() : null,
            directory.getImageHeight() != null ? directory.getImageHeight().intValue() : null,
            directory.getTileWidth() != null ? directory.getTileWidth().intValue() : null,
            directory.getTileHeight() != null ? directory.getTileHeight().intValue() : null,
            directory.getSamplesPerPixel(),
            directory.getBitsPerSample(),
            directory.getSampleFormat(),
            directory.getPlanarConfiguration(),
            directory.getTileOffsets(),
            directory.getTileByteCounts() != null ? directory.getTileByteCounts().stream().map(Number::intValue).toList() : null,
            directory.getStripOffsets() != null ? directory.getStripOffsets().stream().map(Number::longValue).toList() : null,
            directory.getStripByteCounts() != null ? directory.getStripByteCounts().stream().map(Number::intValue).toList() : null,
            directory.getCompression(),
            directory.getPredictor()
        );
    }

}
