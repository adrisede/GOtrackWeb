package beans;

import java.io.Serializable;

public class GeneGoGoNames  implements  Serializable{
        private String uniprot;
        private String goterm;
        private String goName;
  private String ontology;
  
  private Boolean isPickedbyUser=false;
        
        
        public GeneGoGoNames(String uniprot, String goterm, String goName, String ontology){
                this.uniprot=uniprot;
                this.goterm = goterm;
                this.goName=goName;
                this.ontology = ontology;
        }
        
        
        
        public String getUniprot() {
                return uniprot;
        }
        public void setUniprot(String uniprot) {
                this.uniprot = uniprot;
        }
        public String getGoterm() {
                return goterm;
        }
        public void setGoterm(String goterm) {
                this.goterm = goterm;
        }
        public String getGoName() {
                return goName;
        }
        public void setGoName(String goName) {
                this.goName = goName;
        }

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
* @return the isPickedbyUser
*/
  public Boolean getIsPickedbyUser() {
    return isPickedbyUser;
  }

  /**
* @param isPickedbyUser the isPickedbyUser to set
*/
  public void setIsPickedbyUser(Boolean isPickedbyUser) {
    this.isPickedbyUser = isPickedbyUser;
  }

}