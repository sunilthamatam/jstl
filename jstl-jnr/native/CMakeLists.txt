cmake_minimum_required(VERSION 3.10)
project(jstl VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Build shared library
add_library(jstl SHARED
    src/jstl_arraylist.cpp
    src/jstl_hashmap.cpp
    src/jstl_hashset.cpp
)

target_include_directories(jstl PUBLIC include)

# Output to build/lib
set_target_properties(jstl PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
)
