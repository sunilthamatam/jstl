# Cross-Platform Build Instructions

This project supports **Linux**, **macOS**, and **Windows**.

## Prerequisites

### All Platforms
- **Java 21** or later
- **Maven 3.6** or later
- **CMake 3.15** or later
- **C++17** compatible compiler

### Linux
- GCC 7+ or Clang 5+
- Build essentials: `sudo apt install build-essential cmake`

### macOS
- Xcode Command Line Tools: `xcode-select --install`
- Or install via Homebrew: `brew install cmake`

### Windows
- **Visual Studio 2019/2022** with C++ Desktop Development
- Or **MinGW-w64** / **MSYS2**
- **CMake** (download from cmake.org or use `choco install cmake`)

## Building

### Linux / macOS

```bash
# Make scripts executable
chmod +x build.sh run-examples.sh

# Build everything
./build.sh

# Run examples
./run-examples.sh
```

### Windows

#### Option 1: Using Batch Files (Recommended)

```cmd
REM Build everything
build.bat

REM Run examples
run-examples.bat
```

#### Option 2: Using Git Bash / WSL

```bash
./build.sh
./run-examples.sh
```

## Manual Build (All Platforms)

### Step 1: Build Native Library

```bash
# Linux/macOS
cd jstl-core
mkdir -p build && cd build
cmake ..
make
cd ../..
```

```cmd
REM Windows (Command Prompt)
cd jstl-core
mkdir build
cd build
cmake ..
cmake --build . --config Release
cd ..\..
```

```powershell
# Windows (PowerShell)
cd jstl-core
New-Item -ItemType Directory -Force build
cd build
cmake ..
cmake --build . --config Release
cd ..\..
```

### Step 2: Build Java Code

```bash
# All platforms
mvn install
```

### Step 3: Run Examples

```bash
# All platforms
cd jstl-examples
mvn exec:java                # All examples
mvn exec:java -Parraylist    # ArrayList only
mvn exec:java -Phashmap      # HashMap only
mvn exec:java -Phashset      # HashSet only
```

## Platform-Specific Notes

### Linux
- Native library: `libjstl.so`
- Automatically found by NativeLoader

### macOS
- Native library: `libjstl.dylib`
- May need to grant permissions for native code execution
- On Apple Silicon (M1/M2), ensure Java 21 is ARM64 or use Rosetta

### Windows
- Native library: `jstl.dll`
- Build with Visual Studio or MinGW
- Ensure CMake uses correct generator:
  ```cmd
  cmake -G "Visual Studio 17 2022" ..
  REM or
  cmake -G "MinGW Makefiles" ..
  ```
- DLL must be in same directory as application or in PATH

## Compiler Requirements

| Platform | Minimum Compiler |
|----------|-----------------|
| Linux    | GCC 7+ or Clang 5+ |
| macOS    | Apple Clang 10+ (Xcode 10+) |
| Windows  | MSVC 2019+ or GCC 7+ (MinGW) |

## Testing Platform Compatibility

After building, run tests to verify:

```bash
cd jstl-core
mvn test
```

All 82 tests should pass on any platform.

## Troubleshooting

### Library Not Found
If you get `UnsatisfiedLinkError`, ensure:
1. Native library is built: `ls jstl-core/build/lib/`
2. Check NativeLoader paths in error message
3. Try setting `java.library.path`:
   ```bash
   java -Djava.library.path=jstl-core/build/lib ...
   ```

### CMake Can't Find Compiler (Windows)
- Install Visual Studio with C++ Desktop Development
- Or install MinGW-w64
- Run from "Developer Command Prompt for VS"

### Permission Denied (macOS)
```bash
chmod +x build.sh run-examples.sh
```

### Build Fails on Apple Silicon
Ensure Java 21 is native ARM64:
```bash
java -version  # Should show "aarch64"
```

## CI/CD Platforms Tested

- ✅ Linux (Ubuntu 20.04+)
- ✅ macOS (11.0+, Intel and Apple Silicon)
- ✅ Windows (10/11 with Visual Studio 2019+)
