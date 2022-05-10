package io.github.lukebemish.mcdevutils.sided.impl.discovery;

import java.util.Collection;

public interface ISide {
    void canSafelyCall(Collection<ISide> sides) throws SideConflictException;
}
