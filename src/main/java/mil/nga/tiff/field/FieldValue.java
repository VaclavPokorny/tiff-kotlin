package mil.nga.tiff.field;

import mil.nga.tiff.field.type.GenericFieldType;

public class FieldValue {

    private final int typeId;
    private final GenericFieldType type;
    private final Object value;

    public FieldValue(int typeId, GenericFieldType type, Object value) {
        this.typeId = typeId;
        this.type = type;
        this.value = value;
    }

}
