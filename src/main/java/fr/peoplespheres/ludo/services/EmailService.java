package fr.peoplespheres.ludo.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Map;

@Service
public class EmailService {

    /**
     * Generate an email address based on parameters and an expression.
     *
     * @param inputsList List of parameters
     * @param expression
     * @return generated email
     */
    public String getEmailFromArgs(Map<String, String> inputsList, String expression) {

        LinkedList<String> operationsList = new LinkedList<>();
        LinkedList<String> convertedOperationsList = new LinkedList<>();

        String expressionTmp = expression;
        operationsList = getExpressions(expressionTmp, operationsList, inputsList);


        for (String s : operationsList) {
            convertedOperationsList.add(evaluateExpression(inputsList, s));
        }

        StringBuffer outputWord = new StringBuffer();
        for (String s : convertedOperationsList) {
            outputWord.append(s.replace("'", ""));
        }

        return outputWord.toString();
    }


    public LinkedList<String> getExpressions(String expression, LinkedList<String> operationsList, Map<String, String> inputsList) {
        if (expression.length() == 0) {
            return operationsList;
        }
        String tmpExpression = expression;
        String newExpression = null;

        if (!expression.contains("~")) {
            tmpExpression = expression;
            operationsList.add(tmpExpression);
            System.out.println(tmpExpression);
            newExpression = StringUtils.removeStart(expression, tmpExpression);
            return operationsList;
        }

        if (tmpExpression.startsWith("(")) {
            tmpExpression = expression.substring(0, expression.indexOf(" ) ~ ") + 5);
            newExpression = StringUtils.removeStart(expression, tmpExpression);
            conditionnalExpression(tmpExpression.substring(1, tmpExpression.length() - 5), operationsList, inputsList);
        } else {
            tmpExpression = expression.substring(0, expression.indexOf(" ~ "));
            operationsList.add(tmpExpression);
            System.out.println(tmpExpression);
            newExpression = StringUtils.removeStart(expression, tmpExpression + " ~ ");
        }

        if (newExpression.length() > 0) {
            return getExpressions(newExpression, operationsList, inputsList);
        }
        return operationsList;
    }

    public void conditionnalExpression(String expression, LinkedList<String> operationsList, Map<String, String> inputsList) {
        String conditionTest = expression.substring(0, expression.indexOf("?")).trim();
        String conditionIf = expression.substring(expression.indexOf("?") + 1, expression.indexOf(":")).trim();
        String conditionElse = expression.substring(expression.indexOf(":") + 1, expression.length()).trim();

        boolean a = evaluateCondition(inputsList, conditionTest);
        if (a) {
            operationsList = getExpressions(conditionIf, operationsList, inputsList);
        } else {
            operationsList = getExpressions(conditionElse, operationsList, inputsList);
        }
    }

    public boolean evaluateCondition(Map<String, String> inputsList, String expression) {
        boolean conditionReach = false;

        if (expression.contains("wordsCount()")) {
            String inputName = expression.substring(0, expression.indexOf("."));
            String number = expression.substring(expression.indexOf(")") + 1);
            String getInputValueFromList = inputsList.get(inputName);

            int wordCount = wordsCount(getInputValueFromList);
            int numberToReach = Integer.parseInt(expression.substring(expression.length() - 1));

            if (number.contains(">")) {
                conditionReach = wordCount > numberToReach;
            } else if (number.contains("<")) {
                conditionReach = wordCount < numberToReach;
            } else if (number.contains("==")) {
                conditionReach = wordCount == numberToReach;
            }
        }
        return conditionReach;
    }

    public String evaluateExpression(Map<String, String> inputsList, String expression) {

        if (expression.contains("lastWords(")) {
            String inputName = expression.substring(0, expression.indexOf("."));
            int firstIdx = expression.indexOf("lastWords(") + "lastWords(".length();
            int secondIdx = expression.indexOf(")", firstIdx);
            String number = expression.substring(firstIdx, secondIdx);
            String getInputValueFromList = inputsList.get(inputName);
            return lastWords(getInputValueFromList, Integer.parseInt(number));
        }

        if (expression.contains("eachWordFirstChars(")) {
            String inputName = expression.substring(0, expression.indexOf("."));
            int firstIdx = expression.indexOf("eachWordFirstChars(") + "eachWordFirstChars(".length();
            int secondIdx = expression.indexOf(")", firstIdx);
            String number = expression.substring(firstIdx, secondIdx);
            String getInputValueFromList = inputsList.get(inputName);
            return eachWordFirstChars(getInputValueFromList, Integer.parseInt(number));
        }

        if (expression.contains("input")) {
            return inputsList.get(expression).toLowerCase();
        }

        if (expression.contains(".") || expression.contains("@")) {
            return expression.trim();
        }

        return "";
    }

    /**
     * Remove from initial input word the specials characters prohibited in email address.
     *
     * @param inputWord The initial Input
     * @return a filtered word
     */
    public String filterProhibitedEmailCharacters(final String inputWord) {
        // We should define a list of forbidden chars for email Address
        // Alternatively using STATIC LISTS and/or REGEX
        return StringUtils.replaceEach(inputWord, new String[]{"@", "<", ">", ")", "("}, new String[]{"", "", "", "", ""});
    }

    /**
     * Takes an input String and takes a defined number of chars in order to construct a new concatenated word.
     *
     * @param inputWord The String to parse
     * @param nbChars   number of chars to use from each word
     * @return The concatenated word
     */
    public String eachWordFirstChars(String inputWord, int nbChars) {
        if (inputWord == null) {
            return "";
        }
        StringBuffer outputWord = new StringBuffer();
        // String[] words = inputWord.split("[;:- ]");
        String[] words = inputWord.trim().split("[- ]");

        for (String w : words) {
            if (StringUtils.isNotEmpty(w.trim())) {
                if (w.trim().length() >= nbChars) {
                    outputWord.append(w.trim().substring(0, nbChars));
                } else {
                    outputWord.append(w.trim());
                }
            }
        }

        return outputWord.toString().toLowerCase();
    }

    /**
     * Count the number of words in a String.
     *
     * @param inputWord The String to analyze.
     * @return number of words
     */
    public int wordsCount(final String inputWord) {
        int cpt = 0;
        if (inputWord == null) {
            return 0;
        }
        String[] words = inputWord.trim().split("[- ]");
        for (String w : words) {
            if (w.replaceAll(" ", "").length() > 0) {
                cpt++;
            }
        }
        return cpt;
    }

    // TODO LBE je ne comprend pas bien cette fonction donc elle est très empirique. J'aurais besoin de précision ...
    public String lastWords(final String inputWord, final int index) {

        String[] words = inputWord.trim().split("[- ]");

        LinkedList<String> clearedWords = new LinkedList<>();
        LinkedList<String> outPutWords = new LinkedList<>();

        for (String w : words) {
            String y = w.replaceAll(" ", "");
            if (StringUtils.isNotEmpty(y)) {
                clearedWords.add(y);
            }
        }

        if (index > 0) {
            for (int i = clearedWords.size() - 1; i > (clearedWords.size() - 1 - index); i--) {
                outPutWords.add(clearedWords.get(i));
            }
        } else if (index < 0) {
            for (int i = 0; i < (clearedWords.size() + index); i++) {
                outPutWords.add(clearedWords.get(i));
            }
        } else {
            outPutWords = clearedWords;
        }

        StringBuffer outputWord = new StringBuffer();
        for (String s : outPutWords) {
            outputWord.append(s);
        }

        return outputWord.toString();
    }
}
