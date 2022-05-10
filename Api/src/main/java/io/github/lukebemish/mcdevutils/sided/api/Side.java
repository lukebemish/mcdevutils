package io.github.lukebemish.mcdevutils.sided.api;

import io.github.lukebemish.mcdevutils.sided.impl.discovery.BuiltinSide;
import io.github.lukebemish.mcdevutils.sided.impl.discovery.ISide;
import io.github.lukebemish.mcdevutils.sided.impl.discovery.SideConflictException;

import java.util.Collection;
import java.util.List;

public enum Side implements ISide {
    CLIENT,DEDICATED_SERVER,COMMON,PROXY;

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
