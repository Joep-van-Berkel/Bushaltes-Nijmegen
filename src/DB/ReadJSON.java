package DB;

import Model.BushalteLocatieModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;

public class ReadJSON {

    public String getJSONDataFromUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL("https://services.nijmegen.nl/geoservices/extern_Mobiliteit/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=extern_Mobiliteit%3AMOB_BUSHALTES&outputFormat=application%2Fjson");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("ResponseCode: " + responseCode);
            } else {
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine());
                }
                scanner.close();
            }
        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return stringBuilder.toString();
    }

    public ArrayList<BushalteLocatieModel> getBushalteLocatieData() {
        ArrayList<BushalteLocatieModel> bushaltes = new ArrayList<>();
        HashSet<String> halteNamenSet = new HashSet<>();
        JSONParser parser = new JSONParser();
        String jsonData = getJSONDataFromUrl();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
            JSONArray features = (JSONArray) jsonObject.get("features");

            for (Object featureObj : features) {
                JSONObject feature = (JSONObject) featureObj;
                JSONObject geometry = (JSONObject) feature.get("geometry");
                JSONArray coordinates = (JSONArray) geometry.get("coordinates");
                JSONObject properties = (JSONObject) feature.get("properties");

                String halteNaam = (String) properties.get("HALTENAAM");
                String rolstoelToegankelijk = (String) properties.get("ROLSTOELTOEGANKELIJK");
                double x = (Double) coordinates.get(0);
                double y = (Double) coordinates.get(1);

                if (!halteNamenSet.contains(halteNaam)) {
                    halteNamenSet.add(halteNaam);
                    double[] ddCoordinates = rdToDd(x, y);

                    BushalteLocatieModel bushalte = new BushalteLocatieModel(
                            halteNaam,
                            "Ja".equals(rolstoelToegankelijk),
                            ddCoordinates
                    );

                    bushaltes.add(bushalte);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bushaltes;
    }

    public double[] rdToDd(double x, double y) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromName("EPSG:28992"); // Amersfoort / RD New
        CoordinateReferenceSystem targetCRS = crsFactory.createFromName("EPSG:4326"); // WGS 84

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(sourceCRS, targetCRS);

        ProjCoordinate sourceCoord = new ProjCoordinate(x, y);
        ProjCoordinate targetCoord = new ProjCoordinate();

        transform.transform(sourceCoord, targetCoord);

        String lat = String.format(Locale.US, "%.6f", targetCoord.y);
        String lon = String.format(Locale.US, "%.6f", targetCoord.x);

        return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)}; 
    }
}