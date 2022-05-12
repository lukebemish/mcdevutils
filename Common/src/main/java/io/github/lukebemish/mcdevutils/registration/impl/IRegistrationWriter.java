package io.github.lukebemish.mcdevutils.registration.impl;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.List;

public interface IRegistrationWriter {

    void write(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, Types types, String className, TypeMirror registryType, List<? extends VariableElement> fields, String modid, VariableElement target) throws IOException;
}
