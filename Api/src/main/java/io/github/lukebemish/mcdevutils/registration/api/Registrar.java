package io.github.lukebemish.mcdevutils.registration.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a registrar should be generated based on the annotated class. Registrars are generated separately for
 * each platform, and attempt to register members of a class to the proper register. The annotation must be provided
 * with the type of hte objects being registered, and the mod id that they should be registered under. A registrar
 * should have a single static field of its own type annotated with {@link Registrar.Target}, in which the constructed
 * object will be stored for future reference
 *
 * Registrars will attempt to register the following objects stored in instance public fields of the class:
 * <ul>
 *     <li>Objects assignable to the registrar type annotated with {@link Registrar.Named}</li>
 *     <li>Objects of a two-element record type, where one element is assignable to {@link String} and the other to
 *     the registrar type.</li>
 *     <li>Maps mapping from a type assignable to {@link String} to a type assignable to the registrar type.</li>
 *     <li>Collections of any of the above.</li>
 * </ul>
 */
@java.lang.annotation.Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Registrar {
    Class<?> type();


    String mod_id();

    /**
     * Provides a resource location path for the annotated object when registered. The object will be annotated under the location
     * comprised of the mod id and the supplied string.
     */
    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Named {
        String value();
    }

    /**
     * Indicates that the annotated field should be excluded from registration. The registrar will attempt to register
     * all public fields by default; to prevent this, either make the fields private or exclude them.
     */
    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude {}

    /**
     * Indicated that the annotated field should contain a reference to the holder object for the registrar after
     * registration. The field should be public and static.
     */
    @java.lang.annotation.Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Target {}
}
