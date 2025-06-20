import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI extends JFrame {
    private JTextField expressionField;
    private JTextArea resultArea;

    private final ExpressionParser parser;
    private final HistoryManager historyManager;

    public CalculatorGUI(ExpressionParser parser, HistoryManager historyManager) {
        super("数学表达式计算器");
        this.parser = parser;
        this.historyManager = historyManager;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));

        createUI();
        setLocationRelativeTo(null);
    }

    private void createUI() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入表达式"));

        expressionField = new JTextField();
        expressionField.setFont(new Font("Consolas", Font.PLAIN, 25));
        inputPanel.add(expressionField, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        JButton calculateButton = createStyledButton("计算", new Color(70, 130, 180));
        JButton clearButton = createStyledButton("清空", new Color(205, 92, 92));
        JButton historyButton = createStyledButton("查看历史", new Color(60, 179, 113));
        JButton clearHistoryButton = createStyledButton("清除历史", new Color(219, 112, 147));

        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(clearHistoryButton);

        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果和历史记录"));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 30));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
//
        // 添加组件到主面板
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        // 添加主面板到窗口
        add(mainPanel, BorderLayout.CENTER);

        // 添加事件监听器
        calculateButton.addActionListener(new CalculateListener());
        clearButton.addActionListener(new ClearListener());
        historyButton.addActionListener(new HistoryListener());
        clearHistoryButton.addActionListener(new ClearHistoryListener());
        expressionField.addActionListener(new CalculateListener());
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private class CalculateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String expression = expressionField.getText().trim();
            if (expression.isEmpty()) {
                JOptionPane.showMessageDialog(CalculatorGUI.this,
                        "请输入数学表达式", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double result = parser.evaluate(expression);
                String resultStr = String.format("%.6f", result);
                String output = "in " + expression + "\nout " + resultStr + "\n\n";

                // 显示结果
                resultArea.append(output);

                // 保存到历史记录
                historyManager.saveHistory(expression, result);

                expressionField.requestFocus();
                expressionField.selectAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CalculatorGUI.this,
                        "计算错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            expressionField.setText("");
            resultArea.setText("");
            expressionField.requestFocus();
        }
    }

    private class HistoryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String history = historyManager.getHistory();
            resultArea.setText(history);
        }
    }

    private class ClearHistoryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(CalculatorGUI.this,
                    "确定要清除所有历史记录吗？", "确认清除", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                historyManager.clearHistory();
                resultArea.setText("历史记录已清除");
            }
        }
    }
}