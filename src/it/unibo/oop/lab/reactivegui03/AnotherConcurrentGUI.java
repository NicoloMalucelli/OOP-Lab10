package it.unibo.oop.lab.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

public class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int TIME_TO_KILL = 10_000; //milliseconds
    private final JLabel lblCounter = new JLabel("0");

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel mainPanel = new JPanel();
        final JButton btnStop = new JButton("STOP");
        final JButton btnUp = new JButton("Up");
        final JButton btnDown = new JButton("Down");

        this.add(mainPanel);
        mainPanel.add(lblCounter);
        mainPanel.add(btnUp);
        mainPanel.add(btnDown);
        mainPanel.add(btnStop);

        final  BiCounterAgent agent = new BiCounterAgent();
        btnStop.addActionListener(e -> {
            agent.stop();
            btnDown.setEnabled(false);
            btnUp.setEnabled(false);
            btnStop.setEnabled(false);
        });
        btnUp.addActionListener(e -> agent.inc());
        btnDown.addActionListener(e -> agent.dec());

        new Thread(agent).start();
        new Thread(() -> {
            try {
                Thread.sleep(TIME_TO_KILL);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            agent.stop();
            btnDown.setEnabled(false);
            btnUp.setEnabled(false);
            btnStop.setEnabled(false);
        }).start();

        this.setVisible(true);
    }

    private class BiCounterAgent implements Runnable {

        private boolean count = true;
        private boolean up = true;
        private int value = 0;

        public void run() {
            while (count) {
                value += up ? 1 : -1;
                try {
                    SwingUtilities.invokeAndWait(() ->  AnotherConcurrentGUI.this.lblCounter.setText(Integer.toString(value)));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            count = false;
        }

        public void inc() {
            up = true;
        }

        public void dec() {
            up = false;
        }

    }
}
