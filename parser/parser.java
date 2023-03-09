package assignment;

import assignment.lexer;

import java.util.*;

public class parser{

    public static void main(String args[]){

        lexer a = new lexer();

        ArrayList<String> tokens = lexer.tokenize();
        System.out.println(tokens);
    }

}
