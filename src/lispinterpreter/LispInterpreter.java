/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lispinterpreter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *
 * @author dhrubo
 */
public class LispInterpreter {

    Stack<String> tokenStack = new Stack<String>();
    private HashMap<String, Double> valueList = new HashMap<String, Double>();
    private HashMap<String, ArrayList<String>> lamdaParameter = new HashMap<String, ArrayList<String>>();
    private HashMap<String, String> lamdafunctionDefinition = new HashMap<String, String>();
    ;
    boolean isHalt = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Enter the lisp command");
        Scanner input = new Scanner(System.in);
        LispInterpreter objLispInterPreter = new LispInterpreter();
        objLispInterPreter.isHalt = false;
        while (!input.hasNext("quit")) {

            String lispCommand = input.nextLine();
            boolean contains = lispCommand.contains("quote");
            if (!contains) {
                lispCommand = lispCommand.replaceAll("\\(", " $0 ");
                lispCommand = lispCommand.replaceAll("\\)", " $0 ");
            }
            objLispInterPreter.TokensInStack(lispCommand);

            Iterator objItratr = objLispInterPreter.tokenStack.iterator();
            if (objLispInterPreter.tokenStack.size() > 1) {
                objLispInterPreter.isHalt = true;
            }
            if (objLispInterPreter.isHalt) {
                System.err.println("Wrong Input");
            } else {
                while (objItratr.hasNext()) {
                    String outputString = objItratr.next().toString();
                    try {
                        PrintWriter writer = new PrintWriter(new FileWriter("output.txt", true));
                        writer.write(outputString);
                        writer.println();
                        writer.close();
                    } catch (Exception e) {
                        System.err.println("Writing action did not perform. Can you please try again?");
                    }
                    System.out.println(outputString);
                }
            }
            objLispInterPreter.tokenStack.clear();
            objLispInterPreter.isHalt = false;
            System.out.println("Enter the lisp command again");
        }

    }

    public void TokensInStack(String lispCommand) {
        boolean contains = lispCommand.contains("quote");
        if (contains) {
            String[] splitted = lispCommand.split("quote");
            String item = splitted[1].substring(0, splitted[1].length() - 1);
            item = item.trim();

            tokenStack.clear();
            tokenStack.push(item);
        } else {
            boolean lambdaContains = lispCommand.contains("lambda");
            if (lambdaContains) {
                lambdaExpressionHandling(lispCommand);
            } else {
                StringTokenizer st = new StringTokenizer(lispCommand);
                String[] split = lispCommand.split("\\s");
                if (lamdafunctionDefinition.containsKey(split[2])) {
                    lamdafunctionEvaluation(lispCommand);
                } else {
                    while (st.hasMoreTokens()) {
                        String commandUnit = st.nextToken();
                        if (commandUnit.contains("(") && commandUnit.length() > 1) {
                            String[] strArr = commandUnit.split("\\(");
                            tokenStack.push("(");
                            tokenStack.push(strArr[1]);
                        } else if (commandUnit.contains(")") && commandUnit.length() > 1) {
                            String[] strArr = commandUnit.split("\\)");
                            tokenStack.push(strArr[0]);
                            if (!isHalt) {
                                ListInterpreter();
                            } else {
                                break;
                            }
                        } else if (commandUnit.contains(")") && commandUnit.length() == 1) {
                            if (!isHalt) {
                                ListInterpreter();
                            } else {
                                break;
                            }
                        } else {
                            tokenStack.push(commandUnit);
                        }
                    }
                }

            }

        }

    }

    public void lamdafunctionEvaluation(String lispCommand) {
        StringTokenizer st = new StringTokenizer(lispCommand);
        String functionName = "";
        int countOpenParenthesis = 0;
        ArrayList<String> parameterValueList = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            if (element.equals("(")) {
                countOpenParenthesis++;
            } else if (countOpenParenthesis == 1) {
                functionName = element;
            } else if (countOpenParenthesis == 2 && !element.equals(")")) {
                parameterValueList.add(element);
            }

        }
        ArrayList<String> parameterList = lamdaParameter.get(functionName);
        String functionDefinition = lamdafunctionDefinition.get(functionName);
        for (int x = 0; x < parameterValueList.size(); x++) {
            double value = Double.parseDouble(parameterValueList.get(x));
            String variable = parameterList.get(x);
            valueList.put(variable, value);
        }
        TokensInStack(functionDefinition);
    }

    public void lambdaExpressionHandling(String lispCommand) {
        StringTokenizer st = new StringTokenizer(lispCommand);
        boolean nextLamda = false;
        int countOpenParenthesis = 0;
        int countCloseParenthesis = 0;
        boolean firstPart = false;
        boolean secondPart = false;
        ArrayList<String> parameter = new ArrayList<String>();
        ArrayList<String> functionDefinition = new ArrayList<String>();
        String functionDefinitionString = "";
        String functionName = "";
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            if (element.equals("lambda")) {
                nextLamda = true;
            } else if (nextLamda) {
                functionName = element;
                nextLamda = false;
            } else if (!nextLamda) {
                if (element.equals("(")) {
                    countOpenParenthesis = countOpenParenthesis + 1;
                    if (countOpenParenthesis == 2 && firstPart != true) {
                        firstPart = true;
                    } else if (countOpenParenthesis >= 3) {
                        functionDefinition.add(element);
                        secondPart = true;
                    }
                } else if (element.equals(")")) {
                    countCloseParenthesis++;
                    if (countCloseParenthesis == 1 && firstPart == true) {
                        firstPart = false;
                    } else {
                        functionDefinition.add(element);
                    }
                } else if (firstPart) {
                    parameter.add(element);
                } else if (firstPart != true && secondPart == true) {
                    functionDefinition.add(element);
                }
            }

        }
        lamdaParameter.put(functionName, parameter);
        functionDefinition.remove(functionDefinition.size() - 1);
        
        // the following line can only be availabe in the java 1.8
        //functionDefinitionString = functionDefinitionString.join(" ", functionDefinition);
        
        
        // to make it compatible I have to implement in another way to add the array elements in a string.
        String newVersionString = "";

        StringBuilder sb = new StringBuilder();
        for (String s : functionDefinition) {
            sb.append(s);
            sb.append(" ");
        }
        newVersionString = sb.toString();
        lamdafunctionDefinition.put(functionName, newVersionString);
        tokenStack.push(functionName);
    }

//    public void TokensInStack(String lispCommand) {
//        String[] strArr = lispCommand.split("");
//        for (int i = 0; i < strArr.length; i++) {
//            if (!strArr[i].equals(" ")) {
//                tokenStack.push(strArr[i]);
//                if (strArr[i].equals(")")) {
//                    if (!isHalt) {
//                        ListInterpreter();
//                    } else {
//                        break;
//                    }
//                }
//            }
//
//        }
//    }
    public void ListInterpreter() {
        String tok;
        Stack<String> innerStack = new Stack<String>();
        while (!(tok = tokenStack.pop()).equals("(")) {
            innerStack.push(tok);
        }
        LispParser(innerStack);
    }

    public void LispParser(Stack<String> innerStack) {
        String operator = innerStack.pop();
        if (operator.equals("+")) {
            double result = plusFunction(innerStack);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("-")) {
            double result = minusFunction(innerStack);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("*")) {
            double result = timesFunction(innerStack);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("/")) {
            double result = overFunction(innerStack);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("define") || operator.equals("set!")) {
            String result = defvarFunction(innerStack, operator);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("quote")) {
            String result = quoteFunction(innerStack);
            tokenStack.push(result);
        } else if (operator.equals("sin")) {
            double result = sinCosTanSqrtFunction(innerStack, 1);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("cos")) {
            double result = sinCosTanSqrtFunction(innerStack, 2);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("tan")) {
            double result = sinCosTanSqrtFunction(innerStack, 3);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("sqrt")) {
            double result = sinCosTanSqrtFunction(innerStack, 4);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals("if")) {
            double result = ifFunction(innerStack);
            tokenStack.push(String.valueOf(result));
        } else if (operator.equals(">") || operator.equals("<")) {
            boolean result = conditionalFunction(innerStack, operator);
            tokenStack.push(String.valueOf(result));
        } else {
            double result = defaultFunction(operator);
            tokenStack.push(String.valueOf(result));
        }
    }

    public double plusFunction(Stack<String> innerStack) {
        double result = 0;
        Iterator iterator = innerStack.iterator();
        while (iterator.hasNext()) {
            String stackItem = innerStack.pop();
            if (!stackItem.equals(")")) {
                double value = 0;
                if (isNumeric(stackItem)) {
                    value = Double.parseDouble(stackItem);
                    result = value + result;
                } else {
                    value = valueList.get(stackItem);
                    result = value + result;
                }

            }
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        return str.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");
    }

    public double minusFunction(Stack<String> innerStack) {
        String item = innerStack.pop();
        double result = 0;
        if (isNumeric(item)) {
            result = Double.parseDouble(item);
        } else {
            result = valueList.get(item);
        }

        Iterator iterator = innerStack.iterator();
        while (iterator.hasNext()) {
            String stackItem = innerStack.pop();
            if (!stackItem.equals(")")) {
                double value = 0;
                if (isNumeric(stackItem)) {
                    value = Double.parseDouble(stackItem);
                    result = result - value;
                } else {
                    value = valueList.get(stackItem);
                    result = result - value;
                }

            }
        }
        return result;
    }

    public double timesFunction(Stack<String> innerStack) {
        String item = innerStack.pop();
        double result = 0;
        if (isNumeric(item)) {
            result = Double.parseDouble(item);
        } else {
            result = valueList.get(item);
        }
        Iterator iterator = innerStack.iterator();
        while (iterator.hasNext()) {
            String stackItem = innerStack.pop();
            if (!stackItem.equals(")")) {
                double value = 0;
                if (isNumeric(stackItem)) {
                    value = Double.parseDouble(stackItem);
                    result = value * result;
                } else {
                    value = valueList.get(stackItem);
                    result = value * result;
                }
            }
        }
        return result;
    }

    public double overFunction(Stack<String> innerStack) {
        String item = innerStack.pop();
        double result = 0;
        if (isNumeric(item)) {
            result = Double.parseDouble(item);
        } else {
            result = valueList.get(item);
        }
        Iterator iterator = innerStack.iterator();
        while (iterator.hasNext()) {
            String stackItem = innerStack.pop();
            if (!stackItem.equals(")")) {
                double value = 0;
                if (isNumeric(stackItem)) {
                    value = Double.parseDouble(stackItem);
                    result = result / value;
                } else {
                    value = valueList.get(stackItem);
                    result = result / value;
                }
            }
        }
        return result;
    }

    public double sqrtFunction(Stack<String> innerStack) {
        String item = innerStack.pop();
        double result = 0;
        if (isNumeric(item)) {
            result = Double.parseDouble(item);
        } else {
            result = valueList.get(item);
        }
        result = Math.sqrt(result);
        return result;
    }

    public double sinCosTanSqrtFunction(Stack<String> innerStack, int choice) {
        String item = innerStack.pop();
        double result = 0;
        if (isNumeric(item)) {
            result = Double.parseDouble(item);
        } else {
            result = valueList.get(item);
        }
        if (choice == 1) {
            result = Math.sin(result);
        } else if (choice == 2) {
            result = Math.cos(result);
        } else if (choice == 3) {
            result = Math.tan(result);
        } else if (choice == 4) {
            result = Math.sqrt(result);
        } else {
            result = result * result;
        }
        return result;
    }

    public String defvarFunction(Stack<String> innerStack, String operator) {
        double result = 0;
        String valueAssign = innerStack.pop();
        result = Double.parseDouble(innerStack.pop());

        valueList.put(valueAssign, result);
        if (operator.equals("define")) {
            return valueAssign;
        } else {
            return String.valueOf(result);
        }
    }

    public Boolean conditionalFunction(Stack<String> innerStack, String condition) {
        Boolean result = false;
        Double firstItem;
        Double secondItem;
        String firstItemString = innerStack.pop();
        String secondItemString = innerStack.pop();
        if (isNumeric(firstItemString)) {
            firstItem = Double.parseDouble(firstItemString);
        } else {
            firstItem = valueList.get(firstItemString);
        }
        if (isNumeric(secondItemString)) {
            secondItem = Double.parseDouble(secondItemString);
        } else {
            secondItem = valueList.get(secondItemString);
        }

        if (condition.equals(">")) {
            if (firstItem > secondItem) {
                result = true;
            } else {
                result = false;
            }
        } else if (secondItem > firstItem) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public double ifFunction(Stack<String> innerStack) {
        double result = 0;
        boolean choice = Boolean.parseBoolean(innerStack.pop());

        Double firstItem;
        Double secondItem;
        String firstItemString = innerStack.pop();
        String secondItemString = innerStack.pop();
        if (isNumeric(firstItemString)) {
            firstItem = Double.parseDouble(firstItemString);
        } else {
            firstItem = valueList.get(firstItemString);
        }
        if (isNumeric(secondItemString)) {
            secondItem = Double.parseDouble(secondItemString);
        } else {
            secondItem = valueList.get(secondItemString);
        }

        if (choice) {
            result = firstItem;
        } else {
            result = secondItem;
        }
        return result;
    }

    public double defaultFunction(String innerStack) {
        double result = 0;
        if (isNumeric(innerStack)) {
            result = Double.parseDouble(innerStack);
        } else {
            result = valueList.get(innerStack);
        }
        return result;
    }

    public String quoteFunction(Stack<String> innerStack) {
        String result = "";

        return result;
    }
}
