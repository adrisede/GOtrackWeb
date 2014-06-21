/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;
/**
 *
 * @author asedeno
 */
public class EvidenceCodeBean implements Serializable{
   private String uniprot;
    private String goterm;
    private String pubmed;
    private String evidence;
    private Date dateStart;
    private Date dateEnd;

    public EvidenceCodeBean(){
      
    }
    public EvidenceCodeBean(String uniprot, String goterm, String pubmed, String evidence, Date dateStart) {
        this.uniprot = uniprot;
        this.goterm = goterm;
        this.pubmed = pubmed;
        this.evidence = evidence;
        this.dateStart = dateStart;
        this.dateEnd = DateUtils.addMonths(dateStart, 1); // defaulted 1 month to finish the edition
    }

    public EvidenceCodeBean(String goterm, Date dateStart) {
        this.goterm = goterm;
        this.dateStart = dateStart;
        this.dateEnd = DateUtils.addMonths(dateStart, 1); // defaulted 1 month to finish the edition
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

    public String getPubmed() {
        return pubmed;
    }

    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
        this.dateEnd = DateUtils.addMonths(dateStart, 1); 
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
}
