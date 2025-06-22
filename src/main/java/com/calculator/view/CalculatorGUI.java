package com.calculator.view;

import javax.swing.*;
import java.awt.*;

public class CalculatorGUI extends JFrame {
    private JTextField expressionField;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton historyButton;
    private JButton clearHistoryButton;
    private JButton fileInputButton;
    private JButton exportButton;
    private JButton importButton;
    private JButton searchButton;
    private JButton statsButton;
    private JButton advancedButton;

    public CalculatorGUI() {
        super("数学表达式计算器");
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));
        createComponents();
        setLocationRelativeTo(null);
    }

    private void createComponents() {
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
        calculateButton = createStyledButton("计算", new Color(70, 130, 180));
        clearButton = createStyledButton("清空", new Color(205, 92, 92));
        historyButton = createStyledButton("查看历史", new Color(60, 179, 113));
        clearHistoryButton = createStyledButton("清除历史", new Color(219, 112, 147));
        fileInputButton = createStyledButton("文件读入", new Color(106, 90, 205));
        row1.add(calculateButton);
        row1.add(clearButton);
        row1.add(historyButton);
        row1.add(clearHistoryButton);
        row1.add(fileInputButton);

        JPanel row2 = new JPanel(new GridLayout(1, 5, 10, 10));
        exportButton = createStyledButton("导出历史", new Color(255, 165, 0));
        importButton = createStyledButton("导入历史", new Color(46, 139, 87));
        searchButton = createStyledButton("搜索历史", new Color(147, 112, 219));
        statsButton = createStyledButton("统计信息", new Color(0, 139, 139));
        advancedButton = createStyledButton("高级选项", new Color(178, 34, 34));
        row2.add(exportButton);
        row2.add(importButton);
        row2.add(searchButton);
        row2.add(statsButton);
        row2.add(advancedButton);

        mainButtonPanel.add(row1);
        mainButtonPanel.add(row2);

        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果和历史记录"));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("微软雅黑", Font.BOLD, 30));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
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

    // Getters for controller
    public JButton getCalculateButton() { return calculateButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getHistoryButton() { return historyButton; }
    public JButton getClearHistoryButton() { return clearHistoryButton; }
    public JTextField getExpressionField() { return expressionField; }
    public JButton getFileInputButton() { return fileInputButton; }
    public JButton getExportButton() { return exportButton; }
    public JButton getImportButton() { return importButton; }
    public JButton getSearchButton() { return searchButton; }
    public JButton getStatsButton() { return statsButton; }
    public JButton getAdvancedButton() { return advancedButton; }

    // View methods for controller
    public String getExpression() { return expressionField.getText(); }
    public void clearExpression() { expressionField.setText(""); }
    public void clearResult() { resultArea.setText(""); }
    public void setResult(String text) { resultArea.setText(text); }
    public void appendResult(String text) { resultArea.append(text); }
    public void focusExpressionField() { expressionField.requestFocus(); expressionField.selectAll(); }

    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    public String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    public int showCustomDialog(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}