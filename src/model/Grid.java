package model;


public class Grid {
	
	public void matchesPicsToGrids() {
		// Bilder zur Grid zuordnen
		/*
		  Aufgrund der vielen Datensätze direkt auf dem Server ausgeführt und nicht lokal
		  
		INSERT INTO `pae_datastore`.`pae_grid` (`photoID`,`gridID`,`tag`) 
		SELECT DISTINCT ll.photoID, gd.gridID, ds.photoTags 
		FROM pae_datastore.pae_griddefinition_original AS gd, pae_datastore.pae_longlat_original AS ll, pae_datastore.pae_datastore_original AS ds
		WHERE ((ll.latitude BETWEEN gd.latStart AND gd.latEnd) AND (ll.longitude BETWEEN gd.longStart AND gd.longEnd) AND (ll.photoID = ds.photoID));

		INSERT INTO table (id,Col1,Col2) VALUES (1,1,1),(2,2,3),(3,9,3),(4,10,12)
		ON DUPLICATE KEY UPDATE Col1=VALUES(Col1),Col2=VALUES(Col2);

		
		 */
	}
	
}
