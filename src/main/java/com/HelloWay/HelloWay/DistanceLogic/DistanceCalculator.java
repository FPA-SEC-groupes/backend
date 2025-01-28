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
        public static Boolean isTheUserInTheSpaCe(String userLatitude,
                                            String userLongitude,
                                            double accuracy,
                                            Space space) {
        // Retrieve space's latitude and longitude
        String spaceLatitude = space.getLatitude();
        String spaceLongitude = space.getLongitude();

        // Calculate the threshold based on the space's surface area in m²
        double radiusInKm = Math.sqrt(space.getSurfaceEnM2() / Math.PI) / 1000.0; // Convert to kilometers
        double threshold = radiusInKm + accuracy; // Add accuracy (in kilometers) to the threshold

        // Calculate the distance between the user's location and the space's center
        double distanceInKm = calculateDistance(
            Double.parseDouble(userLatitude),
            Double.parseDouble(userLongitude),
            Double.parseDouble(spaceLatitude),
            Double.parseDouble(spaceLongitude)
        );

        // Check if the user is within the threshold
        if (distanceInKm <= threshold) {
            System.out.println("User is near the space.");
            return true;
        } else {
            System.out.println("User is not near the space.");
            return false;
        }
    }

}
