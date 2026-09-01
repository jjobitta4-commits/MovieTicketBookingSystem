package main;

import ui.AppFrame;
import ui.common.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.applyNimbus();
            AppFrame frame = new AppFrame();
            frame.showLogin();
            frame.setVisible(true);
        });
    }
}
