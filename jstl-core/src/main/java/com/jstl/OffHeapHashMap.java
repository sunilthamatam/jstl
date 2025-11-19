package com.jstl;

import com.jstl.internal.NativeHashMap;
import java.lang.foreign.MemorySegment;

/**
 * Off-heap HashMap backed by C++ std::unordered_map.
 * Memory is allocated outside the Java heap, avoiding GC overhead.
 *
 * Currently supports long keys and long values.
 * Auto-closeable to ensure native memory is freed.
 */
public class OffHeapHashMap implements AutoCloseable {
    private final MemorySegment handle;
    private boolean closed = false;

    /**
     * Create a new off-heap HashMap
     */
    public OffHeapHashMap() {
        try {
            this.handle = (MemorySegment) NativeHashMap.CREATE.invoke();
            if (handle == null || handle.address() == 0) {
                throw new OutOfMemoryError("Failed to create native HashMap");
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create HashMap", e);
        }
    }

    /**
     * Put a key-value pair into the map
     */
    public void put(long key, long value) {
        ensureOpen();
        try {
            NativeHashMap.PUT.invoke(handle, key, value);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to put entry", e);
        }
    }

    /**
     * Get the value for the specified key
     * Returns 0 if key not found
     */
    public long get(long key) {
        ensureOpen();
        try {
            return (long) NativeHashMap.GET.invoke(handle, key);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get value", e);
        }
    }

    /**
     * Get the value for the specified key, or return defaultValue if not found
     */
    public long getOrDefault(long key, long defaultValue) {
        ensureOpen();
        if (containsKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    /**
     * Check if the map contains the specified key
     */
    public boolean containsKey(long key) {
        ensureOpen();
        try {
            return ((Integer) NativeHashMap.CONTAINS_KEY.invoke(handle, key)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to check key", e);
        }
    }

    /**
     * Remove the entry for the specified key
     */
    public void remove(long key) {
        ensureOpen();
        try {
            NativeHashMap.REMOVE.invoke(handle, key);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to remove entry", e);
        }
    }

    /**
     * Get the number of entries in the map
     */
    public int size() {
        ensureOpen();
        try {
            return ((Long) NativeHashMap.SIZE.invoke(handle)).intValue();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get size", e);
        }
    }

    /**
     * Remove all entries from the map
     */
    public void clear() {
        ensureOpen();
        try {
            NativeHashMap.CLEAR.invoke(handle);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to clear", e);
        }
    }

    /**
     * Check if the map is empty
     */
    public boolean isEmpty() {
        ensureOpen();
        try {
            return ((Integer) NativeHashMap.IS_EMPTY.invoke(handle)) != 0;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to check if empty", e);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            try {
                NativeHashMap.DESTROY.invoke(handle);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to destroy HashMap", e);
            }
            closed = true;
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("HashMap has been closed");
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
            return "OffHeapHashMap[closed]";
        }
        return "OffHeapHashMap[size=" + size() + "]";
    }
}
