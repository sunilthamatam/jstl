#ifndef JSTL_HASHSET_H
#define JSTL_HASHSET_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handle for HashSet
typedef void* jstl_hashset_t;

// Iterator handle
typedef void* jstl_hashset_iterator_t;

// Create a new HashSet
jstl_hashset_t jstl_hashset_create();

// Destroy a HashSet and free all memory
void jstl_hashset_destroy(jstl_hashset_t set);

// Add an element
int jstl_hashset_add(jstl_hashset_t set, int64_t value);

// Check if contains element
int jstl_hashset_contains(jstl_hashset_t set, int64_t value);

// Remove an element
int jstl_hashset_remove(jstl_hashset_t set, int64_t value);

// Get size
size_t jstl_hashset_size(jstl_hashset_t set);

// Clear all elements
void jstl_hashset_clear(jstl_hashset_t set);

// Check if empty
int jstl_hashset_is_empty(jstl_hashset_t set);

// Create iterator
jstl_hashset_iterator_t jstl_hashset_iterator_create(jstl_hashset_t set);

// Check if iterator has next
int jstl_hashset_iterator_has_next(jstl_hashset_iterator_t iter);

// Get next element
int64_t jstl_hashset_iterator_next(jstl_hashset_iterator_t iter);

// Destroy iterator
void jstl_hashset_iterator_destroy(jstl_hashset_iterator_t iter);

#ifdef __cplusplus
}
#endif

#endif // JSTL_HASHSET_H
