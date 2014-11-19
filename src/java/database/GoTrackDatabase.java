package database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GoTrackDB extends this class.
 * GoTrackDatabase implements generic functions to make
 * a connection to the database.
 * It contains the methods to read the connection parameters from the properties
 * file-
 * @author asedeno
 */
public class GoTrackDatabase {

  protected Connection connect = null;
  protected String database = null;
  protected String user = null;
  protected String password = null;
  protected String host = null;

  public GoTrackDatabase() {

    try {
      readProperties();
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");      
      // Setup the connection with the DB
      connect = DriverManager
              .getConnection("jdbc:mysql://" + host + "/" + database + "?"
              + "user=" + user + "&password=" + password + "");
      
    } catch (SQLException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    } catch (ClassNotFoundException e) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
    }

  }

  private void readProperties() {
    Properties prop = new Properties();
    InputStream input = null;
    OutputStream output = null;

    try {
      input = getClass().getClassLoader().getResourceAsStream("config.properties");
      // load a properties file
      prop.load(input);      
      // get the property value and print it out
      database = prop.getProperty("database");
      user = prop.getProperty("dbuser");
      password = prop.getProperty("dbpassword");
      host = prop.getProperty("dbhost");

      if (database == null || user == null || password == null) {

        System.err.println("Error reading config.properties file. Please define database, dbsuer and dbpassword.\n"
                + "This file should be in  WEB-INF/classes inside gotrack.war or in src/java");        
      }
    } catch (IOException io) {
      Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, io);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
         Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
        }
      }
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          Logger.getLogger(GotrackDB.class.getName()).log(Level.SEVERE, null, e);
        }
      }

    }
  }
}
