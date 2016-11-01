package org.ogu.lang.codegen.jvm;

import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.*;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class JvmInvocableDefinition {

    protected String ownerInternalName;
    protected String name;
    protected String descriptor;

    protected JvmInvocableDefinition(String ownerInternalName, String name, String descriptor) {
        this.descriptor = descriptor;
        this.name = name;
        this.ownerInternalName = ownerInternalName;
    }

    public String getOwnerInternalName() {
        return ownerInternalName;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public JvmType getParamType(int i) {
        MethodTypeSignature methodTypeSignature = SignatureParser.make().parseMethodSig(descriptor);
        TypeSignature typeSignature = methodTypeSignature.getParameterTypes()[i];
        return toJvmType(typeSignature);
    }

    public String getReturnTypeDescriptor() {
        return descriptor.substring(descriptor.indexOf(')') + 1);
    }

    private JvmType toJvmType(TypeSignature typeSignature) {
        if (typeSignature instanceof LongSignature) {
            return JvmType.LONG;
        } else if (typeSignature instanceof IntSignature) {
            return JvmType.INT;
        } else if (typeSignature instanceof BooleanSignature) {
            return JvmType.BOOLEAN;
        } else if (typeSignature instanceof CharSignature) {
            return JvmType.CHAR;
        } else if (typeSignature instanceof ByteSignature) {
            return JvmType.BYTE;
        } else if (typeSignature instanceof ShortSignature) {
            return JvmType.SHORT;
        } else if (typeSignature instanceof FloatSignature) {
            return JvmType.FLOAT;
        } else if (typeSignature instanceof DoubleSignature) {
            return JvmType.DOUBLE;
        } else if (typeSignature instanceof SimpleClassTypeSignature) {
            SimpleClassTypeSignature simpleClassTypeSignature = (SimpleClassTypeSignature)typeSignature;
            return new JvmType(simpleClassTypeSignature.getName());
        } else if (typeSignature instanceof ClassTypeSignature) {
            ClassTypeSignature classTypeSignature = (ClassTypeSignature) typeSignature;
            if (classTypeSignature.getPath().size() == 1) {
                return toJvmType(classTypeSignature.getPath().get(0));
            }
            throw new UnsupportedOperationException(classTypeSignature.getPath().toString());
        } else if (typeSignature instanceof ArrayTypeSignature) {
            ArrayTypeSignature arrayTypeSignature = (ArrayTypeSignature)typeSignature;
            return new JvmType("[" + toJvmType(arrayTypeSignature.getComponentType()).getDescriptor());
        } else {
            throw new UnsupportedOperationException(typeSignature.getClass().getCanonicalName());
        }
    }
}
