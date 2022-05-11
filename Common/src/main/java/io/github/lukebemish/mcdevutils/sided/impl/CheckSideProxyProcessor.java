package io.github.lukebemish.mcdevutils.sided.impl;

import com.google.auto.service.AutoService;
import io.github.lukebemish.mcdevutils.sided.api.CheckSide;
import io.github.lukebemish.mcdevutils.sided.api.Side;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("io.github.lukebemish.mcdevutils.sided.api.CheckSide")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CheckSideProxyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                CheckSide check = e.getAnnotation(CheckSide.class);
                if (check!=null && check.value() == Side.PROXY && !(e instanceof TypeElement)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "@"+CheckSide.class+"("+Side.PROXY.name()+") should only be applied to classes.", e);
                    return false;
                }
            }
        }
        return false;
    }
}
