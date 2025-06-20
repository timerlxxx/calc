public class MathExpressionCalculator {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // 创建各个组件
            ExpressionParser parser = new ExpressionParser();
            HistoryManager historyManager = new HistoryManager("calculator_history.txt");

            // 创建GUI
            CalculatorGUI calculator = new CalculatorGUI(parser, historyManager);
            calculator.setVisible(true);
        });
    }
}