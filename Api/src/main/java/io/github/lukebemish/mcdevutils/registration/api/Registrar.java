package io.github.lukebemish.mcdevutils.registration.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Registrar {
    Class<?> type();


    String mod_id();

    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Named {
        String value();
    }

    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude {}

    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Target {}
}
