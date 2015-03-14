package com.braincode.okap.choklik;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by divoolej on 13.03.15.
 */

public class Words {
    private static HashMap<Character, ArrayList<Character> > keyMap = InitializeKeyMap();

    private static HashMap<Character, ArrayList<Character> > InitializeKeyMap() {
        HashMap<Character, ArrayList<Character> > map = new HashMap<>();
        ArrayList<Character> array = new ArrayList<>(2);

        array.add('w'); array.add('w');
        map.put('q', new ArrayList<>(array));

        array.clear();
        array.add('s'); array.add('s');
        map.put('a', new ArrayList<>(array));

        array.clear();
        array.add('x'); array.add('x');
        map.put('z', new ArrayList<>(array));

        array.clear();
        array.add('o'); array.add('o');
        map.put('p', new ArrayList<>(array));

        array.clear();
        array.add('k'); array.add('k');
        map.put('l', new ArrayList<>(array));

        array.clear();
        array.add('n'); array.add('n');
        map.put('m', new ArrayList<>(array));

        String s = "qwertyuiop";

        for (int i = 1; i < s.length() - 1; i++) {
            array.clear();
            array.add(s.charAt(i - 1));
            array.add(s.charAt(i + 1));
            map.put(s.charAt(i), new ArrayList<>(array));
        }

        s = "asdfghjkl";

        for (int i = 1; i < s.length() - 1; i++) {
            array.clear();
            array.add(s.charAt(i-1));
            array.add(s.charAt(i+1));
            map.put(s.charAt(i), new ArrayList<>(array));
        }

        s = "zxcvbnm";

        for (int i = 1; i < s.length() - 1; i++) {
            array.clear();
            array.add(s.charAt(i-1));
            array.add(s.charAt(i+1));
            map.put(s.charAt(i), new ArrayList<>(array));
        }

        return map;
    }

    public static ArrayList<String> getPossibleMisspelledWords(String givenString) throws WordsException {
        String correctString = givenString.
                replace(",", "").
                replace("?", "").
                replace("!", "").
                replace("\"", "");
        String[] wordsInString = correctString.split("\\s+");

        if (!checkLength(wordsInString)) {
            throw new WordsException("Bad string");
        }

        ArrayList<String> result = new ArrayList<>();

        String query = "(";
        int count = 0;

        for (String word : wordsInString) {
            if (word.length() > 2) {
                ArrayList<String> subResult = generateMisspells(word);
                for (String s : subResult) {
                    query += correctString.replace(word, s) + ", ";
                    count++;
                    if (count == 10) {
                        query = query.substring(0, query.length()-2) + ")";
                        result.add(query);
                        query = "(";
                        count = 0;
                    }
                }
            }
        }
        if (count != 0) {
            query = query.substring(0, query.length()-2) + ")";
            result.add(query);
        }

        return result;
    }

    private static ArrayList<String> generateMisspells(String givenString) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> subresult = new ArrayList<>();
        for (int i = 1; i < givenString.length() - 1; i++) {
            char[] word = givenString.toCharArray();
            if (word[i] != word[i-1] && word[i] != word[i+1]) {
                char a = word[i];
                word[i] = word[i+1];
                word[i+1] = a;
                subresult.add(String.valueOf(word));
                result.add(String.valueOf(word));
            }
        }
        subresult.add(givenString);
        for (String s : subresult) {
            for (int i = 1; i < s.length() - 1; i++) {
                char[] word = s.toCharArray();
                if (keyMap.get(word[i]).get(0) != word[i-1] && keyMap.get(word[i]).get(0) != word[i+1]) {
                    word[i] = keyMap.get(word[i]).get(0);
                    result.add(String.valueOf(word));
                }
                word = s.toCharArray();
                if (keyMap.get(word[i]).get(1) != word[i-1] && keyMap.get(word[i]).get(1) != word[i+1]) {
                    word[i] = keyMap.get(word[i]).get(1);
                    result.add(String.valueOf(word));
                }
            }
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

    public static class WordsException extends Exception {
        private String message;
        public WordsException(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}