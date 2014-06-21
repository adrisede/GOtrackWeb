/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;

/**
 *
 * @author asedeno
 */
public class pumedEntryBean implements Serializable{
  private String goname;
  private String id;
  private String geneid;

  pumedEntryBean(String goName, String pubmed,String geneid) {
     this.goname = goName;
     this.id = pubmed;
     this.geneid = geneid;
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
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the geneid
   */
  public String getGeneid() {
    return geneid;
  }

  /**
   * @param geneid the geneid to set
   */
  public void setGeneid(String geneid) {
    this.geneid = geneid;
  }
  
  
}
