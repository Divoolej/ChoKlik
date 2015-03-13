package com.braincode.okap.choklik;

import java.util.ArrayList;

/**
 * Created by divoolej on 13.03.15.
 */
public class Words {
    public static void main(String[] args) {
        try {
            getPossibleMisspelledWords("samusung! nowy obroża? dla.psa");
        } catch (WordsException we) {
            System.out.println(we.getMessage());
        }
    }

    public static ArrayList<String> getPossibleMisspelledWords(String givenString) throws WordsException {
        String[] wordsInString = givenString.
                replace(",", "").
                replace("?", "").
                replace("!", "").
                replace("\"", "").split("\\s+");

        if (checkLength(wordsInString)) {
            throw new WordsException("Bad string");
        }

        ArrayList<String> result = new ArrayList<>();

        for (String word : wordsInString) {

        }

        return result;
    }

    private static boolean checkLength(String[] givenString) {
        int sum = 0;
        for (String s : givenString) {
            sum += s.length();
        }
        if (sum >= 20)
            return false;

        return true;
    }

    private static class WordsException extends Exception {
        private String message;
        public WordsException(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}