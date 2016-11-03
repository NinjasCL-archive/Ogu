package org.ogu.lang.codegen.jvm;

import org.objectweb.asm.Opcodes;

/**
 * Created by ediaz on 10/31/16.
 */

public class JvmType {

    public static final JvmType DOUBLE = new JvmType("D");
    public static final JvmType FLOAT = new JvmType("F");
    public static final JvmType CHAR = new JvmType("C");
    public static final JvmType BOOLEAN = new JvmType("Z");
    public static final JvmType BYTE = new JvmType("B");
    public static final JvmType SHORT = new JvmType("S");
    public static final JvmType INT = new JvmType("I");
    public static final JvmType LONG = new JvmType("J");
    public static final JvmType VOID = new JvmType("V");

    public boolean isStoredInInt() {
        return this.equals(BYTE) || this.equals(SHORT) || this.equals(INT);
    }

    private String signature;

    public String getSignature() {
        return signature;
    }

    public JvmType(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JvmType jvmType = (JvmType) o;

        if (!signature.equals(jvmType.signature)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "JvmType{" +
                "signature='" + signature + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    public JvmTypeCategory typeCategory() {
        if (signature.startsWith("L")){
            return JvmTypeCategory.REFERENCE;
        }

        switch (signature) {
            case "Z":
            case "B":
            case "S":
            case "C":
            case "I":
                return JvmTypeCategory.INT;
            case"J":
                return JvmTypeCategory.LONG;
            case "F":
                return JvmTypeCategory.FLOAT;
            case "D":
                return JvmTypeCategory.DOUBLE;
            default:
                throw new UnsupportedOperationException(signature);
        }
    }

    public String getDescriptor() {
        // TODO differentiate
        return signature;
    }

    public String getInternalName() {
        if (!signature.startsWith("L")) {
            throw new UnsupportedOperationException();
        }
        return signature.substring(1, signature.length() - 1);
    }

    public int returnOpcode() {
        if (signature.equals("J")){
            return Opcodes.LRETURN;
        } else if (signature.equals("V")) {
            return Opcodes.RETURN;
        } else if (signature.equals("F")) {
            return Opcodes.FRETURN;
        } else if (signature.equals("D")) {
            return Opcodes.DRETURN;
        } else if (signature.equals("B")||signature.equals("S")||signature.equals("C")||signature.equals("I")||signature.equals("Z")) {
            return Opcodes.IRETURN;
        } else {
            return Opcodes.ARETURN;
        }
    }

    public boolean isAssignableBy(JvmType other) {
        return this.equals(other);
    }

    public boolean isPrimitive() {
        if (signature.equals("J")){
            return true;
        } else if (signature.equals("V")) {
            return true;
        } else if (signature.equals("F")) {
            return true;
        } else if (signature.equals("D")) {
            return true;
        } else if (signature.equals("B")||signature.equals("S")||signature.equals("C")||signature.equals("I")||signature.equals("Z")) {
            return true;
        } else {
            return false;
        }
    }

}
