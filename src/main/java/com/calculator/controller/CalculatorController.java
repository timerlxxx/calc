package com.calculator.controller;

import com.calculator.model.ExpressionParser;
import com.calculator.model.HistoryManager;
import com.calculator.view.CalculatorGUI;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CalculatorController {
    private final CalculatorGUI view;
    private final ExpressionParser parser;
    private final HistoryManager historyManager;

    public CalculatorController(CalculatorGUI view, ExpressionParser parser, HistoryManager historyManager) {
        this.view = view;
        this.parser = parser;
        this.historyManager = historyManager;
        attachEventListeners();
    }

    private void attachEventListeners() {
        view.getCalculateButton().addActionListener(this::handleCalculate);
        view.getClearButton().addActionListener(this::handleClear);
        view.getHistoryButton().addActionListener(this::handleHistory);
        view.getClearHistoryButton().addActionListener(this::handleClearHistory);
        view.getExpressionField().addActionListener(this::handleCalculate);
        view.getFileInputButton().addActionListener(this::handleFileInput);
        view.getExportButton().addActionListener(this::handleExport);
        view.getImportButton().addActionListener(this::handleImport);
        view.getSearchButton().addActionListener(this::handleSearch);
        view.getStatsButton().addActionListener(this::handleStats);
        view.getAdvancedButton().addActionListener(this::handleAdvanced);
    }

    private void handleCalculate(ActionEvent e) {
        String expression = view.getExpression().trim();
        if (expression.isEmpty()) {
            view.showWarning("请输入数学表达式", "输入错误");
            return;
        }

        try {
            double result = parser.evaluate(expression);
            String resultStr = String.format("%.6f", result);
            String output = "in " + expression + "\nout " + resultStr + "\n\n";

            view.appendResult(output);
            historyManager.saveHistory(expression, result);
            view.focusExpressionField();
        } catch (Exception ex) {
            view.showError("计算错误: " + ex.getMessage(), "错误");
        }
    }

    private void handleClear(ActionEvent e) {
        view.clearExpression();
        view.clearResult();
        view.focusExpressionField();
    }

    private void handleHistory(ActionEvent e) {
        view.setResult(historyManager.getHistory());
    }

    private void handleClearHistory(ActionEvent e) {
        int option = view.showConfirmDialog("确定要清除所有历史记录吗？", "确认清除");
        if (option == JOptionPane.YES_OPTION) {
            historyManager.clearHistory();
            view.setResult("历史记录已清除");
        }
    }

    private void handleFileInput(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择表达式文件");
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));

        int returnValue = fileChooser.showOpenDialog(view);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                view.setResult("正在处理文件: " + selectedFile.getName() + "\n");
                historyManager.processExpressionFile(selectedFile.getAbsolutePath(), parser);
                view.appendResult("文件处理完成！\n");
                view.appendResult(historyManager.getHistory());
                view.showInfo("文件处理完成！成功处理", "成功");
            } catch (IOException ex) {
                view.showError("文件处理错误: " + ex.getMessage(), "错误");
                view.appendResult("文件处理失败: " + ex.getMessage());
            } catch (Exception ex) {
                view.showError("计算错误: " + ex.getMessage(), "错误");
                view.appendResult("计算错误: " + ex.getMessage());
            }
        }
    }

    private void handleExport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出历史记录");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("文本文件 (*.txt)", "txt");
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV文件 (*.csv)", "csv");
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(txtFilter);

        int returnValue = fileChooser.showSaveDialog(view);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String format = "txt";

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
                view.showInfo("历史记录已成功导出到: " + selectedFile.getName(), "导出成功");
            } catch (IOException ex) {
                view.showError("导出失败: " + ex.getMessage(), "错误");
            }
        }
    }

    private void handleImport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入历史记录");
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本/CSV文件", "txt", "csv"));

        int returnValue = fileChooser.showOpenDialog(view);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                historyManager.importHistory(selectedFile.getAbsolutePath());
                view.setResult("历史记录导入成功！\n");
                view.appendResult(historyManager.getHistory());
                view.showInfo("历史记录已成功导入", "导入成功");
            } catch (IOException ex) {
                view.showError("导入失败: " + ex.getMessage(), "错误");
            }
        }
    }

    private void handleSearch(ActionEvent e) {
        String searchTerm = view.showInputDialog("输入搜索关键词:", "搜索历史记录");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            view.setResult(historyManager.getHistory(100, searchTerm, false));
        }
    }

    private void handleStats(ActionEvent e) {
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

        view.setResult(sb.toString());
    }

    private void handleAdvanced(ActionEvent e) {
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

        int result = view.showCustomDialog(panel, "高级历史选项");
        if (result == JOptionPane.OK_OPTION) {
            int maxEntries = (Integer) maxSpinner.getValue();
            boolean reverseOrder = orderCombo.getSelectedIndex() == 0;
            String filter = filterField.getText().trim();
            view.setResult(historyManager.getHistory(maxEntries, filter, reverseOrder));
        }
    }
}