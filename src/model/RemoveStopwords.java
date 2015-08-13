package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Vector;

public class RemoveStopwords {
	
	public void removeStopwords(){
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		ResultSet ergebnis=null;
		
		String statement="SELECT initialisiert FROM `pae_datastore`.`pae_grid_initialisiert`;";
		
		ergebnis = dbDAO.accessDatabaseSelect(statement);
		
		int stopwordInitialisiert=0;
		
		try {
			while (ergebnis.next()) {
				stopwordInitialisiert=ergebnis.getInt(1);
			}
			
			if (ergebnis != null) {
                ergebnis.close();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(dbDAO.st != null) dbDAO.st.close();
				if(dbDAO.con != null)  dbDAO.con.close(); 
				 if(dbDAO.rs != null)  dbDAO.rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
		
		if(stopwordInitialisiert != 1){
		
		
			statement="SELECT stopword FROM `pae_datastore`.`pae_stopwords_original`;";
			
			ResultSet stopwords=null;
			
			stopwords = dbDAO.accessDatabaseSelect(statement);
			
			int counter=0;
			Vector<String> swVec = new Vector<String>();
			
			try {
				while (stopwords.next()) {
					swVec.add(stopwords.getString(1));
				}
				
				if (stopwords != null) {
	                stopwords.close();
	            }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					if(dbDAO.st != null) dbDAO.st.close();
					if(dbDAO.con != null)  dbDAO.con.close(); 
					 if(dbDAO.rs != null)  dbDAO.rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
			}
						
			for(int i=0; i<swVec.size();i++){
				// stopword am anfang oder ende des tags zu unsicher zu entfernen
				// zb "text send me" stopword= "end " würde end aus send entfernen
				
				// stopword mitten im tag
				statement="UPDATE pae_datastore.pae_grid SET tag = REPLACE(tag, ' "+swVec.get(i)+" ', '');";
				dbDAO.accessDatabaseManipulation(statement);
			}
			
			dbDAO.accessDatabaseManipulation("INSERT INTO `pae_datastore`.`pae_grid_initialisiert` (`initialisiert`) VALUES ('1');");
		}
	}
	
	/**
	 * loescht Fotos ohne Tags und loescht die Stopwords aus den Tags
	 */
	public void removeTags(){
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		ResultSet ergebnis=null;
		
		String statement="SELECT initialisiert FROM `pae_datastore`.`pae_delete_initialisiert`;";
		
		ergebnis = dbDAO.accessDatabaseSelect(statement);
		
		int deleteInitialisiert=0;
		
		try {
			while (ergebnis.next()) {
				deleteInitialisiert=ergebnis.getInt(1);
			}
			
			if (ergebnis != null) {
                ergebnis.close();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(dbDAO.st != null) dbDAO.st.close();
				if(dbDAO.con != null)  dbDAO.con.close(); 
				 if(dbDAO.rs != null)  dbDAO.rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
		
		if(deleteInitialisiert != 1){		
			// Datensätze mit leeren Tags entfernen
			statement="DELETE FROM pae_datastore.pae_grid WHERE (tag is NULL) OR (trim(tag)='')";
			dbDAO.accessDatabaseManipulation(statement);
		}
	}
	
	
	public void removeTagsTestdaten(){
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		ResultSet ergebnis=null;
		
	}
	
}
