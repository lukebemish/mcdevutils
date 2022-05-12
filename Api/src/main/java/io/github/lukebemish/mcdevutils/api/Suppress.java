package io.github.lukebemish.mcdevutils.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicated that the named error should not be raised by the annotation processor on the annotated element.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Suppress {
    /**
     * Allows suppression of errors raised by the use of @Environment or @OnlyIn.
     */
    String INTERNAL_SIDED_ANNOTATION = "internal_sided_annotation";
    String value();
}
