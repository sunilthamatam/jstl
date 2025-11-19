package com.jstl;

import com.jstl.internal.NativeArrayList;
import java.lang.foreign.MemorySegment;

/**
 * Off-heap ArrayList backed by C++ std::vector.
 * Memory is allocated outside the Java heap, avoiding GC overhead.
 *
 * Currently supports long values (including pointers to objects).
 * Auto-closeable to ensure native memory is freed.
 */
public class OffHeapArrayList implements AutoCloseable {
    private final MemorySegment handle;
    private boolean closed = false;

    /**
     * Create a new off-heap ArrayList
     */
    public OffHeapArrayList() {
        try {
            this.handle = (MemorySegment) NativeArrayList.CREATE.invoke();
            if (handle == null || handle.address() == 0) {
                throw new OutOfMemoryError("Failed to create native ArrayList");
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create ArrayList", e);
        }
    }

    /**
     * Add an element to the end of the list
     */
    public void add(long value) {
        ensureOpen();
        try {
            NativeArrayList.ADD.invoke(handle, value);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to add element", e);
        }
    }

    /**
     * Get element at the specified index
     */
    public long get(int index) {
        ensureOpen();
        try {
            return (long) NativeArrayList.GET.invoke(handle, (long) index);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get element", e);
        }
    }

    /**
     * Set element at the specified index
     */
    public void set(int index, long value) {
        ensureOpen();
        try {
            NativeArrayList.SET.invoke(handle, (long) index, value);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to set element", e);
        }
    }

    /**
     * Remove element at the specified index
     */
    public void remove(int index) {
        ensureOpen();
        try {
            NativeArrayList.REMOVE.invoke(handle, (long) index);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to remove element", e);
        }
    }

    /**
     * Get the number of elements in the list
     */
    public int size() {
        ensureOpen();
        try {
            return ((Long) NativeArrayList.SIZE.invoke(handle)).intValue();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get size", e);
        }
    }

    /**
     * Remove all elements from the list
     */
    public void clear() {
        ensureOpen();
        try {
            NativeArrayList.CLEAR.invoke(handle);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to clear", e);
        }
    }

    /**
     * Check if the list is empty
     */
    public boolean isEmpty() {
        ensureOpen();
        try {
            return ((Integer) NativeArrayList.IS_EMPTY.invoke(handle)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to check if empty", e);
        }
    }

    /**
     * Get the current capacity (allocated storage)
     */
    public int capacity() {
        ensureOpen();
        try {
            return ((Long) NativeArrayList.CAPACITY.invoke(handle)).intValue();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get capacity", e);
        }
    }

    /**
     * Reserve capacity for at least the specified number of elements
     */
    public void reserve(int capacity) {
        ensureOpen();
        try {
            NativeArrayList.RESERVE.invoke(handle, (long) capacity);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to reserve capacity", e);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            try {
                NativeArrayList.DESTROY.invoke(handle);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to destroy ArrayList", e);
            }
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ArrayList has been closed");
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
            return "OffHeapArrayList[closed]";
        }
        StringBuilder sb = new StringBuilder("OffHeapArrayList[");
        int size = size();
        for (int i = 0; i < size && i < 100; i++) {
            if (i > 0) sb.append(", ");
            sb.append(get(i));
        }
        if (size > 100) {
            sb.append(", ... (").append(size - 100).append(" more)");
        }
        sb.append("]");
        return sb.toString();
    }
}
