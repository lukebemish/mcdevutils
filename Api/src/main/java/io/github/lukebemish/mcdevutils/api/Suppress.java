package io.github.lukebemish.mcdevutils.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Suppress {
    String SIDED_REMOVAL_ERROR_SUPPRESSION = "sided_removal_error";
    String value();
}
