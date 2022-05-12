package io.github.lukebemish.mcdevutils.registration.impl;

import com.google.auto.service.AutoService;
import io.github.lukebemish.mcdevutils.impl.Services;
import io.github.lukebemish.mcdevutils.registration.api.Registrar;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("io.github.lukebemish.mcdevutils.registration.api.Registrar")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class RegistrarProcessor extends AbstractProcessor {
    IRegistrationWriter REGISTRATION_WRITER = Services.load(RegistrarProcessor.class.getClassLoader(), IRegistrationWriter.class);
    private Types types;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                Registrar registrar = e.getAnnotation(Registrar.class);
                if (registrar != null && e instanceof TypeElement typeElement) {
                    if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "@Registrar may not be applied to an abstract type. ", e);
                        return true;
                    }
                    List<? extends Element> targets = typeElement.getEnclosedElements().stream()
                            .filter(el->el.getAnnotation(Registrar.Target.class)!=null).toList();
                    if (targets.size() != 1 || !(targets.get(0) instanceof VariableElement variableElement)
                            || variableElement.getModifiers().contains(Modifier.FINAL)
                            || !variableElement.getModifiers().contains(Modifier.STATIC)
                            || !variableElement.getModifiers().contains(Modifier.PUBLIC)
                            || !types.isSameType(variableElement.asType(),e.asType())) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Registrar must have exactly one @Registrar.Target mutable static field of its own type. ", e);
                        return true;
                    }
                    // We know that this field is fine to use.
                    VariableElement target = (VariableElement) targets.get(0);
                    var items = typeElement.getEnclosedElements().stream()
                            .filter(el->el instanceof VariableElement)
                            .filter(el->el.getAnnotation(Registrar.Exclude.class)==null)
                            .filter(el->el.getAnnotation(Registrar.Target.class)==null)
                            .filter(el-> el.getModifiers().contains(Modifier.PUBLIC))
                            .map(el->(VariableElement)el).toList();
                    // Feed this to the platform-specific stuff.
                    String className = typeElement.getQualifiedName().toString();
                    TypeMirror registryType;
                    try {
                        Class<?> clazz = registrar.type();
                        registryType = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType();
                    } catch (MirroredTypeException mte) {
                        registryType = mte.getTypeMirror();
                    }

                    try {
                        REGISTRATION_WRITER.write(processingEnv, roundEnv, types, className, registryType, items, registrar.mod_id(), target);
                    } catch (IOException ignored) {

                    }
                }
            }
        }
        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.types = processingEnv.getTypeUtils();
    }
}
