package com.calculator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExpressionParser {
    private final Map<String, Double> variables;

    public ExpressionParser() {
        variables = new HashMap<>();
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);
    }

    public double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", "").toLowerCase();
        return parseExpression(expression);
    }

    private double parseExpression(String expr) {

        Stack<Double> numbers = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expr.length() &&
                        (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    numBuilder.append(expr.charAt(i++));
                }
                i--;

                double number = Double.parseDouble(numBuilder.toString());
                numbers.push(number);
            }

            else if (Character.isLetter(c)) {
                StringBuilder funcBuilder = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    funcBuilder.append(expr.charAt(i++));
                }
                i--;

                String func = funcBuilder.toString();
                if (variables.containsKey(func)) {
                    numbers.push(variables.get(func));
                } else if (i + 1 < expr.length() && expr.charAt(i + 1) == '(') {
                    i++;
                    int start = i + 1;
                    int parenCount = 1;
                    while (i + 1 < expr.length() && parenCount > 0) {
                        i++;
                        if (expr.charAt(i) == '(') parenCount++;
                        if (expr.charAt(i) == ')') parenCount--;
                    }
                    String arg = expr.substring(start, i);
                    double value = parseExpression(arg);
                    numbers.push(applyFunction(func, value));
                } else {
                    throw new IllegalArgumentException("未知标识符: " + func);
                }
            }
            else if (c == '(') {
                ops.push(c);
            }
            else if (c == ')') {
                while (ops.peek() != '(') {
                    numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
                }
                ops.pop();
            }
            else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                while (!ops.empty() && hasPrecedence(c, ops.peek())) {
                    numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.empty()) {
            numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
        }

        if (numbers.size() != 1) {
            throw new IllegalArgumentException("无效表达式");
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return op1 != '^' || op2 == '^';
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("除零错误");
                return a / b;
            case '^': return Math.pow(a, b);
            default: throw new IllegalArgumentException("未知运算符: " + op);
        }
    }

    private double applyFunction(String func, double value) {
        switch (func) {
            case "abs": return Math.abs(value);
            case "floor": return Math.floor(value);
            case "ceil": return Math.ceil(value);
            case "round": return Math.round(value);
            case "sin": return Math.sin(value);
            case "cos": return Math.cos(value);
            case "tan": return Math.tan(value);
            case "asin": return Math.asin(value);
            case "acos": return Math.acos(value);
            case "atan": return Math.atan(value);
            case "sqrt":
                if (value < 0) throw new ArithmeticException("负数的平方根");
                return Math.sqrt(value);
            case "lg":
                if (value <= 0) throw new ArithmeticException("对数参数必须为正数");
                return Math.log10(value);
            case "ln":
                if (value <= 0) throw new ArithmeticException("自然对数参数必须为正数");
                return Math.log(value);
            default: throw new UnsupportedOperationException("不支持的函数: " + func);
        }
    }
}