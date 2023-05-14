import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FactorialSumThreadDemo extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel sumLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JPanel progressPanel;

    private int numTerms;
    private BigInteger  sum;
    private double progress;

    public FactorialSumThreadDemo() {
        super("Factorial Sum Thread Demo");
        setSize(600, 250);
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


        // Create and start the factorial sum thread
        numTerms = 30;
        sum = BigInteger.ZERO;
        progress = 0.0;
        FactorialSumThread thread = new FactorialSumThread(numTerms,progressBar);
        thread.start();

        //Create a new panel to hold the progress bar and progress panel
        JPanel progressPanelWrapper = new JPanel(new BorderLayout());
        progressPanelWrapper.add(progressBar, BorderLayout.NORTH);
        progressPanelWrapper.add(progressPanel, BorderLayout.CENTER);
        getContentPane().add(progressPanelWrapper, BorderLayout.SOUTH);

        // Create and start the progress thread
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

        public FactorialSumThread(int numTerms, JProgressBar progressBar) {
            this.numTerms = numTerms;
            this.sum = BigInteger.ZERO;
            this.progressBar = progressBar;
        }

        public void run() {
            for (int i = 1; i <= numTerms; i++) {
                // 计算 i 的阶乘
                BigInteger fact = BigInteger.ONE;
                for (int j = 2; j <= i; j++) {
                    fact = fact.multiply(BigInteger.valueOf(j));
                }

                // 将阶乘加到总和中
                sum = sum.add(fact);

                // Update progress bar on event dispatch thread
                progress = (double) i / numTerms;

                progressBar.setForeground(Color.GRAY);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        progressBar.setValue((int) ((progress-(1/30))*100));
                    }
                });

                // 输出当前的总和
                DecimalFormat df = new DecimalFormat("#,###");
                System.out.println("当前总和： " + df.format(sum));
                System.out.println("到i的阶乘和： " + df.format((progress-(1/30))*100));
                // 等待 0.5-1.5 秒的随机时间
                try {
//                    Thread.sleep((long) (500 + Math.random() * 1000));
                    Thread.sleep((long) (1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 更新进度
                progress = (double) i / numTerms;

                DecimalFormat dt = new DecimalFormat("#,###");
                sumLabel.setText("1到"+ i +"的阶乘和为： " + df.format(sum));
            }

            // 输出最终总和
//            DecimalFormat df = new DecimalFormat("#,###");
//            System.out.println("总和： " + df.format(sum));
            // 更新总和标签
//            df = new DecimalFormat("#,###");
//            sumLabel.setText("总和： " + df.format(sum));
        }
    }


    private class ProgressThread extends Thread {
        public void run() {
            while (true) {
                // Update the progress label and bar
                DecimalFormat df = new DecimalFormat("0.00%");
                progressLabel.setText("Progress: " + df.format(progress));
                progressBar.setValue((int) (progress * 100));

                // Sleep for 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}