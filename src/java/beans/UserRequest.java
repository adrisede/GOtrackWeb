/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package beans;

import beans.view.CountGOTermsOverTimeBean;
import beans.view.CountGenesPerGoBean;
import database.GotrackDB;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.primefaces.model.StreamedContent;


/**
*
* @author asedeno
*/
public class UserRequest implements java.io.Serializable {
  /*Internal variables*/
  protected ArrayList<CountGenesPerGoBean> countFig1;
  protected ArrayList<CountGOTermsOverTimeBean> countFig1_2;
  protected File fig1File;
  protected ArrayList<GeneGoGoNames> dbResponse ;
  protected String symbol;
  protected String species;
  protected ArrayList<FunctionsPerOntology> functionsPerOnt;
  protected HashSet<String> allIdsToConsider;
  protected String c_ontology;
  protected String p_ontology;
  protected String m_ontology;
  protected Boolean renderFunctionality;
  protected ArrayList<GeneGoGoNames> geneFunctionalities;
  protected List<String> genesPerGOtermList;
  protected String GOTerm;
  protected HashSet<String> symbolsToConsider;
  protected Boolean disableFunctionalityTabPage1=false;
  
  /*Variables for the view layer*/
  protected String graph1Data;
  protected String graph2Data;
  protected String graph3Data;
  protected String userInput;
  protected String userSpecies;
  protected List<String> selectedOntOptions;
  protected ArrayList<String> functionsFOnt;
  protected ArrayList<String> functionsPOnt;
  protected ArrayList<String> functionsCOnt;
  protected List<String> selectedFunctionsFOnt;
  protected List<String> selectedFunctionsCOnt;
  protected List<String> selectedFunctionsPOnt;
  protected List<String> allselectedFunctionsFCPOnt;  
  protected List<pumedEntryBean> pubmedtable;  
  private String[] popularGenes;
  /**Top genes ordered by multifunctionality score all species*/
  private List<String> topMultifunc; 
  
  /**Top genes ordered by multifunc score for a species*/
  protected List<String> topMultifuncSpecies; 
  
  protected Integer progress;  
  private String currStatus;
  protected StreamedContent filefig1;
  protected StreamedContent filepubmedTable;
  protected StreamedContent fileEvidenceCodeHist;
  protected StreamedContent fileAllGenesPerGoterm;
  
  /*File that will contain all the genes in one species ordered by multifunc
   in the last edition*/
  protected StreamedContent fileAllGenesPerSpeciesMultifunc;
  private ArrayList<String> allIdsToConsiderUser; 
  protected ArrayList<EvidenceCodeBean> ecode;
    

  public UserRequest() {
   
  }
   
  public List<String> getGenesPerGOtermList() {
    return genesPerGOtermList;
  }

  public void setGenesPerGOtermList(List<String> genesPerGOtermList) {
    this.genesPerGOtermList = genesPerGOtermList;
  }

  public String getGOTerm() {
    return GOTerm;
  }

  public void setGOTerm(String GOTerm) {
    this.GOTerm = GOTerm;
  }
  
  
  public boolean isEmpty(){
      for(FunctionsPerOntology t : functionsPerOnt){
          if(t.getFunctions().size()>0)
              return false;
      }
      return true;
  }

    /**
* @return the dbResponse
*/
    public ArrayList<GeneGoGoNames> getDbResponse() {
        return dbResponse;
    }

    /**
* @param dbResponse the dbResponse to set
*/
    public void setDbResponse(ArrayList<GeneGoGoNames> dbResponse) {
        this.dbResponse = dbResponse;
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
* @return the functionsPerOnt
*/
    public ArrayList<FunctionsPerOntology> getFunctionsPerOnt() {
        return functionsPerOnt;
    }

    /**
* @param functionsPerOnt the functionsPerOnt to set
*/
    public void setFunctionsPerOnt(ArrayList<FunctionsPerOntology> functionsPerOnt) {
        this.functionsPerOnt = functionsPerOnt;
    }
  
  
    
     public void getGenesPerGOterm(){
     genesPerGOtermList = new LinkedList<String>();
  }
  
  
  /**
   * @return the userInput
   */
  public String getUserInput() {
    return userInput;
  }

  /**
   * @param userInput the userInput to set
   */
  public void setUserInput(String userInput) {
    this.userInput = userInput;
  }

  /**
   * @return the userSpecies
   */
  public String getUserSpecies() {
    return userSpecies;
  }

    /**
   * @return the graph1Data
   */
  public String getGraph1Data() {
    return graph1Data;
  }
  
  


  /**
   * @param graph1Data the graph1Data to set
   */
  public void setGraph1Data(String graph1Data) {
    this.graph1Data = graph1Data;
  }

  
  /**
   * @param userSpecies the userSpecies to set
   */
  public void setUserSpecies(String userSpecies) {
    this.userSpecies = userSpecies;
  }

  /**
   * @return the c_ontology
   */
  public String getC_ontology() {
    return c_ontology;
  }

  /**
   * @param c_ontology the c_ontology to set
   */
  public void setC_ontology(String c_ontology) {
    this.c_ontology = c_ontology;
  }

  /**
   * @return the p_ontology
   */
  public String getP_ontology() {
    return p_ontology;
  }

  /**
   * @param p_ontology the p_ontology to set
   */
  public void setP_ontology(String p_ontology) {
    this.p_ontology = p_ontology;
  }

  /**
   * @return the f_ontology
   */
  public String getM_ontology() {
    return m_ontology;
  }

  /**
   * @param f_ontology the f_ontology to set
   */
  public void setM_ontology(String f_ontology) {
    this.m_ontology = f_ontology;
  }

  /**
   * @return the selectedOntOptions
   */
  public List<String> getSelectedOntOptions() {
    return selectedOntOptions;
  }

  /**
   * @param selectedOntOptions the selectedOntOptions to set
   */
  public void setSelectedOntOptions(List<String> selectedOntOptions) {
    this.selectedOntOptions = selectedOntOptions;
  }

  /**
   * @param selectedFunctions the selectedFunctions to set
   */
  public void setSelectedFunctions(String[] selectedFunctions) {
    this.setSelectedFunctions(selectedFunctions);
  }

  /**
   * @return the functionsMOnt
   */
  public ArrayList<String> getFunctionsFOnt() {    
    Collections.sort(functionsFOnt);
    return functionsFOnt;
  }

  /**
   * @param functionsMOnt the functionsMOnt to set
   */
  public void setFunctionsFOnt(ArrayList<String> functionsMOnt) {    
    this.functionsFOnt = functionsMOnt;
  }

  /**
   * @return the functionsPOnt
   */
  public ArrayList<String> getFunctionsPOnt() {
    Collections.sort(functionsPOnt);
    return functionsPOnt;
  }

  /**
   * @param functionsPOnt the functionsPOnt to set
   */
  public void setFunctionsPOnt(ArrayList<String> functionsPOnt) {
    this.functionsPOnt = functionsPOnt;
  }

  /**
   * @return the functionsCOnt
   */
  public ArrayList<String> getFunctionsCOnt() {
    Collections.sort(functionsCOnt);
    return functionsCOnt;
  }

  /**
   * @param functionsCOnt the functionsCOnt to set
   */
  public void setFunctionsCOnt(ArrayList<String> functionsCOnt) {
    this.functionsCOnt = functionsCOnt;
  }

  /**
   * @return the selectedFunctionsMOnt
   */
  public List<String> getSelectedFunctionsFOnt() {
    return selectedFunctionsFOnt;
  }

  /**
   * @param selectedFunctionsMOnt the selectedFunctionsMOnt to set
   */
  public void setSelectedFunctionsFOnt(List<String> selectedFunctionsMOnt) {
    this.selectedFunctionsFOnt = selectedFunctionsMOnt;
  }

  /**
   * @return the selectedFunctionsCOnt
   */
  public List<String> getSelectedFunctionsCOnt() {
    return selectedFunctionsCOnt;
  }

  /**
   * @param selectedFunctionsCOnt the selectedFunctionsCOnt to set
   */
  public void setSelectedFunctionsCOnt(List<String> selectedFunctionsCOnt) {
    this.selectedFunctionsCOnt = selectedFunctionsCOnt;
  }

  /**
   * @return the selectedFunctionsPOnt
   */
  public List<String> getSelectedFunctionsPOnt() {
    return selectedFunctionsPOnt;
  }

  /**
   * @param selectedFunctionsPOnt the selectedFunctionsPOnt to set
   */
  public void setSelectedFunctionsPOnt(List<String> selectedFunctionsPOnt) {
    this.selectedFunctionsPOnt = selectedFunctionsPOnt;
  }

  /**
   * @return the renderFunctionality
   */
  public Boolean getRenderFunctionality() {
    return renderFunctionality;
  }

  /**
   * @param renderFunctionality the renderFunctionality to set
   */
  public void setRenderFunctionality(Boolean renderFunctionality) {
    this.renderFunctionality = renderFunctionality;
  }

  /**
   * @return the graph2Data
   */
  public String getGraph2Data() {
    return graph2Data;
  }

  /**
   * @param graph2Data the graph2Data to set
   */
  public void setGraph2Data(String graph2Data) {
    this.graph2Data = graph2Data;
  }

  /**
   * @return the graph3Data
   */
  public String getGraph3Data() {
    return graph3Data;
  }

  /**
   * @param graph3Data the graph3Data to set
   */
  public void setGraph3Data(String graph3Data) {
    this.graph3Data = graph3Data;
  }

  /**
   * @return the pubmedtable
   */
  public List<pumedEntryBean> getPubmedtable() {
    return pubmedtable;
  }

  /**
   * @param pubmedtable the pubmedtable to set
   */
  public void setPubmedtable(List<pumedEntryBean> pubmedtable) {
    this.pubmedtable = pubmedtable;
  }

  /**
   * @return the popularGenes
   */
  public String[] getPopularGenes() {
    if(popularGenes==null || popularGenes.length==0)
    {
      GotrackDB dao = new GotrackDB();
      popularGenes = dao.getPopularGenes();
    dao.closeGotrackConnection();
    }
    return popularGenes;
    
  }

  /**
   * @param popularGenes the popularGenes to set
   */
  public void setPopularGenes(String[] popularGenes) {
    this.popularGenes = popularGenes;
  }

  /**
   * @return the topMultifunc
   */
  public List<String> getTopMultifunc() {
    if(topMultifunc==null || topMultifunc.isEmpty()){
       GotrackDB dao = new GotrackDB();
      topMultifunc = dao.getTopMultifunc();      
    dao.closeGotrackConnection();
    }        
    if(topMultifunc!=null)
      Collections.sort(topMultifunc);
    return topMultifunc;
  }
    /**
   * @return the topMultifuncSpecies
   */
  public List<String> getTopMultifuncSpecies() {
    
    GotrackDB dao = new GotrackDB();
    topMultifuncSpecies = dao.getTopMultifuncforSpecies(userSpecies);      
    dao.closeGotrackConnection();
            
    if(topMultifuncSpecies!=null){      
      ArrayList<String> top = new ArrayList<String>();
      for(int i =0; i<10;i++){
        top.add(topMultifuncSpecies.get(i));
      }
      return top;
    }
    return topMultifuncSpecies;
  }
  

  /**
   * @param topMultifunc the topMultifunc to set
   */
  public void setTopMultifunc(List<String> topMultifunc) {
    this.topMultifunc = topMultifunc;
  }

  /**
   * @return the symbolsToConsider
   */
  public HashSet<String> getSymbolsToConsider() {
    return symbolsToConsider;
  }

  /**
   * @param symbolsToConsider the symbolsToConsider to set
   */
  public void setSymbolsToConsider(HashSet<String> symbolsToConsider) {
    this.symbolsToConsider = symbolsToConsider;
  }

  /**
   * @return the progress
   */
  public Integer getProgress() {
    return progress;
  }

  /**
   * @param progress the progress to set
   */
  public void setProgress(Integer progress) {
    this.progress = progress;
  }

  /**
   * @return the currStatus
   */
  public String getCurrStatus() {
    return currStatus;
  }

  /**
   * @param currStatus the currStatus to set
   */
  public void setCurrStatus(String currStatus) {
    this.currStatus = currStatus;
  }

  /**
   * @return the allselectedFunctionsFCPOnt
   */
  public List<String> getAllselectedFunctionsFCPOnt() {
    return allselectedFunctionsFCPOnt;
  }

  /**
   * @param allselectedFunctionsFCPOnt the allselectedFunctionsFCPOnt to set
   */
  public void setAllselectedFunctionsFCPOnt(List<String> allselectedFunctionsFCPOnt) {
    this.allselectedFunctionsFCPOnt = allselectedFunctionsFCPOnt;
  }



  /**
   * @param filefig1 the filefig1 to set
   */
  public void setFilefig1(StreamedContent filefig1) {
    this.filefig1 = filefig1;
  }

  /**
   * @return the allIdsToConsiderUser
   */
  public ArrayList<String> getAllIdsToConsiderUser() {
    if(allIdsToConsider!=null){
       allIdsToConsiderUser= new ArrayList<String>();
       for(String id: allIdsToConsider){
         allIdsToConsiderUser.add(id);
       }
    }
    return allIdsToConsiderUser;
  }

  /**
   * @param allIdsToConsiderUser the allIdsToConsiderUser to set
   */
  public void setAllIdsToConsiderUser(ArrayList<String> allIdsToConsiderUser) {
    this.allIdsToConsiderUser = allIdsToConsiderUser;
  }

  /**
   * @param fileEvidenceCodeHist the fileEvidenceCodeHist to set
   */
  public void setFileEvidenceCodeHist(StreamedContent fileEvidenceCodeHist) {
    this.fileEvidenceCodeHist = fileEvidenceCodeHist;
  }
  /**
   * @param filepubmedTable the filepubmedTable to set
   */
  public void setFilepubmedTable(StreamedContent filepubmedTable) {
    this.filepubmedTable = filepubmedTable;
  }


  /**
   * @param fileAllGenesPerGoterm the fileAllGenesPerGoterm to set
   */
  public void setFileAllGenesPerGoterm(StreamedContent fileAllGenesPerGoterm) {
    this.fileAllGenesPerGoterm = fileAllGenesPerGoterm;
  }



  /**
   * @param topMultifuncSpecies the topMultifuncSpecies to set
   */
  public void setTopMultifuncSpecies(List<String> topMultifuncSpecies) {
    this.topMultifuncSpecies = topMultifuncSpecies;
  }


  /**
   * @param fileAllGenesPerSpeciesMultifunc the fileAllGenesPerSpeciesMultifunc to set
   */
  public void setFileAllGenesPerSpeciesMultifunc(StreamedContent fileAllGenesPerSpeciesMultifunc) {
    this.fileAllGenesPerSpeciesMultifunc = fileAllGenesPerSpeciesMultifunc;
  }

  /**
   * @return the disableFunctionalityTabPage1
   */
  public Boolean getDisableFunctionalityTabPage1() {
    return disableFunctionalityTabPage1;
  }

  /**
   * @param disableFunctionalityTabPage1 the disableFunctionalityTabPage1 to set
   */
  public void setDisableFunctionalityTabPage1(Boolean disableFunctionalityTabPage1) {
    this.disableFunctionalityTabPage1 = disableFunctionalityTabPage1;
  }
  
    
}
