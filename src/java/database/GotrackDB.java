package database;

import beans.EvidenceCodeBean;
import beans.view.CountGOTermsOverTimeBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import beans.GeneGoGoNames;
import beans.view.CountGenesPerGoBean;
import beans.view.Stats1;
import beans.view.Stats2;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements all the methods to query the database
 * Given the Database design the queries must be created dynamically in function
 * of the species and user input
 * 
 * @author asedeno
 */
public class GotrackDB extends GoTrackDatabase {

    /**
     * Close connection instance of this class
     */
  public void closeGotrackConnection() {
    if (this.connect != null) {
      try {
        connect.close();
      } catch (SQLException ex) {
        Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Query the *_unique_gene_symbol table looking for the symbol or gene 
   * provided by user
   * 
   * @param species
   * @param symbolOrGene
   * @param symbols (output)
   * @return
   */
  public HashSet<String> getDirectGenes(String species, String symbolOrGene) {
    HashSet<String> res = new HashSet<String>();

    PreparedStatement prepStmt = null;

    try {
      String sqlStmt = "SELECT distinct(gene) FROM "
              + species
              + "_unique_gene_symbol where upper(symbol) = upper('" + symbolOrGene+"') "
              + "or upper(gene) = upper('" + symbolOrGene + "')";
      prepStmt = connect.prepareStatement(sqlStmt);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement:\n\t{0}", prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        res.add(rs.getString("gene"));
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return res;
  }

  /**
   * Given a goterm id, this function will get the human readable name of the 
   * goterm in the latest version of the tree
   * @param goterm
   * @return 
   */
  public String getGOnames(String goterm) {
    PreparedStatement prepStmt = null;
    String goname = "";
    try {
      String sqlStmt = "SELECT distinct(name) as name FROM GO_names where goterm = ? order by date asc limit 1";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, goterm);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement:\n\t{0}", prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        goname = rs.getString("name");
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return goname;
  }
  
  
  /**
   * This function uses table *_replaced_id to find ids that were mapped to a
   * most updated symbol
   * 
   * @param species
   * @param ids
   * @return Set of replaced id
   */
  public HashSet<String> getReplacedElements(String species, String ids) {
    HashSet<String> replaced = new HashSet<String>();

    PreparedStatement prepStmt = null;

    try {
      String sqlStmt = "SELECT distinct(original) as original, replaced FROM "
              + species
              + "_replaced_id where upper(original)= upper('" + ids + "') or upper(replaced) = upper('" + ids + "')";
      prepStmt = connect.prepareStatement(sqlStmt);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement:\n\t{0}", prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        String original = rs.getString("original");
        String rep = rs.getString("replaced");
        if (original.compareTo("") != 0) {
          replaced.add(original);
        }
        if (rep.compareTo("") != 0) {
          replaced.add(rep);
        }
      }
      rs.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return replaced;
  }

  /**
   * This function queries table *_count to gets gene,symbol,directs, inferred, 
   * edition , directs/inferred (ratio), jaccard, multifunc,date of a set of ids
   * It will get as well the average of all the above mentioned values of the
   * given species.
   * 
   * @param species
   * @param geneids List of gene ids to query
   * @return ArrayList of objects that represent each row of the table
   */
  public ArrayList<CountGOTermsOverTimeBean> countDirectInferredParents(String species, HashSet<String> geneids) {
    String allIds = "";
    for (String id : geneids) {
      allIds += "'" + id + "'" + ",";
    }
    if (allIds.length() > 0) {
      allIds = allIds.substring(0, allIds.length() - 1);
    }

    ArrayList<CountGOTermsOverTimeBean> res = new ArrayList<CountGOTermsOverTimeBean>();

    if (geneids.isEmpty()) {
      return res;
    }

    PreparedStatement prepStmt = null;
   /*This query consists of the union of two queries. The first one will get the
     information of the input list of genes.
     The second query will get the average of the entire species
     */
    try {
      String sqlStmt =
              "(SELECT gene,symbol,directs, inferred, edition , directs/inferred as ratio, jaccard, multifunc,date "
              + "FROM " + species + "_count hdi join "
              + "(select date, editionNo from edition_to_date where species = "
              + "(select objNo from species where name = ?)) etd "
              + "on etd.editionNo=hdi.edition where gene in (" + allIds + ") order by gene, edition)"
              + "union "
              + "(select '.SpeciesMean' as gene, '.SpeciesMean'as symbol, avg_directs, "
              + "avg_inferred, edition,avg_directs/avg_inferred,avg_jaccard,avg_multifunc, av.date from "
              + species + "_avg av join (select date, editionNo from edition_to_date where species = "
              + "(select objNo from species where name = ?)) dts "
              + "on dts.editionNo=av.edition);";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, species);
      prepStmt.setString(2, species);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement:\n\t{0}", prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        CountGOTermsOverTimeBean t = new CountGOTermsOverTimeBean();
        String gene = rs.getString("gene");
        if (gene == null) {
          continue;
        }
        t.setGene(gene);


        t.setSymbol(rs.getString("symbol"));

        String direct = rs.getString("directs");
        if (direct != null) {
          t.setDirects(Float.valueOf(direct));
        } else {
          t.setDirects(null);
        }
        String inferred = rs.getString("inferred");
        if (inferred != null) {
          t.setInferred(Float.valueOf(inferred));
        } else {
          t.setInferred(null);
        }
        String jaccard = rs.getString("jaccard");
        if (jaccard != null) {
          t.setJaccardSim(Double.valueOf(jaccard));
        } else {
          t.setJaccardSim(null);
        }
        String multif = rs.getString("multifunc");
        if (multif != null) {
          t.setMultifunc(Double.valueOf(multif));
        } else {
          t.setMultifunc(null);
        }
        t.setEdition(Integer.valueOf(rs.getString("edition")));
        String rate = rs.getString("ratio");
        if (rate != null && rate.compareTo("NULL") != 0) {
          t.setRate(Double.valueOf(rate));
        }
        String edate = rs.getString("date");
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          Date date = formatter.parse(edate);
          t.setDate(date);
        }
        res.add(t);
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return res;
  }

  /**
   * This function receive a list of gene ids and a list of goterms
   * 
   * It will return the relationship between gene, goterm, pubmed, code and date
   * 
   * @param species
   * @param geneids
   * @param goterms
   * @return A list of objects that represent the history of the pubmed ids
   * associated to the list of gene ids and goterms
   */
  public ArrayList<EvidenceCodeBean> getEvidenceCodeHistory(String species,
          ArrayList<String> geneids, ArrayList<String> goterms) {
    String allIds = "";
    String allgoterms = "";
    HashSet<String> geneidswithNoData = new HashSet<String>();
    for (String id : geneids) {
      allIds += "'" + id + "'" + ",";

    }
    if (allIds.length() > 0) {
      allIds = allIds.substring(0, allIds.length() - 1);
    }

    for (String id : goterms) {
      allgoterms += "'" + id + "'" + ",";
    }
    if (allgoterms.length() > 0) {
      allgoterms = allgoterms.substring(0, allgoterms.length() - 1);
    }
    for (String gene : geneids) {
      for (String go : goterms) {
        geneidswithNoData.add(gene + "|" + go);
      }
    }

    ArrayList<EvidenceCodeBean> res = new ArrayList<EvidenceCodeBean>();

    if (allIds.length() == 0 || allgoterms.length() == 0) {
      return res;
    }

    PreparedStatement prepStmt = null;

    try {
      String sqlStmt =
              "Select distinct gene, goterm, pubmed, code,  DATE_FORMAT(date,'%Y-%m-01') as date "
              + "FROM " + species + "_evidence_code hdi join "
              + " (select distinct(date) as date, max(editionNo) as editionNo from edition_to_date "
              + " where species = (select objNo from species where name = ?) group by date ) etd "
              + "on etd.editionNo=hdi.edition where gene in (" + allIds + ") and goterm in "
              + "(" + allgoterms + ") order by gene, edition;";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, species);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();


      while (rs.next()) {

        EvidenceCodeBean t = new EvidenceCodeBean();
        String gene = rs.getString("gene");
        if (gene == null) {
          continue;
        }
        t.setUniprot(gene);

        String goterm = rs.getString("goterm");
        if (goterm != null) {
          t.setGoterm(goterm);
        } else {
          t.setGoterm(null);
        }
        geneidswithNoData.remove(gene + "|" + goterm);

        String pubmed = rs.getString("pubmed");
        if (pubmed != null) {
          t.setPubmed(pubmed);
        } else {
          t.setPubmed(null);
        }

        String code = rs.getString("code");
        if (code != null) {
          t.setEvidence(code);
        } else {
          t.setEvidence(null);
        }

        String edate = rs.getString("date");
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          Date date = formatter.parse(edate);
          t.setDateStart(date);
        }
        res.add(t);
      }
      rs.close();

    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    if (!geneidswithNoData.isEmpty()) {
      res.addAll(getEvidenceCodeHistory2(species, geneidswithNoData));
    }
    return res;
  }

  /**
   * This function is called by getEvidenceCodeHistory when that function gets
   * no data
   * 
   * @param species
   * @param geneidswithNoData
   * @return 
   */
  public ArrayList<EvidenceCodeBean> getEvidenceCodeHistory2(String species,
          HashSet<String> geneidswithNoData) {
    ArrayList<String> geneids = new ArrayList<String>();
    ArrayList<String> goterms = new ArrayList<String>();
    for (String geneGo : geneidswithNoData) {
      String gene = geneGo.substring(0, geneGo.indexOf("|"));
      String go = geneGo.substring(geneGo.indexOf("|") + 1, geneGo.length());
      geneids.add(gene);
      goterms.add(go);
    }
    String allIds = "";
    String allgoterms = "";
    for (String id : geneids) {
      allIds += "'" + id + "'" + ",";
    }
    if (allIds.length() > 0) {
      allIds = allIds.substring(0, allIds.length() - 1);
    }

    for (String id : goterms) {
      allgoterms += "'" + id + "'" + ",";
    }
    if (allgoterms.length() > 0) {
      allgoterms = allgoterms.substring(0, allgoterms.length() - 1);
    }

    ArrayList<EvidenceCodeBean> res = new ArrayList<EvidenceCodeBean>();

    if (allIds.length() == 0 || allgoterms.length() == 0) {
      return res;
    }

    PreparedStatement prepStmt = null;

    try {
      String sqlStmt =
              "select distinct gene,goterm,pubmedid as pubmed, evidence as code, DATE_FORMAT(date,'%Y-%m-01') as date "
              + " from  " + species + "_gene_annot hga join "
              + " (select distinct(date) as date, max(editionNo) as editionNo from edition_to_date "
              + " where species = (select objNo from species where name = ?) group by date ) etd "
              + "on etd.editionNo=hga.edition where gene in (" + allIds + ") and goterm in "
              + "(" + allgoterms + ") order by gene, edition;";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, species);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        EvidenceCodeBean t = new EvidenceCodeBean();
        String gene = rs.getString("gene");
        if (gene == null) {
          continue;
        }
        t.setUniprot(gene);

        String goterm = rs.getString("goterm");
        if (goterm != null) {
          t.setGoterm(goterm);
        } else {
          t.setGoterm(null);
        }

        if (!geneidswithNoData.contains(gene + "|" + goterm)) {
          continue;
        }

        String pubmed = rs.getString("pubmed");
        if (pubmed != null) {
          if(pubmed.contains(":")){
            pubmed = pubmed.substring(pubmed.indexOf(":")+1, pubmed.length());
          }
          t.setPubmed(pubmed);
        } else {
          t.setPubmed(null);
        }

        String code = rs.getString("code");
        if (code != null) {
          t.setEvidence(code);
        } else {
          t.setEvidence(null);
        }

        String edate = rs.getString("date");
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          Date date = formatter.parse(edate);
          t.setDateStart(date);
        }
        res.add(t);
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return res;
  }

  /**
   * Use table go_names and *_gene_annot to get the relationship between
   * the gene annotation and the goterm name
   * 
   * @param geneids
   * @param ontology
   * @param species
   * @return 
   */
  public ArrayList<GeneGoGoNames> getGeneGotermGoName(HashSet<String> geneids,
          String ontology, String species) {
    String allIds = "";
    for (String id : geneids) {
      allIds += "'" + id + "'" + ",";
    }
    if (allIds.length() > 0) {
      allIds = allIds.substring(0, allIds.length() - 1);
    }

    ArrayList<GeneGoGoNames> result = new ArrayList<GeneGoGoNames>();

    if (allIds.length() == 0) {
      return result;
    }

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select gnam.name as goname, gnam.goterm as goterm, gene from "
              + " (select distinct(goterm) as goterm, gene from " + species + "_gene_annot h where h.ontology = ? "
              + "and h.gene in (" + allIds + ") ) hga on hga.goterm = gnam.goterm where gnam.name !='' ";
            
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, ontology);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      HashSet<String> totalgoterms = new HashSet<String>();
      while (rs.next()) {
        String gene = rs.getString("gene");
        String goterm = rs.getString("goterm");
        String goName = rs.getString("goname");
        if (!totalgoterms.contains(gene + "|" + goterm)) {
          result.add(new GeneGoGoNames(gene, goterm, goName, ontology));
        }
        totalgoterms.add(gene + "|" + goterm);

      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Get the relationship of go name go term of a given ontology
   * 
   * @param ontology
   * @param species
   * @param go
   * @return 
   */
  public ArrayList<GeneGoGoNames> getGeneGotermGoNameSingleGoterm(String ontology, String species, String go) {
    ArrayList<GeneGoGoNames> result = new ArrayList<GeneGoGoNames>();
    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select gnam.name as goname, gnam.goterm as goterm from unique_go_functions gnam join"
              + " (select distinct(goterm) as goterm from " + species + "_gene_annot h where h.ontology = ? and goterm= ?"
              + " ) hga on hga.goterm = gnam.goterm where gnam.name !='' ";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, ontology);
      prepStmt.setString(2, go);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        String goterm = rs.getString("goterm");
        String goName = rs.getString("goname");
        result.add(new GeneGoGoNames("", goterm, goName, ontology));
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Given a list of go terms it will get the number of annotations related to 
   * the go terms per date
   * 
   * @param userInput
   * @param species
   * @return 
   */
  public ArrayList<CountGenesPerGoBean> countGenesPerGOTerm(ArrayList<GeneGoGoNames> userInput,
          String species) {
    ArrayList<CountGenesPerGoBean> count = new ArrayList<CountGenesPerGoBean>();
    String goterms = "";
    
    /*userInput has all the goterms available but user might have picked only
     a few on them.*/
    for (GeneGoGoNames adt : userInput) {
      if (adt.getIsPickedbyUser()) {
        if (goterms.compareTo("") == 0) {
          goterms = "'" + adt.getGoterm() + "'";
        } else {
          goterms = goterms + ",'" + adt.getGoterm() + "'";
        }
      }
    }
    if (goterms.length() == 0) {
      return count;
    }

    PreparedStatement prepStmt = null;
    try {
      /* This query use table 
         * (*_gene_per_go join  edition_to_date) join go_names
         */
      String sqlStmt;
      sqlStmt = "select gnam.goterm as goterm , value, edition, "
              + "count.date as date , editionNo, gnam.name  as name from "
              + "("
              +   "select * from (select distinct(goterm), value, edition "
              +   "FROM " + species + "_gene_per_go where goterm in(" + goterms + ")) hgpg join "
              +   "(select date, editionNo from edition_to_date where species ="
              +   "(select objNo from species where name = ?)) etd on "
              +   "etd.editionNo = hgpg.edition"
              + ")count join GO_names gnam on count.goterm=gnam.goterm and month(count.date) = month(gnam.date) and year(count.date)=year(gnam.date);";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, species);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {

        String goterm = rs.getString("goterm");
        String value = rs.getString("value");
        String edition = rs.getString("edition");
        String edate = rs.getString("date");
        String gotermName = rs.getString("name");
        Date date = null;
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          date = formatter.parse(edate);
        }

        count.add(new CountGenesPerGoBean(goterm, value, edition, date,gotermName));
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }    
    return count;
  }

  /**
   * This function get exactly which genes are associated to the list of goterms
   * @param userInput
   * @param species
   * @return 
   */
  public ArrayList<CountGenesPerGoBean> allGenesPerGOTerm(ArrayList<GeneGoGoNames> userInput,
          String species) {
    ArrayList<CountGenesPerGoBean> count = new ArrayList<CountGenesPerGoBean>();
    String goterms = "";
    for (GeneGoGoNames adt : userInput) {
      if (adt.getIsPickedbyUser()) {
        if (goterms.compareTo("") == 0) {
          goterms = "'" + adt.getGoterm() + "'";
        } else {
          goterms = goterms + ",'" + adt.getGoterm() + "'";
        }
      }
    }
    if (goterms.length() == 0) {
      return count;
    }

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt;
      /**/
      sqlStmt = "select hgpg.gene as gene ,hgpg.symbol as symbol , hgpg.goterm as goterm ,etd.date as date "
              + "from (SELECT distinct gene,symbol, edition, goterm FROM "
              + species + "_gene_annot where goterm in(" + goterms + ")) hgpg join "
              + "(select date, editionNo from edition_to_date where species ="
              + "(select objNo from species where name = ?)) etd on "
              + "etd.editionNo = hgpg.edition order by etd.editionNo, hgpg.goterm";
      prepStmt = connect.prepareStatement(sqlStmt);
      prepStmt.setString(1, species);
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "Prepared Statement after bind variables set:\n\t{0}",
              prepStmt.toString());
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {

        String goterm = rs.getString("goterm");
        String uniprot = rs.getString("gene");
        String symbol = rs.getString("symbol");
        String edate = rs.getString("date");
        Date date = null;
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          date = formatter.parse(edate);
        }

        CountGenesPerGoBean tmp = new CountGenesPerGoBean();
        tmp.setUniprot(uniprot);
        tmp.setGoterm(goterm);
        tmp.setSymbol(symbol);
        tmp.setDate(date);

        count.add(tmp);
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    /*fill goterm names*/
    for (Iterator<CountGenesPerGoBean> it = count.iterator(); it.hasNext();) {
      CountGenesPerGoBean adt = it.next();
      for (GeneGoGoNames g : userInput) {
        if (g.getGoterm().compareTo(adt.getGoterm()) == 0) {
          adt.setGoname(g.getGoName());
        }
      }
    }
    return count;
  }

  
  /*-----------------Global stats functions-----------------------------*/
  
  
  /**
   * Get the average results in table avgAllSpeciesCount of two species
   * @param species1
   * @param species2
   * @return 
   */
  public ArrayList<Stats1> getStats1(String species1, String species2) {

    ArrayList<Stats1> result = new ArrayList<Stats1>();

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select avg_directs, meanMF, meanJacard, uniqueGenes, meanInferred,  "
              + "edition,sp, date from avgAllSpeciesCount where sp in('" + species1 + "','" + species2 + "')";
      System.out.println();
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        Stats1 tmp = new Stats1();
        tmp.setMeanDirects(rs.getString("avg_directs"));
        tmp.setMeanMf(rs.getString("meanMF"));
        tmp.setMeanJaccard(rs.getString("meanJacard"));
        tmp.setMeanInferred(rs.getString("meanInferred"));
        tmp.setUniqueGenes(rs.getString("uniqueGenes"));
        tmp.setEdition(rs.getString("edition"));
        tmp.setSpecies(rs.getString("sp"));
        String edate = rs.getString("date");
        Date date = null;
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          date = formatter.parse(edate);
        }
        tmp.setEddate(date);
        result.add(tmp);

      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Gets the number of annotations that:
   * 1.- Changed from IEA to Manual
   * 2.- Changed from a general annotation to a specific
   * 3.- Not Annotation ratio
   * 4.- Date
   * 
   * 
   * @param species1
   * @param species2
   * @return 
   */
  public ArrayList<Stats2> getStats2(String species1, String species2) {

    ArrayList<Stats2> result = new ArrayList<Stats2>();

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select  annotsIEAtoManual, annotsGeneralToSpecific,date"
              + ", species,notAnnotRatio from annotAnalysis where species in('"
              + species1 + "','" + species2 + "')";
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        Stats2 tmp = new Stats2();
        tmp.setAnnotsIEAtoManual(rs.getString("annotsIEAtoManual"));
        tmp.setAnnotsGeneralToSpecific(rs.getString("annotsGeneralToSpecific"));
        tmp.setNotAnnotRatio(rs.getString("notAnnotRatio"));
        tmp.setSpecies(rs.getString("species"));
        String edate = rs.getString("date");
        Date date = null;
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          date = formatter.parse(edate);
        }
        tmp.setEddate(date);
        result.add(tmp);

      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Use table *_avg to get the average of 
   * 1.- Direct go terms
   * 2.- Inferred go terms
   * 3.- multifunctionality score *10000000
   * 4.- jaccard similarity score
   * 5.- unique gene products
   * 6.- Annotations from general to specific
   * 7.- No annotation ratio
   * 
   * @param species
   * @return 
   */
  public ArrayList<Stats1> getStatsPerSpecies(String species) {

    ArrayList<Stats1> result = new ArrayList<Stats1>();

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select avg_directs, avg_inferred, 10000000*avg_multifunc as avg_multifunc,"
              + "avg_jaccard,uniqueGenes,annotsIEAtoManual,"
              + "annotsGeneralToSpecific,notAnnotRatio,date "
              + "from " + species + "_avg";
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next()) {
        Stats1 tmp = new Stats1();
        tmp.setMeanDirects(rs.getString("avg_directs"));
        tmp.setMeanMf(rs.getString("avg_multifunc"));
        tmp.setMeanJaccard(rs.getString("avg_jaccard"));
        tmp.setUniqueGenes(rs.getString("uniqueGenes"));
        tmp.setMeanInferred(rs.getString("avg_inferred"));
        tmp.setNotAnnotRatio(rs.getString("notAnnotRatio"));
        tmp.setAnnotsGeneralToSpecific(rs.getString("annotsGeneralToSpecific"));
        tmp.setAnnotsIEAtoManual(rs.getString("annotsIEAtoManual"));
        String edate = rs.getString("date");
        Date date = null;
        if (edate != null && edate.compareTo("") != 0) {
          DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
          date = formatter.parse(edate);
        }
        tmp.setEddate(date);
        result.add(tmp);

      }
      rs.close();
    } catch (SQLException e) {
    } catch (ParseException ex) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Store user input to keep track the most frequent ids/symbols searched by 
   * users
   * 
   * Store the value in popularGenes table and update the times the term has
   * been searched
   * 
   * @param symbol
   * @param species 
   */
  public void updatePopularGenes(String symbol, String species) {
    

      PreparedStatement statement = null;

      try {
        if (symbol.contains("Average")) {
          return;
        }
        statement = connect.prepareStatement(
                "INSERT INTO  popularGenes (gene,hits) VALUES (?,1)"
                + "  ON DUPLICATE KEY UPDATE hits=hits+1;");
        statement.setString(1, symbol + "-" + species);
        statement.execute();

      } catch (SQLException ex) {
        Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        if (statement != null) {
          try {
            statement.close();
          } catch (SQLException ex) {
            Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    
  }

  /**
   * Get the most popular genes based on user input
   * It will look at table populargenes
   * 
   * @return Array of top 10 popular genes 
   */
  public String[] getPopularGenes() {

    String[] result = new String[10];

    PreparedStatement prepStmt = null;

    try {
      String sqlStmt = "select  gene from popularGenes order by hits desc limit 10";
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      int c = 0;
      while (rs.next() && c < 15) {

        result[c] = rs.getString("gene");
        if (result[c].contains("Average")) {
          continue;
        }
        c++;
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }

  /**
   * Queries all the *_count tables to get the top 10 multifunctionality ids
   * per species on the last edition
   * @return 
   */
  public List<String> getTopMultifunc() {

    List<String> result = new ArrayList<String>();

    PreparedStatement prepStmt = null;
    try {
        /*Query made out of several unions one per species
         */
      String sqlStmt =
              "(select symbol, multifunc, edition, 'human' as species from human_count a where multifunc is not null and edition = (select max(edition) from human_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'arabidopsis' as species from arabidopsis_count b where multifunc is not null  and edition = (select max(edition) from arabidopsis_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'chicken' as species from chicken_count c  where multifunc is not null  and edition = (select max(edition) from chicken_count where multifunc is not null) order by multifunc desc limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'cow' as species from cow_count d  where multifunc is not null and edition = (select max(edition) from cow_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union \n"
              + "(select symbol, multifunc, edition, 'dicty' as species from dicty_count e  where multifunc is not null and edition = (select max(edition) from dicty_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'dog' as species from dog_count f where multifunc is not null and edition = (select max(edition) from dog_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'fly' as species from fly_count g where multifunc is not null and edition = (select max(edition) from fly_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition , 'mouse' as species from mouse_count h  where multifunc is not null and edition = (select max(edition) from mouse_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition,'pig' as species from pig_count i where multifunc is not null and edition = (select max(edition) from pig_count where multifunc is not null) order by multifunc desc limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'rat' as species from rat_count j where multifunc is not null and edition = (select max(edition) from rat_count where multifunc is not null) order by multifunc desc limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'worm' as species from worm_count k  where multifunc is not null and edition = (select max(edition) from worm_count where multifunc is not null) order by multifunc desc  limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'yeast' as species from yeast_count l  where multifunc is not null and edition = (select max(edition) from yeast_count where multifunc is not null) order by multifunc desc limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition, 'zebrafish' as species from zebrafish_count n  where multifunc is not null and edition = (select max(edition) from zebrafish_count where multifunc is not null) order by multifunc desc limit 1) \n"
              + "union all\n"
              + "(select symbol, multifunc, edition,'ecoli' as species  from ecoli_count o where multifunc is not null and edition = (select max(edition) from ecoli_count where multifunc is not null) order by multifunc desc limit 1) \n";
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      int c = 0;
      while (rs.next() && c < 14) {
        result.add(rs.getString("species") + "-" + rs.getString("symbol"));
        c++;
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }
/**
   * Query the database to get the genes for this species ordered by multi functionality 
   * 
   * @param species
   * @return 
   */
  public List<String> getTopMultifuncforSpecies(String species) {

    List<String> result = new ArrayList<String>();

    PreparedStatement prepStmt = null;
    try {
      String sqlStmt = "select gene, symbol, multifunc, edition, '"+species+"' as species "
              + "from "+species+"_count a where multifunc is not null and "
              + "edition = (select max(edition) from "+species+"_count) "
              + "order by multifunc desc \n";
      Logger.getLogger(GotrackDB.class.getName()).log(Level.FINE,
              "SQL Statement:\n\t{0}", sqlStmt);
      prepStmt = connect.prepareStatement(sqlStmt);
      ResultSet rs = prepStmt.executeQuery();
      while (rs.next() ) {
        result.add(rs.getString("gene")+"-\t"+ rs.getString("symbol"));        
      }
      rs.close();
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      if (prepStmt != null) {
        try {
          prepStmt.close();
        } catch (SQLException ex) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return result;
  }
}