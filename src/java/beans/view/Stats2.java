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
public class Stats2 {
  private String annotsIEAtoManual;
  private String annotsGeneralToSpecific;  
  private String notAnnotRatio;
  private String species;
  private Date eddate;

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


}
