package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalConstructorDefinition;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedSymbolException;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameterSymbol;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.InvocableReferenceTypeUsage;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReflectionBasedTypeDefinition implements TypeDefinition {


    private Class<?> clazz;
    private List<TypeUsage> typeParameters = new LinkedList<>();
    private SymbolResolver resolver;

    public ReflectionBasedTypeDefinition(Class<?> clazz, SymbolResolver resolver) {
        if (!clazz.getCanonicalName().startsWith("java.") && !clazz.getCanonicalName().startsWith("javax.")) {
            throw new IllegalArgumentException(clazz.getCanonicalName());
        }
        this.clazz = clazz;
        this.resolver = resolver;
    }

    public void addTypeParameter(TypeUsage typeUsage) {
        typeParameters.add(typeUsage);
    }

    @Override
    public <T extends TypeUsage> Map<String, TypeUsage> associatedTypeParametersToName(List<T> typeParams) {
        if (typeParams.isEmpty()) {
            return Collections.emptyMap();
        }
        if (clazz.getTypeParameters().length != typeParams.size()) {
            throw new IllegalStateException("It should have " + clazz.getTypeParameters().length + " and it has " + typeParams.size());
        }
        Map<String, TypeUsage> map = new HashMap<>();
        int i=0;
        for (TypeVariable tv : clazz.getTypeParameters()) {
            map.put(tv.getName(), typeParams.get(i));
            i++;
        }
        return map;
    }

    @Override
    public boolean canFieldBeAssigned(String field) {
        return true;
    }

    @Override
    public JvmConstructorDefinition resolveConstructorCall(List<ActualParamNode> actualParams) {
        try {
            List<JvmType> argsTypes = new ArrayList<>();
            for (ActualParamNode actualParam : actualParams) {
                argsTypes.add(actualParam.getValue().calcType().jvmType());
            }
            return ReflectionBasedMethodResolution.findConstructorAmong(argsTypes, resolver, Arrays.asList(clazz.getConstructors()));
        } catch (RuntimeException e){
            throw new RuntimeException("Resolving constructor call on " + clazz.getCanonicalName(), e);
        }
    }

    private TypeUsage typeFor(List<Method> methods, SymbolResolver resolver) {
        if (methods.isEmpty()) {
            throw new IllegalArgumentException();
        }
        methods.forEach((m)-> {
           // Logger.debug("method m = "+m);
        });
        if (methods.size() != 1) {
            throw new UnsupportedOperationException();
        }
        return typeFor(methods.get(0), resolver);
    }

    private TypeUsage typeFor(Method method, SymbolResolver resolver) {
        InvocableReferenceTypeUsage invokableReferenceTypeUsage = new InvocableReferenceTypeUsage(toInternalFunctionDefinition(method));
        return invokableReferenceTypeUsage;
    }

    private InternalFunctionDefinition toInternalFunctionDefinition(Method method) {
        return new InternalFunctionDefinition(method.getName(), formalParameters(method), toTypeUsage(method.getGenericReturnType(), resolver),
                ReflectionTypeDefinitionFactory.toFunctionDefinition(method));
    }

    @Override
    public Optional<InternalConstructorDefinition> findConstructor(List<ActualParamNode> actualParams) {
        Constructor constructor = ReflectionBasedMethodResolution.findConstructorAmongActualParams(
                actualParams, resolver, Arrays.asList(clazz.getConstructors()));
        return Optional.of(toInternalConstructorDefinition(constructor));
    }

    private InternalConstructorDefinition toInternalConstructorDefinition(Constructor<?> constructor) {
        JvmConstructorDefinition jvmConstructorDefinition = ReflectionTypeDefinitionFactory.toConstructorDefinition(constructor);
        return new InternalConstructorDefinition(new ReferenceTypeUsage(this), formalParameters(constructor), jvmConstructorDefinition);
    }

    private static TypeUsage toTypeUsage(Type type, SymbolResolver resolver) {
        return ReflectionBasedMethodResolution.toTypeUsage(type, Collections.emptyMap(), resolver);
    }

    private List<FormalParameterSymbol> formalParameters(Constructor constructor) {
        return ReflectionBasedMethodResolution.formalParameters(constructor, resolver);
    }


    private List<FormalParameterSymbol> formalParameters(Method method) {
        return ReflectionBasedMethodResolution.formalParameters(method, getTypeVariables(), resolver);
    }

    private Map<String, TypeUsage> getTypeVariables() {
        Map<String, TypeUsage> map = new HashMap<>();
        if (clazz.getTypeParameters().length != typeParameters.size()) {
            throw new IllegalStateException("It should have " + clazz.getTypeParameters().length + " and it has " + typeParameters.size());
        }
        int i=0;
        for (TypeVariable tv : clazz.getTypeParameters()) {
            map.put(tv.getName(), typeParameters.get(i));
            i++;
        }
        return map;
    }

    @Override
    public JvmMethodDefinition findFunctionFor(String name, List<JvmType> argsTypes, boolean staticContext) {
        return ReflectionTypeDefinitionFactory.toFunctionDefinition(ReflectionBasedMethodResolution.findMethodAmong(name, argsTypes, resolver, staticContext, Arrays.asList(clazz.getMethods())));
    }



    @Override
    public TypeUsage calcType() {
        return new ReferenceTypeUsage(this);
    }

    @Override
    public String getQualifiedName() {
        return clazz.getCanonicalName();
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isClass() {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation() && !clazz.isArray() && !clazz.isPrimitive();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toString() {
        return "ReflectionBasedTypeDefinition{" +
                "clazz=" + clazz +
                '}';
    }


    @Override
    public boolean hasField(String fieldName, boolean staticContext) {
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName)) {
                if (Modifier.isStatic(field.getModifiers()) == staticContext) {
                    return true;
                }
            }
        }

        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(fieldName)) {
                if (Modifier.isStatic(method.getModifiers()) == staticContext) {
                    return true;
                }
            }
        }

        // TODO consider inherited fields and methods
        return false;
    }
    @Override
    public List<InternalConstructorDefinition> getConstructors() {
        return Arrays.stream(clazz.getConstructors())
                .map((c) -> toInternalConstructorDefinition(c))
                .collect(Collectors.toList());
    }

    @Override
    public TypeDefinition getSuperclass() {
        throw new UnsupportedOperationException();
    }


    @Override
    public TypeUsage getFieldType(String fieldName) {
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName)) {
                    return ReflectionTypeDefinitionFactory.toTypeUsage(field.getType(), resolver);
            }
        }

        List<Method> methods = new LinkedList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(fieldName)) {
                    methods.add(method);
            }
        }
        if (!methods.isEmpty()) {
            return this.typeFor(methods, resolver);
        }

        // TODO consider inherited fields and methods
        throw new UnsupportedOperationException(fieldName);
    }

    @Override
    public TypeUsage getFieldTypeFromJvmSignature(String jvmSignature) {
        String[] parts = jvmSignature.split(":");
        if (parts.length != 2) {
            throw new IllegalStateException("invalid jvmSignature " + jvmSignature);
        }
        String[] parts2 = parts[0].split("/");
        return getFieldType(parts2[1]);
    }

    @Override
    public Symbol getFieldOnInstance(String fieldName, Symbol instance) {
        return internalGetField(fieldName, instance);
    }


    /**
     * Instance null means get static fields.
     */
    private Symbol internalGetField(String fieldName, Symbol instance) {
        boolean isStatic = instance == null;
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName) && Modifier.isStatic(field.getModifiers()) == isStatic) {
                ReflectionBasedField rbf = new ReflectionBasedField(field, resolver);
                return rbf;
            }
        }
        List<Method> matchingMethods = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(fieldName) && Modifier.isStatic(method.getModifiers()) == isStatic){
                matchingMethods.add(method);
            }
        }
        if (matchingMethods.isEmpty()) {
            // TODO improve the error returned
            throw new UnsolvedSymbolException(fieldName);
        } else {
            ReflectionBasedSetOfOverloadedMethods rbsoom = new ReflectionBasedSetOfOverloadedMethods(matchingMethods, instance, resolver);
            return rbsoom;
        }
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunction(String methodName, List<ActualParamNode> actualParams) {
        Optional<Method> res = ReflectionBasedMethodResolution.findMethodAmongActualParams(methodName, actualParams, resolver, Arrays.asList(clazz.getMethods()));
        if (res.isPresent()) {
            return Optional.of(toInternalFunctionDefinition(res.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<InternalFunctionDefinition> findFunctionFromJvmSignature(String jvmSignature) {
        Optional<Method> res = ReflectionBasedMethodResolution.findMethodByJvmSignature(jvmSignature);
        if (res.isPresent()) {
            return Optional.of(toInternalFunctionDefinition(res.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<ReferenceTypeUsage> getAllAncestors() {
        List<ReferenceTypeUsage> ancestors = new ArrayList<>();
        if (clazz.getSuperclass() != null) {
            ReferenceTypeUsage superTypeDefinition = toReferenceTypeUsage(clazz.getSuperclass(), clazz.getGenericSuperclass());
            ancestors.add(superTypeDefinition);
            ancestors.addAll(superTypeDefinition.getAllAncestors());
        }
        int i = 0;
        for (Class<?> interfaze : clazz.getInterfaces()) {
            Type genericInterfaze = clazz.getGenericInterfaces()[i];
            ReferenceTypeUsage superTypeDefinition = toReferenceTypeUsage(interfaze, genericInterfaze);
            ancestors.add(superTypeDefinition);
            ancestors.addAll(superTypeDefinition.getAllAncestors());
            i++;
        }
        return ancestors;
    }

    private ReferenceTypeUsage toReferenceTypeUsage(Class<?> clazz, Type type) {
        TypeDefinition typeDefinition = new ReflectionBasedTypeDefinition(clazz, resolver);
        ReferenceTypeUsage referenceTypeUsage = new ReferenceTypeUsage(typeDefinition);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            for (int tp=0;tp<clazz.getTypeParameters().length;tp++) {
                TypeVariable<? extends Class<?>> typeVariable = clazz.getTypeParameters()[tp];
                Type parameterType = parameterizedType.getActualTypeArguments()[tp];
                referenceTypeUsage.getTypeParameterValues().add(typeVariable.getName(), toTypeUsage(parameterType, resolver));
            }
        }
        return referenceTypeUsage;
    }

}
