package io.github.lukebemish.mcdevutils.sided.impl.discovery;

public class SideConflictException extends Exception {
    public final ISide s1;
    public final ISide s2;
    public SideConflictException(ISide s1, ISide s2) {
        this.s1 = s1;
        this.s2 = s2;
    }
}
