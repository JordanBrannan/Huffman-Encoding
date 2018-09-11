/**
 * Created by Jordan Brannan - Advanced Algorithms
 */

import java.util.*;

public class Node implements Comparable<Node> {
    // Initialise variables
    Node left;
    Node right;
    Node parent;
    String text;
    int frequency;
    // --- Constructor for text and frequency ---
    public Node(String textIn, int frequencyIn) {
        text = textIn;
        frequency = frequencyIn;
        left = null;
        right = null;
        parent = null;
    }
    // --- Constructor for frequency ---
    public Node(int frequencyIn) {
        text = "";
        frequency = frequencyIn;
        left = null;
        right = null;
        parent = null;
    }
    // --- Compare two nodes frequencies ---
    public int compareTo(Node n) {
        if (frequency < n.frequency) {
            return -1;
        } else if (frequency > n.frequency) {
            return 1;
        }
        return 0;
    }
    // --- Print huffman tree to console recursively ---
    public static void printToConsole(Node n, String indent) {
        if (n.text != "") {
            System.out.println(indent + n.frequency + ":" + n.text);
        } else {
            System.out.println(indent + n.frequency);
        }
        if (n.left != null) {
            printToConsole(n.left, indent + "----");
        }
        if (n.right != null) {
            printToConsole(n.right, indent + "----");
        }
    }
    // --- Find and return node in huffman tree recursively ---
    public static Node search(Node node, String key) {
        if (node != null) {
            if (node.text.equals(key)) {
                return node;
            } else {
                Node foundNode = search(node.left, key);
                if (foundNode == null) {
                    foundNode = search(node.right, key);
                }
                return foundNode;
            }
        } else {
            return null;
        }
    }
    // --- Get right child from Node ---
    public Node getRight() {return right;}
    // --- Get left child from Node ---
    public Node getLeft() {
        return left;
    }
    // --- Global path string ---
    public static String path = "";
    // --- Get path from root node to another specific node recursively ---
    public static String printPath(Node root, Node to) {
        if (root.getLeft() != null && root.getLeft() == to) return "0";// {
        if (root.getRight() != null && root.getRight() == to) return "1"; //{
        if (root.getLeft() != null) {
            String leftResult = printPath(root.getLeft(), to);
            if (leftResult != null) return "0" + leftResult;
        }
        if (root.getRight() != null) {
            String rightResult = printPath(root.getRight(), to);
            if (rightResult != null) return "1" + rightResult;
        }
        return null;
    }
    // --- Find a character based off of a path (For decompressing) ---
    public static String findChar(Node node) {
        // Initialise variables
        Node current = node;
        String returnBin = "";
        String returnChar = "";
        Boolean found = false;
        // Iterate through huffman path based off 0 and 1's to find a character in huffman tree
        while (found == false) {
            // If end of file, return and set boolean to end of file as true
            if (current.text.equals("EOF"))
            {
                huffman.eof = true;
                return "";
            }
            // If left and right are not null it can't be the character
            else if (current.getLeft() != null || current.getRight() != null) {
                String tempChar = String.valueOf(huffman.testCode.charAt(huffman.pointer));
                // If the current pointer is on a 0, get current left child node and move pointer one
                if (tempChar.equals("0")) {
                    current = current.getLeft();
                    huffman.pointer += 1;
                // If the current pointer is on a 1, get current right child node and move pointer one
                } else if (tempChar.equals("1")) {
                    current = current.getRight();
                    huffman.pointer += 1;
                }
                // If hit's a dag, move pointer and add string of the next 8 0's and 1's for exchange to ascii
            } else if (current.text.equals("DAG")) {
                for (int i = 0; i < 8; i++) {
                    returnBin += huffman.testCode.charAt(huffman.pointer);
                    huffman.pointer += 1;
                }
                // Mark as found a character to end while loop and exchange binary to ascii character
                found = true;
                int foo = Integer.parseInt(returnBin, 2);
                char ch = (char) foo;
                returnChar = String.valueOf(ch);
                // Add character to hashmap
                huffman.freqMap.put(returnChar, 1);
            }
            // If nothing else, return the character and add frequency on hashmap
            else {
                returnChar = current.text;
                found = true;
                huffman.freqMap.put(returnChar, huffman.freqMap.get(returnChar) + 1);
            }
        }
        // Return character
        return returnChar;
    }
    // --- Make Huffman Tree ---
    // Referenced from: https://stackoverflow.com/questions/15734922/huffman-tree-with-given-frequency-confuse-as-how-to-start-java
    public static Node makeHuff(Map<String, Integer> huffMap) {
        // Initialise huffman queue of nodes
        PriorityQueue<Node> queue = new PriorityQueue<>();
        // Make all nodes for hashmap keys and values and add to queue
        for (Map.Entry<String, Integer> entry : huffMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Node n = new Node(key, value);
            queue.add(n);
        }
        // Initialise root node
        Node root = null;
        // If the queue has one entry, make a new combination node of only it's frequency
        if (queue.size() == 1) {
            Node node1 = queue.poll();
            Node combined = new Node(node1.frequency);
            combined.left = node1;
            node1.parent = combined;
            // Then add to queue and make root the new combined node
            queue.add(combined);
            root = combined;
        }
        // While there are more than one nodes in the queue, iterate through to create combined nodes
        while (queue.size() > 1) {
            // Pull smallest nodes from queue
            Node least1 = queue.poll();
            Node least2 = queue.poll();
            Node combined = new Node(least1.frequency + least2.frequency);
            combined.right = least1;
            combined.left = least2;
            least1.parent = combined;
            least2.parent = combined;
            // Combine together, add to queue and make node the root
            queue.add(combined);
            root = combined;
        }
        // Return root
        return root;
    }
}
