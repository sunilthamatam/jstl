package com.jstl.internal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Panama FFM bindings for HashMap native functions
 */
public class NativeHashMap {
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup SYMBOL_LOOKUP;

    // Function descriptors
    private static final FunctionDescriptor CREATE_DESC = FunctionDescriptor.of(ValueLayout.ADDRESS);
    private static final FunctionDescriptor DESTROY_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor PUT_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor GET_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor CONTAINS_KEY_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor REMOVE_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor SIZE_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
    private static final FunctionDescriptor CLEAR_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor IS_EMPTY_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);

    // Method handles
    public static final MethodHandle CREATE;
    public static final MethodHandle DESTROY;
    public static final MethodHandle PUT;
    public static final MethodHandle GET;
    public static final MethodHandle CONTAINS_KEY;
    public static final MethodHandle REMOVE;
    public static final MethodHandle SIZE;
    public static final MethodHandle CLEAR;
    public static final MethodHandle IS_EMPTY;

    static {
        NativeLoader.loadLibrary();
        SYMBOL_LOOKUP = SymbolLookup.loaderLookup();

        try {
            CREATE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_create").orElseThrow(),
                CREATE_DESC
            );
            DESTROY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_destroy").orElseThrow(),
                DESTROY_DESC
            );
            PUT = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_put").orElseThrow(),
                PUT_DESC
            );
            GET = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_get").orElseThrow(),
                GET_DESC
            );
            CONTAINS_KEY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_contains_key").orElseThrow(),
                CONTAINS_KEY_DESC
            );
            REMOVE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_remove").orElseThrow(),
                REMOVE_DESC
            );
            SIZE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_size").orElseThrow(),
                SIZE_DESC
            );
            CLEAR = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_clear").orElseThrow(),
                CLEAR_DESC
            );
            IS_EMPTY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_hashmap_is_empty").orElseThrow(),
                IS_EMPTY_DESC
            );
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
