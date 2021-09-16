package de.uni_passau.sds.ecmascript2brics;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class AutomatonFactory {

    public static Automaton create(String string) {
        RegExp regExp = new RegExp(string);
        return regExp.toAutomaton();
    }

}
