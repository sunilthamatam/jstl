package com.jstl;

import com.jstl.native.LibJstl;
import jnr.ffi.Pointer;

/**
 * Off-heap HashSet backed by C++ std::unordered_set via JNR.
 *
 * Memory is allocated outside the Java heap using native C++ STL,
 * avoiding GC overhead for large collections.
 *
 * Usage:
 * <pre>
 * try (OffHeapHashSet set = new OffHeapHashSet()) {
 *     set.add(100);
 *     boolean exists = set.contains(100);
 * }
 * </pre>
 */
public class OffHeapHashSet implements AutoCloseable {

    private static final LibJstl LIB = LibJstl.INSTANCE;
    private Pointer handle;
    private boolean closed = false;

    /**
     * Create a new off-heap HashSet
     */
    public OffHeapHashSet() {
        this.handle = LIB.jstl_hashset_create();
        if (handle == null) {
            throw new OutOfMemoryError("Failed to create native HashSet");
        }
    }

    /**
     * Add an element
     * @return true if added, false if already exists
     */
    public boolean add(long value) {
        ensureOpen();
        return LIB.jstl_hashset_add(handle, value) != 0;
    }

    /**
     * Check if element exists
     */
    public boolean contains(long value) {
        ensureOpen();
        return LIB.jstl_hashset_contains(handle, value) != 0;
    }

    /**
     * Remove an element
     * @return true if removed, false if didn't exist
     */
    public boolean remove(long value) {
        ensureOpen();
        return LIB.jstl_hashset_remove(handle, value) != 0;
    }

    /**
     * Get number of elements
     */
    public int size() {
        ensureOpen();
        return (int) LIB.jstl_hashset_size(handle);
    }

    /**
     * Remove all elements
     */
    public void clear() {
        ensureOpen();
        LIB.jstl_hashset_clear(handle);
    }

    /**
     * Check if empty
     */
    public boolean isEmpty() {
        ensureOpen();
        return LIB.jstl_hashset_is_empty(handle) != 0;
    }

    @Override
    public void close() {
        if (!closed && handle != null) {
            LIB.jstl_hashset_destroy(handle);
            handle = null;
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("HashSet has been closed");
        }
    }

    @Override
    public String toString() {
        if (closed) return "OffHeapHashSet[closed]";
        return "OffHeapHashSet[size=" + size() + "]";
    }
}
