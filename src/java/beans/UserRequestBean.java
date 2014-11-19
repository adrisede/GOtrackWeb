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
 * This is a managed bean used to interact with the view layer
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

  /**
   * This function is executed by the only button in index.xhtml
   * It will query the database in order to create the two tabs
   * in unserRequest.xhtml
   * 
   * @return 
   */
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
    /*This satus is going to be displayed as a tooltip in index.xhtml
     to let the user know what the system is doing*/
    setCurrStatus("Starting request");
    
    
    boolean hasdata = computeCount();

    /*Start the database connection*/
    GotrackDB dao = new GotrackDB();
    
    if (symbolsToConsider != null && !userInput.toUpperCase().contains("GO:")) {
        /*update the table that records user searches*/
      dao.updatePopularGenes(userInput, userSpecies);
    }
    if (!userInput.contains("GO:") && !(userInput.contains("go:"))) {
      /*User is looking for a gene symbol*/
      setCurrStatus("Get functionality info");
      
      /*get the go terms associated to this symbol*/
      computeFunctionality();
    }
    if(userInput.toUpperCase().contains("GO")){
      /*User is looking for a single goterm
       there is no use for the functionality tab in this case*/
      setDisableFunctionalityTabPage1(true);      
    }

    dao.closeGotrackConnection();
    
    /*update the status accordingly*/
    if (hasdata) {
      setCurrStatus("");
      /*If the request was succesfull this variable will indicate to the
       framework to jump to the next page*/
      userRequestNextPage = "userRequest?faces-redirect=true";
      return "userRequest?faces-redirect=true";
    } else {
      setCurrStatus("");
      return "";
    }

  }
  
  /**
   * Function called by userRequest.xhtml
   * When user selects the desired GO functionalities. It will call this
   * function to get the GO history
   * 
   * @return 
   */
  public String computeGenesPerGo() {
    boolean hasdata;
    GotrackDB dao = new GotrackDB();
    ArrayList<String> userFuncts = new ArrayList<String>();
    
    /*This variable contains the selected options by user*/
    if (allselectedFunctionsFCPOnt != null) {
      userFuncts.addAll(allselectedFunctionsFCPOnt);
    }
    
    /*Get the go terms that correspond to user input*/
    geneFunctionalities = ViewUtils.getGOterms(userFuncts, geneFunctionalities);
    
    /*Now get the information of the selected go terms*/
    ArrayList<CountGenesPerGoBean> count = dao.countGenesPerGOTerm(geneFunctionalities, userSpecies);   
    
    /*Transform the data from the database to a string that can be understood
     by the google api*/
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
      /*Show the appropriate error if no data*/
      FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "No data found.",
              "Sorry. We don't have information for your request."));
    }
    if (!count.isEmpty()) {
        /*Redirect to the next page to display the information*/
      return "functionality?faces-redirect=true";
    } else {
      return "";
    }
  }

  /**
   * This function will query the database to build the first graphs
   * @return 
   */
  public boolean computeCount() {
   
    GotrackDB dao = new GotrackDB();
    countFig1_2 = null;
    countFig1=null;
    /*User has specified a gene id or gene symbol*/
    if (!userInput.contains("GO:") && !(userInput.contains("go:"))) {
      setCurrStatus("Mapping symbols");
      
      /*Get the ids to consider using the mapping tables*/
      allIdsToConsider = ViewUtils.getAllGenesInHistory(userSpecies, userInput, dao);
      setCurrStatus("Getting edition data");
      
      /*Add userInput to the ids to consider*/
      allIdsToConsider.add(userInput);
      
      /*Count the direct and inferred go terms */
      countFig1_2 = dao.countDirectInferredParents(userSpecies, allIdsToConsider);           
      allIdsToConsider.remove(userInput);
      
      /*Convert the internal data structures into a string tha can be 
       understood by the google api*/
      setGraph1Data(ViewUtils.transformString(countFig1_2, symbolsToConsider));
      if (countFig1_2 == null | (countFig1_2 != null && countFig1_2.isEmpty())) {
          /*Show the appropriate message if no data found*/
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

  /**
   * Function called by computeCount when user input is a go term not a gene 
   * symbol
   * 
   * It will calculate the number of genes per go term
   * @return 
   */
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
    /*count contains the data structure that has all the infromation*/
    return count;
  }
  
  /**
   * Given user input it will query the database to get the evidence code history
   * of the selected go terms
   * @return 
   */
  public boolean computeEvidenceCode() {
    HashSet<String> tmp = new HashSet<String>();
    GotrackDB dao = new GotrackDB();
    List<String> goname = new ArrayList<String>();
    ArrayList<String> goterms = new ArrayList<String>();

    ArrayList<String> genes = new ArrayList<String>();
    
    /*Get user selected go terms*/
    if (allselectedFunctionsFCPOnt != null) {
      goname.addAll(allselectedFunctionsFCPOnt);
    }
    /*Variable geneFunctionalities has all the go terms
     associated to the gene symbol requested by user; later on,
     user can select to explore specific go functionalities, this for loop will
     find the functions selected by user*/
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
    
    /*Query the database to get the information and store it in ecode*/
    ecode = dao.getEvidenceCodeHistory(userSpecies, genes, goterms);

    pubmedtable = new ArrayList<pumedEntryBean>();
    
    /*This for loop will build the string needed by the google api to display
     the information*/
    for (GeneGoGoNames t : geneFunctionalities) {
      for (EvidenceCodeBean ecb : ecode) {
        if (t.getGoterm().compareTo(ecb.getGoterm()) == 0 && uniqueUserGoNames.contains(t.getGoName())) {
          if (ecb.getPubmed() == null || ecb.getPubmed().compareTo("null") == 0) {
              /*No pubmed id associated*/
            ecb.setGoterm(t.getGoName() + "|No_Pub" + "|" + ecb.getUniprot());
          } else {
              /*Pubmed id found*/
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

  /**
   * This function is called by computecount.
   * It will query the database to get the go terms associated to a gene symbol
   * 
   */
  public void computeFunctionality() {
    GotrackDB dao = new GotrackDB();

    if (allIdsToConsider.isEmpty() && !userInput.contains("GO")) {
      allIdsToConsider = ViewUtils.getAllGenesInHistory(userSpecies, userInput, dao);
      ArrayList<CountGOTermsOverTimeBean> count = 
              dao.countDirectInferredParents(userSpecies, allIdsToConsider);
      //ArrayList<countViewBean > countView = ViewUtils.getCount(count);
      setGraph1Data(ViewUtils.transformString(count, symbolsToConsider));
    }
    ArrayList<GeneGoGoNames> resForAll = new ArrayList<GeneGoGoNames>();

    /*Get the go names by ontology*/
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

  public void getCurrentStatus() {
    String currStatus = getCurrStatus();
    FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", currStatus));
  }

  /**
   * Function called by index.xhtml to return what will be the next page to display
   * @return 
   */
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
   * 
   * Function to download the first graph data
   * @return 
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
  
  /**
   * Function called by the view layer to download the first figure data
   * @return 
   */
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
  
  /**
   * Delete local file to clean the server
   * @return 
   */
  public String deletefig1file(){
    if(fig1File!=null){
      fig1File.delete();
    }
    return "";
  }
  
    /**
   * Get pubmed file to download
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
   * Get evidence code history file to download
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
   * Get all genes per go term file to download
   * 
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
   * Get multifunctionality of all genes per species file to download
   * 
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


  /**
   * Clean this variable
   */
  public void deleteSelectedGoNames(){
    allselectedFunctionsFCPOnt = new ArrayList<String>();
  }


}