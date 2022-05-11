package io.github.lukebemish.mcdevutils.impl.gradle.visiting

import io.github.lukebemish.mcdevutils.impl.gradle.Name
import org.objectweb.asm.Type

class TypeSimplifier {
    static String simplifyType(String original) {
        simplifyType(Type.getType(new Name(original).descriptor))
    }

    static String simplifyType(Type original) {
        if (original.sort == Type.OBJECT) {
            return original.descriptor
        } else if (original.sort == Type.ARRAY) {
            return simplifyType(original.getElementType())
        }
        return null
    }
}
