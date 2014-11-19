/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package utils;

import beans.FunctionsPerOntology;
import beans.GeneGoGoNames;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
*
* @author asedeno
*/
public class WebUtils {

   
    /**
     * For each ontology this function gets the unique functions 
     * @param original
     * @return 
     */
    public static ArrayList<FunctionsPerOntology> getUniqueFunctions(ArrayList<GeneGoGoNames> original) {
        ArrayList<FunctionsPerOntology> res = new ArrayList<FunctionsPerOntology>();
        Set uniqueFunctForP = new HashSet(20);
        Set uniqueFunctForF = new HashSet(20);
        Set uniqueFunctForC = new HashSet(20);
        for (GeneGoGoNames g : original) {
            if (g.getOntology().compareToIgnoreCase("F") == 0) {
                uniqueFunctForF.add(g.getGoName());
            }
            if (g.getOntology().compareToIgnoreCase("P") == 0) {
                uniqueFunctForP.add(g.getGoName());
            }
            if (g.getOntology().compareToIgnoreCase("C") == 0) {
                uniqueFunctForC.add(g.getGoName());
            }
        }
               
        ArrayList uniqueFuncF = new ArrayList();
        Iterator iter = uniqueFunctForF.iterator();
        while (iter.hasNext()) {
            uniqueFuncF.add(iter.next());
        }
        FunctionsPerOntology functionF = new FunctionsPerOntology();
            functionF.setOntology("F");
        functionF.setFunctions(uniqueFuncF);
        
        ArrayList uniqueFuncC = new ArrayList();
        Iterator iter2 = uniqueFunctForC.iterator();
        while (iter2.hasNext()) {
            uniqueFuncC.add(iter2.next());
        }
        FunctionsPerOntology functionC = new FunctionsPerOntology();
            functionC.setOntology("C");
        functionC.setFunctions(uniqueFuncC);

                ArrayList uniqueFuncP = new ArrayList();
        Iterator iter3 = uniqueFunctForP.iterator();
        while (iter3.hasNext()) {
            uniqueFuncP.add(iter3.next());
        }
        FunctionsPerOntology functionP = new FunctionsPerOntology();
            functionP.setOntology("P");
        functionP.setFunctions(uniqueFuncP);
        
        res.add(functionP);
        res.add(functionC);
        res.add(functionF);
        
        return res;
    }
}