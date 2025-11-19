#ifndef JSTL_ARRAYLIST_H
#define JSTL_ARRAYLIST_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handle for ArrayList
typedef void* jstl_arraylist_t;

// Create a new ArrayList
jstl_arraylist_t jstl_arraylist_create();

// Destroy an ArrayList and free all memory
void jstl_arraylist_destroy(jstl_arraylist_t list);

// Add an element (stores as long/pointer)
void jstl_arraylist_add(jstl_arraylist_t list, int64_t value);

// Get element at index
int64_t jstl_arraylist_get(jstl_arraylist_t list, size_t index);

// Set element at index
void jstl_arraylist_set(jstl_arraylist_t list, size_t index, int64_t value);

// Remove element at index
void jstl_arraylist_remove(jstl_arraylist_t list, size_t index);

// Get size
size_t jstl_arraylist_size(jstl_arraylist_t list);

// Clear all elements
void jstl_arraylist_clear(jstl_arraylist_t list);

// Check if empty
int jstl_arraylist_is_empty(jstl_arraylist_t list);

// Get capacity
size_t jstl_arraylist_capacity(jstl_arraylist_t list);

// Reserve capacity
void jstl_arraylist_reserve(jstl_arraylist_t list, size_t capacity);

#ifdef __cplusplus
}
#endif

#endif // JSTL_ARRAYLIST_H
