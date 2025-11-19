#ifndef JSTL_HASHMAP_H
#define JSTL_HASHMAP_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handle for HashMap
typedef void* jstl_hashmap_t;

// Iterator handle
typedef void* jstl_hashmap_iterator_t;

// Key-value pair
typedef struct {
    int64_t key;
    int64_t value;
} jstl_hashmap_entry_t;

// Create a new HashMap
jstl_hashmap_t jstl_hashmap_create();

// Destroy a HashMap and free all memory
void jstl_hashmap_destroy(jstl_hashmap_t map);

// Put a key-value pair
void jstl_hashmap_put(jstl_hashmap_t map, int64_t key, int64_t value);

// Get value by key (returns 0 if not found)
int64_t jstl_hashmap_get(jstl_hashmap_t map, int64_t key);

// Check if key exists
int jstl_hashmap_contains_key(jstl_hashmap_t map, int64_t key);

// Remove a key
void jstl_hashmap_remove(jstl_hashmap_t map, int64_t key);

// Get size
size_t jstl_hashmap_size(jstl_hashmap_t map);

// Clear all entries
void jstl_hashmap_clear(jstl_hashmap_t map);

// Check if empty
int jstl_hashmap_is_empty(jstl_hashmap_t map);

// Create iterator
jstl_hashmap_iterator_t jstl_hashmap_iterator_create(jstl_hashmap_t map);

// Check if iterator has next
int jstl_hashmap_iterator_has_next(jstl_hashmap_iterator_t iter);

// Get next entry
jstl_hashmap_entry_t jstl_hashmap_iterator_next(jstl_hashmap_iterator_t iter);

// Destroy iterator
void jstl_hashmap_iterator_destroy(jstl_hashmap_iterator_t iter);

#ifdef __cplusplus
}
#endif

#endif // JSTL_HASHMAP_H
