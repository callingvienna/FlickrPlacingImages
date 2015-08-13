<%@page import="model.GridFinder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>PAE</title>

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
     zoom: 2,
     //mapTypeId: google.maps.MapTypeId.SATELLITE,
     center: new google.maps.LatLng(21.318642, 0.957184)
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
	
	<%
  		GridFinder gridFinder = new GridFinder();
  		// Grid aufbauen dafuer alle Koordinaten der 648 Grids hernehmen
  		for(int i=1; i<=648;i++){
			Double[] gridKoordinaten = new Double[4];
   			
   			// zum erstellen des grid overlays
   			gridKoordinaten = gridFinder.findGridKoordinaten(i);
	%>
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
   	<%
	}// for 648
	%>
 }

  google.maps.event.addDomListener(window, 'load', initialize);
 
     </script>
</head>
<body>
<br />
<br />
<table align="center"  style="width: 1274px;height: 900px;background-color: #EBEBEB;">
<tr style="font-size:larger; background-color: #C4C4C4; text-align: center;padding: 5px;">
<td style="vertical-align:top; padding: 5px;width:250px;height: 200px" align="left">
	<form name="fileupload" action="Uicontroller/csv" method="post" enctype="multipart/form-data">
		SuchBild:<br /> <input type="file"  name="inputfile" /><br /><br />
		OriginalBild:<br /> <input type="file"  name="inputfileOriginal" /><br /><br /><br />
		<input type="submit" value=" Absenden ">
	</form>
</td>
<td>
    <div id="map-canvas" style="width: 1024px; height: 900px">
    </div>
</td>
</tr>
</table>
</body>
</html>