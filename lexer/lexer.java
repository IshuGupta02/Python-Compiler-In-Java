package assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;


public class lexer{

    private static final String EMPTY_STRING = "";
 
    public static String ltrim(String str) {
        return str.replaceAll("^\\s+", EMPTY_STRING);
    }
 
    public static String rtrim(String str) {
        return str.replaceAll("\\s+$", EMPTY_STRING);
    }

    public static boolean checkInteger(String token, ArrayList<String> tokens){
        for(int i=0; i<token.length(); i++){
            if(!(token.charAt(i)>='0' && token.charAt(i)<='9')){
                return false;
            }
        }

        tokens.add("CONST_INTEGER");
        return true;

    }

    public static boolean checkBoolean(String token, ArrayList<String> tokens){
        if(token.equals("False")){
            tokens.add("CONST_FALSE");
            return true;

        }
        else if(token.equals("True")){
            tokens.add("CONST_TRUE");
            return true;

        }

        return false;

    }
    public static boolean checkString(String token, ArrayList<String> tokens){
        if((token.charAt(0)=='"' && token.charAt(token.length()-1)=='"')){
            for(int i=1; i<token.length()-1; i++){
                if(token.charAt(i)=='"'){
                    return false;
                }
            }

            tokens.add("CONST_STRING");

            return true;
            
        }

        if((token.charAt(0)=='\'' && token.charAt(token.length()-1)=='\'')){
            for(int i=1; i<token.length()-1; i++){
                if(token.charAt(i)=='\''){
                    return false;
                }
            }

            tokens.add("CONST_STRING");

            return true;
            
        }

        return false;

    }
   
    public static boolean checkIdentifier(String token, ArrayList<String> tokens){

        if(token.charAt(0)>='0' && token.charAt(0)<='9'){
            return false;
        }
        for(int i=1; i<token.length(); i++){
            if(!(
                (token.charAt(i)>='a' && token.charAt(i)<='z')||
                (token.charAt(i)>='A' && token.charAt(i)<='Z')||
                (token.charAt(i)>='0' && token.charAt(i)<='9')||
                (token.charAt(i)=='_')
            
            )){
                return false;

            }
        }

        tokens.add("IDENTIFIER");
        return true;

    }

    public static boolean checkOperator(String token, ArrayList<String> tokens){
        // ':', '=', '+', '-', '/', '*', '==', '(', ')', '[', ']', '{', '}'
        if(token.equals(":")){
            tokens.add("COLON");
            return true;
        }
        else if(token.equals("=")){
            tokens.add("EQUALS");
            return true;
        }
        else if(token.equals("+")){
            tokens.add("PLUS");
            return true;
        }
        else if(token.equals("-")){
            tokens.add("MINUS");
            return true;
        }
        else if(token.equals("/")){
            tokens.add("SLASH");
            return true;
        }
        else if(token.equals("*")){
            tokens.add("STAR");
            return true;
        }
        else if(token.equals("==")){
            tokens.add("DOUBLE_EQUALS");
            return true;
        }
        else if(token.equals("(")){
            tokens.add("OPEN_PAREN");
            return true;
        }
        else if(token.equals(")")){
            tokens.add("CLOSE_PAREN");
            return true;
        }
        else if(token.equals("[")){
            tokens.add("OPEN_BRAC");
            return true;
        }
        else if(token.equals("]")){
            tokens.add("CLOSE_BRAC");
            return true;
        }
        else if(token.equals("{")){
            tokens.add("OPEN_CURLY");
            return true;
        }
        else if(token.equals("}")){
            tokens.add("CLOSE_CURLY");
            return true;
        }
        
        return false;


    }

    public static boolean checkKeyword(String token, ArrayList<String> tokens){

        if(token.equals("if")){
            tokens.add("IF");
            return true;

        }
        else if(token.equals("else")){
            tokens.add("ELSE");
            return true;

        }
        else if(token.equals("elif")){
            tokens.add("FALSE");
            return true;

        }
        else if(token.equals("for")){
            tokens.add("FOR");
            return true;

        }
        else if(token.equals("pass")){
            tokens.add("PASS");
            return true;
        }
        else if(token.equals("in")){
            tokens.add("IN");
            return true;
        }

        return false;

    }

    public static boolean fillToken(String tokenFound, ArrayList<String> tokens){
        if(!checkKeyword(tokenFound, tokens)){
            if(!checkInteger(tokenFound, tokens)){
                if(!checkBoolean(tokenFound, tokens)){
                    if(!checkString(tokenFound, tokens)){
                        if(!checkIdentifier(tokenFound, tokens)){
                            return false;
                    
                        }
                    }
                }
            }
            
        }

        return true;

    }

    public static void checkToken(String token, ArrayList<String> tokens){

        int lastToken = -1;

        for(int i=0; i<token.length(); i++){
            if(Arrays.asList(':', '=', '+', '-', '/', '*', '(', ')', '[', ']', '{', '}').contains(token.charAt(i))){
                String tokenFound = token.substring(lastToken+1,i);
                
                if(lastToken<i-1 && !fillToken(tokenFound, tokens)){
                    // error
                }
                if(!checkOperator(token.charAt(i)+"", tokens)){
                    //error
                }

                lastToken = i;
                
            }
            else{

            }
        }

        if(lastToken!=token.length()-1){
            String token_left = token.substring(lastToken+1,token.length());
                
            if(!fillToken(token_left, tokens)){
                // error
            }
        }
        
        
    }

    public static ArrayList<String> tokenize(){

        ArrayList<String> tokens = new ArrayList<>();
        try {
            File myObj = new File("samplePython.py");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = rtrim(data);

                int spaceCount = 0;
                int lastToken = -1;

                for(int i=0; i<data.length(); i++){
                    if(data.charAt(i)==' '){
                        if(spaceCount == 0 && lastToken+1<i){
                            checkToken(data.substring(lastToken+1, i), tokens);
                        }

                        spaceCount++;
                        lastToken = i;

                        if(spaceCount == 4){
                            tokens.add("TAB");
                            spaceCount = 0;
                        }
                    }
                    else{

                        for(int j=0; j<spaceCount; j++){
                            tokens.add("SPACE");
                            lastToken = i-1;
                        }

                        spaceCount = 0;
                    }
                }

                if(lastToken!=data.length()-1){
                    checkToken(data.substring(lastToken+1,data.length()), tokens);
                }


                if(myReader.hasNextLine()) tokens.add("NEWLINE");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return tokens;

    }

    public static void main(String[] args){

        // ArrayList<String> tokens = tokenize();
        // System.out.println(tokens);
        
    }
}
