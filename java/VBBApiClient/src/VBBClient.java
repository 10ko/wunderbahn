import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;


/**
 * @author tenko
 * Wunderbar hackaton
 * 26/27 October 2014
 * Berlin
 */
public class VBBClient {
	
	// The VBB endpoint
	private static final String ENDPOINT_URL = "http://demo.hafas.de/bin/pub/vbb-fahrinfo/relaunch2011/extxml.exe/";
	
	// Static list containing all the supported stations
	private static final List<Station> stationListU2 = new ArrayList<Station>();
	private static final List<Station> stationListU8 = new ArrayList<Station>();
	
	
	
	public VBBClient(){
		super();
		populateStations();
	}
	
	
	
	
	/**
	 * Method to retrieve the list of stations that will lit up on the map.
	 * @param departureColor The rgb color representing the departure station
	 * @param arrivalColor The rgb color representing the arrival station
	 * @return The list of the station that will need to be lit up.
	 */
	public static List<Station> getStationListFromColor(String departureColor, String arrivalColor){
		
		Station departure = null;
		Station arrival = null;
		List<Station> result = new ArrayList<Station>();
		
		for(Station s : stationListU2){
			if(s.getColor().equals(departureColor)){
				departure = s;
				if(arrival != null)
					break;
			}
			if(s.getColor().equals(arrivalColor)){
				arrival = s;
				if(departure != null)
					break;
			}	
		}
		
		if(departure == null && arrival == null){
			for(Station s : stationListU8){
				if(s.getColor().equals(departureColor)){
					departure = s;
					if(arrival != null)
						break;
				}
				if(s.getColor().equals(arrivalColor)){
					arrival = s;
					if(departure != null)
						break;
				}	
			}
		}else if(departure == null){
			for(Station s : stationListU8){
				if(s.getColor().equals(departureColor)){
					departure = s;
					break;
				}
			}
		}else if(arrival == null){
			for(Station s : stationListU8){
				if(s.getColor().equals(arrivalColor)){
					arrival = s;
					break;
				}
			}
		}
		
		result = getStationListFromStations(departure, arrival);
		
		
		
		return result;
	}
		
	
	private static List<Station> getStationListFromStations(Station departure, Station arrival) {
		
		List<Station> elementList1 = null;
		List<Station> elementList2 = null;
		List<Station> returnList = new ArrayList<Station>();
		
		if(departure.getLine().equals("u2")){
			elementList1 = new ArrayList<Station>(stationListU2);
			elementList2 = new ArrayList<Station>(stationListU8);
		}else{
			elementList1 = new ArrayList<Station>(stationListU8);
			elementList2 = new ArrayList<Station>(stationListU2);
		}
		
		if(departure.getLine().equals(arrival.getLine())){
			for(Station s : elementList1){
				if((Integer.valueOf(s.getLedId()) < Integer.valueOf(departure.getLedId())
						&& Integer.valueOf(s.getLedId()) > Integer.valueOf(arrival.getLedId()))
						|| (Integer.valueOf(s.getLedId()) > Integer.valueOf(departure.getLedId())
								&& Integer.valueOf(s.getLedId()) < Integer.valueOf(arrival.getLedId()))){
					returnList.add(s);
					
				}
						
			}
		} else {
			if(departure.getLine().equals(elementList1.get(0).getLine())){
				Station alex = null;
				for(Station s : elementList1){
					if(s.getId().equals(StationList.STATION_08.getId()))
						alex = s;
				}
				for(Station s : elementList1){
					if((Integer.valueOf(s.getLedId()) < Integer.valueOf(departure.getLedId())
							&& Integer.valueOf(s.getLedId()) >= Integer.valueOf(alex.getLedId()))
							|| (Integer.valueOf(s.getLedId()) > Integer.valueOf(departure.getLedId())
									&& Integer.valueOf(s.getLedId()) <= Integer.valueOf(alex.getLedId()))){
						returnList.add(s);
						
					}
				}
				
				for(Station s : elementList2){
					if(s.getId().equals(StationList.STATION_08.getId()))
						alex = s;
				}
				for(Station s : elementList2){
					if((Integer.valueOf(s.getLedId()) < Integer.valueOf(arrival.getLedId())
							&& Integer.valueOf(s.getLedId()) >= Integer.valueOf(alex.getLedId()))
							|| (Integer.valueOf(s.getLedId()) > Integer.valueOf(arrival.getLedId())
									&& Integer.valueOf(s.getLedId()) <= Integer.valueOf(alex.getLedId()))){
						returnList.add(s);
						
					}
				}
				
			}else{
				Station alex = null;
				for(Station s : elementList1){
					if(s.getId().equals(StationList.STATION_08.getId()))
						alex = s;
				}
				for(Station s : elementList1){
					if((Integer.valueOf(s.getLedId()) < Integer.valueOf(arrival.getLedId())
							&& Integer.valueOf(s.getLedId()) >= Integer.valueOf(alex.getLedId()))
							|| (Integer.valueOf(s.getLedId()) > Integer.valueOf(arrival.getLedId())
									&& Integer.valueOf(s.getLedId()) <= Integer.valueOf(alex.getLedId()))){
						returnList.add(s);
						
					}
				}
				
				for(Station s : elementList2){
					if(s.getId().equals(StationList.STATION_08.getId()))
						alex = s;
				}
				for(Station s : elementList2){
					if((Integer.valueOf(s.getLedId()) < Integer.valueOf(departure.getLedId())
							&& Integer.valueOf(s.getLedId()) >= Integer.valueOf(alex.getLedId()))
							|| (Integer.valueOf(s.getLedId()) > Integer.valueOf(departure.getLedId())
									&& Integer.valueOf(s.getLedId()) <= Integer.valueOf(alex.getLedId()))){
						returnList.add(s);
						
					}
				}
			}
			
		}
		
		
		return returnList;
	}

	public static void main(String[] args) {
		
		populateStations();
		
		Station moritz = new Station(StationList.STATION_19.getName(), StationList.STATION_19.getId(), StationList.STATION_19.getLedId(), StationList.STATION_19.getLine(), StationList.STATION_19.getColor());
		Station rosent = new Station(StationList.STATION_03.getName(), StationList.STATION_03.getId(), StationList.STATION_03.getLedId(), StationList.STATION_03.getLine(), StationList.STATION_03.getColor());
		System.out.println(moritz);
		System.out.println(rosent);
		for(Station s : getStationListFromStations(moritz, rosent))
			System.out.println(s);
	}


	/**
	 * Method that calls the VBB Api. Gets the departure and arrival station id and returns the raw xml from the endpoint.
	 * 
	 * @param departureId The departure station id
	 * @param arrivalId The arrival station id
	 * @return The string containing the xml response from the endpoint 
	 * @throws Exception
	 */
	public static String getResponse(String departureId, String arrivalId) throws Exception {
		// Creating an instance of HttpClient.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {

			// Creating an instance of HttpPost.
			HttpPost httpost = new HttpPost(ENDPOINT_URL);

			// Adding all form parameters in a List of type NameValuePair

			DateTime dt = new DateTime();
			String now = dt.getHourOfDay() + "." + dt.getMinuteOfHour();
			
			String payload = getPayload(departureId, arrivalId);
			
			
			/**
			 * UrlEncodedFormEntity encodes form parameters and produce an
			 * output like param1=value1&param2=value2
			 */
			httpost.setEntity(new StringEntity(payload));

			// Executing the request.
			CloseableHttpResponse response = httpclient.execute(httpost);
			HttpEntity res = response.getEntity();

			StringWriter writer = new StringWriter();
			IOUtils.copy(res.getContent(), writer);

			try {
				// Do the needful with entity.
				HttpEntity entity = response.getEntity();
			} finally {
				// Closing the response
				response.close();
			}

			return writer.toString();

		} finally {
			httpclient.close();
		}

	}
	
	
	/**
	 * Returns the paylod to be sent to the VBB Api
	 * 
	 * @param departureId The departure station id
	 * @param arrivalId The arrival station id
	 * @return the paylod to be used by the HTTPClient
	 */
	private static String getPayload(String departureId, String arrivalId) {

		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		String time = formatter.format(cal.getTime());
		formatter = new SimpleDateFormat("YYYYMMdd");
		String date = formatter.format(cal.getTime());
		
		
		// Xml payload. We add the departure station id, the arrival station id and the current date and time
		String xmlPayload = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?> "+
				"<ReqC accessId=\"d6d72fbfc001c21abb538008b6f54097\" ver=\"1.1\" prod=\"testsystem\" lang=\"DE\">"+
				"  <ConReq>"+
				"    <RFlags b=\"0\" f=\"1\" /> "+
				"    <Start>"+
				"      <Station externalId=\""+departureId+"\" /> "+
				"      <Prod bike=\"0\" sleeper=\"0\" couchette=\"0\" /> "+
				"    </Start>"+
				"    <Dest>"+
				"      <Station externalId=\""+arrivalId+"\" /> "+
				"    </Dest>"+
				"    <ReqT date=\""+date+"\" time=\""+time+"\" /> "+
				"    <RFlags b=\"0\" chExtension=\"0\" f=\"3\" sMode=\"N\" /> "+
				"  </ConReq>"+
				"</ReqC>";
		
		
		
		return xmlPayload;
	}
	
	
	
	/**
	 * Static method to populate the stations list
	 */
	private static void populateStations() {		
		for(StationList s : StationList.values())
			if(s.getLine().equals("u2"))
				stationListU2.add(new Station(s.getName(), s.getId(), s.getLedId(), s.getLine(), s.getColor()));
			else
				stationListU8.add(new Station(s.getName(), s.getId(), s.getLedId(), s.getLine(), s.getColor()));
		
		Collections.sort(stationListU2);
		Collections.sort(stationListU8);
	}

}
