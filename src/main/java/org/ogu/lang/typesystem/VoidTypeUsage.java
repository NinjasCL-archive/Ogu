package org.ogu.lang.typesystem;

import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.symbols.Symbol;

import java.util.Map;

/**
 * Created by ediaz on 11/4/16.
 */
public class VoidTypeUsage implements TypeUsage {



    @Override
    public boolean isVoid() {
        return true;
    }

    @Override
    public String toString() {
        return "VoidTypeUsage{}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidTypeUsage;
    }

    @Override
    public int hashCode() {
        return 127;
    }

    @Override
    public <T extends TypeUsage> TypeUsage replaceTypeVariables(Map<String, T> typeParams) {
        return this;
    }

    @Override
    public boolean sameType(TypeUsage other) {
        return other.isVoid();
    }

    @Override
    public JvmType jvmType() {
        return new JvmType("V");
    }

    @Override
    public boolean canBeAssignedTo(TypeUsage type) {
        return false;
    }

    @Override
    public Symbol getInstanceField(String fieldName, Symbol instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String describe() {
        return "void";
    }
}
