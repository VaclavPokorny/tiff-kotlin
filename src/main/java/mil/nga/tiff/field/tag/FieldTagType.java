package mil.nga.tiff.field.tag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public sealed interface FieldTagType permits TiffBasicTag, TiffExtendedTag, JpegTag, ExifTag, GeoTiffTag, GdalTag, MetaTag {

    /**
     * Find all available described tags
     *
     * @return list of field tag types
     */
    static List<FieldTagType> findAll() {
        @SuppressWarnings("unchecked")
        Class<FieldTagType>[] subclasses = (Class<FieldTagType>[]) FieldTagType.class.getPermittedSubclasses();

        return Stream.of(subclasses)
            .flatMap(o -> Stream.of(o.getEnumConstants()))
            .toList();
    }

    /**
     * Get a field tag type by id
     *
     * @param id tag id
     * @return field tag type
     */
    static Optional<FieldTagType> getById(int id) {
        return findAll()
            .stream()
            .filter(o -> o.getId() == id)
            .findAny();
    }

    boolean isArray();

    int getId();
}
