package org.ogu.lang.resolvers.compiled;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.compiler.AmbiguousCallException;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 11/6/16.
 */
public class JavassistBasedMethodResolution {

    private static class MethodOrConstructor {
        private CtConstructor constructor;
        private CtMethod method;

        public MethodOrConstructor(CtConstructor constructor) {
            this.constructor = constructor;
        }

        public MethodOrConstructor(CtMethod method) {
            this.method = method;
        }

        public int getParameterCount() throws NotFoundException {
            if (method != null) {
                return method.getParameterTypes().length;
            } else {
                return constructor.getParameterTypes().length;
            }
        }

        public CtClass getParameterType(int i) throws NotFoundException {
            if (method != null) {
                return method.getParameterTypes()[i];
            } else {
                return constructor.getParameterTypes()[i];
            }
        }
    }

    public static CtConstructor findConstructorAmong(List<JvmType> argsTypes, SymbolResolver resolver, List<CtConstructor> constructors) {
        try {
            List<MethodOrConstructor> methodOrConstructors = constructors.stream().map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
            MethodOrConstructor methodOrConstructor = findMethodAmong(argsTypes, resolver, methodOrConstructors, "constructor");
            if (methodOrConstructor == null) {
                throw new RuntimeException("unresolved constructor for " + argsTypes);
            }
            return methodOrConstructor.constructor;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static CtConstructor findConstructorAmongActualParams(List<ActualParamNode> argsTypes, SymbolResolver resolver, List<CtConstructor> constructors) {
        try {
            List<MethodOrConstructor> methodOrConstructors = constructors.stream().map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
            MethodOrConstructor methodOrConstructor = findMethodAmongActualParams(argsTypes, resolver, methodOrConstructors, "constructor");
            if (methodOrConstructor == null) {
                throw new RuntimeException("unresolved constructor for " + argsTypes);
            }
            return methodOrConstructor.constructor;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static CtMethod findMethodAmong(String name, List<JvmType> argsTypes, SymbolResolver resolver,  List<CtMethod> methods) {
        try {
            List<MethodOrConstructor> methodOrConstructors = methods.stream()
                    .filter((m) -> m.getName().equals(name))
                    .map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
            MethodOrConstructor methodOrConstructor = findMethodAmong(argsTypes, resolver, methodOrConstructors, name);
            if (methodOrConstructor == null) {
                throw new RuntimeException("unresolved method " + name + " for " + argsTypes);
            }
            return methodOrConstructor.method;
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<CtMethod> findFunctionAmongActualParams(String name, List<ActualParamNode> argsTypes, SymbolResolver resolver,  List<CtMethod> methods) {
        try {
            List<MethodOrConstructor> methodOrConstructors = methods.stream()
                    .filter((m) -> m.getName().equals(name))
                    .map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
            MethodOrConstructor methodOrConstructor = findMethodAmongActualParams(argsTypes, resolver, methodOrConstructors, name);
            if (methodOrConstructor == null) {
                return Optional.empty();
            } else {
                return Optional.of(methodOrConstructor.method);
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodOrConstructor findMethodAmong(List<JvmType> argsTypes, SymbolResolver resolver, List<MethodOrConstructor> methods, String desc) throws NotFoundException {
        List<MethodOrConstructor> suitableMethods = new ArrayList<>();
        for (MethodOrConstructor method : methods) {
            if (method.getParameterCount() == argsTypes.size()) {
                boolean match = true;
                for (int i = 0; i < argsTypes.size(); i++) {
                    TypeUsage actualType = TypeUsageNode.fromJvmType(argsTypes.get(i), resolver, Collections.emptyMap());
                    TypeUsage formalType = JavassistTypeDefinitionFactory.toTypeUsage(method.getParameterType(i), resolver);
                    if (!actualType.canBeAssignedTo(formalType)) {
                        match = false;
                    }
                }
                if (match) {
                    suitableMethods.add(method);
                }
            }
        }

        if (suitableMethods.size() == 0) {
            return null;
        } else if (suitableMethods.size() == 1) {
            return suitableMethods.get(0);
        } else {
            return findMostSpecific(suitableMethods, new AmbiguousCallException(null, desc, argsTypes), argsTypes, resolver);
        }
    }

    private static MethodOrConstructor findMethodAmongActualParams(List<ActualParamNode> argsTypes, SymbolResolver resolver, List<MethodOrConstructor> methods, String desc) throws NotFoundException {
        // TODO reorder params considering name
        List<MethodOrConstructor> suitableMethods = new ArrayList<>();
        for (MethodOrConstructor method : methods) {
            if (method.getParameterCount() == argsTypes.size()) {
                boolean match = true;
                for (int i = 0; i < argsTypes.size(); i++) {
                    TypeUsage actualType = argsTypes.get(i).getValue().calcType();
                    TypeUsage formalType = JavassistTypeDefinitionFactory.toTypeUsage(method.getParameterType(i), resolver);
                    if (!actualType.canBeAssignedTo(formalType)) {
                        match = false;
                    }
                }
                if (match) {
                    suitableMethods.add(method);
                }
            }
        }

        if (suitableMethods.size() == 0) {
            return null;
        } else if (suitableMethods.size() == 1) {
            return suitableMethods.get(0);
        } else {
            return findMostSpecific(suitableMethods,
                    new AmbiguousCallException(null, argsTypes, desc),
                    argsTypes.stream().map((ap)->ap.getValue().calcType().jvmType()).collect(Collectors.toList()),
                    resolver);
        }
    }

    private static MethodOrConstructor findMostSpecific(List<MethodOrConstructor> methods,
                                                        AmbiguousCallException exceptionToThrow,
                                                        List<JvmType> argsTypes,
                                                        SymbolResolver resolver) throws NotFoundException {
        MethodOrConstructor winningMethod = methods.get(0);
        for (MethodOrConstructor other : methods.subList(1, methods.size())) {
            if (isTheFirstMoreSpecific(winningMethod, other, argsTypes, resolver)) {
            } else if (isTheFirstMoreSpecific(other, winningMethod, argsTypes, resolver)) {
                winningMethod = other;
            } else if (!isTheFirstMoreSpecific(winningMethod, other, argsTypes, resolver)) {
                // neither is more specific
                throw exceptionToThrow;
            }
        }
        return winningMethod;
    }

    private static boolean isTheFirstMoreSpecific(MethodOrConstructor first, MethodOrConstructor second,
                                                  List<JvmType> argsTypes,
                                                  SymbolResolver resolver) throws NotFoundException {
        boolean atLeastOneParamIsMoreSpecific = false;
        if (first.getParameterCount() != second.getParameterCount()) {
            throw new IllegalArgumentException();
        }
        for (int i=0;i<first.getParameterCount();i++){
            CtClass paramFirst = first.getParameterType(i);
            CtClass paramSecond = second.getParameterType(i);
            if (isTheFirstMoreSpecific(paramFirst, paramSecond, argsTypes.get(i), resolver)) {
                atLeastOneParamIsMoreSpecific = true;
            } else if (isTheFirstMoreSpecific(paramSecond, paramFirst, argsTypes.get(i), resolver)) {
                return false;
            }
        }

        return atLeastOneParamIsMoreSpecific;
    }

    private static boolean isTheFirstMoreSpecific(CtClass firstType, CtClass secondType, JvmType targetType, SymbolResolver resolver) {
        boolean firstIsPrimitive = firstType.isPrimitive();
        boolean secondIsPrimitive = secondType.isPrimitive();
        boolean targetTypeIsPrimitive = targetType.isPrimitive();

        // it is a match or a primitive promotion
        if (targetTypeIsPrimitive && firstIsPrimitive && !secondIsPrimitive) {
            return true;
        }
        if (targetTypeIsPrimitive && !firstIsPrimitive && secondIsPrimitive) {
            return false;
        }

        if (firstType.isPrimitive() || firstType.isArray()) {
            return false;
        }
        if (secondType.isPrimitive() || secondType.isArray()) {
            return false;
        }
        // TODO consider generic parameters?
        JavassistTypeDefinition firstDef = new JavassistTypeDefinition(firstType, resolver);
        JavassistTypeDefinition secondDef = new JavassistTypeDefinition(secondType, resolver);
        TypeUsage firstTypeUsage = new ReferenceTypeUsage(firstDef);
        TypeUsage secondTypeUsage = new ReferenceTypeUsage(secondDef);
        return firstTypeUsage.canBeAssignedTo(secondTypeUsage) && !secondTypeUsage.canBeAssignedTo(firstTypeUsage);
    }
}
