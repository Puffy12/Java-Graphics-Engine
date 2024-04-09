package csc133;

import SlRenderer.slWindow;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        slWindow sceneEditor = new slWindow();
        sceneEditor.run();
    }
}

