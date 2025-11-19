package com.jstl.internal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Panama FFM bindings for HashSet native functions
 */
public class NativeHashSet {
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup SYMBOL_LOOKUP;

    // Function descriptors
    private static final FunctionDescriptor CREATE_DESC = FunctionDescriptor.of(ValueLayout.ADDRESS);
    private static final FunctionDescriptor DESTROY_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor ADD_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor CONTAINS_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor REMOVE_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor SIZE_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
    private static final FunctionDescriptor CLEAR_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor IS_EMPTY_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);

    // Method handles
    public static final MethodHandle CREATE;
    public static final MethodHandle DESTROY;
    public static final MethodHandle ADD;
    public static final MethodHandle CONTAINS;
    public static final MethodHandle REMOVE;
    public static final MethodHandle SIZE;
    public static final MethodHandle CLEAR;
    public static final MethodHandle IS_EMPTY;

    static {
        NativeLoader.loadLibrary();
        SYMBOL_LOOKUP = SymbolLookup.loaderLookup();

        try {
            CREATE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_create").orElseThrow(),
                CREATE_DESC
            );
            DESTROY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_destroy").orElseThrow(),
                DESTROY_DESC
            );
            ADD = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_add").orElseThrow(),
                ADD_DESC
            );
            CONTAINS = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_contains").orElseThrow(),
                CONTAINS_DESC
            );
            REMOVE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_remove").orElseThrow(),
                REMOVE_DESC
            );
            SIZE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_size").orElseThrow(),
                SIZE_DESC
            );
            CLEAR = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_clear").orElseThrow(),
                CLEAR_DESC
            );
            IS_EMPTY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashset_is_empty").orElseThrow(),
                IS_EMPTY_DESC
            );
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
