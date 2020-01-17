// A context-free grammar
class CFG {
    int alphabetSize;
    String vars[];
    String R[][];
    String startVar;
}


// A push or pop transition
class StackTrans {
    int source;
    String label;
    int target;
    public StackTrans(int s, String l, int t) {
        source=s; label=l; target=t;
    }
}

// A pushdown automaton
class PDA {
    int alphabetSize;
    String stackAlphabet[];
    int numStates;
    int delta[][];
    StackTrans deltaPop[];
    StackTrans deltaPush[];
    int initialState;
    int finalStates[];
}

class Utils {
    // Print a CFG G, if G is a valid CFG. If not, print error message.
    static void printCFG(CFG G, String name) {
        if (!isValidCFG(G)) {
            System.out.println(name+" is not a valid CFG!");
            return; }
        System.out.print("\n"+name+" = (");

        // print the alphabet ...
        System.out.print("{");
        for(int i=0; i<G.alphabetSize; i++) {
            if(i!=0) System.out.print(", ");
            System.out.print(i);
        }
        System.out.print("}, ");

        // ... and the set of variables ...
        System.out.print("{");
        for(int i=0; i<G.vars.length; i++) {
            if(i!=0) System.out.print(", ");
            System.out.print(G.vars[i]);
        }
        System.out.print("}, R, ");

        // ... and the start variable ...
        System.out.println(G.startVar+")");

        // ... and the set of rules ...
        System.out.println("  where R is the set of rules:");
        for(int i=0; i<G.R.length; i++) {
            System.out.print("  "+G.R[i][0]+" -> ");
            if(G.R[i].length==2 && G.R[i][1].equals("")) System.out.print("eps");
            for(int j=1; j<G.R[i].length; j++) System.out.print(G.R[i][j]+" ");
            System.out.println();
        }

    }

    // Print the A as a six tuple, if A is a valid PDA. If not, print error message.
    static void printPDA(PDA A, String name) {
        if (!isValidPDA(A)) {
            System.out.println(name+" is not a valid PDA!");
            return; }
        System.out.print("\n"+name+" = (");

        // print the alphabet ...
        System.out.print("{");
        for(int i=0; i<A.alphabetSize; i++) {
                if(i!=0) System.out.print(", ");
                System.out.print(i);
            }
        System.out.print("}, ");

        // print the stack alphabet ...
        System.out.print("{");
        for(int i=0; i<A.stackAlphabet.length; i++) {
                if(i!=0) System.out.print(", ");
                System.out.print(A.stackAlphabet[i]);
            }
        System.out.print("}, ");

        // ... and the set of states ...
        System.out.print("{");
        for(int i=0; i<A.numStates; i++) {
                if(i!=0) System.out.print(", ");
                System.out.print("q"+i);
            }
        System.out.print("}, delta, ");

        // ... and the initial state ...
        System.out.print("q"+A.initialState);
        System.out.print(", ");

        // ... and the set of final states
        System.out.print("{");
        for(int i=0; i<A.finalStates.length; i++)
            {
                if(i!=0) System.out.print(", ");
                System.out.print("q"+A.finalStates[i]);
            }
        System.out.print("}");

        System.out.println(")");

        // ... and the transition relation ...
        System.out.println("  where delta is the transition relation:");
        System.out.print("  {");
        int cnt = 0;
        for(int i=0; i<A.delta.length; i++) {
            if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
            if (A.delta[i][1] != -1)
                System.out.print("(q"+A.delta[i][0]+", "+A.delta[i][1]+", q"+A.delta[i][2]+")");
            else
                System.out.print("(q"+A.delta[i][0]+", eps, q"+A.delta[i][2]+")");
        }
        for(int i=0; i<A.deltaPush.length; i++) {
            if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
            if (A.deltaPush[i].label.length() != 0)
                System.out.print("(q"+A.deltaPush[i].source+", push("+A.deltaPush[i].label+"), q"+A.deltaPush[i].target+")");
            else
                System.out.print("(q"+A.deltaPush[i].source+", eps, q"+A.deltaPush[i].target+")");
        }
        for(int i=0; i<A.deltaPop.length; i++) {
                if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
                System.out.print("(q"+A.deltaPop[i].source+", pop("+A.deltaPop[i].label+"), q"+A.deltaPop[i].target+")");
            }
        System.out.println(" }");

    }


    // Checks if G is a valid CFG. If not, it also prints an error message
    static boolean isValidCFG(CFG G) {
        String check=checkCFG(G);
        if(!check.equals("OK")){
            System.out.println("\nGrammar is not OK -- "+check);
            return false;
        }
        return true;
    }

    // Perform checks on a CFG
    // return "OK" if the CFG is OK, otherwise an error message
    static String checkCFG(CFG G) {
        // Check the set of variables is non empty, or null, and contains no duplicates
        if(G.vars==null) return ("Bad set of variables (null)");
        if(G.vars.length<=0) return ("Bad number of variables: "+G.vars.length);
        for(int i=0; i<G.vars.length; i++)
            for(int j=i+1; j<G.vars.length; j++)
                if(G.vars[i].equals(G.vars[j]))
                    return ("This variable appears more than once: "+G.vars[i]);

        // Check that the production rules are all valid
        if(G.R==null) return ("Bad set of rules (null)");
        for(int i=0; i<G.R.length; i++){
            if(G.R[i]==null) return ("Bad rule "+i+" (null)");
            if(G.R[i].length!=2)
                return ("Bad rule length ("+G.R[i].length+") in rule "+i);
            if(!isValidVar(G,G.R[i][0]))
                return ("Bad LHS variable ("+G.R[i][0]+") in rule "+i);
            if(!isValidRuleRHS(G,G.R[i][1]))
                return ("Bad RHS ("+G.R[i][1]+") in rule "+i);
        };

        // Check that start variable is valid
        if(!isValidVar(G,G.startVar))
            return ("Bad start variable: "+G.startVar);

        return "OK";
    }

    static boolean isValidVar(CFG G, String v) {
        for(int i=0; i<G.vars.length; i++){
            if(v.equals(G.vars[i])) return true;
        }
        return false;
    }

    static boolean isValidSymbol(CFG G, String s) {
        // all alphabet symbols are valid symbols
        for(int i=0; i<G.alphabetSize; i++){
            if(s.equals(Integer.toString(i))) return true;
        }
        return false;
    }

    static boolean isValidRuleRHS(CFG G, String s) {
        if(s.length()==0) return true;
        int i = splitter(s);
        if(i==-1) return (isValidVar(G,s)||isValidSymbol(G,s));
        if(i==0||i==(s.length()-1)) return false;
        String x = s.substring(0,i);
        if(!isValidVar(G,x) && !isValidSymbol(G,x)) return false;
        return isValidRuleRHS(G,s.substring(i+1,s.length()));
    }

    // Returns the first occurence of ' ' in s (-1 if none)
    static int splitter(String s) {
        for(int i=0; i<s.length(); i++)
            if(s.substring(i,i+i).equals(" ")) return i;
        return -1;
    }

    // Checks if A is a valid PDA. If not, it also prints an error message
    static boolean isValidPDA(PDA A) {
        String check=checkPDA(A);
        if(!check.equals("OK")){
            System.out.println("\nPDA is not OK -- "+check);
            return false;
        }
        return true;
    }

    // Perform checks on a PDA
    // return "OK" if the PDA is OK, otherwise an error message
    static String checkPDA(PDA A) {

        // Check the alphabet size is valid
        if(A.alphabetSize<0) return ("Bad alphabet size: "+A.alphabetSize);

        // Check the stack alphabet size is valid
        if(A.stackAlphabet==null) return ("Bad stack alphabet (null)");

        // Check the number of states is valid
        if(A.numStates<=0) return ("Bad number of states: "+A.numStates);

        // Check the initial state is a valid state
        if(!isValidState(A,A.initialState))
            return ("Bad inital state: "+A.initialState);

	// Check that the input transition relation is valid
        if(A.delta==null) return ("Bad input transition relation (null)");
    	for(int i=0; i<A.delta.length; i++) {
                if(A.delta[i]==null) return ("Bad input transition "+i+" (null)");
    	    if(A.delta[i].length!=3)
    		return ("Bad transition length ("+A.delta[i].length+") in input transition "+i);
    	    if(!isValidState(A,A.delta[i][0]))
    		return ("Bad state ("+A.delta[i][0]+") in input transition "+i);
    	    if(!isValidState(A,A.delta[i][2]))
    		return ("Bad state ("+A.delta[i][2]+") in input transition "+i);
    	    if(!isValidSymbol(A,A.delta[i][1]) && !(A.delta[i][1]==-1))
    		return ("Bad symbol ("+A.delta[i][1]+") in input transition "+i);
	    }

	// Check that the push transition relation is valid
        if(A.deltaPush==null) return ("Bad push transition relation (null)");
    	for(int i=0; i<A.deltaPush.length; i++) {
    	    if(!isValidState(A,A.deltaPush[i].source))
    		return ("Bad state ("+A.deltaPush[i].source+") in push transition "+i);
    	    if(!isValidState(A,A.deltaPush[i].target))
    		return ("Bad state ("+A.deltaPush[i].target+") in push transition "+i);
    	    if(!isValidPushString(A,A.deltaPush[i].label))
    		return ("Bad string label ("+A.deltaPush[i].label+") in push transition "+i);
	     }

	// Check that the pop transition relation is valid
        if(A.deltaPop==null) return ("Bad pop transition relation (null)");
    	for(int i=0; i<A.deltaPop.length; i++) {
    	    if(!isValidState(A,A.deltaPop[i].source))
    		return ("Bad state ("+A.deltaPop[i].source+") in pop transition "+i);
    	    if(!isValidState(A,A.deltaPop[i].target))
    		return ("Bad state ("+A.deltaPop[i].target+") in pop transition "+i);
    	    if(!isValidPopString(A,A.deltaPop[i].label))
    		return ("Bad string label ("+A.deltaPop[i].label+") in pop transition "+i);
    	}

        // Check that final states are valid
        if(A.finalStates==null) return ("Bad final states (null)");
        for(int i=0; i<A.finalStates.length; i++) {
            if(!isValidState(A,A.finalStates[i]))
                return ("Bad final state: "+A.finalStates[i]);
        }

        return "OK";
    }

    static boolean isValidState(PDA A,int i) {
        return (i>=0 && i<A.numStates);
    }

    static boolean isValidSymbol(PDA A,int a) {
        return (a>=0 && a<A.alphabetSize);
    }

    static boolean isValidStackSymbol(PDA A, String s) {
        for(int i=0; i<A.stackAlphabet.length; i++)
            if(s.equals(A.stackAlphabet[i]))
                return true;

        return false;
    }

    static boolean isValidPopString(PDA A, String s) {
        return isValidStackSymbol(A, s);
    }

    static boolean isValidPushString(PDA A, String s) {
        if(s.length()==0) return true;
        int i = splitter(s);
        if(i==-1) return isValidStackSymbol(A,s);
        if(i==0||i==(s.length()-1)) return false;
        String x = s.substring(0,i);
        if(!isValidStackSymbol(A,x)) return false;
        return isValidPushString(A,s.substring(i+1,s.length()));
    }

}
