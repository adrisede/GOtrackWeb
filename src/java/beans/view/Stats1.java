/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.view;

import java.util.Date;

/**
 *
 * @author asedeno
 */
public class Stats1 {
  private String meanDirects;
  private String meanMf;
  private String meanJaccard;
  private String uniqueGenes;
  private String notAnnotRatio;
  private String edition;
  private String species;
  private String annotsIEAtoManual;
  private String annotsGeneralToSpecific;
  private String meanInferred;
  private Date eddate;

  /**
   * @return the meanMf
   */
  public String getMeanMf() {
    return meanMf;
  }

  /**
   * @param meanMf the meanMf to set
   */
  public void setMeanMf(String meanMf) {
    this.meanMf = meanMf;
  }

  /**
   * @return the meanJaccard
   */
  public String getMeanJaccard() {
    return meanJaccard;
  }

  /**
   * @param meanJaccard the meanJaccard to set
   */
  public void setMeanJaccard(String meanJaccard) {
    this.meanJaccard = meanJaccard;
  }

  /**
   * @return the uniqueGenes
   */
  public String getUniqueGenes() {
    return uniqueGenes;
  }

  /**
   * @param uniqueGenes the uniqueGenes to set
   */
  public void setUniqueGenes(String uniqueGenes) {
    this.uniqueGenes = uniqueGenes;
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
   * @return the species
   */
  public String getSpecies() {
    return species;
  }

  /**
   * @param species the species to set
   */
  public void setSpecies(String species) {
    this.species = species;
  }


  /**
   * @return the eddate
   */
  public Date getEddate() {
    return eddate;
  }

  /**
   * @param eddate the eddate to set
   */
  public void setEddate(Date eddate) {
    this.eddate = eddate;
  }

  /**
   * @return the meanDirects
   */
  public String getMeanDirects() {
    return meanDirects;
  }

  /**
   * @param meanDirects the meanDirects to set
   */
  public void setMeanDirects(String meanDirects) {
    this.meanDirects = meanDirects;
  }

  /**
   * @return the notAnnotRatio
   */
  public String getNotAnnotRatio() {
    return notAnnotRatio;
  }

  /**
   * @param notAnnotRatio the notAnnotRatio to set
   */
  public void setNotAnnotRatio(String notAnnotRatio) {
    this.notAnnotRatio = notAnnotRatio;
  }

  /**
   * @return the annotsIEAtoManual
   */
  public String getAnnotsIEAtoManual() {
    return annotsIEAtoManual;
  }

  /**
   * @param annotsIEAtoManual the annotsIEAtoManual to set
   */
  public void setAnnotsIEAtoManual(String annotsIEAtoManual) {
    this.annotsIEAtoManual = annotsIEAtoManual;
  }

  /**
   * @return the annotsGeneralToSpecific
   */
  public String getAnnotsGeneralToSpecific() {
    return annotsGeneralToSpecific;
  }

  /**
   * @param annotsGeneralToSpecific the annotsGeneralToSpecific to set
   */
  public void setAnnotsGeneralToSpecific(String annotsGeneralToSpecific) {
    this.annotsGeneralToSpecific = annotsGeneralToSpecific;
  }

  /**
   * @return the meanInferred
   */
  public String getMeanInferred() {
    return meanInferred;
  }

  /**
   * @param meanInferred the meanInferred to set
   */
  public void setMeanInferred(String meanInferred) {
    this.meanInferred = meanInferred;
  }
  
  
}
