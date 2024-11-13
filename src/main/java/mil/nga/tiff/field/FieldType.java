package mil.nga.tiff.field;

import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field type metadata
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldType {
    int id();
    int bytesPerSample();
    SampleFormat sampleFormat() default SampleFormat.UNDEFINED;
    boolean multivalue() default false;
}
