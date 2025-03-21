package frontend.parser;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLParser {
	
	public static void main(String[] args) {
        String parseTrips = "http://localhost:8080/A00279259_Backend/rest/trips";
        String parseActivities = "http://localhost:8080/A00279259_Backend/rest/activities";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Accept XML
            HttpGet requestTrips = new HttpGet(parseTrips);
            requestTrips.setHeader("Accept", "application/xml");
            
            HttpGet requestActivities = new HttpGet(parseActivities);
            requestActivities.setHeader("Accept", "application/xml");

            try (CloseableHttpResponse responseTrips = httpClient.execute(requestTrips)) {
                if (responseTrips.getStatusLine().getStatusCode() == 200) {
                	// Parse Trips
                    System.out.println("Parsing Trips XML Response...");
                    parseXMLTrips(responseTrips.getEntity().getContent());
                } else {
                    System.out.println("Error: " + responseTrips.getStatusLine().getStatusCode());
                }
                EntityUtils.consume(responseTrips.getEntity());
            }
            
            try (CloseableHttpResponse responseActivities = httpClient.execute(requestActivities)) {
                // Parse Activities
            	System.out.println("Parsing Activities XML Response...");
                parseXMLActivities(responseActivities.getEntity().getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseXMLTrips(InputStream inputStream) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(inputStream));

        int eventType = parser.getEventType();
        String tagName = "";
        String tripId = "", destination = "", startDate = "", endDate = "", budget = "", notes = "";

        System.out.println("\n--- List of Trips ---");

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    break;

                case XmlPullParser.TEXT:
                    String text = parser.getText().trim();
                    if ("tripId".equals(tagName)) {
                        tripId = text;
                    } else if ("destination".equals(tagName)) {
                        destination = text;
                    } else if ("startDate".equals(tagName)) {
                        startDate = text;
                    } else if ("endDate".equals(tagName)) {
                        endDate = text;
                    } else if ("budget".equals(tagName)) {
                        budget = text;
                    } else if ("notes".equals(tagName)) {
                        notes = text;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("trip".equals(parser.getName())) {
                        System.out.println("TripID: " + tripId + ", Destination: " + destination +
                                           ", Start: " + startDate + ", End: " + endDate +
                                           ", Budget: €" + budget + ", Notes: " + notes);
                    }
                    break;
            }
            eventType = parser.next();
        }
        System.out.println("\nFinished Parsing Trips XML Response...\n");
        System.out.println("---------------------------------------------------------------\n"
        				+ "===============================================================\n"
        				+ "---------------------------------------------------------------\n");
    }
    
    private static void parseXMLActivities(InputStream inputStream) throws Exception {
    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(inputStream));

	    int eventType = parser.getEventType();
	    String tagName = "", name = "", activityDate = "", location = "", cost = "";
	    int activityId = 0, tripId = 0;

	    
	    System.out.println("\n--- List of Activities ---");

	    while (eventType != XmlPullParser.END_DOCUMENT) {
	        switch (eventType) {
	            case XmlPullParser.START_TAG:
	                tagName = parser.getName();
	                
	                if ("activityId".equals(tagName)) {
	                    activityId = Integer.parseInt(parser.nextText().trim());
	                } else if ("tripId".equals(tagName)) {
	                    tripId = Integer.parseInt(parser.nextText().trim());
	                } else if ("name".equals(tagName)) {
	                    name = parser.nextText().trim();
	                } else if ("activityDate".equals(tagName)) {
	                	activityDate = parser.nextText().trim();
	                } else if ("location".equals(tagName)) {
	                    location = parser.nextText().trim();
	                } else if ("cost".equals(tagName)) {
	                    cost = parser.nextText().trim();
	                }
	                
	                break;
	                
	            case XmlPullParser.END_TAG:
	                if ("activity".equals(parser.getName())) {
	                    System.out.println("TripID: " + tripId + ", ActivityID: " + activityId +
	                                       ", Name: " + name + ", Date: " + activityDate +
	                                       ", Location: " + location + ", Cost: €" + cost);
	                }
	                break;
	        }
	        eventType = parser.next();
	    }
	    System.out.println("\nFinished Parsing Activities XML Response...\n");
	    System.out.println("---------------------------------------------------------------\n"
				+ "===============================================================\n");
	}
}
