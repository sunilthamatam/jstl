package com.jstl;

import com.jstl.internal.NativeHashSet;
import java.lang.foreign.MemorySegment;

/**
 * Off-heap HashSet backed by C++ std::unordered_set.
 * Memory is allocated outside the Java heap, avoiding GC overhead.
 *
 * Currently supports long values.
 * Auto-closeable to ensure native memory is freed.
 */
public class OffHeapHashSet implements AutoCloseable {
    private final MemorySegment handle;
    private boolean closed = false;

    /**
     * Create a new off-heap HashSet
     */
    public OffHeapHashSet() {
        try {
            this.handle = (MemorySegment) NativeHashSet.CREATE.invoke();
            if (handle == null || handle.address() == 0) {
                throw new OutOfMemoryError("Failed to create native HashSet");
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create HashSet", e);
        }
    }

    /**
     * Add an element to the set
     * @return true if the element was added, false if it already existed
     */
    public boolean add(long value) {
        ensureOpen();
        try {
            return ((Integer) NativeHashSet.ADD.invoke(handle, value)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to add element", e);
        }
    }

    /**
     * Check if the set contains the specified element
     */
    public boolean contains(long value) {
        ensureOpen();
        try {
            return ((Integer) NativeHashSet.CONTAINS.invoke(handle, value)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to check element", e);
        }
    }

    /**
     * Remove the specified element from the set
     * @return true if the element was removed, false if it didn't exist
     */
    public boolean remove(long value) {
        ensureOpen();
        try {
            return ((Integer) NativeHashSet.REMOVE.invoke(handle, value)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to remove element", e);
        }
    }

    /**
     * Get the number of elements in the set
     */
    public int size() {
        ensureOpen();
        try {
            return ((Long) NativeHashSet.SIZE.invoke(handle)).intValue();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get size", e);
        }
    }

    /**
     * Remove all elements from the set
     */
    public void clear() {
        ensureOpen();
        try {
            NativeHashSet.CLEAR.invoke(handle);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to clear", e);
        }
    }

    /**
     * Check if the set is empty
     */
    public boolean isEmpty() {
        ensureOpen();
        try {
            return ((Integer) NativeHashSet.IS_EMPTY.invoke(handle)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to check if empty", e);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            try {
                NativeHashSet.DESTROY.invoke(handle);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to destroy HashSet", e);
            }
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("HashSet has been closed");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public String toString() {
        if (closed) {
            return "OffHeapHashSet[closed]";
        }
        return "OffHeapHashSet[size=" + size() + "]";
    }
}
