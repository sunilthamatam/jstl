package com.jstl;

import com.jstl.native.LibJstl;
import jnr.ffi.Pointer;

/**
 * Off-heap ArrayList backed by C++ std::vector via JNR.
 *
 * Memory is allocated outside the Java heap using native C++ STL,
 * avoiding GC overhead for large collections.
 *
 * Usage:
 * <pre>
 * try (OffHeapArrayList list = new OffHeapArrayList()) {
 *     list.add(100);
 *     list.add(200);
 *     long value = list.get(0);
 * }
 * </pre>
 */
public class OffHeapArrayList implements AutoCloseable {

    private static final LibJstl LIB = LibJstl.INSTANCE;
    private Pointer handle;
    private boolean closed = false;

    /**
     * Create a new off-heap ArrayList
     */
    public OffHeapArrayList() {
        this.handle = LIB.jstl_arraylist_create();
        if (handle == null) {
            throw new OutOfMemoryError("Failed to create native ArrayList");
        }
    }

    /**
     * Add an element to the end of the list
     */
    public void add(long value) {
        ensureOpen();
        LIB.jstl_arraylist_add(handle, value);
    }

    /**
     * Get element at the specified index
     */
    public long get(int index) {
        ensureOpen();
        return LIB.jstl_arraylist_get(handle, index);
    }

    /**
     * Set element at the specified index
     */
    public void set(int index, long value) {
        ensureOpen();
        LIB.jstl_arraylist_set(handle, index, value);
    }

    /**
     * Remove element at the specified index
     */
    public void remove(int index) {
        ensureOpen();
        LIB.jstl_arraylist_remove(handle, index);
    }

    /**
     * Get the number of elements
     */
    public int size() {
        ensureOpen();
        return (int) LIB.jstl_arraylist_size(handle);
    }

    /**
     * Remove all elements
     */
    public void clear() {
        ensureOpen();
        LIB.jstl_arraylist_clear(handle);
    }

    /**
     * Check if empty
     */
    public boolean isEmpty() {
        ensureOpen();
        return LIB.jstl_arraylist_is_empty(handle) != 0;
    }

    /**
     * Get current capacity
     */
    public int capacity() {
        ensureOpen();
        return (int) LIB.jstl_arraylist_capacity(handle);
    }

    /**
     * Reserve capacity
     */
    public void reserve(int capacity) {
        ensureOpen();
        LIB.jstl_arraylist_reserve(handle, capacity);
    }

    @Override
    public void close() {
        if (!closed && handle != null) {
            LIB.jstl_arraylist_destroy(handle);
            handle = null;
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ArrayList has been closed");
        }
    }

    @Override
    public String toString() {
        if (closed) return "OffHeapArrayList[closed]";
        return "OffHeapArrayList[size=" + size() + "]";
    }
}
