package com.spt;

import java.io.File;
import java.util.*;

public class Main {

    private static HashMap<Integer, Stack<String>> dictionary = new HashMap<Integer, Stack<String>>();

    public static void main(String[] args) {
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File file = new File(classLoader.getResource("dictionary.txt").getFile());
            String content = new Scanner(file).useDelimiter("\\Z").next();
            buildDictionary(content);
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        Random rand = new Random();
        Scanner in = new Scanner(System.in);
        int option;
        String enc, dec;
        do {
            System.out.println("Options\n===============\n1) Encrypt\n2) Decrypt\n3) Exit");
            try {
                option = Integer.parseInt(in.nextLine());
            } catch (Exception e) {
                option = 999;
            }
            switch (option) {
                case 1:
                    System.out.print("Input String : ");
                    dec = in.nextLine().toLowerCase();
                    int  n = rand.nextInt(25) + 1;
                    enc = shiftString(dec, n);
                    System.out.println("Encrypted String : '" + enc + "' with offset " + n);
                    break;
                case 2:
                    System.out.print("Input String : ");
                    enc = in.nextLine().toLowerCase();
                    List<String> solutions = getDecryptions(enc);
                    if (solutions.size() > 1) {
                        System.out.print("Solutions :");
                        int i = 1;
                        for (String solution : solutions) {
                            System.out.println(i++ + ") " + solution);
                        }
                    } else System.out.println("Solution : " + solutions.get(0));
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        } while (option != 3);
    }

    private static String shiftString(String S, int offset) {
        return shiftString(S, offset, 1);
    }

    private static String shiftString(String S, int offset, int add) {
        StringBuilder X = new StringBuilder(S);
        for (int i = 0; i < X.length(); ++i) {
            char c = X.charAt(i);
            if (c == ' ') continue;
            c = (char)(c + add * offset);
            if (!(c >= 'a' && c <= 'z')) {
                c = (char)(c - 26);
                if (!(c >= 'a' && c <= 'z')) c = (char)(c + 52);
            }
            X.setCharAt(i, c);
        }
        return X.toString();
    }

    private static List<String> getDecryptions(String enc) {
        List<String> solutions = new ArrayList<String>();
        Set<Integer> offsets = getPossibleOffsets(enc);
        for (Integer offset : offsets) {
            solutions.add(shiftString(enc, offset, -1));
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
        Stack<String> possibleSolutions = getDecryptedSolutionsForWord(delta);
        for (String solution : possibleSolutions) {
            int offset = e.charAt(0) - solution.charAt(0);
            offset = (offset < 0) ? offset + 26 : offset;
            possibleOffsets.add(offset);
        }
        return possibleOffsets;
    }

    private static Stack<String> getDecryptedSolutionsForWord(List<Integer> delta) {
        Stack<String> decryptedSolutionsForWord = dictionary.get(delta.hashCode());
        return (decryptedSolutionsForWord == null) ? new Stack<String>() : decryptedSolutionsForWord;
    }

    private static void buildDictionary(String d) {
        String[] words = d.split("\n");
        for (String word : words) {
            word = word.toLowerCase();
            List<Integer> delta = getDeltaString(word);
            if (!dictionary.containsKey(delta.hashCode())) {
                dictionary.put(delta.hashCode(), new Stack<String>());
            }
            getDecryptedSolutionsForWord(delta).push(word);
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
