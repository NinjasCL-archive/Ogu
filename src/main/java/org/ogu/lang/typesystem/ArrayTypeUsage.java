package org.ogu.lang.typesystem;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.virtual.ArrayLength;
import org.ogu.lang.symbols.Symbol;

import java.util.Map;
import java.util.Optional;

/**
 * Created by ediaz on 10/31/16.
 */
public class ArrayTypeUsage implements TypeUsage {

    private static String LENGTH_FIELD_NAME = "length";

    private TypeUsage componentType;

    public ArrayTypeUsage(TypeUsage componentType) {
        this.componentType = componentType;
    }

    public TypeUsage getComponentType() {
        return componentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayTypeUsage that = (ArrayTypeUsage) o;

        if (!componentType.equals(that.componentType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return componentType.hashCode();
    }


    @Override
    public boolean isArray() { return true; }

    @Override
    public boolean isReference() { return true;  }

    @Override
    public ArrayTypeUsage asArrayTypeUsage() { return this; }


    @Override
    public JvmType jvmType() {
        return new JvmType("[" + componentType.jvmType().getSignature());
    }


    @Override
    public boolean hasInstanceField(String fieldName, Symbol instance) {
        return fieldName.equals(LENGTH_FIELD_NAME);
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        if (fieldName.equals(LENGTH_FIELD_NAME)) {
            return new ArrayLength(instance);
        } else {
            throw new IllegalArgumentException("An array has no field named " + fieldName);
        }
    }

    @Override
    public Optional<Invocable> getFunction(String method) {
        return Optional.empty();
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        if (type.isArray()) {
            return componentType.equals(type.asArrayTypeUsage().getComponentType());
        } else {
            return type.isReferenceTypeUsage() && type.asReferenceTypeUsage().getQualifiedName().equals(Object.class.getCanonicalName());
        }
    }

    @Override
    public String toString() {
        return "ArrayTypeUsage{" +
                "componentType=" + componentType +
                '}';
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return this;
    }

    @Override
    public boolean sameType(TypeUsage other) {
        if (!other.isArray()) {
            return false;
        }
        return this.getComponentType().sameType(other.asArrayTypeUsage().getComponentType());
    }

    @Override
    public String describe() {
        return "array of " + getComponentType().describe();
    }

}


