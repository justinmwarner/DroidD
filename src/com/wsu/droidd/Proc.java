package com.wsu.droidd;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;

public class Proc implements Parcelable {
	protected static final String TAG = "Proc";
	private String ip, name;
	private String lat, lon, state, city, zip, whois; // These are obtained by
	private int port; // the IP and external sources.

	public Proc() {
		name = "";
		ip = "";
		lat = "";
		lon = "";
		state = "";
		city = "";
		zip = "";
		whois = "";
	}

	public Proc(String ip, String port, String name) {
		this.ip = ip.replaceAll(" ", "");
		try {
			this.port = Integer.parseInt(port.replaceAll("[^0-9]", ""));
		} catch (NumberFormatException e) {
			Log.d(TAG, port);
		}
		this.name = name;
		setExtraInfo(ip);
	}

	public Proc(String name, String ip, int port, String lat, String lon, String state,
			String city, String zip, String whois) {
		this.port = port;
		this.ip = ip;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.state = state;
		this.city = city;
		this.zip = zip;
		this.whois = whois;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(ip);
		dest.writeInt(port);
		dest.writeString(lat);
		dest.writeString(lon);
		dest.writeString(state);
		dest.writeString(city);
		dest.writeString(zip);
		dest.writeString(whois);
	}

	@Override
	public String toString() {
		return name + " " + ip + " " + port + "\nCoordinates: " + lat + ", " + lon + "\n" + city
				+ ", " + state + " " + zip + "\n" + whois;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getWhois() {
		return whois;
	}

	public void setWhois(String whois) {
		this.whois = whois;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private void setExtraInfo(String temp) {
		// Now do longitutde and latitude based on IP.
		// API Key:
		// aa1f6451c8150cc06d80cf9c0c2f82e477ba5948de7463e5480894619718c2d7
		String apiIPKey = "aa1f6451c8150cc06d80cf9c0c2f82e477ba5948de7463e5480894619718c2d7";
		String url;
		// Example output:
		// OK;;74.125.142.108;US;UNITED STATES;NEW YORK;NEW YORK
		// CITY;10081;40.7143;-74.006;-05:00
		url = "http://api.ipinfodb.com/v3/ip-city/?key=" + apiIPKey + "&ip=" + ip;
		String out = getWebPage(url);
		String[] parsed = out.split(";");
		state = parsed[5];
		city = parsed[6];
		zip = parsed[7];
		lat = parsed[8];
		lon = parsed[9];

		// Do whois lookup.
		// http://adam.kahtava.com/services/whois.{xml|json|jsonp|csv}?query={ipAddress}
		// Example output:
		// "74.125.133.106","arin-contact@google.com","Google Inc","+1-650-253-0000","arin-contact@google.com","Google Inc","+1-650-253-0000","2007-03-13T12:09:54-04:00","1600 Amphitheatre Parkway","Mountain View","US","Google Inc.","94043","CA","arin-contact@google.com","Google Inc","+1-650-253-0000","2012-02-24T09:44:34-05:00"
		url = "http://adam.kahtava.com/services/whois.csv?query=" + ip;
		out = getWebPage(url);
		whois = out.replace("\",\"", "\n");

	}

	String webpageResult = "";

	private String getWebPage(final String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		// Get the response
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			webpageResult = client.execute(request, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return webpageResult;
	}
}