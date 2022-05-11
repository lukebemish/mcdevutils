package io.github.lukebemish.mcdevutils.impl.gradle.visiting

import io.github.lukebemish.mcdevutils.impl.gradle.Constants
import io.github.lukebemish.mcdevutils.impl.gradle.Name
import io.github.lukebemish.mcdevutils.sided.api.Side
import io.github.lukebemish.mcdevutils.sided.impl.discovery.BuiltinSide
import io.github.lukebemish.mcdevutils.sided.impl.discovery.ISide
import io.github.lukebemish.mcdevutils.sided.impl.discovery.SideConflictException
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class NamingClassExtractor {
    Map<String, Set<String>> dependencyMap = new HashMap<>()
    Map<String, Set<ISide>> sideMap = new HashMap<>()
    Set<String> classSet = new HashSet<>()
    HashSet<String> computing = new HashSet<>()
    HashMap<String, HashSet<ISide>> cache = new HashMap<>()
    HashSet<String> toCheck = new HashSet<>()

    void recordDependencies(ClassNode node) {
        Set<ISide> sides = new HashSet<>()
        Set<String> dependencies = new HashSet<>()

        Name className = new Name(node.name)

        if (className.isPackage()) {
            className = className.parent
        }

        List<AnnotationNode> annotations = new ArrayList<>();
        if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations)
        if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations)

        annotations.forEach(annotation -> {
            var descriptor = new Name(annotation.desc)
            if (descriptor == Constants.ENVIRONMENT_PATH || descriptor == Constants.ONLYIN_PATH) {
                annotation.values.forEach(obj -> {
                    if (obj instanceof String[]) {
                        var value = obj[1]
                        if (value == "CLIENT") {
                            sides.add(BuiltinSide.CLIENT)
                        } else if (value == "DEDICATED_SERVER" || value == "SERVER") {
                            sides.add(BuiltinSide.SERVER)
                        }
                    }
                })
            } else if (descriptor == Constants.CHECKSIDE_PATH) {
                annotation.values.forEach(obj -> {
                    if (obj instanceof String[]) {
                        var value = obj[1]
                        Side side = Side.valueOf(value)
                        if (side != null) {
                            sides.add(side)
                            toCheck.add(className.descriptor)
                        }
                    }
                })
            }
        })

        if (node.methods != null) {
            // Collect/handle dependencies
            node.methods.forEach(methodNode -> {
                dependencies.add(className.descriptor + methodNode.name + methodNode.desc)
                recordDependencies(methodNode, className.descriptor)
            })
        }

        if (node.innerClasses != null) {
            node.innerClasses.forEach(classNode -> {
                dependencies.add(new Name(classNode.outerName + "\$" + classNode.innerName).descriptor)
            })
        }

        dependencyMap.putIfAbsent(className.descriptor, new HashSet<String>())
        dependencyMap.get(className.descriptor).addAll(dependencies)
        sideMap.put(className.descriptor, sides)
        classSet.add(className.descriptor)

        if (className.parent != null) {
            dependencyMap.putIfAbsent(className.parent.descriptor, new HashSet<>())
            dependencyMap.get(className.parent.descriptor).add(className.descriptor)
        }
    }

    void recordDependencies(MethodNode node, String classDesc) {
        String methodDesc = classDesc+node.name+node.desc

        Set<ISide> sides = new HashSet<>()
        Set<String> dependencies = new HashSet<>()

        List<AnnotationNode> annotations = new ArrayList<>();
        if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations)
        if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations)

        annotations.forEach(annotation -> {
            var descriptor = new Name(annotation.desc)
            if (descriptor == Constants.ENVIRONMENT_PATH || descriptor == Constants.ONLYIN_PATH) {
                annotation.values.forEach(obj -> {
                    if (obj instanceof String[]) {
                        var value = obj[1]
                        if (value == "CLIENT") {
                            sides.add(BuiltinSide.CLIENT)
                        } else if (value == "DEDICATED_SERVER" || value == "SERVER") {
                            sides.add(BuiltinSide.SERVER)
                        }
                    }
                })
            } else if (descriptor == Constants.CHECKSIDE_PATH) {
                annotation.values.forEach(obj -> {
                    if (obj instanceof String[]) {
                        var value = obj[1]
                        Side side = Side.valueOf(value)
                        if (side != null) {
                            sides.add(side)
                        }
                    }
                })
            }
        })

        if (node.instructions != null) {
            //TODO: all of this
            node.instructions.forEach(insn -> {
                if (insn instanceof FieldInsnNode) {
                    dependencies.add(new Name(insn.owner).descriptor)
                    String type = TypeSimplifier.simplifyType(insn.desc)
                    if (type != null) {
                        dependencies.add(new Name(type).descriptor)
                    }
                } else if (insn instanceof MethodInsnNode) {
                    dependencies.add(new Name(insn.owner).descriptor)
                    dependencies.add(new Name(insn.owner).descriptor+insn.name+insn.desc)
                } else if (insn instanceof TypeInsnNode) {
                    String type = TypeSimplifier.simplifyType(insn.desc)
                    if (type != null) {
                        dependencies.add(new Name(type).descriptor)
                    }
                }
            })
        }
        if (node.localVariables != null) {
            node.localVariables.forEach(local -> {
                String type = TypeSimplifier.simplifyType(local.desc)
                if (type != null) {
                    dependencies.add(new Name(type).descriptor)
                }
            })
        }

        for (Type type : Type.getArgumentTypes(node.desc)) {
            String typeDesc = TypeSimplifier.simplifyType(type)
            if (typeDesc != null) {
                dependencies.add(new Name(typeDesc).descriptor)
            }
        }

        var type = Type.getReturnType(node.desc)
        String typeDesc = TypeSimplifier.simplifyType(type)
        if (typeDesc != null) {
            dependencies.add(new Name(typeDesc).descriptor)
        }


        dependencyMap.putIfAbsent(methodDesc, new HashSet<String>())
        dependencyMap.get(methodDesc).addAll(dependencies)
        sideMap.put(methodDesc, sides)
    }

    void checkElements() {
        cache.clear()
        for (String checking : toCheck) {
            computing.clear()
            checkElement(checking, List.of())
        }
    }

    // returns a set of all sides callable by an element, unless:
    //  - the element is a PROXY, in which case only PROXY is returned
    //  - the element is a class without a side annotation, in which sub-elements with builtin annotations are ignored
    HashSet<ISide> checkElement(String element, List<String> stack) {
        stack = new ArrayList<>(stack)
        stack.add(element)

        if (computing.contains(element)) return new HashSet<>()
        if (cache.containsKey(element)) {
            return cache.get(element)
        }
        computing.add(element)
        Set<ISide> original = sideMap.getOrDefault(element, new HashSet<>())
        checkOrError(stack, original)
        HashSet<ISide> out = new HashSet<>(original)

        Collection<String> dependencies = dependencyMap.getOrDefault(element, new HashSet<>())

        if (original.contains(Side.PROXY)) {
            out = new HashSet<>(List.of(Side.PROXY));
        } else {
            if (classSet.contains(element)) {
                for (String e : dependencies) {
                    if (original.size() == 0) {
                        HashSet<ISide> contains = sideMap.getOrDefault(e, new HashSet<>())
                        if (!contains.stream().allMatch(s -> s instanceof BuiltinSide) || contains.size() == 0) {
                            HashSet<ISide> sides = checkElement(e, stack)
                            for (ISide side : original) {
                                checkSingleSide(stack, side, sides, e)
                            }
                            out.addAll(sides)
                        }
                    } else {
                        HashSet<ISide> sides = checkElement(e, stack)
                        for (ISide side : original) {
                            checkSingleSide(stack, side, sides, e)
                        }
                        out.addAll(sides)
                    }
                }
            } else {
                for (String e : dependencies) {
                    HashSet<ISide> sides = checkElement(e, stack)
                    for (ISide side : original) {
                        checkSingleSide(stack, side, sides, e)
                    }
                    out.addAll(sides)
                }
            }
        }

        cache.put(element,out);

        return out;
    }

    private static void checkOrError(Collection<String> elements, Collection<ISide> sides) {
        for (ISide side : sides) {
            try {
                side.canSafelyCall(sides);
            } catch (SideConflictException e) {
                throw new ConflictDiscoveryException(elements, e);
            }
        }
    }

    private static void checkSingleSide(Collection<String> elements, ISide side, Collection<ISide> sides, String newElement) {
        try {
            side.canSafelyCall(sides);
        } catch (SideConflictException e) {
            ArrayList<String> stack = new ArrayList<>(elements)
            stack.add(newElement)
            throw new ConflictDiscoveryException(stack, e)
        }
    }
}
