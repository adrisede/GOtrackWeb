/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.view;

/**
 *
 * @author asedeno
 */
public class UserGoTermsSelected {
  private String goname;
  private String displayGoName;
  private String goterm;

  
  public UserGoTermsSelected(String goname, String goterm){
    this.goname = goname;
    this.displayGoName = goname.substring(0,Math.min(goname.length(), 46));
    this.goterm = goterm;
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
   * @return the displayGoName
   */
  public String getDisplayGoName() {
    return displayGoName;
  }

  /**
   * @param displayGoName the displayGoName to set
   */
  public void setDisplayGoName(String displayGoName) {
    this.displayGoName = displayGoName;
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
  
}
