package app.roadquality.roadquality.utils;

import java.util.ArrayList;
import java.util.List;

import app.roadquality.roadquality.data.entities.Accelerometer;
import app.roadquality.roadquality.data.entities.Gps;
import app.roadquality.roadquality.data.entities.RoadPoint;

public class maps {

    public static final String API_KEY = "GOOGLE_MAPS_API_KEY";

    public static class utils {

        public static double deg2Rad(double deg) {
            return deg * Math.PI / 180.0;
        }

        public static double rad2Deg(double rad) {
            return 180.0 * rad / Math.PI;
        }

        public static double latitudeRad(LatLng latLng) {
            return deg2Rad(latLng.lat());
        }

        public static double longitudeRad(LatLng latLng) {
            return deg2Rad(latLng.lng());
        }

        public static double angleBetween(LatLng startLatLng, LatLng endLatLng) {
            double a = longitudeRad(startLatLng);
            double c = latitudeRad(startLatLng);
            double b = longitudeRad(endLatLng);
            double d = latitudeRad(endLatLng);
            return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((c - d) / 2), 2)
                    + Math.cos(c) * Math.cos(d) * Math.pow(Math.sin((a - b) / 2), 2)));
        }

        public static double limitBetween(double a, double b, double c) {
            c -= b;
            return ((a - b) % c + c) % c + b;
        }

        public static List<RoadPoint> interpolateAccelerometerAndGpsData(
                List<Accelerometer> accelerometerList, List<Gps> gpsList) {

            ArrayList<RoadPoint> roadPointList = new ArrayList<>();

            if (gpsList.size() == 0 || accelerometerList.size() == 0) return roadPointList;

            int gpsIndex = 0;
            int accelIndex = 0;

            Gps startGps = gpsList.get(gpsIndex);

            long tripId = startGps.getTimestamp();

            Accelerometer accelerometer = accelerometerList.get(accelIndex);

            while (accelerometer.getTimestamp() < startGps.getTimestamp()) {
                if (accelIndex >= accelerometerList.size() - 1) {
                    break;
                }
                accelIndex++;
                accelerometer = accelerometerList.get(accelIndex);
            }

            Gps endGps = gpsList.get(gpsIndex);

            for (gpsIndex = 1; gpsIndex < gpsList.size(); gpsIndex++) {
                roadPointList.add(RoadPoint.fromGps(startGps, tripId));
                endGps = gpsList.get(gpsIndex);

                LatLng startLatLng = new LatLng(startGps.getLatitude(),
                        startGps.getLongitude());
                LatLng endLatLng = new LatLng(endGps.getLatitude(),
                        endGps.getLongitude());

                double gpsSpeed =
                        geometry.spherical.computeDistanceBetween(startLatLng, endLatLng) /
                                (endGps.getTimestamp() - startGps.getTimestamp()); //meters per ms

                double heading = geometry.spherical.computeHeading(startLatLng, endLatLng);

                while (accelerometer.getTimestamp() <= endGps.getTimestamp() &&
                        accelIndex < accelerometerList.size()) {
                    double interpolatedDuration = accelerometer.getTimestamp() - startGps.getTimestamp();
                    double interpolatedDistance = gpsSpeed * interpolatedDuration;

                    LatLng interpolatedLatLng = geometry.spherical.computeOffset(startLatLng,
                            interpolatedDistance, heading);

                    roadPointList.add(
                            RoadPoint.fromAccelerometer(
                                    accelerometer,
                                    interpolatedLatLng,
                                    tripId,
                                    (float) interpolatedDuration,
                                    (float) interpolatedDistance,
                                    gpsSpeed * 1000 // meters per ms -> meters per second
                            )
                    );

                    if (accelIndex < accelerometerList.size() - 1) {
                        accelIndex++;
                        accelerometer = accelerometerList.get(accelIndex);
                    } else {
                        break;
                    }
                }

                startGps = gpsList.get(gpsIndex);

            }

            roadPointList.add(RoadPoint.fromGps(endGps, tripId));


            return roadPointList;
        }
    }

    public static class LatLng {
        final double x, y;

        public LatLng(double lat, double lng) {
            this.y = lat;
            this.x = lng;
        }

        public double lat() {
            return this.y;
        }

        public double lng() {
            return this.x;
        }

        public String toString() {
            return"(" + this.lat() + ", " + this.lng() + ")";
        }

        public String toUrlValue(int precision) {
            return String.format("%." + precision + "f", this.lat()) + ", " + String.format("%." + precision + "f", this.lng());
        }

        public String toUrlValue() {
            return this.toUrlValue(6);
        }
    }

    public static class geometry {
        public static class spherical {

            public static final double EARTH_RADIUS = 6378137;

            public static double computeDistanceBetween(LatLng startLatLng, LatLng endLatLng, double r) {
                return utils.angleBetween(startLatLng, endLatLng) * r;
            }

            public static double computeDistanceBetween(LatLng startLatLng, LatLng endLatLng) {
                return computeDistanceBetween(startLatLng, endLatLng, EARTH_RADIUS);
            }

            public static double computeHeading(LatLng startLatLng, LatLng endLatLng) {
                double c = utils.latitudeRad(startLatLng);
                double d = utils.longitudeRad(startLatLng);
                double a = utils.latitudeRad(endLatLng);
                double b = utils.longitudeRad(endLatLng) - d;
                return utils.limitBetween(
                        utils.rad2Deg(
                                Math.atan2(
                                        Math.sin(b) * Math.cos(a),
                                        Math.cos(c) * Math.sin(a) - Math.sin(c) * Math.cos(a) * Math.cos(b)
                                )
                        ),
                        -180, 180
                );
            }

            public static LatLng computeOffset(
                    LatLng startLatLng, double distance, double heading, double r) {
                distance /= r;
                heading = utils.deg2Rad(heading);
                double e = utils.latitudeRad(startLatLng);
                double a = utils.longitudeRad(startLatLng);
                double d = Math.cos(distance);
                double b = Math.sin(distance);
                double f = Math.sin(e);
                e = Math.cos(e);

                double g = d * f + b * e * Math.cos(heading);
                return new LatLng(
                        utils.rad2Deg(Math.asin(g)),
                        utils.rad2Deg(a + Math.atan2(b * e * Math.sin(heading), d - f * g))
                );
            }

            public static LatLng computeOffset(
                    LatLng startLatLng, double distance, double heading) {
                return computeOffset(startLatLng, distance, heading, EARTH_RADIUS);
            }
        }
    }

    public static class webservices {

//        public JSONObject snapToRoad(JSONArray path, boolean interpolate, String apiKey) {
//
//            return null;
//        }
//        public JSONObject snapToRoad(JSONArray path, boolean interpolate) {
//            return this.snapToRoad(path, interpolate, API_KEY);
//        }
//        public JSONObject snapToRoad(JSONArray path) {
//            return this.snapToRoad(path, true);
//        }

    }

}
