package stats;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import beans.view.Stats1;
import beans.view.Stats2;
import database.GotrackDB;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import utils.ViewUtils;

/**
 *
 * @author asedeno
 */
@ManagedBean
@SessionScoped
public class StatsBean  extends StatsData  {

   private String graph1=null;
   private String graph2 = null;
   private String graph3 = null;
   private String species1 = "";
   private String species2 = "";
   private String specificSpecies="";
   
   public void statsForSpecies(){
     GotrackDB dao = new GotrackDB();
    ArrayList<Stats1> stats1 = dao.getStatsPerSpecies(specificSpecies);
    setGraph3(ViewUtils.transformStatsPerSpecies(stats1, specificSpecies));
    dao.closeGotrackConnection();
   }

   public void compareSpecies(){
     getGraph1FromDB();
     getGraph2FromDB();     
   }
   
   public void getGlobalParams(){
     statsForSpecies();
   }
   
  /**
   * @return the graph1
   */
  public String getGraph1() {
    if(graph1==null){
     // getGraph1FromDB();
    }
    return graph1;
  }

  /**
   * @param graph1 the graph1 to set
   */
  public void setGraph1(String graph1) {
    this.graph1 = graph1;
  }
  
  /**
   * Creates a new instance of StatsBean
   */
  public StatsBean() {
  }
  
  public void getGraph1FromDB (){
    GotrackDB dao = new GotrackDB();
    ArrayList<Stats1> stats1 = dao.getStats1(species1,species2);
    setGraph1(ViewUtils.transformStats1tring(stats1));
    dao.closeGotrackConnection();
  }

  public void getGraph2FromDB (){
    GotrackDB dao = new GotrackDB();
    ArrayList<Stats2> stats2 = dao.getStats2(species1, species2);
    setGraph2(ViewUtils.transformStats2tring(stats2));
    dao.closeGotrackConnection();
  }
  /**
   * @return the graph2
   */
  public String getGraph2() {
    if(graph2==null){
      //getGraph2FromDB();
    }
    return graph2;
  }

  /**
   * @param graph2 the graph2 to set
   */
  public void setGraph2(String graph2) {
    this.graph2 = graph2;
  }

  /**
   * @return the species1
   */
  public String getSpecies1() {
    return species1;
  }

  /**
   * @param species1 the species1 to set
   */
  public void setSpecies1(String species1) {
    this.species1 = species1;
  }

  /**
   * @return the species2
   */
  public String getSpecies2() {
    return species2;
  }

  /**
   * @param species2 the species2 to set
   */
  public void setSpecies2(String species2) {
    this.species2 = species2;
  }

  /**
   * @return the graph3
   */
  public String getGraph3() {
    return graph3;
  }

  /**
   * @param graph3 the graph3 to set
   */
  public void setGraph3(String graph3) {
    this.graph3 = graph3;
  }

  /**
   * @return the specificSpecies
   */
  public String getSpecificSpecies() {
    return specificSpecies;
  }

  /**
   * @param specificSpecies the specificSpecies to set
   */
  public void setSpecificSpecies(String specificSpecies) {
    this.specificSpecies = specificSpecies;
  }
  
}
