package io.github.lukebemish.mcdevutils.registration.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Registrar {
    Class<?> type();
    String mod_id();

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Named {
        String path();
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude {}
}
