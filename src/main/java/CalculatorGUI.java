import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

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
        expressionField.setFont(new Font("微软雅黑", Font.BOLD, 25));
        inputPanel.add(expressionField, BorderLayout.CENTER);

        // 按钮面板
        JPanel mainButtonPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel row1 = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton calculateButton = createStyledButton("计算", new Color(70, 130, 180));
        JButton clearButton = createStyledButton("清空", new Color(205, 92, 92));
        JButton historyButton = createStyledButton("查看历史", new Color(60, 179, 113));
        JButton clearHistoryButton = createStyledButton("清除历史", new Color(219, 112, 147));
        JButton fileInputButton = createStyledButton("文件读入", new Color(106, 90, 205));


        row1.add(calculateButton);
        row1.add(clearButton);
        row1.add(historyButton);
        row1.add(clearHistoryButton);
        row1.add(fileInputButton);

        JPanel row2 = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton exportButton = createStyledButton("导出历史", new Color(255, 165, 0)); // 橙色
        JButton importButton = createStyledButton("导入历史", new Color(46, 139, 87)); // 海绿色
        JButton searchButton = createStyledButton("搜索历史", new Color(147, 112, 219)); // 中紫色
        JButton statsButton = createStyledButton("统计信息", new Color(0, 139, 139)); // 深青色

        JButton advancedButton = createStyledButton("高级选项", new Color(178, 34, 34)); // 火砖色
        row2.add(exportButton);
        row2.add(importButton);
        row2.add(searchButton);
        row2.add(statsButton);
        row2.add(advancedButton);

        mainButtonPanel.add(row1);
        mainButtonPanel.add(row2);

        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果和历史记录"));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("微软雅黑", Font.BOLD, 30));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
//
        // 添加组件到主面板
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        // 添加主面板到窗口
        add(mainPanel, BorderLayout.CENTER);

        // 添加事件监听器
        calculateButton.addActionListener(new CalculateListener());
        clearButton.addActionListener(new ClearListener());
        historyButton.addActionListener(new HistoryListener());
        clearHistoryButton.addActionListener(new ClearHistoryListener());
        expressionField.addActionListener(new CalculateListener());
        fileInputButton.addActionListener(new FileInputListener());
        exportButton.addActionListener(new ExportListener());
        importButton.addActionListener(new ImportListener());
        searchButton.addActionListener(new SearchListener());
        statsButton.addActionListener(new StatsListener());
        advancedButton.addActionListener(new AdvancedListener());

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

    private class FileInputListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择表达式文件");
            fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));

            int returnValue = fileChooser.showOpenDialog(CalculatorGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // 显示处理中消息
                    resultArea.setText("正在处理文件: " + selectedFile.getName() + "\n");

                    // 处理文件中的表达式
                    int processedCount = historyManager.processExpressionFile(selectedFile.getAbsolutePath(), parser);

                    // 显示处理结果
                    resultArea.append("文件处理完成！\n");
                    resultArea.append(historyManager.getHistory());

                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "文件处理完成！成功处理 ",
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "文件处理错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    resultArea.append("文件处理失败: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "计算错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    resultArea.append("计算错误: " + ex.getMessage());
                }
            }
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

    private class ExportListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("导出历史记录");

            // 设置文件过滤器
            FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("文本文件 (*.txt)", "txt");
            FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV文件 (*.csv)", "csv");
            fileChooser.addChoosableFileFilter(txtFilter);
            fileChooser.addChoosableFileFilter(csvFilter);
            fileChooser.setFileFilter(txtFilter); // 默认选择文本文件

            int returnValue = fileChooser.showSaveDialog(CalculatorGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String format = "txt";

                // 确定文件格式
                if (fileChooser.getFileFilter() == csvFilter) {
                    format = "csv";
                    if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
                    }
                } else if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                }

                try {
                    historyManager.exportHistory(selectedFile.getAbsolutePath(), format);
                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "历史记录已成功导出到: " + selectedFile.getName(),
                            "导出成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ImportListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("导入历史记录");
            fileChooser.setFileFilter(new FileNameExtensionFilter("文本/CSV文件", "txt", "csv"));

            int returnValue = fileChooser.showOpenDialog(CalculatorGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    historyManager.importHistory(selectedFile.getAbsolutePath());

                    // 显示导入结果
                    String history = historyManager.getHistory();
                    resultArea.setText("历史记录导入成功！\n");
                    resultArea.append(history);

                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "历史记录已成功导入", "导入成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(CalculatorGUI.this,
                            "导入失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SearchListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = JOptionPane.showInputDialog(CalculatorGUI.this,
                    "输入搜索关键词:", "搜索历史记录", JOptionPane.QUESTION_MESSAGE);

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String history = historyManager.getHistory(100, searchTerm, false);
                resultArea.setText(history);
            }
        }
    }

    private class StatsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Map<String, Object> stats = historyManager.getHistoryStats();

            StringBuilder sb = new StringBuilder();
            sb.append("===== 历史记录统计 =====\n");

            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    sb.append("\n").append(entry.getKey()).append(":\n");
                    @SuppressWarnings("unchecked")
                    Map<String, Integer> opStats = (Map<String, Integer>) entry.getValue();
                    for (Map.Entry<String, Integer> opEntry : opStats.entrySet()) {
                        sb.append(String.format("  %-6s: %d次\n", opEntry.getKey(), opEntry.getValue()));
                    }
                } else {
                    sb.append(String.format("%-10s: %s\n", entry.getKey(), entry.getValue()));
                }
            }

            resultArea.setText(sb.toString());
        }
    }

    private class AdvancedListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // 创建高级选项面板
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel maxLabel = new JLabel("最大显示条目:");
            JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
            maxSpinner.setValue(10);

            JLabel orderLabel = new JLabel("排序顺序:");
            JComboBox<String> orderCombo = new JComboBox<>(new String[]{"最新优先", "最早优先"});

            JLabel filterLabel = new JLabel("过滤条件:");
            JTextField filterField = new JTextField();

            panel.add(maxLabel);
            panel.add(maxSpinner);
            panel.add(orderLabel);
            panel.add(orderCombo);
            panel.add(filterLabel);
            panel.add(filterField);

            int result = JOptionPane.showConfirmDialog(CalculatorGUI.this,
                    panel, "高级历史选项", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int maxEntries = (Integer) maxSpinner.getValue();
                boolean reverseOrder = orderCombo.getSelectedIndex() == 0; // 最新优先
                String filter = filterField.getText().trim();

                String history = historyManager.getHistory(maxEntries, filter, reverseOrder);
                resultArea.setText(history);
            }
        }
    }
}