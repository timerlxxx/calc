package com.calculator;

import com.calculator.controller.CalculatorController;
import com.calculator.model.ExpressionParser;
import com.calculator.model.HistoryManager;
import com.calculator.view.CalculatorGUI;

public class MathExpressionCalculator {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // 创建视图
            CalculatorGUI view = new CalculatorGUI();

            // 创建模型
            ExpressionParser parser = new ExpressionParser();
            HistoryManager historyManager = new HistoryManager("calculator_history.txt");

            // 创建控制器并连接视图和模型
            new CalculatorController(view, parser, historyManager);

            // 显示界面
            view.setVisible(true);
        });
    }
}