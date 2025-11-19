# JSTL Core Library

Core library providing off-heap data structures backed by C++ STL.

## Contents

This module contains:
- **Java API**: User-friendly collection classes (`OffHeapArrayList`, `OffHeapHashMap`, `OffHeapHashSet`)
- **FFM Bindings**: Panama Foreign Function & Memory bindings to C++ native code
- **Native Code**: C++ implementations using STL (`std::vector`, `std::unordered_map`, `std::unordered_set`)
- **Tests**: Comprehensive JUnit Jupiter test suite (82 tests)

## Building

```bash
# Build native library
mkdir -p build
cd build
cmake ..
make
cd ..

# Build Java library
mvn install
```

## Usage in Your Project

Add as a Maven dependency:

```xml
<dependency>
    <groupId>com.jstl</groupId>
    <artifactId>jstl-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Example

```java
import com.jstl.OffHeapArrayList;

try (OffHeapArrayList list = new OffHeapArrayList()) {
    list.add(100);
    list.add(200);
    long value = list.get(0);
}
```

See the `jstl-examples` module for more detailed examples.
