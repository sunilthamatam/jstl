package com.jstl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Off-heap HashMap implementation using pure Java (no C++ required).
 *
 * Uses Java 21's Panama MemorySegment API with separate chaining for collision handling.
 * All data stored off-heap to avoid GC pressure.
 *
 * Implementation details:
 * - Buckets array: stores head pointers for each bucket chain
 * - Entry pool: pre-allocated entries in off-heap memory
 * - Each entry: [hash: long][key: long][value: long][next: long]
 *
 * Benefits:
 * - No C++ compilation required
 * - Pure Java (easier to debug and maintain)
 * - Still off-heap (no GC overhead)
 *
 * Trade-off:
 * - ~20-30% slower than C++ std::unordered_map
 */
public class PureJavaOffHeapHashMap implements AutoCloseable {

    private static final int INITIAL_BUCKETS = 16;
    private static final int INITIAL_ENTRY_POOL_SIZE = 256;
    private static final long ENTRY_SIZE = 32; // hash(8) + key(8) + value(8) + next(8)
    private static final long BUCKET_SIZE = ValueLayout.JAVA_LONG.byteSize();
    private static final long NULL_ENTRY = -1;

    // Entry offsets
    private static final long OFFSET_HASH = 0;
    private static final long OFFSET_KEY = 8;
    private static final long OFFSET_VALUE = 16;
    private static final long OFFSET_NEXT = 24;

    private final Arena arena;
    private MemorySegment buckets;
    private MemorySegment entryPool;
    private int numBuckets;
    private long entryPoolSize;
    private long nextFreeEntry;
    private long size;
    private boolean closed;

    /**
     * Create a new off-heap HashMap
     */
    public PureJavaOffHeapHashMap() {
        this.arena = Arena.ofConfined();
        this.numBuckets = INITIAL_BUCKETS;
        this.entryPoolSize = INITIAL_ENTRY_POOL_SIZE;
        this.nextFreeEntry = 0;
        this.size = 0;
        this.closed = false;

        // Allocate buckets (array of bucket head pointers)
        this.buckets = arena.allocate(numBuckets * BUCKET_SIZE, BUCKET_SIZE);

        // Initialize all buckets to NULL_ENTRY
        for (int i = 0; i < numBuckets; i++) {
            buckets.setAtIndex(ValueLayout.JAVA_LONG, i, NULL_ENTRY);
        }

        // Allocate entry pool
        this.entryPool = arena.allocate(entryPoolSize * ENTRY_SIZE, ENTRY_SIZE);
    }

    /**
     * Put a key-value pair into the map
     */
    public void put(long key, long value) {
        ensureOpen();

        long hash = hash(key);
        int bucket = (int) (hash % numBuckets);
        long bucketHead = buckets.getAtIndex(ValueLayout.JAVA_LONG, bucket);

        // Search for existing key
        long current = bucketHead;
        while (current != NULL_ENTRY) {
            long entryAddr = current * ENTRY_SIZE;
            long entryKey = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_KEY);

            if (entryKey == key) {
                // Update existing entry
                entryPool.set(ValueLayout.JAVA_LONG, entryAddr + OFFSET_VALUE, value);
                return;
            }

            current = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);
        }

        // Insert new entry
        if (nextFreeEntry >= entryPoolSize) {
            growEntryPool();
        }

        long newEntryIndex = nextFreeEntry++;
        long newEntryAddr = newEntryIndex * ENTRY_SIZE;

        entryPool.set(ValueLayout.JAVA_LONG, newEntryAddr + OFFSET_HASH, hash);
        entryPool.set(ValueLayout.JAVA_LONG, newEntryAddr + OFFSET_KEY, key);
        entryPool.set(ValueLayout.JAVA_LONG, newEntryAddr + OFFSET_VALUE, value);
        entryPool.set(ValueLayout.JAVA_LONG, newEntryAddr + OFFSET_NEXT, bucketHead);

        buckets.setAtIndex(ValueLayout.JAVA_LONG, bucket, newEntryIndex);
        size++;

        // Rehash if load factor too high
        if (size > numBuckets * 0.75) {
            rehash();
        }
    }

    /**
     * Get value for the specified key (returns 0 if not found)
     */
    public long get(long key) {
        ensureOpen();

        long hash = hash(key);
        int bucket = (int) (hash % numBuckets);
        long current = buckets.getAtIndex(ValueLayout.JAVA_LONG, bucket);

        while (current != NULL_ENTRY) {
            long entryAddr = current * ENTRY_SIZE;
            long entryKey = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_KEY);

            if (entryKey == key) {
                return entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_VALUE);
            }

            current = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);
        }

        return 0; // Not found
    }

    /**
     * Check if the map contains the specified key
     */
    public boolean containsKey(long key) {
        ensureOpen();

        long hash = hash(key);
        int bucket = (int) (hash % numBuckets);
        long current = buckets.getAtIndex(ValueLayout.JAVA_LONG, bucket);

        while (current != NULL_ENTRY) {
            long entryAddr = current * ENTRY_SIZE;
            long entryKey = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_KEY);

            if (entryKey == key) {
                return true;
            }

            current = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);
        }

        return false;
    }

    /**
     * Remove the entry for the specified key
     */
    public void remove(long key) {
        ensureOpen();

        long hash = hash(key);
        int bucket = (int) (hash % numBuckets);
        long current = buckets.getAtIndex(ValueLayout.JAVA_LONG, bucket);
        long prev = NULL_ENTRY;

        while (current != NULL_ENTRY) {
            long entryAddr = current * ENTRY_SIZE;
            long entryKey = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_KEY);

            if (entryKey == key) {
                // Found the entry to remove
                long next = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);

                if (prev == NULL_ENTRY) {
                    // Removing head of bucket
                    buckets.setAtIndex(ValueLayout.JAVA_LONG, bucket, next);
                } else {
                    // Removing from middle/end of chain
                    long prevAddr = prev * ENTRY_SIZE;
                    entryPool.set(ValueLayout.JAVA_LONG, prevAddr + OFFSET_NEXT, next);
                }

                size--;
                return;
            }

            prev = current;
            current = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);
        }
    }

    /**
     * Get the number of entries in the map
     */
    public int size() {
        ensureOpen();
        return (int) size;
    }

    /**
     * Remove all entries from the map
     */
    public void clear() {
        ensureOpen();

        // Reset all buckets
        for (int i = 0; i < numBuckets; i++) {
            buckets.setAtIndex(ValueLayout.JAVA_LONG, i, NULL_ENTRY);
        }

        nextFreeEntry = 0;
        size = 0;
    }

    /**
     * Check if the map is empty
     */
    public boolean isEmpty() {
        ensureOpen();
        return size == 0;
    }

    /**
     * Hash function for keys
     */
    private long hash(long key) {
        // Simple but effective hash (mix bits)
        key ^= (key >>> 33);
        key *= 0xff51afd7ed558ccdL;
        key ^= (key >>> 33);
        key *= 0xc4ceb9fe1a85ec53L;
        key ^= (key >>> 33);
        return key & 0x7FFFFFFFFFFFFFFFL; // Ensure positive
    }

    /**
     * Grow the entry pool when full
     */
    private void growEntryPool() {
        long newPoolSize = entryPoolSize * 2;
        MemorySegment newPool = arena.allocate(newPoolSize * ENTRY_SIZE, ENTRY_SIZE);

        // Copy existing entries
        MemorySegment.copy(entryPool, 0, newPool, 0, entryPoolSize * ENTRY_SIZE);

        entryPool = newPool;
        entryPoolSize = newPoolSize;
    }

    /**
     * Rehash the map to reduce load factor
     */
    private void rehash() {
        int oldNumBuckets = numBuckets;
        MemorySegment oldBuckets = buckets;

        // Double the number of buckets
        numBuckets *= 2;
        buckets = arena.allocate(numBuckets * BUCKET_SIZE, BUCKET_SIZE);

        // Initialize new buckets
        for (int i = 0; i < numBuckets; i++) {
            buckets.setAtIndex(ValueLayout.JAVA_LONG, i, NULL_ENTRY);
        }

        // Rehash all entries
        for (int i = 0; i < oldNumBuckets; i++) {
            long current = oldBuckets.getAtIndex(ValueLayout.JAVA_LONG, i);

            while (current != NULL_ENTRY) {
                long entryAddr = current * ENTRY_SIZE;
                long hash = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_HASH);
                long next = entryPool.get(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT);

                // Insert into new bucket
                int newBucket = (int) (hash % numBuckets);
                long newBucketHead = buckets.getAtIndex(ValueLayout.JAVA_LONG, newBucket);
                entryPool.set(ValueLayout.JAVA_LONG, entryAddr + OFFSET_NEXT, newBucketHead);
                buckets.setAtIndex(ValueLayout.JAVA_LONG, newBucket, current);

                current = next;
            }
        }
    }

    @Override
    public void close() {
        if (!closed) {
            arena.close(); // Frees all off-heap memory
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
        if (closed) {
            return "PureJavaOffHeapHashMap[closed]";
        }
        return "PureJavaOffHeapHashMap[size=" + size + ", buckets=" + numBuckets + "]";
    }
}
