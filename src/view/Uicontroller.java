package view;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.Distance;
import model.GridFinder;
import model.PhotoFinder;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;


/**
 * 
 * View
 * Herzstueck und Verwaltung des kompletten view.
 * Managen von Servlet und JSPs
 * 
 */
@WebServlet(  
        urlPatterns = {"/Uicontroller/csv", "/Uicontroller/googleAPI", "/Uicontroller/index"}  
        )
@MultipartConfig
public class Uicontroller extends HttpServlet {

	private RequestDispatcher JSPIndex = null;
	private RequestDispatcher JSPResult = null;
	
	
	/**
	 *
	 * Initialisierung beim laden des Servlets
	 * 
	 */
	public void init() throws ServletException
	{
		JSPIndex = getServletContext().getRequestDispatcher("/index.jsp");
		JSPResult = getServletContext().getRequestDispatcher("/result.jsp");
	}
	
	/**
	 *
	 * Abfangen von Get-Anfragen
	 *
	 * @param request Http Servlet Request
	 * @param response Http Servlet Response
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		try{
		doRequest(request, response);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 *
	 * Abfangen von Post-Anfragen
	 *
	 * @param request Http Servlet Request
	 * @param response Http Servlet Response
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		doRequest(request, response);
	}

	/**
	 *
	 * Verarbeitung des Request, egal ob durch Post oder Get eingegangen
	 *
	 * @param request Http Servlet Request
	 * @param response Http Servlet Response
	 * 
	 */
	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		HttpSession session = request.getSession(false);
		
		String gewuenschteSeite ="";
		
		gewuenschteSeite = request.getParameter("gewuenschteSeite");
		
		int foundGrid;
		
		if(ServletFileUpload.isMultipartContent(request)){
			if(request.getPart("inputfile") != null && request.getPart("inputfile").getSize()>0 && request.getPart("inputfileOriginal") != null && request.getPart("inputfileOriginal").getSize()>0){
				
				if (session == null)
				{
					// neue Session erstellen und zuweisen
					session = request.getSession(true);
				}else{
					session.invalidate();
					session = request.getSession(true);
				}
				long start = new Date().getTime();
				
				session.setAttribute("start", start);
				
				//zu suchendes Bild
				Part filePart = request.getPart("inputfile");
			    String fileName = filePart.getSubmittedFileName();
			    
			    InputStream fileContent = filePart.getInputStream();
			    
			    InputStreamReader isr = null;
			    BufferedReader in = null;
			    
			    
			    // original Bild
			    Part filePartOriginal = request.getPart("inputfileOriginal");
			    String fileNameOriginal = filePart.getSubmittedFileName();
			    
			    InputStream fileContentOriginal = filePartOriginal.getInputStream();
				
			    InputStreamReader isrOriginal = null;
			    BufferedReader inOriginal = null;
			    
		        try
		        {
		        	
		            // Erstellung eines ObjectInputStreams, der 
		            // die eigentlichen Lesemethoden enthält
		            // wird mit dem FileInputStream erzeugt.
		        		isr = new InputStreamReader(fileContent);
		        		isrOriginal = new InputStreamReader(fileContentOriginal);
		
		
		            // Die Methode readObject liest komplexe 
		            // Datentypen aus der Datei
		        		String tag = null;
		        		String photoIDOriginal = null;
		            
		        		in = new BufferedReader(isr);
					tag = in.readLine();
		            
		            inOriginal = new BufferedReader(isrOriginal);
					photoIDOriginal = inOriginal.readLine();
					
					long runningTime = new Date().getTime() - start;
					
					System.out.println("Laufzeit2: "+runningTime);
					
					long start1 = new Date().getTime();
					
		            GridFinder gridFinder = new GridFinder();
		    			foundGrid = gridFinder.findGridMostFrequency(tag);
		    			
		    			session.setAttribute("foundGrid", foundGrid);
		    			
		    			// 0=Lat  1=Long
		    			Double[] gridLatLong = new Double[2];
		    					    			
		    			gridLatLong = gridFinder.findGridLatLong(foundGrid);
		    			
		    			session.setAttribute("gridLat", gridLatLong[0]);
		    			session.setAttribute("gridLong", gridLatLong[1]);
		    			
		    			
		    			long runningTime1 = new Date().getTime() - start1;
						
					System.out.println("Laufzeit3: "+runningTime1);
					
					long start2 = new Date().getTime();
					
		    			
		    			List<double[]> gridPics = new ArrayList<double[]>();
		    			
		    			gridPics = gridFinder.findGridPics(foundGrid, session);
		    			
		    			
		    			session.setAttribute("gridPics", gridPics);
		    			
		    			
		    			long runningTime2 = new Date().getTime() - start2;
						
					System.out.println("Laufzeit4: "+runningTime2);
					
					long start3 = new Date().getTime();
	
		    			
		    			Double[] gridKoordinaten = new Double[4];
		    			
		    			// zum erstellen des grid overlays
		    			gridKoordinaten = gridFinder.findGridKoordinaten(foundGrid);

		    			session.setAttribute("gridKoordinaten", gridKoordinaten);
		    			
		    			long runningTime3 = new Date().getTime() - start3;
						
					System.out.println("Laufzeit5: "+runningTime3);
					
					long start4 = new Date().getTime();
		    			
		    			
		    			// finden der latlong des zu suchenden bildes
		    			PhotoFinder photoFinder = new PhotoFinder();
		    			
		    			long photoIDSearchedPhoto = photoFinder.foundPhotoID(tag, foundGrid);

		    			Double[] photoLatLongSearchedPhoto = photoFinder.foundPhotoLatLong(photoIDSearchedPhoto, true);
		    			
		    			session.setAttribute("photoIDSearchedPhoto", photoIDSearchedPhoto);
		    			session.setAttribute("photoLatLongSearchedPhoto", photoLatLongSearchedPhoto);
		    			
		    			long runningTime4 = new Date().getTime() - start4;
						
					System.out.println("Laufzeit6: "+runningTime4);
					
					long start5 = new Date().getTime();
		    			
		    			Double[] photoLatLongOriginalPhoto = photoFinder.foundPhotoLatLong(Long.valueOf(photoIDOriginal), false);
		    			
		    			session.setAttribute("photoIDOriginal", photoIDOriginal);
		    			session.setAttribute("photoLatLongOriginalPhoto", photoLatLongOriginalPhoto);
		    			
		    			
		    			long runningTime5 = new Date().getTime() - start5;
						
					System.out.println("Laufzeit7: "+runningTime5);
					
					long start6 = new Date().getTime();
		    			
					
		    			String[] longLatDistance = new String[4];

		    			
		    			longLatDistance[0] = photoLatLongOriginalPhoto[1].toString();
		    			longLatDistance[1] = photoLatLongOriginalPhoto[0].toString();
		    			longLatDistance[2] = photoLatLongSearchedPhoto[1].toString();
		    			longLatDistance[3] = photoLatLongSearchedPhoto[0].toString();
		    					   
		    			Distance distance = new Distance();
		    			double dist = distance.getDistance(longLatDistance);
		    			
		    			session.setAttribute("distance", dist);
		    			
		    			long runningTime6 = new Date().getTime() - start6;
						
					System.out.println("Laufzeit8: "+runningTime6);
		    			
		    			JSPResult.include(request, response);
		    			   
		        }
		        catch (IOException ex)
		        {
		            ex.printStackTrace();
		        }
			}else{// if >0
				session.invalidate();
				session = request.getSession(true);

				response.sendRedirect("http://localhost:8080/pae/");

			}
		}else{ // if multipart
			if ((gewuenschteSeite == null) || (gewuenschteSeite.equals("zurueckIndex")))
			{
				session.invalidate();
				session = request.getSession(true);
				
				JSPIndex.include(request, response);
			}
		}
	}// dorequest
}
