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
public class CountGenesPerGoBean implements Serializable{
  private String goterm;
  private String goname;
  private String associatedGenes;
  private String edition;
  private Date date ;
  /*to download all genes per goterm*/
  protected String uniprot;
  protected String symbol;
  

  
  
  
  public CountGenesPerGoBean(String goterm, String value, String edition, Date date, String goname){
    this.goterm = goterm;
    this.associatedGenes = value;
    this.edition = edition;
    this.date = date;
    this.goname = goname;
  }
   public CountGenesPerGoBean(){
    
  }
  
  /**
* @return the goterm
*/
  public String getGoterm() {
    return goterm;
  }

  /**
* @param goterm the goterm to set
*/
  public void setGoterm(String goterm) {
    this.goterm = goterm;
  }

  /**
* @return the associatedGenes
*/
  public String getAssociatedGenes() {
    return associatedGenes;
  }

  /**
* @param associatedGenes the associatedGenes to set
*/
  public void setAssociatedGenes(String associatedGenes) {
    this.associatedGenes = associatedGenes;
  }

  /**
* @return the edition
*/
  public String getEdition() {
    return edition;
  }

  /**
* @param edition the edition to set
*/
  public void setEdition(String edition) {
    this.edition = edition;
  }

  /**
* @return the goname
*/
  public String getGoname() {
    return goname;
  }

  /**
* @param goname the goname to set
*/
  public void setGoname(String goname) {
    this.goname = goname;
  }

  /**
* @return the date
*/
  public Date getDate() {
    return date;
  }

  /**
* @param date the date to set
*/
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * @return the uniprot
   */
  public String getUniprot() {
    return uniprot;
  }

  /**
   * @param uniprot the uniprot to set
   */
  public void setUniprot(String uniprot) {
    this.uniprot = uniprot;
  }

  /**
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * @param symbol the symbol to set
   */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  
}