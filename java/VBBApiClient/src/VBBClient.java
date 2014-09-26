import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class VBBClient {
	
	private static final String ENDPOINT_URL = "http://demo.hafas.de/bin/pub/vbb-fahrinfo/relaunch2011/extxml.exe/";
	
	
	public static void main(String[] args) throws Exception {
		System.out.println( getResponse("009100701", "009013101") );
	}

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
	


	private static String getPayload(String departureId, String arrivalId) {

		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		String time = formatter.format(cal.getTime());
		formatter = new SimpleDateFormat("YYYYMMdd");
		String date = formatter.format(cal.getTime());
		
		
		
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

}
