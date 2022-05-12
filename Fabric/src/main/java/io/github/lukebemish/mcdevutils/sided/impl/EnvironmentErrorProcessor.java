package io.github.lukebemish.mcdevutils.sided.impl;

import io.github.lukebemish.mcdevutils.api.Suppress;
import com.google.auto.service.AutoService;
import net.fabricmc.api.Environment;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("net.fabricmc.api.Environment")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class EnvironmentErrorProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                Environment environment = e.getAnnotation(Environment.class);
                Suppress suppression = e.getAnnotation(Suppress.class);
                if (environment!=null) {
                    processingEnv.getMessager().printMessage(
                            (suppression==null || !suppression.value().equals(Suppress.INTERNAL_SIDED_ANNOTATION))?
                                    Diagnostic.Kind.ERROR:Diagnostic.Kind.WARNING,
                            String.format("Detected use of @%s, which is highly inadvisable. If you have no other " +
                                            "option, you can suppress the error using @%s(\"%s\").",Environment.class.getSimpleName(),
                                    Suppress.class.getSimpleName(), Suppress.INTERNAL_SIDED_ANNOTATION), e);
                    return false;
                }
            }
        }
        return false;
    }
}
