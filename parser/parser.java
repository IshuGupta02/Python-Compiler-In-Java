package parser;

import lexer.lexer;

import java.util.*;

public class parser{

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

    public static void checkSyntax(ArrayList<ArrayList<String>> line_seperated_tokens, ArrayList<Integer> tab_count){
        
    }

    public static void main(String args[]){

        lexer a = new lexer();

        ArrayList<String> tokens = lexer.tokenize();

        ArrayList<ArrayList<String>> line_seperated_tokens = seperateLines(tokens);

        ArrayList<Integer> tab_count = removeIntermediateSpaces(line_seperated_tokens);

        checkSyntax(line_seperated_tokens, tab_count);

    }

}
