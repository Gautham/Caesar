package com.spt;

import java.io.File;
import java.util.*;

public class Main {

    private static HashMap<Integer, Stack<String>> dictionary = new HashMap<Integer, Stack<String>>();

    public static void main(String[] args) {
	    Scanner in = new Scanner(System.in);
        String enc = in.nextLine();
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File file = new File(classLoader.getResource("dictionary.txt").getFile());
            String content = new Scanner(file).useDelimiter("\\Z").next();
            buildDictionary(content);
        } catch (Exception e) {
            //Do Nothing
            System.out.print(e.toString());
        }
        List<String> solutions = getDecryptions(enc);
        for (String solution : solutions) {
            System.out.println(solution);
        }
    }

    private static List<String> getDecryptions(String enc) {
        List<String> solutions = new ArrayList<String>();
        Set<Integer> offsets = getPossibleOffsets(enc);
        for (Integer offset : offsets) {
            StringBuilder solution = new StringBuilder(enc);
            for (int i = 0; i < solution.length(); ++i) {
                char c = solution.charAt(i);
                if (c == ' ') continue;
                c = (char)(c - offset);
                if (!(c >= 'a' && c <= 'z')) c = (char)(c - 26);
                solution.setCharAt(i, c);
            }
            solutions.add(solution.toString());
        }
        if (solutions.size() == 0) solutions.add("Decryption Failed!");
        return solutions;
    }

    private static Set<Integer> getPossibleOffsets(String enc) {
        String[] words = enc.split(" ");
        Set<Integer> possibleOffsets = new HashSet<Integer>();
        for (int i = -26; i < 26; ++i) possibleOffsets.add(i);
        for (String word : words) {
            Set<Integer> possibleOffsetsForWord = getDecryptionOffsetsForWord(word);
            possibleOffsets.retainAll(possibleOffsetsForWord);
        }
        return possibleOffsets;
    }

    private static Set<Integer> getDecryptionOffsetsForWord(String e) {
        Set<Integer> possibleOffsets = new HashSet<Integer>();
        List<Integer> delta = getDeltaString(e);
        Stack<String> possibleSolutions = getDecrpytedSolutionsForWord(delta);
        for (String solution : possibleSolutions) {
            possibleOffsets.add(e.charAt(0) - solution.charAt(0));
        }
        return possibleOffsets;
    }

    private static Stack<String> getDecrpytedSolutionsForWord(List<Integer> delta) {
        Stack<String> decryptedSolutionsForWord = dictionary.get(delta.hashCode());
        return (decryptedSolutionsForWord == null) ? new Stack<String>() : decryptedSolutionsForWord;
    }

    private static void buildDictionary(String d) {
        String[] words = d.split(",");
        for (String word : words) {
            List<Integer> delta = getDeltaString(word);
            if (!dictionary.containsKey(delta.hashCode())) {
                dictionary.put(delta.hashCode(), new Stack<String>());
            }
            getDecrpytedSolutionsForWord(delta).push(word);
        }
    }

    private static List<Integer> getDeltaString(String e) {
        List<Integer> delta = new ArrayList<Integer>();
        for (int i = 0; i < e.length() - 1; ++i) {
            int del = e.charAt(i + 1) - e.charAt(i);
            if (del < 0) del = del + 26;
            delta.add(del);
        }
        return delta;
    }
}
