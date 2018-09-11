/**
 * Created by Jordan Brannan - Advanced Algorithms
 */

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

public class huffman {
    // Initialise global variables
    public static Map<String, Integer> freqMap = new HashMap<>();
    public static String testCode = "";
    public static char testChar;
    public static String tempTestString;
    public static int pointer = 0;
    public static boolean eof = false;

    //--- Main method ---
    public static void options(){
        Scanner in = new Scanner(System.in);
        System.out.println("Would you like to compress or decompress a file?\n");
        System.out.println("\t********************");
        System.out.println("\t1. Compress file.");
        System.out.println("\t2. Decompress file.");
        System.out.println("\t0. Exit.");
        System.out.println("\t********************\n");
        System.out.println("Please input a single digit (0-2):\n\n");
        String answer = in.nextLine();
        if (answer.equals("1")) {
            compress();
        }
        else if (answer.equals("2")){
            decompress();
        }
        else if (answer.equals("0"))
        {
            System.exit(0);
        }

    }
// --- Method for Compressing file ---
    public static void compress() {
        // Initialising variables etc
        Scanner in = new Scanner(System.in);
        Node root;
        String huffmanCode = "";
        Node curNode;
        int intChar;
        char textChar;
        String everything = "";
        String dag = "DAG";
        String eof = "EOF";
        // Add Dagger and End of File to HashMap
        freqMap.put("DAG", 1);
        freqMap.put("EOF", 1);
        // Take file name to decompress
        System.out.println("Enter the file name to compress (Without the file extension):");
        String fileName = in.nextLine();
        File file = new File(fileName+".txt");
        // Read file character by character and add to one string.
        try {
            FileReader fileReader = new FileReader(fileName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((intChar = bufferedReader.read()) != -1) {
                textChar = (char) intChar;
                everything += String.valueOf(textChar);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        // Start iterating through characters in file to generate paths
        for (int i = 0; i < everything.length(); i++) {
            // Make huffman tree from Hashmap
            root = Node.makeHuff(freqMap);
            // Print Huffman tree to console
            Node.printToConsole(root, "");
            testChar = everything.charAt(i);
            tempTestString = String.valueOf(testChar);
            // If current character of iteration is in Hashmap get current path
            if (freqMap.get(tempTestString) != null) {
                // Find node with character and find path
                curNode = Node.search(root, tempTestString);
                Node.path += Node.printPath(root, curNode);
                // Add to path string
                huffmanCode += Node.path;
                // Print current contents of Hashmap, the input character and output path
                System.out.print("A{ ");
                for (String key : freqMap.keySet()) {
                    System.out.print(key + "(" + freqMap.get(key) + ") ");
                }
                System.out.println("}");
                System.out.println("Input: " + curNode.text);
                System.out.println("Output: " + Node.path +"\n");
                Node.path = "";
                // Increase value of character in Hashmap
                freqMap.put(tempTestString, freqMap.get(tempTestString) + 1);
            // If not in Hashmap, get path to Dagger and get binary value of character
            } else {
                // Find node with dagger and path to it
                curNode = Node.search(root, dag);
                Node.path = Node.printPath(root, curNode);
                // Add path to dagger to path string
                huffmanCode += Node.path;
                // Print current contents of Hashmap, character input and output path
                System.out.print("A{ ");
                for (String key : freqMap.keySet()) {
                    System.out.print(key + "(" + freqMap.get(key) + ") ");
                }
                System.out.println("}");
                System.out.println("Input: " + testChar);
                System.out.println("Output: " + Node.path +"\n");
                Node.path = "";
                // Get binary string of character
                int ascii = (int) testChar;
                String temp = Integer.toBinaryString(ascii);
                int foo = Integer.parseInt(temp);
                // Format to 8 characters
                String binaryString = String.format("%08d", foo);
                // Add to path string
                huffmanCode += binaryString;
                // Add character to Hashmap
                freqMap.put(tempTestString, 1);
            }
        }
        // Add End of file path to path string
        root = Node.makeHuff(freqMap);
        curNode = Node.search(root, eof);
        Node.path = Node.printPath(root, curNode);
        huffmanCode += Node.path;
        // Split path string into array of 8 characters each
        String[] split = huffmanCode.split("(?<=\\G.{8})");
        // Pad any characters to right of last in string array in case less than 8 characters
        split[split.length-1] = String.format("%-8s", split[split.length-1]).replace(' ', '1');
        // Initialise byte array of same length as split path string
        byte[] bytes = new byte[split.length];
        // Add each split path string to byte array
        for (int i = 0; i < split.length; i++) {
            bytes[i] = ((byte) Integer.parseInt(split[i], 2));
        }
        System.out.println("Enter a name to save your compressed file (Without the file extension):");
        String fileName3 = in.nextLine();
        // Write bytes to file
        try {
            FileOutputStream fos = new FileOutputStream(fileName3+".txt");
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            System.out.println("Error");
        }
        // Get % of compression and print to console
        double x = (double) bytes.length;
        double y = (double) file.length();
        double ratio = (x/y);
        ratio = ratio * 100;
        System.out.println("Your file has been compressed to: " + fileName3 + ".txt");
        System.out.println("Compressed from: " + file.length() + " bytes to " + bytes.length + " bytes." +
                "\nCompressed to " + String.format("%,.2f", ratio) +"% of original file.");
        System.out.println("");
    }
    // --- Method for decompressing ---
    public static void decompress() {
        // Initialise variables
        Scanner in = new Scanner(System.in);
        byte[] data = new byte[1];
        Node root;
        String huffmanText = "";
        // Get filename to decompress
        System.out.println("Enter the file name to decompress (Without the file extension):");
        String fileName = in.nextLine();
        Path filePath = Paths.get(fileName+".txt");
        // Read all bytes and store in byte array
        try {
            data = Files.readAllBytes(filePath);
        }
        catch(Exception e)
        {
            System.out.println("Error getting bytes.");
        }
        // Build string from byte array.
        // Referenced from: https://coderanch.com/t/662356/java/Java-convert-String-binary
        StringBuilder binary = new StringBuilder();
        for (byte b : data)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        testCode = binary.toString();
        testCode = testCode.replaceAll("\\s+","");
        // Add dagger and end of file to hashmap
        freqMap.put("DAG", 1);
        freqMap.put("EOF", 1);
        root = Node.makeHuff(freqMap);
        // Iterate through newly constructed path string and get characters
        while (pointer < testCode.length() && eof == false) {
            huffmanText += Node.findChar(root);
            root = Node.makeHuff(freqMap);
        }
        System.out.println("Enter a name to save your decompressed file (Without the file extension):");
        String fileName2 = in.nextLine();
        // Write decompressed string to file.
        try {
            PrintWriter writer = new PrintWriter(fileName2 + ".txt", "UTF-8");
            writer.println(huffmanText);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing message to file.");
        }
        System.out.println("Your file has been decompressed.");
    }
}
