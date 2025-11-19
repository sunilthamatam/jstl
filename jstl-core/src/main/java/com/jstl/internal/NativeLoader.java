package com.jstl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for loading native libraries
 */
public class NativeLoader {
    private static boolean loaded = false;
    private static final String LIBRARY_NAME = "jstl";

    public static synchronized void loadLibrary() {
        if (loaded) {
            return;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String libraryFileName;
        if (osName.contains("win")) {
            libraryFileName = LIBRARY_NAME + ".dll";
        } else if (osName.contains("mac")) {
            libraryFileName = "lib" + LIBRARY_NAME + ".dylib";
        } else {
            libraryFileName = "lib" + LIBRARY_NAME + ".so";
        }

        // Try to load from java.library.path first
        try {
            System.loadLibrary(LIBRARY_NAME);
            loaded = true;
            return;
        } catch (UnsatisfiedLinkError e) {
            // Continue to try loading from resources
        }

        // Try to load from build directory
        String[] possiblePaths = {
            "jstl-core/build/lib/" + libraryFileName,
            "../jstl-core/build/lib/" + libraryFileName,
            "build/lib/" + libraryFileName,
            "../build/lib/" + libraryFileName,
            "../../build/lib/" + libraryFileName,
            "jstl-core/native/build/lib/" + libraryFileName
        };

        for (String path : possiblePaths) {
            try {
                Path libPath = Path.of(path);
                if (Files.exists(libPath)) {
                    System.load(libPath.toAbsolutePath().toString());
                    loaded = true;
                    return;
                }
            } catch (Exception e) {
                // Continue trying
            }
        }

        throw new UnsatisfiedLinkError(
            "Could not load native library: " + libraryFileName +
            ". Please build the native library first using: mkdir build && cd build && cmake .. && make"
        );
    }
}
