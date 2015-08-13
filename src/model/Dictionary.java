package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Dictionary {
	public void createDictionary(){
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		ResultSet ergebnis=null;
		
		String statement="SELECT initialisiert FROM `pae_datastore`.`pae_woerterbuch_initialisiert`;";
		
		ergebnis = dbDAO.accessDatabaseSelect(statement);
		
		int woerterbuchInitialisiert=0;
		
		try {
			while (ergebnis.next()) {
				woerterbuchInitialisiert=ergebnis.getInt(1);
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
		
		if(woerterbuchInitialisiert != 1){
			// Anzahl Grids
			int gridCounter=0;
			statement="SELECT count(gridID) FROM pae_datastore.pae_griddefinition_original;";
			
			ResultSet gridCounterResult=null;
			
			gridCounterResult = dbDAO.accessDatabaseSelect(statement);
			
			try {
				while (gridCounterResult.next()) {
					gridCounter = gridCounterResult.getInt(1);
				}
				
				if (gridCounterResult != null) {
					gridCounterResult.close();
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
			
			// Wörter seperieren
			// jede grid durchlaufen und wörterbuch generieren
			for(int i = 1; i <=gridCounter;i++){		
				statement="SELECT tag FROM pae_datastore.pae_grid where gridID = "+i+";";
				
				ResultSet gridTagResult=null;
				Vector<String> gridTagTemp= new Vector<String>();
				HashMap<String, Integer> gridTag= new HashMap<String, Integer>();
				
				gridTagResult = dbDAO.accessDatabaseSelect(statement);
				int counter = 0;
				try {
					while (gridTagResult.next()) {
						gridTagTemp.add(gridTagResult.getString(1).trim());
						
						String[] segs = gridTagTemp.get(counter).split( Pattern.quote( " " ) );
						counter++;
						
						for(int j = 0; j <segs.length; j++){
							String tempWord = segs[j];
							if(gridTag.get(tempWord) != null){
								int tempCount = gridTag.get(tempWord);
								tempCount++;
								gridTag.put(tempWord, tempCount);
							}else{
								gridTag.put(tempWord, 1);
							}
						}
					}
					
					if (gridTagResult != null) {
						gridTagResult.close();
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
				
				
				for (Map.Entry<String, Integer> entry : gridTag.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    dbDAO.accessDatabaseManipulation("INSERT INTO `pae_datastore`.`pae_dictionary` (`gridID`, `word`, `frequency`) VALUES ('"+i+"', '"+key+"', '"+value+"');");
				    // ...
				}
			}//for
			dbDAO.accessDatabaseManipulation("INSERT INTO `pae_datastore`.`pae_woerterbuch_initialisiert` (`initialisiert`) VALUES ('1');");
		}//if initialisiert
	}
}
