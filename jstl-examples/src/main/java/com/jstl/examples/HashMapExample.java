package com.jstl.examples;

import com.jstl.OffHeapHashMap;

/**
 * Example demonstrating OffHeapHashMap usage
 */
public class HashMapExample {
    public static void main(String[] args) {
        System.out.println("=== OffHeapHashMap Example ===\n");

        // Using try-with-resources for automatic cleanup
        try (OffHeapHashMap map = new OffHeapHashMap()) {
            System.out.println("Created new OffHeapHashMap");
            System.out.println("Initial size: " + map.size());
            System.out.println("Is empty: " + map.isEmpty());

            // Put entries
            System.out.println("\nPutting key-value pairs...");
            for (long i = 1; i <= 10; i++) {
                map.put(i, i * i);  // key -> key^2
            }
            System.out.println("Size after insertions: " + map.size());

            // Get values
            System.out.println("\nGetting values:");
            for (long i = 1; i <= 10; i++) {
                System.out.println("  map[" + i + "] = " + map.get(i));
            }

            // Check if key exists
            System.out.println("\nChecking keys:");
            System.out.println("Contains key 5: " + map.containsKey(5));
            System.out.println("Contains key 99: " + map.containsKey(99));

            // Get with default value
            System.out.println("\nUsing getOrDefault:");
            System.out.println("map.getOrDefault(5, -1) = " + map.getOrDefault(5, -1));
            System.out.println("map.getOrDefault(99, -1) = " + map.getOrDefault(99, -1));

            // Remove entry
            System.out.println("\nRemoving key 5");
            map.remove(5);
            System.out.println("Contains key 5: " + map.containsKey(5));
            System.out.println("New size: " + map.size());

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
            System.out.println("Average time per insertion: " + String.format("%.2f", ms / n * 1000) + " ns");
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
            System.out.println("Average time per lookup: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Sum: " + sum);

            // containsKey test
            start = System.nanoTime();
            int found = 0;
            for (long i = 0; i < n; i++) {
                if (map.containsKey(i)) {
                    found++;
                }
            }
            end = System.nanoTime();

            ms = (end - start) / 1_000_000.0;
            System.out.println("\nChecked " + n + " keys in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per check: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Found: " + found);

            System.out.println("\n✓ Memory will be freed automatically when try block exits");
        }

        System.out.println("\n✓ All native resources cleaned up!");
    }
}
