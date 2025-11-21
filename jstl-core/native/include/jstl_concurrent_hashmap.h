#ifndef JSTL_CONCURRENT_HASHMAP_H
#define JSTL_CONCURRENT_HASHMAP_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handle for ConcurrentHashMap
typedef void* jstl_concurrent_hashmap_t;

// Create a new ConcurrentHashMap
jstl_concurrent_hashmap_t jstl_concurrent_hashmap_create();

// Destroy a ConcurrentHashMap and free all memory
void jstl_concurrent_hashmap_destroy(jstl_concurrent_hashmap_t map);

// Put a key-value pair (thread-safe)
void jstl_concurrent_hashmap_put(jstl_concurrent_hashmap_t map, int64_t key, int64_t value);

// Get value by key (thread-safe, returns 0 if not found)
int64_t jstl_concurrent_hashmap_get(jstl_concurrent_hashmap_t map, int64_t key);

// Check if key exists (thread-safe)
int jstl_concurrent_hashmap_contains_key(jstl_concurrent_hashmap_t map, int64_t key);

// Remove a key (thread-safe)
void jstl_concurrent_hashmap_remove(jstl_concurrent_hashmap_t map, int64_t key);

// Get size (thread-safe)
size_t jstl_concurrent_hashmap_size(jstl_concurrent_hashmap_t map);

// Clear all entries (thread-safe)
void jstl_concurrent_hashmap_clear(jstl_concurrent_hashmap_t map);

// Check if empty (thread-safe)
int jstl_concurrent_hashmap_is_empty(jstl_concurrent_hashmap_t map);

// Atomic put if absent (returns 1 if inserted, 0 if already exists)
int jstl_concurrent_hashmap_put_if_absent(jstl_concurrent_hashmap_t map, int64_t key, int64_t value);

#ifdef __cplusplus
}
#endif

#endif // JSTL_CONCURRENT_HASHMAP_H
