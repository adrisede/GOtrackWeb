package utils;

import beans.CountGOTermsDirectInferred;
import beans.EvidenceCodeBean;
import beans.GeneGoGoNames;
import beans.view.countViewBean;
import database.GotrackDB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import beans.view.CountGOTermsOverTimeBean;
import beans.view.CountGenesPerGoBean;
import beans.view.Stats1;
import beans.view.Stats2;
import java.util.Calendar;


import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import java.util.List;

/**
 *
 * @author asedeno
 */
public class ViewUtils {

  /**Using the go functions names selected by user, this function gets the go terms
   that correspond to user input*/
  public static ArrayList<GeneGoGoNames> getGOterms(ArrayList<String> userGOFunctsNames, ArrayList<GeneGoGoNames> geneFunctionalities) {

    for(GeneGoGoNames g: geneFunctionalities){
      g.setIsPickedbyUser(false);
    }
    
    for (String func : userGOFunctsNames) {
      for (GeneGoGoNames adt : geneFunctionalities) {
        if (adt.getGoName().compareTo(func)==0) {
          adt.setIsPickedbyUser(true);
        }
      }
    }

    return geneFunctionalities;
  }

  public static ArrayList<countViewBean> getCount(ArrayList<CountGOTermsDirectInferred> count) {
    //Get max number of editions
    Integer maxNumberOfEditions = 0;
    for (CountGOTermsDirectInferred c : count) {
      if (Integer.valueOf(c.getEdition()) > maxNumberOfEditions) {
        maxNumberOfEditions = Integer.valueOf(c.getEdition());
      }
    }

    ArrayList<countViewBean> countView = new ArrayList<countViewBean>();
    HashMap<String, countViewBean> dic = new HashMap<String, countViewBean>(30);

    for (CountGOTermsDirectInferred c : count) {
      if (!dic.containsKey(c.getGeneid())) {
        countViewBean t = new countViewBean();
        t.setGene(c.getGeneid());
        dic.put(c.getGeneid(), t);
      }
    }

    for (String geneId : dic.keySet()) {
      String[] directs = new String[maxNumberOfEditions + 1];
      Arrays.fill(directs, "0");
      String[] inferred = new String[maxNumberOfEditions + 1];
      Arrays.fill(inferred, "0");
      String[] rate = new String[maxNumberOfEditions + 1];
      Arrays.fill(rate, "0");
      for (CountGOTermsDirectInferred c : count) {
        if (c.getGeneid().compareTo(geneId) == 0) {
          directs[Integer.valueOf(c.getEdition())] = c.getDirect();
          inferred[Integer.valueOf(c.getEdition())] = c.getInferred();
          rate[Integer.valueOf(c.getEdition())] = c.getRatio();
        }
      }
      countViewBean t = new countViewBean();
      t.setGene(geneId);
      t.setDirects(new ArrayList<String>(Arrays.asList(directs)));
      t.setInferred(new ArrayList<String>(Arrays.asList(inferred)));
      t.setRate(new ArrayList<String>(Arrays.asList(rate)));
      countView.add(t);
    }

    return countView;
  }

  public static HashSet<String> getAllGenesInHistory(String species, String userInput, GotrackDB dao) {


    //look directly if userInpur is currently a gene or symbol, get all its
    //uniprots
    HashSet<String> allGenesToConsider = dao.getDirectGenes(species, userInput);
    //Look for synonyms in the dictionary
    //HashSet<String> synonyms = dao.lookUniprotInDic(species, userInput);

    //allGenesToConsider.addAll(synonyms);

    //Look for replaced ids
    allGenesToConsider.addAll(dao.getReplacedElements(species, userInput));

    return allGenesToConsider;
  }

  /**
   * Returns only the var "data" from a given list of countGOTermsOvertime
   *
   * @param table
   * @return var data. used in DrawVisualization();
   */
  public static String transformString(ArrayList<CountGOTermsOverTimeBean> table, HashSet<String> allsymbols) {

    if(table==null | (table!=null && table.isEmpty())){
      /*There is no data*/      
      return "var data = 'null'";
    }
    
    StringBuilder var = new StringBuilder();
    var.append("var data = new google.visualization.DataTable();");
    // init columns
    var.append("data.addColumn('string', 'GeneProduct');");
    var.append("data.addColumn('date', 'Edition');");
    var.append("data.addColumn('number', 'NumDirectGOterms');");
    var.append("data.addColumn('number', 'NumPropagatedGOterms');");
    var.append("data.addColumn('number', 'RatioDirectvsPropagatedGOterms');");
    var.append("data.addColumn('number', 'MultifunctionalityScore');");
    var.append("data.addColumn('number', 'SemanticSimiliarity_Jaccard');");
    var.append("data.addColumn('string', 'symbol');");
    var.append("data.addRows([ ");
    // init rows
    int i = 1;
    for (CountGOTermsOverTimeBean bean : table) {
      if(allsymbols!=null && bean.getSymbol()!=null && !bean.getSymbol().contains("SpeciesMean")){        
        allsymbols.add(bean.getSymbol());
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getDate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      var.append(String.format("[ '%s', new Date(%s, %s, %s), %s, %s, %s,%s, %s,'%s' ]",
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
      if (i != table.size()) {
        var.append(",");
      } else {
        i += 1;
      }
    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }

  
  
  /**
   * Returns only the var "data" from a given list of countGOTermsOvertime
   *
   * @param table
   * @return var data. used in DrawVisualization();
   */
  public static String transformStringGenesPerGO(ArrayList<CountGenesPerGoBean> table) {
    if(table==null | (table!=null && table.isEmpty())){
      /*There is no data*/      
      return "var data2 = 'null'";
    }
    StringBuilder var = new StringBuilder();
    var.append("var data2 = new google.visualization.DataTable();");
    // init columns
    var.append("data2.addColumn('string', 'Name');");
    var.append("data2.addColumn('date', 'Edition');");
    var.append("data2.addColumn('number', 'GeneNumber');");
    var.append("data2.addColumn('string', 'GoTerm');");
    var.append("data2.addRows([ ");
    // init rows
    int i = 1;
    for (CountGenesPerGoBean bean : table) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getDate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      var.append(String.format("[ '%s', new Date(%s, %s, %s), %s,'%s' ]",
              bean.getGoname(),
              year,
              month + 1,
              day,
              bean.getAssociatedGenes(),
              bean.getGoterm()));
      if (i != table.size()) {
        var.append(",");
      }
      i += 1;

    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }

  public static DataTable transformECDataTable(List<EvidenceCodeBean> table) throws TypeMismatchException {

    DataTable data = new DataTable();

    data.addColumn(new ColumnDescription("Goterm", ValueType.TEXT, "Goterm"));
    data.addColumn(new ColumnDescription("Evidence", ValueType.TEXT, "Evidence"));
    data.addColumn(new ColumnDescription("Start", ValueType.DATE, "Start"));
    data.addColumn(new ColumnDescription("End", ValueType.DATE, "End"));


    for (EvidenceCodeBean bean : table) {


      TableRow row = new TableRow();
      row.addCell(bean.getGoterm());
      String Start = String.format("new Date(%s, %s, %s)", bean.getDateStart().getYear(),
              bean.getDateStart().getMonth(),
              bean.getDateStart().getDate());
      row.addCell(Start);
      String End = String.format("new Date(%s, %s, %s)", bean.getDateEnd().getYear(),
              bean.getDateEnd().getMonth(),
              bean.getDateEnd().getDate());
      row.addCell(Start);
      row.addCell(bean.getEvidence());
      row.addCell(End);

      data.addRow(row);
    }

    return data;
  }

  /**
   * Returns only the var "data" from a given list of countGOTermsOvertime
   *
   * @param table
   * @return var data. used in DrawVisualization();
   */
  public static String transformECString(List<EvidenceCodeBean> table) {
     if(table==null | (table!=null && table.isEmpty())){
      /*There is no data*/      
      return "var dataTable = 'null'";
    }
    StringBuilder var = new StringBuilder();
    var.append("var dataTable = new google.visualization.DataTable();");
    // init columns
    var.append("dataTable.addColumn({type:'string', id:'Goterm'});");
    var.append("dataTable.addColumn({type:'string', id:'Evidence'});");
    var.append("dataTable.addColumn({type:'date', id:'Start'});");
    var.append("dataTable.addColumn({type:'date', id:'End'});");

    var.append("dataTable.addRows([ ");
    // init rows
    int i = 1;
    for (EvidenceCodeBean bean : table) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getDateStart());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(bean.getDateEnd());
      int year2 = cal2.get(Calendar.YEAR);
      int month2 = cal2.get(Calendar.MONTH);
      int day2 = cal2.get(Calendar.DAY_OF_MONTH);
      var.append(String.format("[ '%s', '%s',new Date(%s, %s, %s), new Date(%s, %s, %s) ]",
              bean.getGoterm(),
              bean.getEvidence(),
              year,
              month,
              day,
              year2,
              month2,
              day2));
      if (i != table.size()) {
        var.append(",");
      }
      i += 1;

    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }

  public static String transformStats1tring(List<Stats1> table) {

    StringBuilder var = new StringBuilder();
    var.append("var dataTable = new google.visualization.DataTable();");
    // init columns
    var.append("dataTable.addColumn('string', 'Species');");
    var.append("dataTable.addColumn('date',   'Edition');");   
    var.append("dataTable.addColumn('number', 'meanMultifunctionalityScore');");
    var.append("dataTable.addColumn('number', 'meanSemanticSimiliarity_Jaccard');");
    var.append("dataTable.addColumn('number', 'meanPropagatedGOterms');");
    var.append("dataTable.addColumn('number', 'uniqueGeneProducts');");
    var.append("dataTable.addColumn('number', 'meanDirectsGOterms');");    
     

    var.append("dataTable.addRows([ ");
    // init rows
    int i = 1;
    for (Stats1 bean : table) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getEddate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      
      var.append(String.format("[ '%s',new Date(%s, %s, %s),%s,%s,%s,%s,%s ]",
              bean.getSpecies(),
              year,
              month,
              day,
              bean.getMeanMf(),
              bean.getMeanJaccard(),
              bean.getMeanInferred(),
              bean.getUniqueGenes(),
              bean.getMeanDirects()             
              ));
      if (i != table.size()) {
        var.append(",");
      }
      i += 1;

    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }
  
  public static String transformStatsPerSpecies(List<Stats1> table, String species) {

    StringBuilder var = new StringBuilder();
    var.append("var dataTable3 = new google.visualization.DataTable();");
    // init columns
    var.append("dataTable3.addColumn('string', 'Variable');");
    var.append("dataTable3.addColumn('date',   'Edition');");   
    var.append("dataTable3.addColumn('number', '").append(species).append("');");         
    var.append("dataTable3.addRows([ ");
    // init rows
    int i = 1;
    for (Stats1 bean : table) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getEddate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
         
      var.append(String.format("[ 'MeanDirectGOterms',new Date(%s, %s, %s),%s],",              
              year,
              month,
              day,
              bean.getMeanDirects()
              ));
      var.append(String.format("[ 'MeanPropagatedGOterms',new Date(%s, %s, %s),%s],",             
              year,
              month,
              day,
              bean.getMeanInferred()
              ));
      var.append(String.format("[ 'UniqueGeneProducts',new Date(%s, %s, %s),%s],",              
              year,
              month,
              day,
              bean.getUniqueGenes()
              ));
      var.append(String.format("[ 'PromotedAnnotsGeneralToSpecific',new Date(%s, %s, %s),%s],",             
              year,
              month,
              day,
              bean.getAnnotsGeneralToSpecific()
              ));
      
      var.append(String.format("[ 'MeanMultifunctionalityScore',new Date(%s, %s, %s),%s],",             
              year,
              month,
              day,
              bean.getMeanMf()
              ));
      
      var.append(String.format("[ 'PromotedAnnotsIEAtoManual',new Date(%s, %s, %s),%s]",             
              year,
              month,
              day,
              bean.getAnnotsIEAtoManual()
              ));
      
      if (i != table.size()) {
        var.append(",");
      }
      i += 1;

    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }
  
   public static String transformStats2tring(List<Stats2> table) {

    StringBuilder var = new StringBuilder();
    var.append("var dataTable2 = new google.visualization.DataTable();");
    // init columns
    var.append("dataTable2.addColumn('string', 'Species');");
    var.append("dataTable2.addColumn('date',   'Edition');");   
    var.append("dataTable2.addColumn('number', 'PromotedannotsIEAtoManual');");
    var.append("dataTable2.addColumn('number', 'PromotedAnnotsGeneralToSpecific');");  
    var.append("dataTable2.addColumn('number', 'notAnnotRatio');");
     

    var.append("dataTable2.addRows([ ");
    // init rows
    int i = 1;
    for (Stats2 bean : table) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(bean.getEddate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      
      var.append(String.format("[ '%s',new Date(%s, %s, %s),%s,%s,%s ]",
              bean.getSpecies(),
              year,
              month,
              day,
              bean.getAnnotsIEAtoManual(),
              bean.getAnnotsGeneralToSpecific(),
              bean.getNotAnnotRatio()
              ));
      if (i != table.size()) {
        var.append(",");
      }
      i += 1;

    }
    // end rows
    var.append(" ]);");
    return var.toString();
  }
}