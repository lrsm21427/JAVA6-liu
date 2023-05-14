import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel sumLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JPanel progressPanel;

    private int numTerms;
    private long sum;
    private double progress;

    public Main() {
        super("Factorial Sum Thread Demo");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the sum label
        sumLabel = new JLabel();
        sumLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        sumLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create the progress label
        progressLabel = new JLabel();
        progressLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        progressLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create the progress bar
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setPreferredSize(new Dimension(400, 20));

        // Create the progress panel
        progressPanel = new JPanel(new GridLayout(1, numTerms));
        progressPanel.setPreferredSize(new Dimension(400, 40));

        // Add the labels and panel to the content pane
        getContentPane().add(sumLabel, BorderLayout.CENTER);
        getContentPane().add(progressLabel, BorderLayout.NORTH);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        getContentPane().add(progressPanel, BorderLayout.SOUTH);

        // Create and start the factorial sum thread
        numTerms = 30;
        sum = 0;
        progress = 0.0;
        FactorialSumThread thread = new FactorialSumThread(numTerms);
        thread.start();

        // Create and start the progress thread
        ProgressThread progressThread = new ProgressThread();
        progressThread.start();
    }

    public static void main(String[] args) {
        new FactorialSumThreadDemo().setVisible(true);
    }

    private class FactorialSumThread extends Thread {
        private int numTerms;

        public FactorialSumThread(int numTerms) {
            this.numTerms = numTerms;
        }

        public void run() {
            for (int i = 1; i <= numTerms; i++) {
                // Calculate the factorial of i
                long fact = 1;
                for (int j = 2; j <= i; j++) {
                    fact *= j;
                }

                // Add the factorial to the sum
                sum += fact;

                // Sleep for a random time between 0.5 and 1 second
                try {
                    TimeUnit.MILLISECONDS.sleep((long) (500 + Math.random() * 500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Update the progress
                progress = (double) i / numTerms;
            }

            // Update the sum label
            DecimalFormat df = new DecimalFormat("#,###");
            sumLabel.setText("Sum: " + df.format(sum));
        }
    }

    private class ProgressThread extends Thread {
        public void run() {
            for (int i = 1; i <= numTerms; i++) {
                // Create a progress bar for this term
                JLabel termLabel = new JLabel();
                termLabel.setPreferredSize(new Dimension(12, 12));
                termLabel.setOpaque(true);
                progressPanel.add(termLabel);

                // Update the progress label
                DecimalFormat df = new DecimalFormat("0%");
                progressLabel.setText("Progress: " + df.format(progress));

                // Sleep for 1 second
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Set the color of the progress bar
                if (progress >= (double) i / numTerms) {
                    termLabel.setBackground(java.awt.Color.GREEN);
                } else {
                    termLabel.setBackground(java.awt.Color.RED);
                }
            }
        }
    }

}