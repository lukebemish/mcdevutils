package io.github.lukebemish.mcdevutils.sided.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that sidedness-checking gradle tasks should propagate side information through this element and every
 * element it references. Takes a {@link Side} determining which sides the annotated element can be referenced from.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(RetentionPolicy.CLASS)
public @interface CheckSide {
    Side value();
}
