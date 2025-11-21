package com.jstl;

import com.jstl.native.LibJstl;
import jnr.ffi.Pointer;

/**
 * Off-heap HashMap backed by C++ std::unordered_map via JNR.
 *
 * Memory is allocated outside the Java heap using native C++ STL,
 * avoiding GC overhead for large collections.
 *
 * Usage:
 * <pre>
 * try (OffHeapHashMap map = new OffHeapHashMap()) {
 *     map.put(1, 100);
 *     long value = map.get(1);
 * }
 * </pre>
 */
public class OffHeapHashMap implements AutoCloseable {

    private static final LibJstl LIB = LibJstl.INSTANCE;
    private Pointer handle;
    private boolean closed = false;

    /**
     * Create a new off-heap HashMap
     */
    public OffHeapHashMap() {
        this.handle = LIB.jstl_hashmap_create();
        if (handle == null) {
            throw new OutOfMemoryError("Failed to create native HashMap");
        }
    }

    /**
     * Put a key-value pair
     */
    public void put(long key, long value) {
        ensureOpen();
        LIB.jstl_hashmap_put(handle, key, value);
    }

    /**
     * Get value by key (returns 0 if not found)
     */
    public long get(long key) {
        ensureOpen();
        return LIB.jstl_hashmap_get(handle, key);
    }

    /**
     * Check if key exists
     */
    public boolean containsKey(long key) {
        ensureOpen();
        return LIB.jstl_hashmap_contains_key(handle, key) != 0;
    }

    /**
     * Remove entry by key
     */
    public void remove(long key) {
        ensureOpen();
        LIB.jstl_hashmap_remove(handle, key);
    }

    /**
     * Get number of entries
     */
    public int size() {
        ensureOpen();
        return (int) LIB.jstl_hashmap_size(handle);
    }

    /**
     * Remove all entries
     */
    public void clear() {
        ensureOpen();
        LIB.jstl_hashmap_clear(handle);
    }

    /**
     * Check if empty
     */
    public boolean isEmpty() {
        ensureOpen();
        return LIB.jstl_hashmap_is_empty(handle) != 0;
    }

    @Override
    public void close() {
        if (!closed && handle != null) {
            LIB.jstl_hashmap_destroy(handle);
            handle = null;
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("HashMap has been closed");
        }
    }

    @Override
    public String toString() {
        if (closed) return "OffHeapHashMap[closed]";
        return "OffHeapHashMap[size=" + size() + "]";
    }
}
