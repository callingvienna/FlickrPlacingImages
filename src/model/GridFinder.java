package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

/**
 * @author Z4
 * Get the fitting Grid for an image with the use of the tags
 *
 */
public class GridFinder {

	// alle photoids einer grid
	public Vector<Long> gridPhotoID(int gridID){
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		
		statement="select photoID from pae_datastore.pae_grid where gridID =  "+gridID+" LIMIT 0, 5000;";
		
		ResultSet findPhotoID=null;

		long start = new Date().getTime();
		
		
		findPhotoID = dbDAO.accessDatabaseSelect(statement);
		
		Vector<Long> photoIDs = new Vector<Long>();
		
		try {
			while (findPhotoID.next()) {
				photoIDs.add(findPhotoID.getLong(1));
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
		
		long runningTime = new Date().getTime() - start;
		
		System.out.println("Laufzeit1: "+runningTime);
		return photoIDs;
	}
	
	// gibt für alle Bilder einer Grid die LatLong zurueck
	public List<double[]> findGridPics(int gridID, HttpSession session){
		List<double[]> gridPics = new ArrayList<double[]>();
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;

		Vector<Long> photoIDs = new Vector<Long>();
		
		photoIDs = gridPhotoID(gridID);
				
		session.setAttribute("photoIDs", photoIDs);
		
		int counter=0;
		
		for(int i = 0; i<photoIDs.size();i++){
			statement="select latitude, longitude from pae_datastore.pae_longlat_original where photoID  =  "+photoIDs.get(i)+";";
			
			ResultSet findPhotoLatLong=null;

			findPhotoLatLong = dbDAO.accessDatabaseSelect(statement);
			
			try {
				while (findPhotoLatLong.next()) {
					gridPics.add(new double[] { findPhotoLatLong.getDouble(1), findPhotoLatLong.getDouble(2) });
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
			counter++;
		}
				
		return gridPics;
	}
	
	// zum Google Map Zoomen, die Gridmitte finden
	public Double[] findGridLatLong(int gridID){
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		
		
		statement="select latStart, latEnd, longStart, longEnd from pae_datastore.pae_griddefinition_original where gridID = "+gridID+";";
		
		ResultSet findLatLongResult=null;

		
		findLatLongResult = dbDAO.accessDatabaseSelect(statement);
		
		Double[] gridLatLongTemp = new Double[4];
		
		try {
			while (findLatLongResult.next()) {
				// latStart
				gridLatLongTemp[0] = findLatLongResult.getDouble(1);
				
				// latEnd
				gridLatLongTemp[1] = findLatLongResult.getDouble(2);
				
				// longStart
				gridLatLongTemp[2] = findLatLongResult.getDouble(3);
				
				// longEnd
				gridLatLongTemp[3] = findLatLongResult.getDouble(4);
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
		
		
		double latTemp = gridLatLongTemp[1] - gridLatLongTemp[0];
		double longTemp = gridLatLongTemp[3] - gridLatLongTemp[2];
		
		latTemp = latTemp/2;
		longTemp = longTemp/2;
		
		latTemp = latTemp + gridLatLongTemp[0];
		longTemp = longTemp + gridLatLongTemp[2];
		
		
		// 0 = Lat  1 = Long
		Double[] gridLatLong = new Double[2];
		
		gridLatLong[0] = latTemp;
		gridLatLong[1] = longTemp;

		return gridLatLong;
	}

	/**
	 * Find the Grids where the tags of the image have the most frequency
	 * @param allG List of all existing Grids
	 * @return List of the Grids where the tags of the images have a high frequency
	 */
	public Integer findGridMostFrequency(String searchTag) {
		//run through all Grids
		//check where the tags of the image have the most frequency
		//for this check and run through each tagList of the Grid
		//add the top Grids to the mostFrequency List
		DatabaseDAO dbDAO = new DatabaseDAO();
				
		String statement = null;
		
		/*
		 id: '3229381'

		tag: hongkong lantau buddha vacation travel statue statues

		grid:426
		 * 
		 */
		//String tag = "hongkong lantau buddha vacation travel statue statues";
		
		String tag = searchTag;
		
		// Separieren der einzelnen Woerter aus dem Tag
		Vector<String> tagVec= new Vector<String>();
		String[] tagArrayTemp = tag.split( Pattern.quote( " " ));

		for(int z=0; z<tagArrayTemp.length; z++){
			tagVec.add(tagArrayTemp[z]);
		}
		
		for(int i=0; i< tagVec.size(); i++){
			
			tagVec.set(i, tagVec.get(i).trim());
			
			if(tagVec.get(i).matches("\\W+") || tagVec.get(i).isEmpty() || tagVec.get(i).matches("\\s+")){
				tagVec.remove(i);
				i--;
			}
		}

		// Die oben separierten Woerter im dictionary suchen und abspeichern, in welcher Grid sie vorkommen
		// und in welcher Haeufigkeit
		HashMap<String, SortedMap<Integer, Integer>> findLoacationHM = new HashMap<String, SortedMap<Integer, Integer>>();
		
		String searchWord = null;
		
		
		for(int k=0; k< tagVec.size(); k++){
			//System.out.println("7: "+k);
			searchWord = tagVec.get(k);
			
			statement="select * from pae_datastore.pae_dictionary where word = '"+searchWord+"';";
			
			ResultSet findLocationResult=null;
	
			
			findLocationResult = dbDAO.accessDatabaseSelect(statement);
			
			TreeMap<Integer, Integer> findLoacationTempHM = new TreeMap<Integer, Integer>();
	
			int counter = 0;
			try {
				while (findLocationResult.next()) {
					counter++;
					findLoacationTempHM.put(findLocationResult.getInt(3), findLocationResult.getInt(1));
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
					
			findLoacationHM.put(searchWord, findLoacationTempHM.descendingMap());
			
			
		}//for tagvec
		
		//System.out.println("9");
		/*buddha
		{439=389, 221=426, 155=479, 121=464, 101=463}
		Biggest frequency: 439
		gridID: 389


		1037 = 100%
		221 = x%

		(221*100)/1037
		*/
		
		// Für jedes Wort zunaechst das Gesamtvorkommen ueber allen Grids ermitteln
		TreeMap<Integer, Double> ratingMap = new TreeMap<Integer, Double>();
		
		// durchlaufen aller Woerter
		for (Map.Entry<String, SortedMap<Integer, Integer>> entry : findLoacationHM.entrySet()) {
			// Key ist die frequency
			String key = entry.getKey();
			
			// Value ist die Grid
			SortedMap<Integer, Integer> value = entry.getValue();
						
			Double sumFrequency = 0.0;
			
			Set<Integer> keySet = value.keySet();
			for(Integer frequencyTemp : keySet) {
				sumFrequency = sumFrequency+frequencyTemp;				
			}

			// Fuer jedes Wort Grid uebergreifend die Einzelwahrscheinlichkeiten speichern
			Double sumTemp=0.0;
			for (Map.Entry<Integer, Integer> entry2 : value.entrySet()) {

				if(ratingMap.get(entry2.getValue()) != null){
					sumTemp = (entry2.getKey()*100)/sumFrequency;
					sumTemp = sumTemp+ratingMap.get(entry2.getValue());
					
					ratingMap.put(entry2.getValue(), sumTemp);
				}else{
					sumTemp = (entry2.getKey()*100)/sumFrequency;
					ratingMap.put(entry2.getValue(), sumTemp);
				}
			}
		}
		
		// Die konkrete Grid finden, welche am wahrscheinlichsten ist, in dem für jede Grid
		// die Wahrscheinlichkeiten, jedes Wortes summiert werden. Die Grid mit der groessten Zahl
		// ist die Wahrscheinlichste Grid
		
		int foundGrid =0;
		Double foundFrequency =0.0;
		
		for (Map.Entry<Integer, Double> entrySum : ratingMap.entrySet()) {
			if(entrySum.getValue()>foundFrequency){
				foundFrequency = entrySum.getValue();
				foundGrid = entrySum.getKey();
			}
		}

		if(foundGrid == 0){
			foundGrid=415;
		}
		return foundGrid;
	}

	// zum Google Map Zoomen, die Gridmitte finden
		public Double[] findGridKoordinaten(int gridID){
			
			DatabaseDAO dbDAO = new DatabaseDAO();
			
			String statement = null;
			
			
			statement="select latStart, latEnd, longStart, longEnd from pae_datastore.pae_griddefinition_original where gridID = "+gridID+";";
			
			ResultSet findLatLongResult=null;

			
			findLatLongResult = dbDAO.accessDatabaseSelect(statement);
			
			Double[] gridLatLongTemp = new Double[4];
			
			try {
				while (findLatLongResult.next()) {
					// latStart
					gridLatLongTemp[0] = findLatLongResult.getDouble(1);
					
					// latEnd
					gridLatLongTemp[1] = findLatLongResult.getDouble(2);
					
					// longStart
					gridLatLongTemp[2] = findLatLongResult.getDouble(3);
					
					// longEnd
					gridLatLongTemp[3] = findLatLongResult.getDouble(4);
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
			
			return gridLatLongTemp;
		}
	

		public double gridGroesse(int gridID){
			double gridGroesse =0.0;
			
			Double[] gridLatLongTemp = new Double[4];
			
			gridLatLongTemp = findGridKoordinaten(gridID);
			//02 13
			
			Distance distance = new Distance();
			
			String[] longLatDistance= new String[2];
			
			longLatDistance[0] = gridLatLongTemp[0].toString();
			longLatDistance[1] = gridLatLongTemp[2].toString();
			
			
			Double distance1 = distance.getDistance(longLatDistance);
			
			longLatDistance[0] = gridLatLongTemp[1].toString();
			longLatDistance[1] = gridLatLongTemp[3].toString();
			
			Double distance2 = distance.getDistance(longLatDistance);
			
			gridGroesse = distance1*distance2;
			
			return gridGroesse;
		}
}
