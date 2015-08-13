package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class FindPhotoURL {

	public String findPhotoURL(long photoID){
		String photoURL = null;
		
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		String statement = null;
		
		
		statement="SELECT photoLink FROM `pae_datastore`.`pae_datastore_original` where photoID = "+photoID+";";
		
		ResultSet findPhotoURL=null;

		
		findPhotoURL = dbDAO.accessDatabaseSelect(statement);
		
		
		try {
			while (findPhotoURL.next()) {
				photoURL = findPhotoURL.getString(1);
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
		
		return photoURL;
	}
}
