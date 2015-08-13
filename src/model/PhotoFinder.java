package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class PhotoFinder {
	// @ToDo konkrete photoid finden von dem wir ausgehen das es die richtige longlat
	// fuer das uebergebene foto beinhaltet
	public long foundPhotoID(String searchTag, int gridID){
		long photoID = 0;
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		
		System.out.println("gridIDfoundPhoto: "+ gridID);
		
		if(gridID==0){
			return photoID;
		}
		
		statement="SELECT photoID FROM pae_datastore.pae_grid where gridID = "+gridID+" AND tag = '"+searchTag+"' LIMIT 1;";
		
		ResultSet findPhoto=null;

		
		findPhoto = dbDAO.accessDatabaseSelect(statement);
		
		
		try {
			while (findPhoto.next()) {
				photoID = findPhoto.getLong(1);
				return photoID;
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
		
		// wenn nicht 100%ige uebereinstimmung
		
		statement="SELECT photoID, tag FROM pae_datastore.pae_grid where gridID = "+gridID+";";
		
		findPhoto = dbDAO.accessDatabaseSelect(statement);
		
		HashMap<Long, String> allPhotosFromGrid = new HashMap<Long, String>();
		
		try {
			while (findPhoto.next()) {
				allPhotosFromGrid.put(findPhoto.getLong(1), findPhoto.getString(2));
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
		
		// alle woerter des searchtag extrahieren
		String tag = searchTag;
		
		// Separieren der einzelnen Woerter aus dem Tag
		Vector<String> tagVec= new Vector<String>();
		
		if(tag.matches("\\W+") || tag.isEmpty() || tag.matches("\\s+")){
			tagVec.add(" ");
		}else{
			String[] tagArrayTemp = tag.split( Pattern.quote( " " ));
			
			for(int z=0; z<tagArrayTemp.length; z++){
				tagVec.add(tagArrayTemp[z]);
				tagVec.set(z, tagVec.get(z).trim());
			}
		}
		
		//Vector<Integer> toRemove= new Vector<Integer>();

		TreeMap<Integer, Long> wordCounter = new TreeMap<Integer, Long>();
		
		for (Map.Entry<Long, String> entry : allPhotosFromGrid.entrySet()) {
			// Key ist die frequency
			Long key = entry.getKey();
			
			// Value ist die Grid
			String value = entry.getValue();
			int count=0;
			
			for(int i=0; i< tagVec.size(); i++){
				if(StringUtils.countMatches(value, tagVec.get(i))>0){
					count++;
				}
			}
			
			wordCounter.put(count, key);
			
		}
		
		SortedMap<Integer, Long> wordCounterSorted = wordCounter;

		photoID = wordCounterSorted.get(wordCounterSorted.lastKey());

		//
		if(photoID==0){
			
			DatabaseDAO dbDAO2 = new DatabaseDAO();
			
			String statement2 = null;
						
			statement2="SELECT photoID FROM pae_datastore.pae_grid where gridID = "+gridID+" limit 0,1;";
			
			ResultSet findPhoto2=null;

			findPhoto2 = dbDAO.accessDatabaseSelect(statement);
			
			try {
				while (findPhoto2.next()) {
					photoID = findPhoto2.getLong(1);
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
		}
		
		return photoID;
	}
	
	public Double[] foundPhotoLatLong(long photoID, boolean searchedOriginal){

		Double[] photoLatLong = new Double[2];
		
		if(photoID==0){
			
			photoLatLong[0] = 21.318642;
			photoLatLong[1] = 0.957184;
			
			return photoLatLong;
		}
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		// true=searchedphoto
		if(searchedOriginal){
			statement="SELECT latitude, longitude FROM `pae_datastore`.`pae_longlat_original` where photoID = "+photoID+";";
		}else{
			statement="SELECT latitude, longitude FROM `pae_datastore`.`pae_longlat_testdaten` where photoID = "+photoID+";";
		}
			
		ResultSet findPhotoLatLong=null;

		findPhotoLatLong = dbDAO.accessDatabaseSelect(statement);
		
		try {
			if(findPhotoLatLong.wasNull()){
								
				photoLatLong[0] = 21.318642;
				photoLatLong[1] = 0.957184;
				
				try {
					dbDAO.con.close();
					dbDAO.st.close();
					dbDAO.rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return photoLatLong;
			}
			
			while (findPhotoLatLong.next()) {
				photoLatLong[0] = findPhotoLatLong.getDouble(1);
				photoLatLong[1] = findPhotoLatLong.getDouble(2);
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
		return photoLatLong;
	}
}
