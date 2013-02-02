package com.wsu.droidd;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Proc implements Parcelable {
	private int id;
	private String name;
	private ArrayList<IP> ips; // 0.0.0.0:0 1.1.1.1:1 format.

	public Proc() {
		id = 0;
		name = "";
		ips = new ArrayList<IP>();
	}

	public Proc(int i, String n) {
		id = i;
		name = n;
		ips = new ArrayList<IP>();
	}

	public Proc(int i, String n, String ip) {
		id = i;
		name = n;
		ips = new ArrayList<IP>();
		ips.add(new IP(ip));
	}

	public Proc(int i, String n, String[] ips) {
		id = i;
		name = n;
		this.ips = new ArrayList<IP>();
		for (String ip : ips) {
			this.ips.add(new IP(ip));
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int i) {
		id = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public void addIp(String ip) {
		ips.add(new IP(ip));
	}

	public ArrayList<IP> getIps() {
		return ips;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeArray(ips.toArray());
	}

	@Override
	public String toString() {
		return id + " " + name + " " + ips;
	}

	public class IP {
		protected static final String TAG = "IP";
		String ip, lat, lon, state, city, zip, whois;
		int port;

		public IP() {
		}

		public IP(String temp) {
			ip = temp.substring(0, temp.indexOf(":"));
			port = Integer.parseInt(temp.substring(temp.indexOf(":")).replaceAll("[^0-9]", ""));

			// Now do longitutde and latitude based on IP.
			// API Key:
			// aa1f6451c8150cc06d80cf9c0c2f82e477ba5948de7463e5480894619718c2d7
			String apiIPKey = "aa1f6451c8150cc06d80cf9c0c2f82e477ba5948de7463e5480894619718c2d7";
			String url;
			// Example output: OK;;74.125.45.100;US;UNITED
			// STATES;CALIFORNIA;MOUNTAIN VIEW;94043;37.3861;-122.084;-08:00
			url = "http://api.ipinfodb.com/v3/ip-city/?key=" + apiIPKey + "&ip=" + ip;
			String out = getWebPage(url);
			String[] parsed = out.split(";");
			state = parsed[1];
			city = parsed[2];
			zip = parsed[3];
			lat = parsed[4];
			lon = parsed[5];

			// Do whois lookup.
			// http://adam.kahtava.com/services/whois.{xml|json|jsonp|csv}?query={ipAddress}
			// Example output:
			// "74.125.133.106","arin-contact@google.com","Google Inc","+1-650-253-0000","arin-contact@google.com","Google Inc","+1-650-253-0000","2007-03-13T12:09:54-04:00","1600 Amphitheatre Parkway","Mountain View","US","Google Inc.","94043","CA","arin-contact@google.com","Google Inc","+1-650-253-0000","2012-02-24T09:44:34-05:00"
			url = "http://adam.kahtava.com/services/whois.csv?query=" + ip;
			out = getWebPage(url);
			whois = out.replace("\",\"", "\n");

		}

		private String getWebPage(final String url) {
			final StringBuffer sb = new StringBuffer();

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						URL u = new URL(url);
						URLConnection uconn = u.openConnection();
						HttpURLConnection conn = (HttpURLConnection) uconn;
						conn.connect();
						Object content = conn.getContent();
						java.io.InputStream stream = (java.io.InputStream) content;
						java.io.DataInputStream din = new java.io.DataInputStream(stream);
						byte[] buffer = new byte[1024];
						while (din.read(buffer) != -1) {
							sb.append(buffer.toString());
						}
					} catch (Exception e) {
						Log.e(TAG, "Error in getWebPage with url = " + url);
						Log.e(TAG, e.getMessage());
					}
				}
			});
			t.start();
			while (t.isAlive()) {
			}
			return sb.toString();

		}
	}
}