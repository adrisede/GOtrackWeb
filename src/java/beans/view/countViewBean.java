/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package beans.view;

import java.util.ArrayList;

/**
*
* @author
*/
public class countViewBean {
    private String gene;
    private ArrayList<String> directs;
    private ArrayList<String> inferred;
    private ArrayList<String> rate;

    /**
* @return the gene
*/
    public String getGene() {
        return gene;
    }

    /**
* @param gene the gene to set
*/
    public void setGene(String gene) {
        this.gene = gene;
    }

    /**
* @return the directs
*/
    public ArrayList<String> getDirects() {
        return directs;
    }

    /**
* @param directs the directs to set
*/
    public void setDirects(ArrayList<String> directs) {
        this.directs = directs;
    }

    /**
* @return the inferred
*/
    public ArrayList<String> getInferred() {
        return inferred;
    }

    /**
* @param inferred the inferred to set
*/
    public void setInferred(ArrayList<String> inferred) {
        this.inferred = inferred;
    }

    /**
* @return the rate
*/
    public ArrayList<String> getRate() {
        return rate;
    }

    /**
* @param rate the rate to set
*/
    public void setRate(ArrayList<String> rate) {
        this.rate = rate;
    }
    
}
