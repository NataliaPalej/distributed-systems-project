package a00279259.parser;

import java.io.StringReader;

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
            // Send GET request
            HttpGet requestTrips = new HttpGet(parseTrips);
            HttpGet requestActivities = new HttpGet(parseActivities);
            requestTrips.setHeader("Accept", "application/xml");
            requestActivities.setHeader("Accept", "application/xml");
            
            try (CloseableHttpResponse responseTrips = httpClient.execute(requestTrips)) {
            	String xmlResponseTrips = EntityUtils.toString(responseTrips.getEntity());
            	System.out.println("Raw XML Response Trips:\n" + xmlResponseTrips);
                // Parse XML response
            	parseXMLTrips(xmlResponseTrips);
            }
            
            try (CloseableHttpResponse responseActivities = httpClient.execute(requestActivities)) {
            	String xmlResponseActivities = EntityUtils.toString(responseActivities.getEntity());
            	System.out.println("\n\nRaw XML Response Activities:\n" + xmlResponseActivities);
                // Parse XML response
            	parseXMLActivities(xmlResponseActivities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static void parseXMLTrips(String xml) throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xml));

	    int eventType = parser.getEventType();
	    String tagName = "", destination = "", startDate = "", endDate = "", budget = "", notes = "";

	    System.out.println("\nParsed List of Trips:");

	    while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if ("destination".equals(tagName)) {
                        destination = parser.nextText().trim();
                    } else if ("startDate".equals(tagName)) {
                        startDate = parser.nextText().trim();
                    } else if ("endDate".equals(tagName)) {
                        endDate = parser.nextText().trim();
                    } else if ("budget".equals(tagName)) {
                        budget = parser.nextText().trim();
                    } else if ("notes".equals(tagName)) {
                        notes = parser.nextText().trim();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("trip".equals(parser.getName())) {
                        System.out.println("Destination: " + destination + ", Start Date: " + startDate +
                                           ", End Date: " + endDate + ", Budget: €" + budget + ", Notes: " + notes);
                    }
                    break;
            }
            eventType = parser.next();
	    }
	}
	
	private static void parseXMLActivities(String xml) throws Exception {
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser parser = factory.newPullParser();
	    parser.setInput(new StringReader(xml));

	    int eventType = parser.getEventType();
	    String tagName = "", name = "", activityDate = "", location = "", cost = "";
	    int activityId = 0, tripId = 0;

	    System.out.println("\nList of Activities:");

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
	                	System.out.println("\nactivityDate: " + activityDate);
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
	}


}