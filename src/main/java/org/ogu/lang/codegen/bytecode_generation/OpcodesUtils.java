package org.ogu.lang.codegen.bytecode_generation;

import org.ogu.lang.codegen.jvm.JvmType;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ALOAD;

/**
 * Created by ediaz on 11/2/16.
 */
public class OpcodesUtils {

    private OpcodesUtils() {
        // prevent instantiation
    }

    public static int returnTypeFor(JvmType jvmType) {
        switch (jvmType.getDescriptor()) {
            case "Z":
            case "B":
            case "S":
            case "C":
            case "I":
                // used for boolean, byte, short, char, or int
                return IRETURN;
            case "J":
                return LRETURN;
            case "F":
                return FRETURN;
            case "D":
                return DRETURN;
            case "V":
                return RETURN;
            default:
                return ARETURN;
        }
    }

    public static int loadTypeFor(JvmType jvmType) {
        switch (jvmType.getDescriptor()) {
            case "Z":
            case "B":
            case "S":
            case "C":
            case "I":
                // used for boolean, byte, short, char, or int
                return ILOAD;
            case "J":
                return LLOAD;
            case "F":
                return FLOAD;
            case "D":
                return DLOAD;
            default:
                return ALOAD;
        }
    }
}
