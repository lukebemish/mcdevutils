package io.github.lukebemish.mcdevutils.sided.api;

import io.github.lukebemish.mcdevutils.sided.impl.discovery.BuiltinSide;
import io.github.lukebemish.mcdevutils.sided.impl.discovery.ISide;
import io.github.lukebemish.mcdevutils.sided.impl.discovery.SideConflictException;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

/**
 * Specifies the sides an element annotated with {@link CheckSide} is allowed to access.
 */
public enum Side implements ISide {
    /**
     * The element may access client-only, proxy, and common code.
     */
    CLIENT,
    /**
     * The element may access dedicated-server-only, proxy, and common code.
     */
    DEDICATED_SERVER,
    /**
     * The element may access proxy and common code.
     */
    COMMON,
    /**
     * The element may access any code. Additionally, elements accessing this element are not considered to reference
     * any code that this element references. This is meant to be used to establish boundaries between common and,
     * for example, client-only code, and may only be given to classes.
     */
    PROXY;

    @ApiStatus.Internal
    @Override
    public void canSafelyCall(Collection<ISide> sides) throws SideConflictException {
        for (ISide c : this.cantCall) {
            if (sides.contains(c)) {
                throw new SideConflictException(this,c);
            }
        }
    }

    private List<ISide> cantCall;
    static {
        CLIENT.cantCall = List.of(DEDICATED_SERVER, BuiltinSide.SERVER);
        DEDICATED_SERVER.cantCall = List.of(CLIENT, BuiltinSide.CLIENT);
        COMMON.cantCall = List.of(BuiltinSide.SERVER, BuiltinSide.CLIENT, DEDICATED_SERVER, CLIENT);
        PROXY.cantCall = List.of();
    }

    @Override
    public String toString() {
        return "@"+CheckSide.class.getSimpleName()+"("+this.name()+")";
    }
}
