/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import beans.view.CountGOTermsOverTimeBean;
import beans.view.CountGenesPerGoBean;
import database.GotrackDB;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.StreamedContent;
import utils.DownloadData;
import utils.ViewUtils;
import utils.WebUtils;

/**
 *
 * @author asedeno
 */
@ManagedBean
@SessionScoped
public class UserRequestBean extends UserRequest {

  private String userRequestNextPage = "";

  /**
   * Creates a new instance of UserRequestBean
   */
  public UserRequestBean() {
    setRenderFunctionality(Boolean.FALSE);
    functionsCOnt = new ArrayList<String>();
    functionsFOnt = new ArrayList<String>();
    functionsPOnt = new ArrayList<String>();
    allIdsToConsider = new HashSet<String>();
    symbolsToConsider = new HashSet<String>();

  }

  
  public ArrayList<CountGenesPerGoBean> computeGenesPerGoInternal()
  {    
    GotrackDB dao = new GotrackDB();
    ArrayList<String> userFuncts = new ArrayList<String>();
    if (geneFunctionalities != null) {
      for(GeneGoGoNames func: geneFunctionalities)
      userFuncts.add(func.getGoName());
    }
    geneFunctionalities = ViewUtils.getGOterms(userFuncts, geneFunctionalities);
    ArrayList<CountGenesPerGoBean> count = dao.countGenesPerGOTerm(geneFunctionalities, userSpecies);       
    dao.closeGotrackConnection();
    return count;
  }
  public String computeGenesPerGo() {
    boolean hasdata;
    GotrackDB dao = new GotrackDB();
    ArrayList<String> userFuncts = new ArrayList<String>();
    if (allselectedFunctionsFCPOnt != null) {
      userFuncts.addAll(allselectedFunctionsFCPOnt);
    }
    geneFunctionalities = ViewUtils.getGOterms(userFuncts, geneFunctionalities);
    ArrayList<CountGenesPerGoBean> count = dao.countGenesPerGOTerm(geneFunctionalities, userSpecies);   
    setGraph2Data(ViewUtils.transformStringGenesPerGO(count));
    if (count == null | (count != null && count.isEmpty())) {
      FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "No data found.",
              "Sorry. We don't have information for your request."));

    }
    //geneFunctionalities
    hasdata = computeEvidenceCode();
    dao.closeGotrackConnection();
    
    if(!hasdata){
      FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "No data found.",
              "Sorry. We don't have information for your request."));
    }
    if (!count.isEmpty()) {
      return "functionality?faces-redirect=true";
    } else {
      return "";
    }
  }

  public boolean computeCount() {
    /*Build list of unique functions*/

    GotrackDB dao = new GotrackDB();
    countFig1_2 = null;
    countFig1=null;
    /*User has specified a gene id or gene symbol*/
    if (!userInput.contains("GO:") && !(userInput.contains("go:"))) {
      setCurrStatus("Mapping symbols");
      allIdsToConsider = ViewUtils.getAllGenesInHistory(userSpecies, userInput, dao);
      setCurrStatus("Getting edition data");
      countFig1_2 = dao.countDirectInferredParents(userSpecies, allIdsToConsider);
      setGraph1Data(ViewUtils.transformString(countFig1_2, symbolsToConsider));
      if (countFig1_2 == null | (countFig1_2 != null && countFig1_2.isEmpty())) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "No data found.",
                "Sorry. We don't have information for your request."));
        return false;
      }
    } else {
      /*User is looking for a goterm*/
      userInput = userInput.toUpperCase();
      /*The User has specified a GO term*/
      geneFunctionalities = new ArrayList<GeneGoGoNames>();      
      userInput= userInput.toUpperCase();
      GeneGoGoNames tmp = new GeneGoGoNames("", userInput, "", "");
      tmp.setIsPickedbyUser(true);
      tmp.setGoName(dao.getGOnames(tmp.getGoterm()));
      geneFunctionalities.add(tmp);
      
      setCurrStatus("Getting available data");
      //countFig1 = dao.countGenesPerGOTerm(geneFunctionalities, userSpecies);
      //setGraph1Data(ViewUtils.transformStringGenesPerGO(countFig1).replaceAll("data2", "data"));
      countFig1 = computeGenesPerGoInternal();
      setGraph1Data(ViewUtils.transformStringGenesPerGO(countFig1).replaceAll("data2", "data"));
      
      if (countFig1 == null | (countFig1 != null && countFig1.isEmpty())) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "No data found.",
                "Sorry. We don't have information for your request."));
        return false;
      }
    }
    dao.closeGotrackConnection();    
    return true;
  }

  public boolean computeEvidenceCode() {
    HashSet<String> tmp = new HashSet<String>();
    GotrackDB dao = new GotrackDB();
    List<String> goname = new ArrayList<String>();
    ArrayList<String> goterms = new ArrayList<String>();

    ArrayList<String> genes = new ArrayList<String>();
    if (allselectedFunctionsFCPOnt != null) {
      goname.addAll(allselectedFunctionsFCPOnt);
    }

    for (GeneGoGoNames t : geneFunctionalities) {
      for (String name : goname) {
        if (name.compareTo(t.getGoName()) == 0) {
          goterms.add(t.getGoterm());
          genes.add(t.getUniprot());
        }
      }
    }

    HashSet<String> uniqueUserGoNames = new HashSet<String>();
    for (String name : goname) {
      uniqueUserGoNames.add(name);
    }
    //allIdsToConsider = ViewUtils.getAllGenesInHistory(userSpecies, userInput, dao);
    ecode = dao.getEvidenceCodeHistory(userSpecies, genes, goterms);

    pubmedtable = new ArrayList<pumedEntryBean>();
    for (GeneGoGoNames t : geneFunctionalities) {
      for (EvidenceCodeBean ecb : ecode) {
        if (t.getGoterm().compareTo(ecb.getGoterm()) == 0 && uniqueUserGoNames.contains(t.getGoName())) {
          if (ecb.getPubmed() == null || ecb.getPubmed().compareTo("null") == 0) {
            ecb.setGoterm(t.getGoName() + "|No_Pub" + "|" + ecb.getUniprot());
          } else {
            ecb.setGoterm(t.getGoName() + "|" + ecb.getPubmed() + "|" + ecb.getUniprot());
            if (!tmp.contains(t.getGoName() + "|" + ecb.getPubmed() + "|" + ecb.getUniprot())) {
              pubmedtable.add(new pumedEntryBean(t.getGoName(), ecb.getPubmed(), ecb.getUniprot()));
            }
            tmp.add(t.getGoName() + "|" + ecb.getPubmed() + "|" + ecb.getUniprot());
            //ecb.setGoterm(t.getGoName()+"|<a href=\"http://www.ncbi.nlm.nih.gov/pubmed/?term="+ecb.getPubmed()+"\">"+ecb.getPubmed()+"</a>");
          }
        }
      }
    }
    setGraph3Data(ViewUtils.transformECString(ecode));
    dao.closeGotrackConnection();
    if (ecode.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }

  public void computeFunctionality() {
    GotrackDB dao = new GotrackDB();

    if (allIdsToConsider.isEmpty() && !userInput.contains("GO")) {
      allIdsToConsider = ViewUtils.getAllGenesInHistory(userSpecies, userInput, dao);
      ArrayList<CountGOTermsOverTimeBean> count = dao.countDirectInferredParents(userSpecies, allIdsToConsider);
      //ArrayList<countViewBean > countView = ViewUtils.getCount(count);
      setGraph1Data(ViewUtils.transformString(count, symbolsToConsider));
    }
    ArrayList<GeneGoGoNames> resForAll = new ArrayList<GeneGoGoNames>();

    resForAll.addAll(dao.getGeneGotermGoName(allIdsToConsider, "F", userSpecies));
    resForAll.addAll(dao.getGeneGotermGoName(allIdsToConsider, "C", userSpecies));
    resForAll.addAll(dao.getGeneGotermGoName(allIdsToConsider, "P", userSpecies));

    geneFunctionalities = resForAll;
    UserRequest req = new UserRequest();
    req.setDbResponse(resForAll);
    req.setSpecies(userSpecies);
    req.setSymbol(userInput);
    req.setFunctionsPerOnt(WebUtils.getUniqueFunctions(resForAll));
    //fill bean data for the view layer
    for (FunctionsPerOntology t : req.getFunctionsPerOnt()) {
      for (String f : t.getFunctions()) {
        if (t.getOntology().compareToIgnoreCase("P") == 0) {
          functionsPOnt.add(f);
        }
        if (t.getOntology().compareToIgnoreCase("F") == 0) {
          functionsFOnt.add(f);
        }
        if (t.getOntology().compareToIgnoreCase("C") == 0) {
          functionsCOnt.add(f);
        }
      }
    }
    renderFunctionality = true;
    dao.closeGotrackConnection();
  }
  
  public String computeAllUserRequest() {
    userRequestNextPage = "nodata";
    setCurrStatus("");
    setDisableFunctionalityTabPage1(false);
    functionsCOnt = new ArrayList<String>();
    functionsFOnt = new ArrayList<String>();
    functionsPOnt = new ArrayList<String>();
    allIdsToConsider = new HashSet<String>();
    symbolsToConsider = new HashSet<String>();
    allselectedFunctionsFCPOnt = new ArrayList<String>();
    pubmedtable = new ArrayList<pumedEntryBean>();
    setCurrStatus("Starting request");
    boolean hasdata = computeCount();

    GotrackDB dao = new GotrackDB();
    if (symbolsToConsider != null) {
      dao.updatePopularGenes(symbolsToConsider, userSpecies);
    }
    if (!userInput.contains("GO:") && !(userInput.contains("go:"))) {
      /*User is looking for a gene symbol*/
      setCurrStatus("Get functionality info");
      computeFunctionality();
    }
    if(userInput.contains("GO")){
      /*User is looking for a single goterm
       there is no use for the functionality tab in this case*/
      setDisableFunctionalityTabPage1(true);      
    }

    dao.closeGotrackConnection();
    if (hasdata) {
      setCurrStatus("");
      userRequestNextPage = "userRequest?faces-redirect=true";
      return "userRequest?faces-redirect=true";
    } else {
      setCurrStatus("");
      return "";
    }

  }

  public List<String> completeCellularComponentBiolProcessMolecularFunction(String query) {
    List<String> suggestions = new ArrayList<String>();
    //functionsFOnt
    for (String p : functionsCOnt) {
      String uppercase = p.toUpperCase();
      if (uppercase.startsWith(query.toUpperCase())) {
        suggestions.add(p);
      }
    }
    for (String p : functionsFOnt) {
       String uppercase = p.toUpperCase();
      if (uppercase.startsWith(query.toUpperCase())) {
        suggestions.add(p);
      }
    }

    for (String p : functionsPOnt) {
      String uppercase = p.toUpperCase();
      if (uppercase.startsWith(query.toUpperCase())) {
        suggestions.add(p);
      }
    }
    return suggestions;
  }

  public void addItmsCellComponentBiolProcessMolecularFunction(String itms) {
    boolean isalready = false;
    if (allselectedFunctionsFCPOnt == null) {
      allselectedFunctionsFCPOnt = new ArrayList<String>();
    }
    for (String s : allselectedFunctionsFCPOnt) {
      if (s.compareTo(itms) == 0) {
        isalready = true;
      }
    }
    if (!isalready) {
      allselectedFunctionsFCPOnt.add(itms);
    }
  }

  public void getCurrentStatus() {
    String currStatus = getCurrStatus();
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", currStatus));
  }

  public String getNextPage() {
    if (userRequestNextPage.compareTo("nodata") == 0) {
      return "";
    }
    if (userRequestNextPage.compareTo("userRequest?faces-redirect=true") == 0) {
      return "userRequest?faces-redirect=true";
    }
    return "";
  }
  
    /**
   * @return the filefig1
   */
  public StreamedContent getFilefig1() {
    if(userInput.contains("GO"))
    {
       filefig1=getFileAllGenesPerGoterm();
    }else{
       filefig1=FileDownloadViewFig1();
    }
    return filefig1;
  }
  private StreamedContent FileDownloadViewFig1() {
    try {
      String serverfile = userSpecies + "_" + userInput+".csv";      
      if(countFig1_2!=null)
         fig1File=DownloadData.createTmpFileFig1_2(serverfile, countFig1_2);
      if(countFig1!=null)
        fig1File=DownloadData.createTmpFileFig1(serverfile, countFig1);
      
      return DownloadData.downloadFile(fig1File,serverfile, DownloadData.TXTTYPE);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(UserRequestBean.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  public String deletefig1file(){
    if(fig1File!=null){
      fig1File.delete();
    }
    return "";
  }
  
    /**
   * @return the filepubmedTable
   */
  public StreamedContent getFilepubmedTable() {
    
    try {
      String serverfile = userSpecies +"_pubmed.csv";      
      if(pubmedtable!=null)
         fig1File=DownloadData.createTmpPubmedTable(serverfile, pubmedtable);      
      return DownloadData.downloadFile(fig1File,serverfile, DownloadData.TXTTYPE);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(UserRequestBean.class.getName()).log(Level.SEVERE, null, ex);
    }    
    return filepubmedTable;
  }

    /**
   * @return the fileEvidenceCodeHist
   */
  public StreamedContent getFileEvidenceCodeHist() {
     try {
      String serverfile = userSpecies +"_evidenceCodeHistory.csv";      
      if(ecode!=null)
         fig1File=DownloadData.createTmpEcodeHist(serverfile, ecode);      
      return DownloadData.downloadFile(fig1File,serverfile, DownloadData.TXTTYPE);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(UserRequestBean.class.getName()).log(Level.SEVERE, null, ex);
    }    
    return fileEvidenceCodeHist;
  }

  
  /**
   * @return the fileAllGenesPerGoterm
   */
  public StreamedContent getFileAllGenesPerGoterm() {
    GotrackDB dao = new GotrackDB();
    try {
      String serverfile = userSpecies +"_allGenesPerGoterm.csv";
      
      ArrayList<CountGenesPerGoBean> count = dao.allGenesPerGOTerm(geneFunctionalities, userSpecies);
      
      if(count!=null)
         fig1File=DownloadData.createTmpAllGenesPerGoterm(serverfile, count);      
      return DownloadData.downloadFile(fig1File,serverfile, DownloadData.TXTTYPE);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(UserRequestBean.class.getName()).log(Level.SEVERE, null, ex);
    }finally{
      dao.closeGotrackConnection();
    }    
    return fileAllGenesPerGoterm;
  }

    /**
   * @return the fileAllGenesPerSpeciesMultifunc
   */
  public StreamedContent getFileAllGenesPerSpeciesMultifunc() {
    try {
      String serverfile = userSpecies +"_allgenes_multifunc.csv";      
      if(topMultifuncSpecies!=null)
         fig1File=DownloadData.
                 createTmpAllGenesPerSpeciesOrderedByMultifunc(serverfile, 
                 topMultifuncSpecies);      
      return DownloadData.downloadFile(fig1File,serverfile, DownloadData.TXTTYPE);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(UserRequestBean.class.getName()).log(Level.SEVERE, null, ex);
    } 
    return fileAllGenesPerSpeciesMultifunc;
  }


  
  public void deleteSelectedGoNames(){
    allselectedFunctionsFCPOnt = new ArrayList<String>();
  }


}