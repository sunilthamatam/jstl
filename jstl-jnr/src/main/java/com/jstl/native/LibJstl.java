package com.jstl.native;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;

/**
 * JNR FFI interface to the native JSTL library (libjstl.so/dylib/dll).
 *
 * This interface defines the C functions exported by the native library.
 * JNR automatically generates the bindings at runtime.
 */
public interface LibJstl {

    // Singleton instance
    LibJstl INSTANCE = LibraryLoader.create(LibJstl.class)
            .search("jstl-jnr/native/build/lib")
            .search("native/build/lib")
            .search("build/lib")
            .search("lib")
            .search(".")
            .load("jstl");

    // ==================== ArrayList ====================

    Pointer jstl_arraylist_create();

    void jstl_arraylist_destroy(Pointer list);

    void jstl_arraylist_add(Pointer list, long value);

    long jstl_arraylist_get(Pointer list, long index);

    void jstl_arraylist_set(Pointer list, long index, long value);

    void jstl_arraylist_remove(Pointer list, long index);

    long jstl_arraylist_size(Pointer list);

    void jstl_arraylist_clear(Pointer list);

    int jstl_arraylist_is_empty(Pointer list);

    long jstl_arraylist_capacity(Pointer list);

    void jstl_arraylist_reserve(Pointer list, long capacity);

    // ==================== HashMap ====================

    Pointer jstl_hashmap_create();

    void jstl_hashmap_destroy(Pointer map);

    void jstl_hashmap_put(Pointer map, long key, long value);

    long jstl_hashmap_get(Pointer map, long key);

    int jstl_hashmap_contains_key(Pointer map, long key);

    void jstl_hashmap_remove(Pointer map, long key);

    long jstl_hashmap_size(Pointer map);

    void jstl_hashmap_clear(Pointer map);

    int jstl_hashmap_is_empty(Pointer map);

    // ==================== HashSet ====================

    Pointer jstl_hashset_create();

    void jstl_hashset_destroy(Pointer set);

    int jstl_hashset_add(Pointer set, long value);

    int jstl_hashset_contains(Pointer set, long value);

    int jstl_hashset_remove(Pointer set, long value);

    long jstl_hashset_size(Pointer set);

    void jstl_hashset_clear(Pointer set);

    int jstl_hashset_is_empty(Pointer set);
}
