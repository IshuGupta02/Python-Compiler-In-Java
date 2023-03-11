package parser;

import lexer.lexer;

import java.util.*;

public class parser{

    public static int expected_tabs = 0;
    public static boolean exact_tabs_required = true;
    public static HashSet<Integer> if_indents = new HashSet<Integer>();

    public static ArrayList<ArrayList<String>> repaceEqualDoubleEquals(ArrayList<ArrayList<String>> tokens){

        ArrayList<ArrayList<String>> answer = new ArrayList<>();

        for(int i=0; i<tokens.size(); i++){
            ArrayList<String> line = tokens.get(i);

            ArrayList<String> line_formated = new ArrayList<>();

            for(int j=0; j<line.size(); j++){
                if(j<line.size()-1 && line.get(j).equals("EQUALS") && line.get(j+1).equals("EQUALS")){
                    line_formated.add("DOUBLE_EQUALS");
                    j++;

                }
                else if(j<line.size()-1 && line.get(j).equals("EXCLAMATION") && line.get(j+1).equals("EQUALS")){
                    line_formated.add("NOT_EQUALS");
                    j++;

                }
                else if(j<line.size()-1 && line.get(j).equals("LESS_THAN") && line.get(j+1).equals("EQUALS")){
                    line_formated.add("LESS_THAN_EQUALS");
                    j++;

                }
                else if(j<line.size()-1 && line.get(j).equals("GREATER_THAN") && line.get(j+1).equals("EQUALS")){
                    line_formated.add("GREATER_THAN_EQUALS");
                    j++;

                }
                else{
                    line_formated.add(line.get(j));
                }
            }

            // System.out.println(line_formated);

            answer.add(line_formated);
        }

        return answer;

    }

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

    public static ArrayList<ArrayList<String>> removeEmptyLines(ArrayList<ArrayList<String>> line_seperated_tokens){

        ArrayList<ArrayList<String>> final_tokens = new ArrayList();

        for(int i=0 ; i<line_seperated_tokens.size(); i++){
            if(line_seperated_tokens.get(i).size()>0){
                final_tokens.add(line_seperated_tokens.get(i));
            }
        }
        
        return final_tokens;
    }

    public static boolean checkIfElse(ArrayList<String> tokens, int tab_count){

        if(tokens.get(0)=="IF"){
            try{
                if(!checkExpression(tokens, 1, tokens.size()-2)){
                    throw new RuntimeException("Invalid ELIF condition");
                }
                if(!tokens.get(tokens.size()-1).equals("COLON")){
                    throw new RuntimeException("Invalid End of line in ELIF");
                }
                if_indents.add(tab_count);

            }
            catch(Exception e){
                throw new RuntimeException("Invalid IF statement");
            }
            
        }
        else if(tokens.get(0)=="ELIF"){
            try{
                if(!if_indents.contains(tab_count)){
                    throw new RuntimeException("ELIF without IF found");
                }
                else{
                    if(!checkExpression(tokens, 1, tokens.size()-2)){
                        throw new RuntimeException("Invalid ELIF condition");
                    }
                    if(!tokens.get(tokens.size()-1).equals("COLON")){
                        throw new RuntimeException("Invalid End of line in ELIF");
                    }
                }

            }
            catch(Exception e){
                throw new RuntimeException("Invalid ELIF statement");
            }
            
        }
        else if(tokens.get(0)=="ELSE"){

            try{
                if(!if_indents.contains(tab_count)){
                    throw new RuntimeException("ELSE without IF found");
                }
                else{
                    removeIfIndentsGreaterThanEqualTo(tab_count);
                }

                if(tokens.get(1).equals("COLON")){
                    exact_tabs_required= true;
                    expected_tabs = tab_count+1;
                }
                else{
                    throw new RuntimeException("Invalid Else statement");
                }

            }
            catch(Exception e){
                throw new RuntimeException("Invalid Else statement");
            }
            
        }
        else{
            return false;
        }

        return true;

    }

    public static boolean checkForLoop(ArrayList<String> tokens, int tab_count){
        // System.out.println(tokens);
        try{
            if(tokens.get(0).equals("FOR")){
                if(tokens.get(1).equals("IDENTIFIER")){
                    if(tokens.get(2).equals("IN")){
                        int i=3;
                        while(!tokens.get(i).equals("COLON")){
                            i++;
                        }

                        if(!tokens.get(i).equals("COLON") || i!=tokens.size()-1){

                            // System.out.println("no1");
                            throw new RuntimeException("Invalid for loop");
                        }
                        
                        expected_tabs = tab_count+1;
                        exact_tabs_required = true;

                        return true;

                    }
                    else{
                        // System.out.println("no2");
                        throw new RuntimeException("Invalid for loop");
                    }
                }
                else{
                    // System.out.println("no3");
                    throw new RuntimeException("Invalid for loop");
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

    public static boolean checkExpression(ArrayList<String> tokens, int first_token, int last_token){
        boolean result = true;
        /*stores digits*/
        Stack<String> st1 = new Stack<>();
        /*stores operators and parantheses*/
        Stack<String> st2 = new Stack<>();
        boolean isTrue = true;

        for (int i = first_token; i <= last_token; i++) {
            String temp = tokens.get(i);
            /*if the character is a digit, we push it to st1*/
            if (isDigit(temp)) {
                st1.push(temp);
                if(isTrue) {
                    isTrue = false;
                }
                else {
                    return false;
                }
            }
            /*if the character is an operator, we push it to st2*/
            else if (isOperator(temp)) {
                st2.push(temp);
                isTrue = true;
            }
            else {
                /*if the character is an opening parantheses we push it to st2*/
                if(isBracketOpen(temp)) {
                    st2.push(temp);
                }
                /*If it is a closing bracket*/
                else {
                    boolean flag = true;
                    /*we keep on removing characters until we find the corresponding
                    open bracket or the stack becomes empty*/
                    while (!st2.isEmpty()) {
                        String c = st2.pop();
                        if (c.equals(getCorrespondingChar(temp))) {
                            flag = false;
                            break;
                        }
                        else {
                            if (st1.size() < 2) {
                                return false;
                            }
                            else {
                                st1.pop();
                            }
                        }
                    }
                    if (flag) {
                        return false;
                    }

                }
            }
        }
        while (!st2.isEmpty()) {
            String c = st2.pop();
            if (!isOperator(c)) {
                return false;
            }
            if (st1.size() < 2) {
                return false;
            }
            else {
                st1.pop();
            }
        }
        if (st1.size() > 1 || !st2.isEmpty()) {
            return false;
        }
        return result;
    }
    public static String getCorrespondingChar(String c) {
        if (c.equals("OPEN_PAREN")) {
            return "CLOSE_PAREN";
        }
        else if (c.equals("OPEN_BRAC")) {
            return "CLOSE_BRAC";
        }
        return "CLOSE_CURLY";
    }

    public static boolean isBracketOpen(String c) {
        if (c.equals("OPEN_PAREN") || c.equals("OPEN_BRAC")|| c.equals("OPEN_CURLY")) {
            return true;
        }
        return false;
    }

    public static boolean isDigit(String c) {
        if (c.equals("CONST_INTEGER") || 
        c.equals("CONST_FALSE") || 
        c.equals("CONST_TRUE") || 
        c.equals("CONST_STRING") ||
        c.equals("IDENTIFIER")) {
            return true;
        }
        return false;
    }

    public static boolean isOperator(String c) {
        if (c.equals("PLUS") || 
        c.equals("MINUS") || 
        c.equals("STAR") || 
        c.equals("SLASH") || 
        c.equals("DOUBLE_EQUALS") || 
        c.equals("NOT_EQUALS") ||
        c.equals("LESS_THAN") ||
        c.equals("GREATER_THAN") ||
        c.equals("LESS_THAN_EQUALS") ||
        c.equals("GREATER_THAN_EQUALS")
        ) {
            return true;
        }
        return false;
    }

    public static boolean checkAssignment(ArrayList<String> tokens, int tab_count){
        
        try{
            if(tokens.get(0).equals("IDENTIFIER")){
                if(tokens.get(1).equals("EQUALS")){
                    if(checkExpression(tokens, 2, tokens.size()-1)){
                        exact_tabs_required = false;
                        expected_tabs = tab_count;
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
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
        if(checkAssignment(tokens, tab_count)){
            expected_tabs = tab_count;
            exact_tabs_required = false;
            return true;
        }

        return false;
    }

    public static void removeIfIndentsGreaterThanEqualTo(int tab_count){
        HashSet<Integer> new_if_indents = new HashSet<>();
        for(int i=0; i<tab_count; i++){
            if(if_indents.contains(i)){
                new_if_indents.add(i);
            }
        }

        if_indents = new_if_indents;
    }

    public static void removeIfIndentsGreaterThan(int tab_count){
        HashSet<Integer> new_if_indents = new HashSet<>();
        for(int i=0; i<=tab_count; i++){
            if(if_indents.contains(i)){
                new_if_indents.add(i);
            }
        }

        if_indents = new_if_indents;
    }

    public static void checkSyntax(ArrayList<ArrayList<String>> line_seperated_tokens, ArrayList<Integer> tab_count){
        
        boolean noError = true;
        for(int i=0; i<line_seperated_tokens.size(); i++){

            try{
                ArrayList<String> tokens = line_seperated_tokens.get(i);
                if(tokens.size()==0) continue;
                
                if(exact_tabs_required){
                    if(tab_count.get(i)!=expected_tabs){
                        throw new RuntimeException("Unexpected Indent at Line number: "+(i+1));
                    }
                }
                else{
                    if(tab_count.get(i)>expected_tabs){
                        throw new RuntimeException("Unexpected Indent at Line number: "+(i+1));
                    }

                }

                // ifelse, for, assignment -> declaration, expression

                if(!checkIfElse(tokens, tab_count.get(i))){
                    if(!checkForLoop(tokens, tab_count.get(i))){
                        if(!checkStatement(tokens, tab_count.get(i))){
                            throw new RuntimeException("Not a valid construct at: "+(i+1));
                        }
                    }
                    removeIfIndentsGreaterThanEqualTo(tab_count.get(i));
                }
                else{
                    removeIfIndentsGreaterThan(tab_count.get(i));
                }


            }
            catch(Exception e){
                noError = false;
                System.out.println("Line: "+(i+1)+" : "+ e);
            }
            

        }

        if(noError){
            System.out.println("All okay in syntax!");
        }


    }

    public static void main(String args[]){

        try{
            lexer a = new lexer();

            ArrayList<String> tokens = lexer.tokenize();

            ArrayList<ArrayList<String>> line_seperated_tokens = seperateLines(tokens);

            // System.out.println(line_seperated_tokens);

            ArrayList<ArrayList<String>> doubleEqualsSorted = repaceEqualDoubleEquals(line_seperated_tokens);

            // System.out.println(doubleEqualsSorted);

            ArrayList<Integer> tab_count = removeIntermediateSpaces(doubleEqualsSorted);

            // ArrayList<ArrayList<String>> final_tokens = removeEmptyLines(line_seperated_tokens);

            checkSyntax(doubleEqualsSorted, tab_count);

        }
        catch(Exception e){
            System.out.println(e);
        }
        

    }

}
