package io.github.lukebemish.mcdevutils.sided.impl.discovery;

import io.github.lukebemish.mcdevutils.sided.api.Side;

import java.util.Collection;
import java.util.List;

public enum BuiltinSide implements ISide {
    CLIENT,SERVER;

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
        CLIENT.cantCall = List.of(SERVER, Side.DEDICATED_SERVER);
        SERVER.cantCall = List.of(CLIENT, Side.CLIENT);
    }

    @Override
    public String toString() {
        return SideStringFormatter.FORMATTER.stringify(this);
    }
}
