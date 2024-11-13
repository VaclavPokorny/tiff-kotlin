package mil.nga.tiff.domain;

public interface Rational<T extends Number> {
    T numerator();
    T denominator();
}
