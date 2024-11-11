package com.calculatortest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.calculator.Calculator; // Корректный импорт Calculator

class CalculatorTest {

    private final Calculator evaluator = new Calculator();

    @BeforeEach
    public void setUp() {
        System.out.println("Запуск теста: " + this.getCurrentTestName());
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Тест завершен: " + this.getCurrentTestName());
    }

    @Test
    public void testBasicOperations() {
        assertEquals(7, evaluator.evaluate("3 + 4"), 0.001);
        assertEquals(-1, evaluator.evaluate("3 - 4"), 0.001);
        assertEquals(12, evaluator.evaluate("3 * 4"), 0.001);
        assertEquals(2, evaluator.evaluate("8 / 4"), 0.001);
        assertEquals(9, evaluator.evaluate("3 ^ 2"), 0.001);
    }

    @Test
    public void testParentheses() {
        assertEquals(14, evaluator.evaluate("(3 + 4) * 2"), 0.001);
    }

    @Test
    public void testFunctions() {
        assertEquals(0, evaluator.evaluate("sin(0)"), 0.001);
        assertEquals(1, evaluator.evaluate("cos(0)"), 0.001);
        assertEquals(2, evaluator.evaluate("sqrt(4)"), 0.001);
    }

    @Test
    public void testVariables() {
        evaluator.setVariableValue("x", 5); // Устанавливаем значение переменной x
        assertEquals(10, evaluator.evaluate("2 * x"), 0.001); // Если x = 5, то 2 * 5 = 10
    }

    @Test
    public void testInvalidExpression() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("3 + "));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("3 + (4 * 2"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("3 / 0"));
    }

    private String getCurrentTestName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].getMethodName();
    }
}
