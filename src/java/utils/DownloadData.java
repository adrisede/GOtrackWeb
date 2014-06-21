/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import beans.EvidenceCodeBean;
import beans.pumedEntryBean;
import beans.view.CountGOTermsOverTimeBean;
import beans.view.CountGenesPerGoBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author asedeno
 */
public class DownloadData {

  public static String TMPDIR = "/resources/tmp/";
  public static String TXTTYPE = "text/txt";

  public static StreamedContent downloadFile(File f, String userfileName, String filetype) throws FileNotFoundException {
    if (f == null) {
      return null;
    }

    StreamedContent files;
    InputStream stream;
    /*stream = ((ServletContext) FacesContext.getCurrentInstance().
     getExternalContext().getContext()).getResourceAsStream(TMPDIR+serverfile);*/
    stream = new FileInputStream(f);
    files = new DefaultStreamedContent(stream, filetype, userfileName);
    return files;
  }

  /**
   *
   * @param serverFile
   * @param count
   * @return
   */
  public static File createTmpFileFig1(String serverFile, ArrayList<CountGenesPerGoBean> count) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));

      for (CountGenesPerGoBean bean : count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(bean.getDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuilder var = new StringBuilder();
        var.append(String.format("'%s', %s/%s/%s, '%s','%s' \n",
                bean.getGoterm(),
                year,
                month + 1,
                day,
                bean.getAssociatedGenes(),
                bean.getGoname()));
        bw.write(var + "");
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }

  public static File createTmpFileFig1_2(String serverFile, ArrayList<CountGOTermsOverTimeBean> count) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write("Gene,Date,Directs,Inferred, Ratio,Multifunctionality,JaccardSimilarity,Symbol\n");
      for (CountGOTermsOverTimeBean bean : count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(bean.getDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuilder var = new StringBuilder();
        var.append(String.format(" '%s', %s/%s/%s, %s, %s, %s,%s, %s,'%s' \n",
                bean.getGene(),
                year,
                month + 1,
                day,
                bean.getDirects(),
                bean.getInferred(),
                bean.getRate(),
                bean.getMultifunc(),
                bean.getJaccardSim(),
                bean.getSymbol()));
        bw.write(var + "");
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }

  public static File createTmpPubmedTable(String serverFile, List<pumedEntryBean> pubmedtable) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write("Geneid, Goname, PubmedId\n");
      for (pumedEntryBean bean : pubmedtable) {
        StringBuilder var = new StringBuilder();
        var.append(String.format(" '%s', '%s','%s' \n",
                bean.getGeneid(),
                bean.getGoname(), bean.getId()));
        bw.write(var + "");
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }

  public static File createTmpEcodeHist(String serverFile, ArrayList<EvidenceCodeBean> ecode) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write("Geneid, GOterm, PubmedId, EvidenceCode, Date\n");
      for (EvidenceCodeBean bean : ecode) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(bean.getDateStart());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuilder var = new StringBuilder();
        String goname = bean.getGoterm();
        goname = bean.getGoterm().replace(",", "");
        StringTokenizer tok = new StringTokenizer(goname, "|");
        goname = tok.nextToken();
        var.append(String.format(" '%s', '%s','%s','%s' , %s/%s/%s,\n",
                bean.getUniprot(),
                goname,
                bean.getPubmed(),
                bean.getEvidence(),
                 year,
                month + 1,
                day));
        bw.write(var + "");
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }
  
    public static File createTmpAllGenesPerGoterm(String serverFile,  ArrayList<CountGenesPerGoBean> count) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write("Uniprot, Symbol, GO term, GO Name, Date\n");
      for (CountGenesPerGoBean bean : count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(bean.getDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuilder var = new StringBuilder();               
        var.append(String.format(" '%s', '%s','%s','%s' , %s/%s/%s,\n",
                bean.getUniprot(),
                bean.getSymbol(),
                bean.getGoterm(),
                bean.getGoname().replace(",", ""),
                 year,
                month + 1,
                day));
        bw.write(var + "");
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }
    
     public static File createTmpAllGenesPerSpeciesOrderedByMultifunc(
             String serverFile,  List<String> topMultifuncSpecies) {
    File f = null;
    try {
      f = File.createTempFile(serverFile, "tmp");
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write("Position, Uniprot, Symbol\n");
      int i =1;
      for (String bean : topMultifuncSpecies) {        
        StringBuilder var = new StringBuilder();               
        var.append(String.format(i+",%s\n",
                bean.replace("-\t", ",")));
        bw.write(var + "");
        i++;
      }
      bw.close();

    } catch (IOException ex) {
      Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
    }
    return f;
  }
}
