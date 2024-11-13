package mil.nga.tiff.domain;

import org.joou.UInteger;

public record UnsignedRational(UInteger numerator, UInteger denominator) implements Rational<UInteger> {

}

