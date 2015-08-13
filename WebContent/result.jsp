<%@page import="model.FindPhotoURL"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Hashtable"%> 
<%@page import="java.util.Map"%> 
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.Date"%>

<%
long start = new Date().getTime();

int foundGrid = (int) session.getAttribute("foundGrid");

double gridLat = (double) session.getAttribute("gridLat");
double gridLong = (double) session.getAttribute("gridLong");

List<double[]> gridPics = new ArrayList<double[]>();
gridPics = (List<double[]>) session.getAttribute("gridPics");

Vector<Long> photoIDs = new Vector<Long>();
photoIDs = (Vector<Long>) session.getAttribute("photoIDs");

double distance = (double) session.getAttribute("distance");

Double[] gridKoordinaten = (Double[]) session.getAttribute("gridKoordinaten");

long photoIDSearchedPhoto = (long) session.getAttribute("photoIDSearchedPhoto");
Double[] photoLatLongSearchedPhoto = (Double[]) session.getAttribute("photoLatLongSearchedPhoto");

long photoIDOriginalPhoto = Long.valueOf((String) session.getAttribute("photoIDOriginal"));
Double[] photoLatLongOriginalPhoto = (Double[]) session.getAttribute("photoLatLongOriginalPhoto");

long startAnfang = (long) session.getAttribute("start");

long runningTime = new Date().getTime() - start;

%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Result</title>
 <style>
      html, body, #map-canvas {
        height: 90%;
        margin: 0px;
        padding: 0px
      }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
    <script>


 function initialize() {
   var mapOptions = {
     zoom: 6,
     //mapTypeId: google.maps.MapTypeId.SATELLITE,
     center: new google.maps.LatLng(<%= gridLat %>, <%= gridLong %>)
   }
   var map = new google.maps.Map(document.getElementById('map-canvas'),
                                 mapOptions);

   
   var gridRechteck;

	// latStart
	// latEnd
	// longStart
	// longEnd
	// 20.0/29.999999/110.0/119.999999
	// grid einzeichnen
   var rectangle = new google.maps.Rectangle({
	    strokeColor: 'yellow',
	    strokeOpacity: 0.8,
	    strokeWeight: 2,
	    fillColor: 'yellow',
	    fillOpacity: 0.1,
	    map: map,
	    bounds: new google.maps.LatLngBounds(
	      new google.maps.LatLng(<%= gridKoordinaten[0] %>, <%= gridKoordinaten[2] %>),
	      new google.maps.LatLng(<%= gridKoordinaten[1] %>, <%= gridKoordinaten[3] %>))
	  });
	
   var distancePolylineKoordinaten = [
     new google.maps.LatLng(<%= photoLatLongSearchedPhoto[0] %>, <%= photoLatLongSearchedPhoto[1] %>),
     new google.maps.LatLng(<%= photoLatLongOriginalPhoto[0] %>, <%= photoLatLongOriginalPhoto[1] %>),
   ];
	
   var distancePolyline = new google.maps.Polyline({
	    path: distancePolylineKoordinaten,
	    geodesic: true,
	    strokeColor: '#FF0000',
	    strokeOpacity: 1.0,
	    strokeWeight: 2
	  });

   distancePolyline.setMap(map);
   
   setMarkers(map, picsMarker);
 }

 /**
  * Data for the markers consisting of a name, a LatLng and a zIndex for
  * the order in which these markers should display on top of each
  * other.
  */
 var picsMarker = [
   <%
   int counter = 0;

	FindPhotoURL findPhotoURL = new FindPhotoURL();
	
	long start2 = new Date().getTime();
	
	for (int h = 0; h<gridPics.size();h++) {
		  
		Double latTemp = gridPics.get(h)[0];
		Double longTemp = gridPics.get(h)[1];
		String photoID = photoIDs.get(h).toString();
		
		if(Long.valueOf(photoID) == photoIDSearchedPhoto || Long.valueOf(photoID) == photoIDOriginalPhoto){
			
		}else{
		
			String photoURL = null;
			
			photoURL = findPhotoURL.findPhotoURL(photoIDs.get(h));
						
	  	    %>
	  	    ['<%= photoID %>', <%= latTemp %>, <%= longTemp %>, <%= counter+3 %>, 'yellow', '<%= photoURL %>'],
	  	    <%
	  	  	counter++;
		}
    } 
	
	long runningTime2 = new Date().getTime() - start2;
	       
		String photoURL = null;
		String photoURLOriginal = null;
		
		photoURL = findPhotoURL.findPhotoURL(photoIDSearchedPhoto);
		photoURLOriginal = findPhotoURL.findPhotoURL(photoIDOriginalPhoto);
		
		if(photoIDSearchedPhoto == photoIDOriginalPhoto){
   %>
   	
   
   ['<%= photoIDSearchedPhoto %>', <%= photoLatLongSearchedPhoto[0] %>, <%= photoLatLongSearchedPhoto[1] %>, 99999999999999, 'green', '<%= photoURL %>'],
 
   <%
		}else{
   %>
	   ['<%= photoIDSearchedPhoto %>', <%= photoLatLongSearchedPhoto[0] %>, <%= photoLatLongSearchedPhoto[1] %>, 99999999999999, 'red', '<%= photoURL %>'],
	   
	   ['<%= photoIDOriginalPhoto %>', <%= photoLatLongOriginalPhoto[0] %>, <%= photoLatLongOriginalPhoto[1] %>, 99999999999998, 'blue', '<%= photoURLOriginal %>']
   
   
   <%
		}
   %>
 ];

 function setMarkers(map, locations) {
   // Add markers to the map

   // Shapes define the clickable region of the icon.
   // The type defines an HTML &lt;area&gt; element 'poly' which
   // traces out a polygon as a series of X,Y points. The final
   // coordinate closes the poly by connecting to the first
   // coordinate.
   var shape = {
       coords: [1, 1, 1, 20, 18, 20, 18 , 1],
       type: 'poly'
   };


   for (var i = 0; i < locations.length; i++) {
     var pics = locations[i];
     var myLatLng = new google.maps.LatLng(pics[1], pics[2]);
     var marker = new google.maps.Marker({
         position: myLatLng,
         map: map,
         icon: new google.maps.MarkerImage("http://maps.google.com/mapfiles/ms/icons/" + pics[4] + ".png"),
         shape: shape,
         title: pics[0],
         zIndex: pics[3],
         url: pics[5]
     });
     
     google.maps.event.addListener(marker, 'click', function() {
    	 	window.open(this.url);
 	});
   }
 }

 google.maps.event.addDomListener(window, 'load', initialize);

     </script>
</head>
<body>
<br />
<br />
<%

long runningTimeEnd = new Date().getTime() - startAnfang;

System.out.println("LaufzeitGesamt: "+runningTimeEnd);

%>
<table align="center">
<tr style="background-color: #C4C4C4;">
	<td valign="top" style="padding: 10px;">
       	<u><b>Infos:</b></u><br /><br />
        GridID: <%= foundGrid %> <br/>
        PositionierteBilder: <%= gridPics.size() %> <br/>
		=========================<br />
		Distanz <img alt="" src="http://maps.google.com/mapfiles/ms/icons/red.png" /> - <img alt="" src="http://maps.google.com/mapfiles/ms/icons/blue.png" />:  <%= distance %> km<br /><br />
		
		<form name="zurueckIndex" action="http://localhost:8080/pae/" method="post" >
			<input type="hidden" name="gewuenschteSeite" value="zurueckIndex">
			<input type="submit" value=" Zur&uuml;ck ">
		</form><br />
    </td>
    <td>
        <div id="map-canvas" style="width: 1024px; height: 900px">
        </div>
    </td>
    <td valign="top" style="padding: 10px;">
        <u><b>Legende:</b></u><br /><br />
        <img alt="" src="http://maps.google.com/mapfiles/ms/icons/red.png" />
        Gesuchtes Bild<br />
        <img alt="" src="http://maps.google.com/mapfiles/ms/icons/blue.png" />
        Original Position<br />
        <img alt="" src="http://maps.google.com/mapfiles/ms/icons/yellow.png" />
        Grid<br />
    </td>
</tr>
</table>

</body>
</html>