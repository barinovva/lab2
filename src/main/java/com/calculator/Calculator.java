package com.calculator;

import java.util.*;
import java.util.regex.*;

public class Calculator {
    private final Map<String, Double> variables = new HashMap<>();
    private final Map<String, MathFunction> functions = new HashMap<>();

    public Calculator() {
        functions.put("sin", args -> Math.sin(args[0]));
        functions.put("cos", args -> Math.cos(args[0]));
        functions.put("tan", args -> Math.tan(args[0]));
        functions.put("sqrt", args -> Math.sqrt(args[0]));
    }

    public void setVariableValue(String variable, double value) {
        variables.put(variable, value);
    }

    public double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", ""); // Исправлено регулярное выражение
        // Обработка математических функций
        if (expression.startsWith("sin(")) {
            return Math.sin(evaluate(expression.substring(4, expression.length() - 1)));
        } else if (expression.startsWith("cos(")) {
            return Math.cos(evaluate(expression.substring(4, expression.length() - 1)));
        } else if (expression.startsWith("sqrt(")) {
            return Math.sqrt(evaluate(expression.substring(5, expression.length() - 1)));
        }

        if (expression.matches("[a-zA-Z]+")) {
            return getVariableValue(expression);
        }

        return parseExpression(expression);
    }

    private double parseExpression(String expression) {
        if (expression.matches("-?\\d+(\\.\\d+)?")) { // Исправлено регулярное выражение
            return Double.parseDouble(expression);
        }

        List<String> tokens = tokenize(expression);
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix); // Вызываем метод для вычисления после преобразования
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?|[()+\\-*/^]|\\w+").matcher(expression); // Исправлено
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private List<String> infixToPostfix(List<String> tokens) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                postfix.add(token);
            } else if (functions.containsKey(token)) {
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postfix.add(operators.pop());
                }
                if (operators.isEmpty()) throw new IllegalArgumentException("Несоответствие скобок");
                operators.pop();
                if (!operators.isEmpty() && functions.containsKey(operators.peek())) {
                    postfix.add(operators.pop());
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    postfix.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (op.equals("(")) throw new IllegalArgumentException("Несоответствие скобок");
            postfix.add(op);
        }

        return postfix;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                stack.push(getVariableValue(token));
            } else if (functions.containsKey(token)) {
                Double[] args = { stack.pop() };
                stack.push(functions.get(token).apply(args));
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(token, a, b));
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Ошибка при вычислении выражения");
        return stack.pop();
    }

    private boolean isNumber(String token) {
        return token.matches("\\d+(\\.\\d+)?");
    }

    private boolean isVariable(String token) {
        return token.matches("[a-zA-Z]\\w*");
    }

    private double getVariableValue(String variable) {
        if (!variables.containsKey(variable)) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите значение для переменной " + variable + ": ");
            double value = scanner.nextDouble();
            variables.put(variable, value);
        }
        return variables.get(variable);
    }

    private boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }

    private double applyOperator(String operator, double a, double b) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("Деление на ноль невозможно.");
                }
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        };
    }
}

