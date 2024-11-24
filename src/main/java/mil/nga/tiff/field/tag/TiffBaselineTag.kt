package mil.nga.tiff.field.tag

import mil.nga.tiff.field.FieldTagType

/**
 * TIFF basic tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class TiffBaselineTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    Artist(315, false),
    BitsPerSample(258, true),
    CellLength(265, false),
    CellWidth(264, false),
    ColorMap(320, false),
    Compression(259, false),
    Copyright(33432, false),
    DateTime(306, false),
    ExtraSamples(338, true),
    FillOrder(266, false),
    FreeByteCounts(289, false),
    FreeOffsets(288, false),
    GrayResponseCurve(291, false),
    GrayResponseUnit(290, false),
    HostComputer(316, false),
    ImageDescription(270, false),
    ImageLength(257, false),
    ImageWidth(256, false),
    Make(271, false),
    MaxSampleValue(281, false),
    MinSampleValue(280, false),
    Model(272, false),
    NewSubfileType(254, false),
    Orientation(274, false),
    PhotometricInterpretation(262, false),
    PlanarConfiguration(284, false),
    ResolutionUnit(296, false),
    RowsPerStrip(278, false),
    SamplesPerPixel(277, false),
    Software(305, false),
    StripByteCounts(279, true),
    StripOffsets(273, true),
    SubfileType(255, false),
    Threshholding(263, false),
    XResolution(282, false),
    YResolution(283, false)
}
