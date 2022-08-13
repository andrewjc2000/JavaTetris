package tetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Dimension;

public class Tetris {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 650;

    private static final int TIMER_RATE = 5;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            GameComponent mainComponent = new GameComponent();
            frame.setTitle("Tetris");
            frame.setResizable(false);
            frame.setSize(WIDTH, HEIGHT);
            frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(mainComponent);
            frame.addKeyListener(mainComponent);
            frame.pack();
            frame.setVisible(true);
            new Timer(TIMER_RATE, (actionEvent) -> mainComponent.update()).start();
        });
    }
}
