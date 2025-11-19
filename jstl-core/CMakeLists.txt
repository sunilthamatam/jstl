cmake_minimum_required(VERSION 3.15)
project(jstl_native VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Source files
set(SOURCES
    native/src/jstl_arraylist.cpp
    native/src/jstl_hashmap.cpp
    native/src/jstl_hashset.cpp
)

# Create shared library
add_library(jstl SHARED ${SOURCES})

# Set output directory
set_target_properties(jstl PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
)

# Include directories
target_include_directories(jstl PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/native/include
)

# Installation
install(TARGETS jstl
    LIBRARY DESTINATION lib
    RUNTIME DESTINATION lib
)
