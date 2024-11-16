package mil.nga.tiff.domain.container;

import mil.nga.tiff.field.type.GenericFieldType;

import java.util.List;

public record FieldMultiValueContainer<T>(GenericFieldType<T> type, List<T> value) {



}
