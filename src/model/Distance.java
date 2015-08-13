package model;

import java.text.DecimalFormat;

/**
 *  Haversine Distance Algorithmus 
 *  R = Erd radius (mean radius = 6,371km)
    Δlat = lat2− lat1
    Δlong = long2− long1
    a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
    c = 2.atan2(√a, √(1−a))
    d = R.c
 *
 */
public class Distance {
	/**
     * @param longLat
     */
    public double getDistance(String[] longLat) {

    		final int R = 6371; // Weltradius
        Double lat1 = Double.parseDouble(longLat[0]);
        Double long1 = Double.parseDouble(longLat[1]);
        Double lat2 = Double.parseDouble(longLat[2]);
        Double long2 = Double.parseDouble(longLat[3]);
        Double latDistance = toRad(lat2-lat1);
        Double longDistance = toRad(long2-long1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
                   Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
                 
        DecimalFormat df = new DecimalFormat("#.00");
        String temp = df.format(distance);
        temp = temp.replaceAll(",", ".");
        distance = Double.parseDouble(temp);
        
        return distance;
    }
     
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
}
