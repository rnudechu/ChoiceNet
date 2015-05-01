import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;


public class CouchDBOperations {

	public static CouchDBOperations instance = new CouchDBOperations();
	
	public static CouchDBOperations getInstance()
	{
		return instance;
	}

	public void postRestInterface(String url, Object payload) 
	{
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
	
			Gson gson = new Gson();
			String json = "";
			if(payload.getClass() != String.class)
			{
				json = gson.toJson(payload);
			}
			else
			{
				json = payload.toString();
			}
			System.out.println(json);
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(json);
			wr.flush();
			wr.close();
	
			int responseCode = con.getResponseCode();
			Logger.log("\nSending 'POST' request to URL : " + url);
			Logger.log("Post parameters : " + json);
			Logger.log("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
			Logger.log(response.toString());
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void putRestInterface(String url, String payload) 
	{
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			//add request header
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json");
	
			String json = payload.toString();
	
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(json);
			wr.flush();
			wr.close();
	
			int responseCode = con.getResponseCode();
			Logger.log("\nSending 'PUT' request to URL : " + url);
			Logger.log("Post parameters : " + json);
			Logger.log("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
			Logger.log(response.toString());
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// HTTP GET request
	public String getRestInterface(String url)  
	{
		String result = "";
		try
		{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// optional default is GET
			con.setRequestMethod("GET");
	
			int responseCode = con.getResponseCode();
			Logger.log("\nSending 'GET' request to URL : " + url);
			Logger.log("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
			result = response.toString();
		} 
		catch (FileNotFoundException e) {
			Logger.log("WARNING: Requested document ("+url+") not found!");
			result = "404";
		}
		catch (MalformedURLException e) {
			Logger.log("ERROR: Requested document ("+url+") was malformed!");
			result = "404";
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void deleteRestInterface(String url) 
	{
		URL obj;
		String result;
		try {
			obj = new URL(url);
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			//add request header
			con.setRequestMethod("DELETE");
	
			int responseCode = con.getResponseCode();
			Logger.log("\nSending 'DELETE' request to URL : " + url);
			Logger.log("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
			result = response.toString();
	
			//print result
			Logger.log(result);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CouchDBResponse retrieveCouchObject (String url)
	{
		CouchDBResponse cResponse;
		String response = getRestInterface(url);
		cResponse = CouchDBResponse.parseJson(response);
		return cResponse;
	}

}
