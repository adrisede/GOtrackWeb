/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package beans.view;

import java.io.Serializable;
import java.util.Date;
/**
*
* @author asedeno
*/
public class CountGOTermsOverTimeBean implements Serializable{
    private String gene;
    private String symbol;
    private Float directs;
    private Float inferred;
    private Date date;
    private Integer edition;
    private Double rate;
    private Double multifunc;
    private Double jaccardSim;
    
    

    public CountGOTermsOverTimeBean(){
        
    }
    /**
*
* @param gene
* @param symbol
* @param directs
* @param inferred
* @param date
* @param edition
* @param rate
*/
    public CountGOTermsOverTimeBean(String gene, String symbol, Float directs, Float inferred, Date date, Integer edition, Double rate) {
        this.gene = gene;
        this.symbol = symbol;
        this.directs = directs;
        this.inferred = inferred;
        this.date = date;
        this.edition = edition;
        this.rate = rate;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Float getDirects() {
        return directs;
    }

    public void setDirects(Float directs) {
        this.directs = directs;
    }

    public Float getInferred() {
        return inferred;
    }

    public void setInferred(Float inferred) {
        this.inferred = inferred;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getEdition() {
        return edition;
    }

    public void setEdition(Integer edition) {
        this.edition = edition;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

  /**
* @return the multifunc
*/
  public Double getMultifunc() {
    return multifunc;
  }

  /**
* @param multifunc the multifunc to set
*/
  public void setMultifunc(Double multifunc) {
    this.multifunc = multifunc;
  }

  /**
   * @return the jaccardSim
   */
  public Double getJaccardSim() {
    return jaccardSim;
  }

  /**
   * @param jaccardSim the jaccardSim to set
   */
  public void setJaccardSim(Double jaccardSim) {
    this.jaccardSim = jaccardSim;
  }
}