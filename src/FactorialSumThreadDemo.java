import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.awt.Color;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FactorialSumThreadDemo extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Random rand = new Random();
    private JLabel sumLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JPanel progressPanel;
    private JButton pauseButton;

    private int numTerms;
    private BigInteger sum;
    private double progress;
    private boolean isPaused;

    private FactorialSumThread thread;

    public FactorialSumThreadDemo() {
        super("阶乘和");
        setSize(700, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sumLabel = new JLabel();
        sumLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        sumLabel.setHorizontalAlignment(JLabel.CENTER);

        progressLabel = new JLabel();
        progressLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        progressLabel.setHorizontalAlignment(JLabel.CENTER);

        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setPreferredSize(new Dimension(400, 20));

        progressPanel = new JPanel(new GridLayout(1, numTerms));
        progressPanel.setPreferredSize(new Dimension(400, 40));

        pauseButton = new JButton("暂停");
        pauseButton.addActionListener(e -> {
            isPaused = !isPaused;
            pauseButton.setText(isPaused ? "开始" : "暂停");
            if (isPaused) {
                thread.pauseThread();
            } else {
                thread.resumeThread();
            }
        });

        getContentPane().add(sumLabel, BorderLayout.CENTER);
        getContentPane().add(progressLabel, BorderLayout.NORTH);

        numTerms = 30;
        sum = BigInteger.ZERO;
        progress = 0.0;
        isPaused = false;

        thread = new FactorialSumThread(numTerms, progressBar);
        thread.start();
//        thread.pauseThread();
        JPanel progressPanelWrapper = new JPanel(new BorderLayout());
        progressPanelWrapper.add(progressBar, BorderLayout.NORTH);
        progressPanelWrapper.add(progressPanel, BorderLayout.CENTER);
        getContentPane().add(progressPanelWrapper, BorderLayout.SOUTH);
        getContentPane().add(pauseButton, BorderLayout.WEST);

        ProgressThread progressThread = new ProgressThread();
        progressThread.start();
    }

    public static void main(String[] args) {
        new FactorialSumThreadDemo().setVisible(true);
    }

    private class FactorialSumThread extends Thread {
        private int numTerms;
        private BigInteger sum;
        private JProgressBar progressBar;
        private boolean isThreadPaused;

        public FactorialSumThread(int numTerms, JProgressBar progressBar) {
            this.numTerms = numTerms;
            this.sum = BigInteger.ZERO;
            this.progressBar = progressBar;
            this.isThreadPaused = false;
        }

        public void run() {
            for (int i = 1; i <= numTerms; i++) {
                synchronized (this) {
                    while (isThreadPaused) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                BigInteger fact = BigInteger.ONE;
                for (int j = 2; j <= i; j++) {
                    fact = fact.multiply(BigInteger.valueOf(j));
                }

                sum = sum.add(fact);

                progress = (double) i / numTerms;

                progressBar.setForeground(Color.GRAY);
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue((int) ((progress - (1.0 / numTerms)) * 100));
                });
                DecimalFormat df = new DecimalFormat("#,###");
                System.out.println("当前总和： " + df.format(sum));
                System.out.println("到i的阶乘和： " + df.format((progress - (1.0 / 30)) * 100));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progress = (double) i / numTerms;

                DecimalFormat dt = new DecimalFormat("#,###");
                sumLabel.setText("1到" + i + "的阶乘和为： " + df.format(sum));
            }
        }

        public synchronized void pauseThread() {
            isThreadPaused = true;
        }

        public synchronized void resumeThread() {
            isThreadPaused = false;
            notify();
        }
    }

    private class ProgressThread extends Thread {
        public void run() {
            while (true) {
                DecimalFormat df = new DecimalFormat("0.00%");
                progressLabel.setText("Progress: " + df.format(progress));
                progressBar.setValue((int) (progress * 100));

                try {
                    Thread.sleep(1000 + rand.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
