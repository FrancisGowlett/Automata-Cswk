class Cfgpda {
    public static void main(String[] args) {

        Utils.printCFG(genCfg0(), "G0");
        Utils.printCFG(genCfg1(), "G1");
        Utils.printPDA(genPda0(), "A0");
        Utils.printPDA(genPda1(), "A1");
        Utils.printPDA(genPda2(), "A2");
        printResultingPDAs();
    }

    static CFG genCfg0() {
        CFG G = new CFG();
        G.alphabetSize = 5;
        G.vars = new String[] { "S", "Y" };
        G.R = new String[][] { { "S", "1 S 1" }, { "S", "3 S 3" }, { "S", "Y" }, { "Y", "4 2" } };
        G.startVar = "S";
        return G;
    }

    static CFG genCfg1() {
        CFG G = new CFG();
        G.alphabetSize = 3;
        G.vars = new String[] {"S", "Y"};
        G.R = new String[][] {
            {"S", "0"},
            {"S", "1 Y"},
            {"S", "2 S"},
            {"Y", "S 0"},
            {"Y", "1 Y 1"},
            {"Y", "2 Y 2"},
            {"Y", ""}
        };
        G.startVar = "S";
        return G;
    }

    static PDA genPda0() {
        PDA A = new PDA();
        A.alphabetSize = 2;
        A.stackAlphabet = new String[] { "$", "0" };
        A.numStates = 6;
        A.delta = new int[][] { { 1, 0, 2 }, { 1, -1, 3 }, { 4, 1, 3 } };
        A.deltaPop = new StackTrans[2];
        A.deltaPop[1] = new StackTrans(3, "0", 4);
        A.deltaPop[0] = new StackTrans(3, "$", 5);
        A.deltaPush = new StackTrans[2];
        A.deltaPush[0] = new StackTrans(0, "$", 1);
        A.deltaPush[1] = new StackTrans(2, "0", 1);
        A.initialState = 0;
        A.finalStates = new int[] { 5 };
        return A;
    }

    static PDA genPda1() {
        PDA A = new PDA();
        A.alphabetSize = 2;
        A.stackAlphabet = new String[] {"$", "0", "1"};
        A.numStates = 8;
        A.delta = new int[][] { {1, 1, 6}, {1, 0, 2}, {1, -1, 3}, {7, 1, 3}, {4, 0, 3} };
        A.deltaPop = new StackTrans[3];
        A.deltaPop[0] = new StackTrans(3, "0", 4);
        A.deltaPop[1] = new StackTrans(3, "1", 7);
        A.deltaPop[2] = new StackTrans(3, "$", 5);
        A.deltaPush = new StackTrans[3];
        A.deltaPush[0] = new StackTrans(0, "$", 1);
        A.deltaPush[1] = new StackTrans(6, "1", 1);
        A.deltaPush[2] = new StackTrans(2, "0", 1);
        A.initialState = 0;
        A.finalStates = new int[] { 5 };
        return A;
    }

    static PDA genPda2() {
        PDA A = new PDA();
        A.alphabetSize = 2;
        A.stackAlphabet = new String[] {"$", "0"};
        A.numStates = 8;
        A.delta = new int[][] { {1, 0, 2}, {2, 1, 6}, {1, -1, 3}, {4, 1, 7}, {7, 0, 3}};
        A.deltaPop = new StackTrans[2];
        A.deltaPop[0] = new StackTrans(3, "0", 4);
        A.deltaPop[1] = new StackTrans(3, "$", 5);
        A.deltaPush = new StackTrans[2];
        A.deltaPush[0] = new StackTrans(0, "$", 1);
        A.deltaPush[1] = new StackTrans(6, "0", 1);
        A.initialState = 0;
        A.finalStates = new int[] { 5 };
        return A;
    }

    static PDA buildPDA(CFG G) {
        PDA A = new PDA();

        // the alphabets are the same
        int n = G.alphabetSize;
        A.alphabetSize = n;

        // the stack alphabet is "$", along with all variables and all letters
        // of the original alphabet
        A.stackAlphabet = new String[n + G.vars.length + 1];
        A.stackAlphabet[0] = "$";
        for (int i = 0; i < G.vars.length; i++)
            A.stackAlphabet[1 + i] = G.vars[i];
        for (int i = 0; i < n; i++)
            A.stackAlphabet[G.vars.length + 1 + i] = Integer.toString(i);

        // the states of the PDA are:
        // - 3 core states: 0, 1, 2
        // - one state for each letter of the alphabet: letter i -> state 3+i
        // - one state for each variable: variable i -> state 3+n+i
        // where n is the size of the alphabet
        A.numStates = 3 + n + G.vars.length;

        // the transitions of the PDA are:
        // - one input transition for each alphabet letter
        // - one push transition from 0 to 1
        // - one push transition per grammar rule
        // - one pop transition from 1 to 2
        // - one pop transition per alphabet symbol and variable
        A.delta = new int[n][];
        A.deltaPush = new StackTrans[1 + G.R.length];
        A.deltaPop = new StackTrans[1 + n + G.vars.length];

        A.initialState = 0;
        A.finalStates = new int[] { 2 };

        // push(S$)
        A.deltaPush[0] = new StackTrans(0, G.startVar + " $", 1);
        // pop($)
        A.deltaPop[0] = new StackTrans(1, "$", 2);

        // Counters for transitions of each kind
        int inpCnt = 0;
        int pushCnt = 1;
        int popCnt = 1;

        // add transitions for each alphabet letter
        for (int i = 0; i < n; i++) {
            // For each alphabet letter i, two transitions are added:
            // - a pop(i) transition from state 1 to state 3+i
            // - a transition from state 3+i to 1 that inputs i
            int letterState = 3 + i;
            // input transition for the i-th alphabet letter
            A.delta[inpCnt] = new int[]{letterState, i, 1 };
            // corresponding pop transition
            A.deltaPop[popCnt] = new StackTrans(1, Integer.toString(i), letterState);

            popCnt++;
            inpCnt++;
        }

        // Add transitions for each variable
        for (int i = 0; i < G.vars.length; i++) {
            // For each variable i, we add:
            // - one pop transition from state 1 to state 3+n+i
            // - for each grammar rule of i, one push/epsilon transition from 3+n+i to 1
            // where n is the size of the alphabet
            int varState = 3 + n + i;

            // Here is the pop transition from 1 to 3+n+i
            A.deltaPop[popCnt] = new StackTrans(1, G.vars[i], varState);
            popCnt++;

            // Loop through all grammar rules and, for each rule with variable i
            // on the LHS, add a push/epsilon transition from state 3+n+i to state 1
            for (int j = 0; j < G.R.length; j++) {
                String[] rule = G.R[j];
                if (rule[0].equals(G.vars[i])) { // check G.vars[i]
                    A.deltaPush[pushCnt] = new StackTrans(varState, rule[1], 1);
                    pushCnt++;
                }
            }
        }
        return A;
    }

    static void printResultingPDAs() {
        Utils.printPDA(buildPDA(genCfg0()), "CFG 0");
        Utils.printPDA(buildPDA(genCfg1()), "CFG 1");
    }

}
