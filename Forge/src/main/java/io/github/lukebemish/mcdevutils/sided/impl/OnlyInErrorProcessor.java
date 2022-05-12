package io.github.lukebemish.mcdevutils.sided.impl;

import io.github.lukebemish.mcdevutils.api.Suppress;
import com.google.auto.service.AutoService;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("net.minecraftforge.api.distmarker.OnlyIn")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class OnlyInErrorProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                OnlyIn environment = e.getAnnotation(OnlyIn.class);
                Suppress suppression = e.getAnnotation(Suppress.class);
                if (environment!=null) {
                    processingEnv.getMessager().printMessage(
                            (suppression==null || !suppression.value().equals(Suppress.SIDED_REMOVAL_ERROR_SUPPRESSION))?
                                    Diagnostic.Kind.ERROR:Diagnostic.Kind.WARNING,
                            String.format("Detected use of @%s, which is highly inadvisable. If you have no other " +
                                    "option, you can suppress the error using @%s(\"%s\").",OnlyIn.class.getSimpleName(),
                                    Suppress.class.getSimpleName(), Suppress.SIDED_REMOVAL_ERROR_SUPPRESSION), e);
                    return false;
                }
            }
        }
        return false;
    }
}
