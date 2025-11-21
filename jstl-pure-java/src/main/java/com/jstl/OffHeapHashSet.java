package com.jstl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Off-heap HashSet implementation using pure Java (no C++ required).
 *
 * Uses Java 21's Panama MemorySegment API with open addressing (linear probing).
 * All data stored off-heap to avoid GC pressure.
 *
 * Implementation details:
 * - Open addressing with linear probing
 * - Each slot: [state: byte][value: long]
 * - States: EMPTY=0, OCCUPIED=1, DELETED=2
 *
 * Benefits:
 * - No C++ compilation required
 * - Pure Java (easier to debug and maintain)
 * - Still off-heap (no GC overhead)
 */
public class OffHeapHashSet implements AutoCloseable {

    private static final int INITIAL_CAPACITY = 16;
    private static final long SLOT_SIZE = 16; // state(8 aligned) + value(8)
    private static final long OFFSET_STATE = 0;
    private static final long OFFSET_VALUE = 8;

    private static final long STATE_EMPTY = 0;
    private static final long STATE_OCCUPIED = 1;
    private static final long STATE_DELETED = 2;

    private final Arena arena;
    private MemorySegment slots;
    private int capacity;
    private long size;
    private boolean closed;

    /**
     * Create a new off-heap HashSet
     */
    public OffHeapHashSet() {
        this.arena = Arena.ofConfined();
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
        this.closed = false;
        this.slots = arena.allocate(capacity * SLOT_SIZE, 8);

        // Initialize all slots to EMPTY
        for (int i = 0; i < capacity; i++) {
            setSlotState(i, STATE_EMPTY);
        }
    }

    /**
     * Add an element to the set
     * @return true if the element was added, false if it already existed
     */
    public boolean add(long value) {
        ensureOpen();

        // Check load factor and resize if needed
        if (size >= capacity * 0.75) {
            resize();
        }

        int index = findSlot(value);
        long state = getSlotState(index);

        if (state == STATE_OCCUPIED) {
            // Already exists
            return false;
        }

        // Insert new element
        setSlotState(index, STATE_OCCUPIED);
        setSlotValue(index, value);
        size++;
        return true;
    }

    /**
     * Check if the set contains the specified element
     */
    public boolean contains(long value) {
        ensureOpen();

        int index = findSlotForLookup(value);
        return index != -1;
    }

    /**
     * Remove the specified element from the set
     * @return true if the element was removed, false if it didn't exist
     */
    public boolean remove(long value) {
        ensureOpen();

        int index = findSlotForLookup(value);
        if (index == -1) {
            return false;
        }

        // Mark as deleted (tombstone)
        setSlotState(index, STATE_DELETED);
        size--;
        return true;
    }

    /**
     * Get the number of elements in the set
     */
    public int size() {
        ensureOpen();
        return (int) size;
    }

    /**
     * Remove all elements from the set
     */
    public void clear() {
        ensureOpen();

        for (int i = 0; i < capacity; i++) {
            setSlotState(i, STATE_EMPTY);
        }
        size = 0;
    }

    /**
     * Check if the set is empty
     */
    public boolean isEmpty() {
        ensureOpen();
        return size == 0;
    }

    /**
     * Find slot for insertion (returns first empty or deleted slot, or existing slot)
     */
    private int findSlot(long value) {
        int hash = hash(value);
        int index = hash % capacity;
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            int probe = (index + i) % capacity;
            long state = getSlotState(probe);

            if (state == STATE_EMPTY) {
                // Found empty slot - use first deleted if available
                return firstDeleted != -1 ? firstDeleted : probe;
            } else if (state == STATE_DELETED) {
                // Remember first deleted slot
                if (firstDeleted == -1) {
                    firstDeleted = probe;
                }
            } else if (state == STATE_OCCUPIED) {
                // Check if this is the value we're looking for
                if (getSlotValue(probe) == value) {
                    return probe; // Already exists
                }
            }
        }

        // Table is full (shouldn't happen with proper load factor)
        return firstDeleted != -1 ? firstDeleted : 0;
    }

    /**
     * Find slot for lookup (returns -1 if not found)
     */
    private int findSlotForLookup(long value) {
        int hash = hash(value);
        int index = hash % capacity;

        for (int i = 0; i < capacity; i++) {
            int probe = (index + i) % capacity;
            long state = getSlotState(probe);

            if (state == STATE_EMPTY) {
                return -1; // Not found
            } else if (state == STATE_OCCUPIED) {
                if (getSlotValue(probe) == value) {
                    return probe; // Found
                }
            }
            // Continue probing for DELETED slots
        }

        return -1; // Not found
    }

    /**
     * Hash function
     */
    private int hash(long value) {
        value ^= (value >>> 33);
        value *= 0xff51afd7ed558ccdL;
        value ^= (value >>> 33);
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= (value >>> 33);
        return (int) (value & 0x7FFFFFFF);
    }

    /**
     * Resize the hash table
     */
    private void resize() {
        int oldCapacity = capacity;
        MemorySegment oldSlots = slots;

        capacity *= 2;
        slots = arena.allocate(capacity * SLOT_SIZE, 8);

        // Initialize new slots
        for (int i = 0; i < capacity; i++) {
            setSlotState(i, STATE_EMPTY);
        }

        // Rehash existing elements
        size = 0;
        for (int i = 0; i < oldCapacity; i++) {
            long state = oldSlots.get(ValueLayout.JAVA_LONG, i * SLOT_SIZE + OFFSET_STATE);
            if (state == STATE_OCCUPIED) {
                long value = oldSlots.get(ValueLayout.JAVA_LONG, i * SLOT_SIZE + OFFSET_VALUE);
                add(value);
            }
        }
    }

    private long getSlotState(int index) {
        return slots.get(ValueLayout.JAVA_LONG, index * SLOT_SIZE + OFFSET_STATE);
    }

    private void setSlotState(int index, long state) {
        slots.set(ValueLayout.JAVA_LONG, index * SLOT_SIZE + OFFSET_STATE, state);
    }

    private long getSlotValue(int index) {
        return slots.get(ValueLayout.JAVA_LONG, index * SLOT_SIZE + OFFSET_VALUE);
    }

    private void setSlotValue(int index, long value) {
        slots.set(ValueLayout.JAVA_LONG, index * SLOT_SIZE + OFFSET_VALUE, value);
    }

    @Override
    public void close() {
        if (!closed) {
            arena.close();
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
        if (closed) {
            return "OffHeapHashSet[closed]";
        }
        return "OffHeapHashSet[size=" + size + ", capacity=" + capacity + "]";
    }
}
