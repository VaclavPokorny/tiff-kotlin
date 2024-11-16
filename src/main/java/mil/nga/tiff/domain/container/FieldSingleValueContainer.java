package mil.nga.tiff.domain.container;

import mil.nga.tiff.field.type.GenericFieldType;

public record FieldSingleValueContainer<T>(GenericFieldType<T> type, T value) {



}
