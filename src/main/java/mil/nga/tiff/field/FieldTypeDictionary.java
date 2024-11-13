package mil.nga.tiff.field;

import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.field.type.NumericFieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Dictionary for field types lookup
 *
 * Default implementation is {@link DefaultFieldTypeDictionary}.
 */
public interface FieldTypeDictionary {

    /**
     * Find field type for given type ID
     *
     * @param id field type number
     * @return field type
     */
    GenericFieldType findById(int id);

    /**
     * Find the field type for given sample format and and bits per sample
     *
     * @param sampleFormat  sample format
     * @param bitsPerSample bits per sample
     * @return field type
     */
    NumericFieldType findBySampleParams(SampleFormat sampleFormat, int bitsPerSample);

}
