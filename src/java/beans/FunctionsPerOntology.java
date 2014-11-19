/*
* Class that implements a row tha contains
 * String ontology;
   ArrayList<String> functions;
*/
package beans;

import java.util.ArrayList;
/**
*
* @author asedeno
*/
public class FunctionsPerOntology {
    private String ontology;
    private ArrayList<String> functions;

    /**
* @return the ontology
*/
    public String getOntology() {
        return ontology;
    }

    /**
* @param ontology the ontology to set
*/
    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    /**
* @return the functions
*/
    public ArrayList<String> getFunctions() {
        return functions;
    }

    /**
* @param functions the functions to set
*/
    public void setFunctions(ArrayList<String> functions) {
        this.functions = functions;
    }
    
    
}
