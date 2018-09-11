/**
 * Created by Jordan Brannan - Advanced Algorithms
 */

import java.util.Scanner;

public class menu {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String TITLE =
                "\n2010226 Advanced Algorithms Coursework\n"+
                        "by Jordan-Brannan\n\n"+
                        "\t********************\n"+
                        "\t1. Q1 (Huffman Encoding) \n" +
                        "\t0. Exit \n"+
                        "\t********************\n\n"+
                        "Please input a single digit (0-1):\n\n";
        System.out.println(TITLE);
        String answer = in.nextLine();
        if (answer.equals("1"))
        {
            huffman.options();
        }
        else if (answer.equals("0"))
        {
            System.exit(0);
        }
    }
}
