package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 
 * @author Z4
 * creates the Grids
 */
public class GridGenerator {
	
	/**
	 * erzeugt die endgueltigen Grids
	 */
	public void generateGrid(){
		// Gitter aufbauen
		// Lat -90° Long -180° bis Lat 90° Long 180° in 10° Schritten
		
		ResultSet ergebnis=null;
		String statement="SELECT initialisiert FROM `pae_datastore`.`pae_griddefinition_initialisiert`;";
		DatabaseDAO dbDAO = new DatabaseDAO();
		
		ergebnis = dbDAO.accessDatabaseSelect(statement);
		
		int intervallInitialisiert=0;
		
		try {
			while (ergebnis.next()) {
				intervallInitialisiert=ergebnis.getInt(1);
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
		
		if(intervallInitialisiert != 1){
			
			int count = 0;
			double latEnd=0;
			double longEnd=0;
			
			NumberFormat n = NumberFormat.getInstance();
			n.setMaximumFractionDigits(6); // max. 6 stellen hinter komma
			
			//Intervalle schaffen
			for(double latStart=-90; latStart<90;latStart=latStart+10){
				for(double longStart=-180;longStart <180; longStart=longStart+10){
					count++;
					if(latStart<=80){
						//latEnd=0;
						if(latStart == 80){
							latEnd = 90;
						}else{
							latEnd = latStart+9.999999;
						}
							
						try {
							latEnd = n.parse(n.format(latEnd)).doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(longStart<=170){
						//longEnd=0;
						
						if(longStart == 170){
							longEnd = 180;
						}else{
							longEnd = longStart+9.999999;
						}
						
						try {
							longEnd = n.parse(n.format(longEnd)).doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					try {
						dbDAO.accessDatabaseManipulation("INSERT INTO `pae_datastore`.`pae_griddefinition_original` (`longStart`, `longEnd`, `latStart`, `latEnd`) VALUES ('"+longStart+"', '"+longEnd+"', '"+latStart+"', '"+latEnd+"');");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			dbDAO.accessDatabaseManipulation("INSERT INTO `pae_datastore`.`pae_griddefinition` (`initialisiert`) VALUES ('1');");
		}
	}
}
