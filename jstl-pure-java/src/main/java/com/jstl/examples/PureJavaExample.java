package com.jstl.examples;

import com.jstl.PureJavaOffHeapArrayList;
import com.jstl.PureJavaOffHeapHashMap;

/**
 * Example demonstrating pure Java off-heap collections.
 *
 * NO C++ COMPILATION REQUIRED!
 * Just add the JAR to your classpath and run.
 */
public class PureJavaExample {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Pure Java Off-Heap Collections                      ║");
        System.out.println("║   NO C++ COMPILATION REQUIRED!                         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        arrayListExample();
        System.out.println("\n" + "=".repeat(60) + "\n");
        hashMapExample();
    }

    private static void arrayListExample() {
        System.out.println("=== Pure Java Off-Heap ArrayList ===\n");

        try (PureJavaOffHeapArrayList list = new PureJavaOffHeapArrayList()) {
            // Add elements
            System.out.println("Adding elements 1-10...");
            for (long i = 1; i <= 10; i++) {
                list.add(i * 100);
            }

            System.out.println("Size: " + list.size());
            System.out.println("Capacity: " + list.capacity());

            // Get elements
            System.out.println("\nElements:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("  list[" + i + "] = " + list.get(i));
            }

            // Modify
            list.set(5, 999);
            System.out.println("\nAfter setting list[5] = 999:");
            System.out.println("  list[5] = " + list.get(5));

            // Performance test
            System.out.println("\n=== Performance Test ===");
            list.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                list.add(i);
            }
            long end = System.nanoTime();

            double ms = (end - start) / 1_000_000.0;
            System.out.println("Added " + n + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average: " + String.format("%.2f", ms / n * 1000) + " ns per operation");
            System.out.println("Final size: " + list.size());

            // Memory is off-heap - NO GC PRESSURE!
            System.out.println("\n✓ All memory is off-heap (no GC pressure)");
            System.out.println("✓ No C++ compilation needed!");
        }
    }

    private static void hashMapExample() {
        System.out.println("=== Pure Java Off-Heap HashMap ===\n");

        try (PureJavaOffHeapHashMap map = new PureJavaOffHeapHashMap()) {
            // Put entries
            System.out.println("Putting key-value pairs...");
            for (long i = 1; i <= 10; i++) {
                map.put(i, i * i);
            }

            System.out.println("Size: " + map.size());

            // Get values
            System.out.println("\nEntries:");
            for (long i = 1; i <= 10; i++) {
                System.out.println("  map[" + i + "] = " + map.get(i));
            }

            // Contains key
            System.out.println("\nContains key 5: " + map.containsKey(5));
            System.out.println("Contains key 99: " + map.containsKey(99));

            // Remove
            map.remove(5);
            System.out.println("\nAfter removing key 5:");
            System.out.println("  Contains key 5: " + map.containsKey(5));
            System.out.println("  Size: " + map.size());

            // Performance test
            System.out.println("\n=== Performance Test ===");
            map.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                map.put(i, i * 2);
            }
            long end = System.nanoTime();

            double ms = (end - start) / 1_000_000.0;
            System.out.println("Inserted " + n + " entries in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average: " + String.format("%.2f", ms / n * 1000) + " ns per operation");
            System.out.println("Final size: " + map.size());

            // Lookup test
            start = System.nanoTime();
            long sum = 0;
            for (long i = 0; i < n; i++) {
                sum += map.get(i);
            }
            end = System.nanoTime();

            ms = (end - start) / 1_000_000.0;
            System.out.println("\nLooked up " + n + " entries in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average: " + String.format("%.2f", ms / n * 1000) + " ns per operation");
            System.out.println("Sum: " + sum);

            System.out.println("\n✓ All memory is off-heap (no GC pressure)");
            System.out.println("✓ No C++ compilation needed!");
            System.out.println("✓ Just add JAR and run!");
        }

        System.out.println("\n✓ All off-heap memory cleaned up automatically!");
    }
}
