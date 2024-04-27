package com.HelloWay.HelloWay.DistanceLogic;

import com.HelloWay.HelloWay.entities.Space;

import static java.lang.Math.sqrt;

public class DistanceCalculator {
    private  static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers

    public static double calculateDistance(double userLatitude, double userLongitude, double cafeLatitude, double cafeLongitude) {
        // Convert latitude and longitude to radians
        double userLatRad = Math.toRadians(userLatitude);
        double userLonRad = Math.toRadians(userLongitude);
        double cafeLatRad = Math.toRadians(cafeLatitude);
        double cafeLonRad = Math.toRadians(cafeLongitude);

        // Calculate the differences between the latitude and longitude coordinates
        double latDiff = cafeLatRad - userLatRad;
        double lonDiff = cafeLonRad - userLonRad;

        // Apply the Haversine formula
        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.cos(userLatRad) * Math.cos(cafeLatRad) *
                        Math.pow(Math.sin(lonDiff / 2), 2);
        double c = 2 * Math.atan2(sqrt(a), sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
    }

    //  TODO : we neeed to update and test this : for the cast and the threshold

    /**
     *
     * @param userLatitude : double
     * @param userLongitude : double
     * @param space : Space
     * @return Boolean to say if the user in the space or not
     */
    public static  Boolean isTheUserInTheSpaCe(String userLatitude,
                                               String userLongitude,
                                               double accuracy,
                                               Space space
                                               ){
        String spaceLatitude = space.getLatitude();
        String spaceLongitude = space.getLongitude();
        // By calculating the threshold based on the space's surface area,
        // the proximity criteria will be adjusted dynamically according to the size of the space.
        // Larger spaces will have larger thresholds,
        // allowing users to be considered near the space even if they are farther away.
        // Smaller spaces will have smaller thresholds,
        // requiring users to be closer to be considered near the space.
         double cast = space.getSurfaceEnM2()/Math.pow(10,6); //from m2 to km2 for the Rayon
        double threshold = sqrt(cast/Math.PI) + accuracy;
        // double threshold = 5.0; // Threshold distance in kilometers
        double distance = 1000 * calculateDistance(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude), Double.parseDouble(spaceLatitude), Double.parseDouble(spaceLongitude));
        if (distance <= threshold) {
            System.out.println("User is near the space.");
            return true;
        }
        else {
            System.out.println("User is not near the space.");
            return false ;
        }
    }
}
