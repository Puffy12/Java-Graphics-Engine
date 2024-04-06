# SlRenderer

SlRenderer is a Java package designed to facilitate simple rendering tasks using LWJGL (Lightweight Java Game Library) for OpenGL rendering. It provides classes for managing shaders, textures, and rendering scenes.

## Contents

1. [Installation](#installation)
2. [Usage](#usage)
3. [Examples](#examples)

---

## Installation <a name="installation"></a>

To use SlRenderer in your Java project, follow these steps:

1. **Download LWJGL**: SlRenderer relies on LWJGL for OpenGL rendering. Download the LWJGL library from [here](https://www.lwjgl.org/download) and include it in your project's classpath.

2. **Clone SlRenderer**: Clone this repository and include the necessary Java files (`slLevelSceneEditor.java`, `slShaderManager.java`, `slTextureManager.java`, and `slWindow.java`) in your project's source directory.

3. **Dependency Management**: Ensure that your project's dependencies are managed properly. SlRenderer depends on the JOML library for vector and matrix operations. You can include it manually or through a build tool like Maven or Gradle.

## Usage <a name="usage"></a>

Once SlRenderer is included in your project, you can use its classes to set up OpenGL rendering. Here's a basic usage example:

```java
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            slWindow window = slWindow.get();
            window.run();
        } catch (FileNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
