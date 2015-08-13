package model;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

// vorbereitung fuer das pear evaluation script
public class Testdaten {
	
	public void makeCSVParticipantForPearl(){
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		
		
		statement="select * from pae_datastore.pae_testdaten;";
		
		ResultSet testdatenResult=null;

		
		testdatenResult = dbDAO.accessDatabaseSelect(statement);
		
		long photoID=0;
		String tag = null;
		
		GridFinder gridFinder = new GridFinder();
		int foundGrid=0;
		
		String CRLF = "\r\n";
		//String CRLF = "\n";
	    String delimiter = ";"; 

	    // finden der latlong des zu suchenden bildes
		PhotoFinder photoFinder = new PhotoFinder();
	    
	    FileWriter writer;
		try {
			writer = new FileWriter("testdatenParticipant.csv");
	    
			try {
				int counter=0;
				while (testdatenResult.next() && !testdatenResult.wasNull()) {
					counter++;

					photoID = testdatenResult.getLong(1);
					tag = testdatenResult.getString(2);
					
	    				foundGrid = gridFinder.findGridMostFrequency(tag);
		    			
		    			long photoIDSearchedPhoto = photoFinder.foundPhotoID(tag, foundGrid);
		    			// lat=0 long=1
		    			Double[] photoLatLongSearchedPhoto = photoFinder.foundPhotoLatLong(photoIDSearchedPhoto, true);
		    			
		    			writer.append(String.valueOf(photoID));

		   	        //Das Trennzeichen einfuegen
		   	        writer.append(delimiter); 

		   	        writer.append(photoLatLongSearchedPhoto[0].toString());

		   	        writer.append(delimiter); 

		   	        writer.append(photoLatLongSearchedPhoto[1].toString());

		   	        //Das Zeilenende einfuegen
		   	        writer.append(CRLF);   

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
			
			writer.flush(); 
		    writer.close();	 

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void makeCSVGroundTruthForPearl(){
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
				
		statement="select * from pae_datastore.pae_longlat_testdaten;";
		
		ResultSet testdatenResult=null;
		
		testdatenResult = dbDAO.accessDatabaseSelect(statement);
		
		long photoID=0;
		Double latitude = 0.0;
		Double longitude = 0.0;

		String CRLF = "\r\n";
		//String CRLF = "\n";
	    String delimiter = " "; 

	    FileWriter writer;
		try {
			writer = new FileWriter("testdatenGroundTruth.csv");
	    
			try {
				int counter=0;
				while (testdatenResult.next()) {
					counter++;

					photoID = testdatenResult.getLong(1);
					latitude = testdatenResult.getDouble(2);
					longitude = testdatenResult.getDouble(3);
		    			
		    			writer.append(String.valueOf(photoID));

		   	        //Das Trennzeichen einfuegen
		   	        writer.append(delimiter); 

		   	        writer.append(latitude.toString());

		   	        writer.append(delimiter); 

		   	        writer.append(longitude.toString());

		   	        //Das Zeilenende einfuegen
		   	        writer.append(CRLF);   

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
			
			writer.flush(); 
		    writer.close();	 

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
