package csc133;

import SlRenderer.slLevelSceneEditor;
import static SlRenderer.slSingleBatchRenderer.render;

public class Main {
    public static void main(String[] args) {
        slLevelSceneEditor sceneEditor = new slLevelSceneEditor();
        sceneEditor.render();
    }
}

/*
package csc133;

import static SlRenderer.slSingleBatchRenderer.render;

public class Main {
    
    public static void main(String[] args) {
        render();
    } // public static void main(String[] args)

    
} // public class Main

 */