package com.jstl.internal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Panama FFM bindings for ArrayList native functions
 */
public class NativeArrayList {
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup SYMBOL_LOOKUP;

    // Function descriptors
    private static final FunctionDescriptor CREATE_DESC = FunctionDescriptor.of(ValueLayout.ADDRESS);
    private static final FunctionDescriptor DESTROY_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor ADD_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor GET_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor SET_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor REMOVE_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);
    private static final FunctionDescriptor SIZE_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
    private static final FunctionDescriptor CLEAR_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor IS_EMPTY_DESC = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final FunctionDescriptor CAPACITY_DESC = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
    private static final FunctionDescriptor RESERVE_DESC = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG);

    // Method handles
    public static final MethodHandle CREATE;
    public static final MethodHandle DESTROY;
    public static final MethodHandle ADD;
    public static final MethodHandle GET;
    public static final MethodHandle SET;
    public static final MethodHandle REMOVE;
    public static final MethodHandle SIZE;
    public static final MethodHandle CLEAR;
    public static final MethodHandle IS_EMPTY;
    public static final MethodHandle CAPACITY;
    public static final MethodHandle RESERVE;

    static {
        NativeLoader.loadLibrary();
        SYMBOL_LOOKUP = SymbolLookup.loaderLookup();

        try {
            CREATE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_create").orElseThrow(),
                CREATE_DESC
            );
            DESTROY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_destroy").orElseThrow(),
                DESTROY_DESC
            );
            ADD = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_add").orElseThrow(),
                ADD_DESC
            );
            GET = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_get").orElseThrow(),
                GET_DESC
            );
            SET = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_set").orElseThrow(),
                SET_DESC
            );
            REMOVE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_remove").orElseThrow(),
                REMOVE_DESC
            );
            SIZE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_size").orElseThrow(),
                SIZE_DESC
            );
            CLEAR = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_clear").orElseThrow(),
                CLEAR_DESC
            );
            IS_EMPTY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_is_empty").orElseThrow(),
                IS_EMPTY_DESC
            );
            CAPACITY = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_capacity").orElseThrow(),
                CAPACITY_DESC
            );
            RESERVE = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("jstl_arraylist_reserve").orElseThrow(),
                RESERVE_DESC
            );
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
