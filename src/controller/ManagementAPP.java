package controller;

import model.Dictionary;
import model.Grid;
import model.GridFinder;
import model.GridGenerator;
import model.RemoveStopwords;
import model.Testdaten;

public class ManagementAPP {
	
	public static void main(String[] args) {

		// Gitter aufbauen
		// Lat -90° Long -180° bis Lat 90° Long 180° in 10° Schritten
		GridGenerator gridGenerator = new GridGenerator();
		gridGenerator.generateGrid();
		
		// Bilder zur Grid zuordnen
		Grid grid = new Grid();
		grid.matchesPicsToGrids();
		
		
		// Stopwörter entfernen
		RemoveStopwords removeStopwords = new RemoveStopwords();
		removeStopwords.removeStopwords();

		// Datensaetze mit leeren Tags entfernen
		removeStopwords.removeTags();
		
		// Wörterbuch erstellen für jede Grid
		Dictionary dictionary = new Dictionary();
		dictionary.createDictionary();
		
		
		// GridFinder
		//GridFinder gridFinder = new GridFinder();
		//gridFinder.findGridMostFrequency(tag);
		
		//stapelverarbeitung der testdaten fuer das pearscript
		//Testdaten testdaten = new Testdaten();
		//testdaten.makeCSVGroundTruthForPearl();
		//testdaten.makeCSVParticipantForPearl();
	}
}
