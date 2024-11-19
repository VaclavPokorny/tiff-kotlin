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

}
