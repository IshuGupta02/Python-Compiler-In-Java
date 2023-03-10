package parser;

import lexer.lexer;

import java.util.*;

public class parser{

    public static int expected_tabs = 0;
    public static boolean exact_tabs_required = true;
    // public static boolean ifFound = false;

    public static ArrayList<ArrayList<String>> seperateLines(ArrayList<String> tokens){
        ArrayList<ArrayList<String>> new_list = new ArrayList();
        if(tokens.size()==0){
            return new_list;
        }

        ArrayList<String> list_= new ArrayList();
        for(int i=0; i<tokens.size(); i++){
            if(tokens.get(i).equals("NEWLINE")){
                new_list.add(list_);
                list_ = new ArrayList();
            }
            else{
                list_.add(tokens.get(i));
            }
        }

        new_list.add(list_);

        return new_list;

    }

    public static ArrayList<Integer> removeIntermediateSpaces(ArrayList<ArrayList<String>> tokens) {

        ArrayList<Integer> tabCount = new ArrayList();

        for(int i=0; i<tokens.size(); i++){
            boolean nonSpaceFound = false;

            ArrayList<String> old_list = tokens.get(i);
            ArrayList<String> new_list = new ArrayList();

            int tabs = 0;

            for(int j=0; j<old_list.size(); j++){
                if(old_list.get(j).equals("TAB")){
                    if(!nonSpaceFound){
                        tabs++;
                    }
                }
                else if(old_list.get(j).equals("SPACE")){
                    if(!nonSpaceFound){
                        throw new RuntimeException("Unexpected Indent at Line number: "+i);
                    }
                }
                else{
                    nonSpaceFound = true;
                    new_list.add(old_list.get(j));
                }
            }
            
            tabCount.add(tabs);
            tokens.set(i, new_list);

        }

        return tabCount;
    }

    public static void removeEmptyLines(ArrayList<ArrayList<String>> line_seperated_tokens){

        ArrayList<ArrayList<String>> final_tokens = new ArrayList();

        for(int i=0 ; i<line_seperated_tokens.size(); i++){
            if(line_seperated_tokens.get(i).size()>0){
                final_tokens.add(line_seperated_tokens.get(i));
            }
        }
        
        return final_tokens;
    }

    public static boolean checkIfElse(ArrayList<String> tokens, int tab_count){
        // if(tokens.get(0)=="IF"){
        //     // ifFound = true;

        // }
        // else if(tokens.get(0)=="ELIF"){
        //     if(!ifFound){
        //         throw new RuntimeException("ELIF without IF found");
        //     }
        //     else{

        //     }
        // }
        // else if(tokens.get(0)=="ELSE"){
        //     if(!ifFound){
        //         throw new RuntimeException("ELIF without IF found");
        //     }
        //     else{
                
        //     }
        // }
        // else{
        //     return false;

        // }

        return false;

    }

    public static boolean checkForLoop(ArrayList<String> tokens, int tab_count){
        try{
            if(tokens.get(0).equals("FOR")){
                if(tokens.get(1).equals("IDENTIFIER")){
                    if(tokens.get(2).equals("IN")){
                        int i=3;
                        while(!tokens.get(i).equals("COLON")){
                            i++;
                        }

                        if(!tokens.get(i).equals("COLON") || i!=tokens.size()-1){
                            return new RuntimeException("Invalid for loop");
                        }
                        
                        expected_tabs = tab_count+1;
                        exact_tabs_required = true;

                        return true;

                    }
                    else{
                        return new RuntimeException("Invalid for loop");
                    }
                }
                else{
                    return new RuntimeException("Invalid for loop");
                }
            }
            else{
                return false;
            }

        }
        catch(Exception e){
            return false;
        }
    }

    public static boolean checkExpression(ArrayList<String> tokens, int first_token){
        return true;
    }

    public static boolean checkAssignment(ArrayList<String> tokens, int tab_count){
        
        try{
            if(token.get(0).equals("IDENTIFIER")){
                if(token.get(1).equals("EQUALS")){
                    if(checkExpression(tokens, 2)){
                        exact_tabs_required = false;
                        expected_tabs = tab_count;
                        return true;
                    }
                }

            }
            else{
                return false;
            }

        }
        catch(Exception e){
            return false;

        }

    }

    public static boolean checkStatement(ArrayList<String> tokens, int tab_count){
        // return false;
        return checkAssignment(tokens, tab_count);
    }

    public static void checkSyntax(ArrayList<ArrayList<String>> line_seperated_tokens, ArrayList<Integer> tab_count){
        
        for(int i=0; i<line_seperated_tokens.size(); i++){
            ArrayList<String> tokens = line_seperated_tokens.get(i);
            if(token.size()==0) continue;
            
            if(exact_tabs_required){
                if(tab_count.get(i)!=expected_tabs){
                    throw RuntimeException("Unexpected Indent at Line number: "+i);
                }
            }
            else{
                if(tab_count.get(i)>expected_tabs){
                    throw RuntimeException("Unexpected Indent at Line number: "+i);
                }

            }

            // ifelse, for, assignment -> declaration, expression

            if(!checkIfElse(tokens, tab_count.get(i))){
                if(!checkForLoop(tokens, tab_count.get(i))){
                    if(!checkStatement(tokens, tab_count.get(i))){
                        throw new RuntimeException("Not a valid syntax at: "+i);
                    }
                }
            }

        }

        System.out.println("All okay in syntax!");

    }

    public static void main(String args[]){

        try{
            lexer a = new lexer();

            ArrayList<String> tokens = lexer.tokenize();

            ArrayList<ArrayList<String>> line_seperated_tokens = seperateLines(tokens);

            ArrayList<Integer> tab_count = removeIntermediateSpaces(line_seperated_tokens);

            // ArrayList<ArrayList<String>> final_tokens = removeEmptyLines(line_seperated_tokens);

            checkSyntax(line_seperated_tokens, tab_count);

        }
        catch(Exception e){
            System.out.println(e);
        }
        

    }

}
