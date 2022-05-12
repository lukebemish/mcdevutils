package io.github.lukebemish.mcdevutils.registration.impl;

import com.google.auto.service.AutoService;
import io.github.lukebemish.mcdevutils.registration.api.Registrar;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@AutoService(IRegistrationWriter.class)
public class QuiltRegistrationWriter implements IRegistrationWriter {
    @Override
    public void write(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, Types types, String className, TypeMirror registryType, List<? extends VariableElement> fields, String mod_id, VariableElement target) throws IOException {
        String packageName = null;
        int packageClassDivide = className.lastIndexOf('.');
        if (packageClassDivide > 0) {
            packageName = className.substring(0, packageClassDivide);
        }

        String simpleClassName = className.substring(packageClassDivide + 1);
        String registrarClassName = className + "Registrar";
        String registrarSimpleClassName = registrarClassName
                .substring(packageClassDivide + 1);

        JavaFileObject registrarFile = processingEnv.getFiler()
                .createSourceFile(registrarClassName);

        String typeQualifiedName = String.valueOf(((TypeElement)((DeclaredType)registryType).asElement()).getQualifiedName());
        String typeSimpleName = String.valueOf(((DeclaredType)registryType).asElement().getSimpleName());

        try (PrintWriter out = new PrintWriter(registrarFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.println("import "+typeQualifiedName+";");
            out.println("import net.minecraft.resources.ResourceLocation;");
            out.println("import net.minecraft.core.Registry;");
            out.println();

            out.print("public class ");
            out.print(registrarSimpleClassName);
            out.println(" {");
            out.println();

            out.println("    public static void init(Registry<"+typeSimpleName+"> registry) {");
            out.println(String.format("        %s holder = new %s();",simpleClassName,simpleClassName));
            out.println(String.format("        %s.%s = holder;",simpleClassName,target.getSimpleName()));
            out.println("        String mod_id = \""+mod_id+"\";");
            out.println();

            for (VariableElement variableElement : fields) {
                TypeMirror fieldType = variableElement.asType();
                if (types.isAssignable(fieldType,registryType)) {
                    Registrar.Named name = variableElement.getAnnotation(Registrar.Named.class);
                    if (name==null || name.value()==null) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Registrar fields of the registry type must have a @"+
                                        "Registrar.Named annotation.", variableElement);
                        return;
                    }
                    out.println("        Registry.register(registry, new ResourceLocation(mod_id, \""+name.value()+"\"), holder."+variableElement.getSimpleName()+");");
                } else {
                    Registrar.Named name = variableElement.getAnnotation(Registrar.Named.class);
                    if (name!=null) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Registrar fields not of the registry type may not have a @"+
                                        "Registrar.Named annotation.", variableElement);
                        return;
                    }
                    try {
                        List<String> toWrite = writeForField(processingEnv, types, variableElement.asType(), "holder." + variableElement.getSimpleName(), 2, registryType);
                        toWrite.forEach(out::println);
                    } catch (Throwable thr) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Registrar field had unknown type.", variableElement);
                        return;
                    }
                }
            }


            out.println("    }");
            out.println("}");
        }
    }

    private List<String> writeForField(ProcessingEnvironment processingEnv, Types types, TypeMirror type, String varAccessor, int index, TypeMirror regType) throws IllegalArgumentException {
        String indent = new String(new char[index]).replace("\0", "    ");
        var elementUtils = processingEnv.getElementUtils();
        if (type instanceof DeclaredType declaredType) {
            if (types.isAssignable(declaredType, types.getDeclaredType(elementUtils.getTypeElement(Map.class.getCanonicalName()),
                    types.getWildcardType(elementUtils.getTypeElement(String.class.getCanonicalName()).asType(),null),
                    types.getWildcardType(regType,null)))) {
                // It's a map
                return List.of(String.format("%s%s.forEach((name%s, value%s) -> " +
                        "Registry.register(registry, new ResourceLocation(mod_id, name%s), value%s));",indent,varAccessor,index,index,index,index));
            } else if (types.isAssignable(declaredType, types.getDeclaredType(elementUtils.getTypeElement(Collection.class.getCanonicalName()),
                    types.getWildcardType(null,null)))) {
                var executable = types.asMemberOf(declaredType, elementUtils.getTypeElement(Collection.class.getCanonicalName())
                        .getEnclosedElements().stream().filter(element -> element instanceof ExecutableElement)
                        .filter(element -> String.valueOf(element.getSimpleName()).equals("add")).findFirst().orElseThrow(IllegalArgumentException::new));

                var newType = ((ExecutableType)executable).getParameterTypes().get(0);
                ArrayList<String> lines = new ArrayList<>();
                lines.add(String.format("%s%s.forEach(e%s -> {",indent,varAccessor,index));

                lines.addAll(writeForField(processingEnv, types, newType, "e"+index, index+1, regType));

                lines.add(String.format("%s});",indent));
                return lines;
            } else {
                Map<String, TypeMirror> recordStuff = parseRecord(types, declaredType);
                if (recordStuff != null && recordStuff.size() == 2
                        && recordStuff.values().stream().anyMatch(t->types.isAssignable(t, elementUtils.getTypeElement(String.class.getCanonicalName()).asType()))
                        && recordStuff.values().stream().anyMatch(t->types.isAssignable(t, regType))) {
                    String stringKey = recordStuff.entrySet().stream().filter(t->types.isAssignable(t.getValue(), elementUtils.getTypeElement(String.class.getCanonicalName()).asType()))
                            .findFirst().get().getKey();
                    String regTypeKey = recordStuff.entrySet().stream().filter(t->types.isAssignable(t.getValue(), regType)).findFirst().get().getKey();
                    return List.of(String.format("%sRegistry.register(registry, new ResourceLocation(mod_id, %s.%s()), %s.%s());",
                            indent, varAccessor, stringKey, varAccessor, regTypeKey));
                }
            }
            // I'll figure out other potential formats later
        }
        System.out.println(type);
        System.out.println(processingEnv.getElementUtils().getTypeElement(Map.class.getCanonicalName()).asType());
        throw new IllegalArgumentException();
    }

    Map<String, TypeMirror> parseRecord(Types types, DeclaredType declaredType) {
        TypeElement element = (TypeElement) declaredType.asElement();
        List<? extends ExecutableElement> execs = element.getEnclosedElements().stream()
                .filter(e->e instanceof ExecutableElement)
                .map(e->(ExecutableElement)e).toList();
        List<? extends ExecutableElement> constructors = execs.stream().filter(e->String.valueOf(e.getSimpleName()).equals("<init>")).toList();
        if (constructors.size()!=1) {
            return null;
        }
        ExecutableType constructor = (ExecutableType) types.asMemberOf(declaredType, constructors.get(0));
        Map<String, TypeMirror> out = new HashMap<>();
        for (int i = 0; i < constructors.get(0).getParameters().size(); i++) {
            String paramName = String.valueOf(constructors.get(0).getParameters().get(i).getSimpleName());
            out.put(paramName,constructor.getParameterTypes().get(i));
            if (execs.stream().anyMatch(e->String.valueOf(e.getSimpleName()).equals(paramName))) {
                ExecutableElement exec = execs.stream().filter(e->String.valueOf(e.getSimpleName()).equals(paramName)).findFirst().get();
                if (exec.getParameters().size()==0
                        && types.isAssignable(exec.getReturnType(),constructor.getParameterTypes().get(i))) {
                    continue;
                }
            }
            return null;
        }

        return out;
    }
}
