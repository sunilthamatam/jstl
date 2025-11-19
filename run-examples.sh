#!/bin/bash
set -e

echo "Running all JSTL examples..."
echo ""

# Run all examples
mvn exec:java \
    -Dexec.mainClass=com.jstl.examples.AllExamples \
    -Dexec.args="--enable-preview --enable-native-access=ALL-UNNAMED"

echo ""
echo "✓ All examples completed!"
