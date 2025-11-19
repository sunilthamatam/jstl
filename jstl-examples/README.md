# JSTL Examples

Example applications demonstrating usage of JSTL off-heap collections.

## Running Examples

```bash
# Run all examples
mvn exec:java

# Run specific examples
mvn exec:java -Parraylist     # ArrayList examples
mvn exec:java -Phashmap       # HashMap examples
mvn exec:java -Phashset       # HashSet examples
```

## Examples Included

### AllExamples
Runs all three examples in sequence with performance benchmarks.

### ArrayListExample
Demonstrates `OffHeapArrayList` usage:
- Basic operations (add, get, set, remove)
- Capacity management
- Performance testing with 1M elements

### HashMapExample
Demonstrates `OffHeapHashMap` usage:
- Put/get operations
- Key existence checking
- Performance testing with 1M entries

### HashSetExample
Demonstrates `OffHeapHashSet` usage:
- Add/remove operations
- Duplicate handling
- Performance testing with 1M elements

## Dependencies

This module depends on `jstl-core` which must be built first.

## JVM Arguments

The examples automatically use:
- `--enable-preview` - For Panama FFM API
- `--enable-native-access=ALL-UNNAMED` - For native library access
