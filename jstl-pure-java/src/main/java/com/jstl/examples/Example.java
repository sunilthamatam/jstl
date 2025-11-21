package com.jstl.examples;

import com.jstl.OffHeapArrayList;
import com.jstl.OffHeapHashMap;
import com.jstl.OffHeapHashSet;

/**
 * Example demonstrating pure Java off-heap collections.
 *
 * NO C++ COMPILATION REQUIRED!
 * Just add the JAR to your classpath and run.
 */
public class Example {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Java Off-Heap Collections (No C++ Required!)         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        arrayListExample();
        System.out.println("\n" + "=".repeat(60) + "\n");
        hashMapExample();
        System.out.println("\n" + "=".repeat(60) + "\n");
        hashSetExample();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║        All examples completed successfully!            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static void arrayListExample() {
        System.out.println("=== OffHeapArrayList ===\n");

        try (OffHeapArrayList list = new OffHeapArrayList()) {
            // Basic operations
            for (long i = 1; i <= 10; i++) {
                list.add(i * 100);
            }

            System.out.println("Size: " + list.size());
            System.out.println("Elements: " + list);

            // Performance test
            list.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                list.add(i);
            }
            long end = System.nanoTime();

            System.out.println("\nPerformance: Added " + n + " elements in " +
                String.format("%.2f", (end - start) / 1_000_000.0) + " ms");
            System.out.println("Average: " + String.format("%.1f", (end - start) / (double) n) + " ns/op");
        }
    }

    private static void hashMapExample() {
        System.out.println("=== OffHeapHashMap ===\n");

        try (OffHeapHashMap map = new OffHeapHashMap()) {
            // Basic operations
            for (long i = 1; i <= 10; i++) {
                map.put(i, i * i);
            }

            System.out.println("Size: " + map.size());
            System.out.println("map[5] = " + map.get(5));
            System.out.println("Contains 5: " + map.containsKey(5));

            // Performance test
            map.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                map.put(i, i * 2);
            }
            long end = System.nanoTime();

            System.out.println("\nPerformance: Inserted " + n + " entries in " +
                String.format("%.2f", (end - start) / 1_000_000.0) + " ms");
            System.out.println("Average: " + String.format("%.1f", (end - start) / (double) n) + " ns/op");
        }
    }

    private static void hashSetExample() {
        System.out.println("=== OffHeapHashSet ===\n");

        try (OffHeapHashSet set = new OffHeapHashSet()) {
            // Basic operations
            for (long i = 1; i <= 10; i++) {
                set.add(i * 10);
            }

            System.out.println("Size: " + set.size());
            System.out.println("Contains 50: " + set.contains(50));
            System.out.println("Contains 99: " + set.contains(99));

            // Test duplicate rejection
            boolean added = set.add(50);
            System.out.println("Added duplicate 50: " + added);

            // Performance test
            set.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                set.add(i);
            }
            long end = System.nanoTime();

            System.out.println("\nPerformance: Added " + n + " elements in " +
                String.format("%.2f", (end - start) / 1_000_000.0) + " ms");
            System.out.println("Average: " + String.format("%.1f", (end - start) / (double) n) + " ns/op");
        }
    }
}
