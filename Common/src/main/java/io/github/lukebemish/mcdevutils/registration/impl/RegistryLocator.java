package io.github.lukebemish.mcdevutils.registration.impl;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Arrays;

public class RegistryLocator {
    public static VariableElement locate(ProcessingEnvironment processingEnv, Types types, TypeMirror registryType) {
        TypeElement registryTypeElement = ((TypeElement)((DeclaredType)registryType).asElement());
        int param_count = registryTypeElement.getTypeParameters().size();
        if (param_count>0) {
            registryType = types.getDeclaredType(registryTypeElement,
                    Arrays.stream(new Object[param_count])
                            .map(o -> types.getWildcardType(null, null)).toList().toArray(new TypeMirror[param_count]));
        }
        ArrayList<VariableElement> fields = new ArrayList<>();
        TypeElement registryClassType = processingEnv.getElementUtils().getTypeElement("net.minecraft.core.Registry");
        TypeElement builtinRegClassType = processingEnv.getElementUtils().getTypeElement("net.minecraft.data.BuiltinRegistries");
        if (registryClassType != null) {
            TypeMirror finalRegistryType = types.getDeclaredType(registryClassType, registryType);
            registryClassType.getEnclosedElements().stream().filter(e -> e instanceof VariableElement)
                    .map(e -> (VariableElement) e)
                    .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                    .filter(e -> e.getModifiers().contains(Modifier.STATIC))
                    .filter(e -> types.isAssignable(e.asType(), finalRegistryType))
                    .forEach(fields::add);
            if (builtinRegClassType != null) {
                builtinRegClassType.getEnclosedElements().stream().filter(e->e instanceof VariableElement)
                        .map(e->(VariableElement)e)
                        .filter(e->e.getModifiers().contains(Modifier.PUBLIC))
                        .filter(e->e.getModifiers().contains(Modifier.STATIC))
                        .filter(e->types.isAssignable(e.asType(), finalRegistryType))
                        .forEach(fields::add);
            }
        }
        if (fields.size()==1) {
            return fields.get(0);
        }
        return null;
    }
}
