//package com.braincode.okap.choklik;

import java.util.ArrayList;

/**
 * Created by divoolej on 13.03.15.
 */

public class Words {
    public static void main(String[] args) {
        try {
            getPossibleMisspelledWords("samsung daj");
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

        if (!checkLength(wordsInString)) {
            throw new WordsException("Bad string");
        }

        ArrayList<String> result = new ArrayList<>();

        for (String word : wordsInString) {
            if (word.length() > 2) {
                ArrayList<String> subResult = new ArrayList<>();
                subResult = generateMisspells(word);

            } else {
                continue;
            }
        }

        return result;
    }

    private static ArrayList<String> generateMisspells(String givenString) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> subresult = new ArrayList<>();
        for (int i = 1; i < givenString.length() - 2; i++) {
            char[] word = givenString.toCharArray();
            if (word[i] != word[i-1] && word[i] != word[i+1]) {
                char a = word[i];
                word[i] = word[i+1];
                word[i+1] = a;
                subresult.add(String.valueOf(word));
            }
        }
        return subresult;
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