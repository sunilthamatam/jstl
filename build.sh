#!/bin/bash
set -e

echo "================================"
echo "Building JSTL Native Library"
echo "================================"
echo ""

# Create build directory
mkdir -p build
cd build

# Run CMake
echo "Running CMake..."
cmake ..

# Build the native library
echo ""
echo "Building native library..."
make -j$(nproc)

echo ""
echo "✓ Native library built successfully!"
echo "  Library location: $(pwd)/lib/"
ls -lh lib/

cd ..

echo ""
echo "================================"
echo "Building Java Code"
echo "================================"
echo ""

# Build Java code
mvn clean compile

echo ""
echo "✓ Build completed successfully!"
echo ""
echo "To run examples:"
echo "  ./run-examples.sh"
echo ""
echo "Or run individual examples:"
echo "  mvn exec:java -Dexec.mainClass=com.jstl.examples.ArrayListExample"
echo "  mvn exec:java -Dexec.mainClass=com.jstl.examples.HashMapExample"
echo "  mvn exec:java -Dexec.mainClass=com.jstl.examples.HashSetExample"
