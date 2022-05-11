package io.github.lukebemish.mcdevutils.impl.gradle.visiting

import io.github.lukebemish.mcdevutils.sided.impl.discovery.ISide
import io.github.lukebemish.mcdevutils.sided.impl.discovery.SideConflictException

class ConflictDiscoveryException extends RuntimeException {
    public final Collection<String> e;
    public final ISide s1;
    public final ISide s2;

    ConflictDiscoveryException(Collection<String> e, SideConflictException exception) {
        super(String.format("Conflicting annotations %s referencing %s found while propagating:\n    %s",exception.s1, exception.s2, String.join("\n    ", e)))
        this.e = e
        this.s1 = exception.s1
        this.s2 = exception.s2
    }
}
