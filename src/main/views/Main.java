package main.views;

public class Main {
    public static void main(String[] args) {
        // Ensures that the Swing components are managed by the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            AdminControlPanel.getInstance().setVisible(true);
        });
    }
}
