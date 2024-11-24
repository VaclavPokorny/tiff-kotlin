package mil.nga.tiff.field.tag

/**
 * TIFF extended tags
 *
 * @param id      tag id
 * @param isArray true if an array type
 */
enum class TiffExtendedTag(override val id: Int, override val isArray: Boolean) : FieldTagType {
    BadFaxLines(326, false),
    CleanFaxData(327, false),
    ClipPath(343, false),
    ConsecutiveBadFaxLines(328, false),
    Decode(433, false),
    DefaultImageColor(434, false),
    DocumentName(269, false),
    DotRange(336, false),
    HalftoneHints(321, false),
    Indexed(346, false),
    JPEGTables(347, false),
    PageName(285, false),
    PageNumber(297, false),
    Predictor(317, false),
    PrimaryChromaticities(319, false),
    ReferenceBlackWhite(532, false),
    SampleFormat(339, true),
    SMinSampleValue(340, false),
    SMaxSampleValue(341, false),
    StripRowCounts(559, true),
    SubIFDs(330, false),
    T4Options(292, false),
    T6Options(293, false),
    TileByteCounts(325, true),
    TileLength(323, false),
    TileOffsets(324, true),
    TileWidth(322, false),
    TransferFunction(301, false),
    WhitePoint(318, false),
    XClipPathUnits(344, false),
    XPosition(286, false),
    YCbCrCoefficients(529, false),
    YCbCrPositioning(531, false),
    YCbCrSubSampling(530, false),
    YClipPathUnits(345, false),
    YPosition(287, false)
}
